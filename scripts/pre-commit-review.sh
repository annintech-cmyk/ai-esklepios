#!/usr/bin/env bash
# scripts/pre-commit-review.sh
# Automated code-quality gate run before every git commit.
# Checks both Android (Kotlin/Compose) and iOS (Swift/SwiftUI) staged changes.
#
# Exit codes:
#   0  — passed (or warnings only — commit proceeds)
#   1  — critical issues found; commit blocked
#
# To skip in an emergency: git commit --no-verify

set -uo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"

# ─── Colour helpers ───────────────────────────────────────────────────────────
RED='\033[0;31m'; YELLOW='\033[1;33m'; GREEN='\033[0;32m'
CYAN='\033[0;36m'; BOLD='\033[1m'; RESET='\033[0m'

info()    { echo -e "${CYAN}[INFO]${RESET}  $*"; }
ok()      { echo -e "${GREEN}[OK]${RESET}    $*"; }
warn()    { echo -e "${YELLOW}[WARN]${RESET}  $*"; }
err()     { echo -e "${RED}[ERROR]${RESET} $*"; }
section() { echo -e "\n${BOLD}── $* ──${RESET}"; }

ERRORS=0
WARNINGS=0
inc_error() { ERRORS=$((ERRORS + 1)); }
inc_warn()  { WARNINGS=$((WARNINGS + 1)); }

echo -e "\n${BOLD}╔══════════════════════════════════════════════════════╗${RESET}"
echo -e "${BOLD}║         eSklepios Pre-Commit Code Review             ║${RESET}"
echo -e "${BOLD}╚══════════════════════════════════════════════════════╝${RESET}"

cd "$PROJECT_ROOT"

# ─── Collect staged files ──────────────────────────────────────────────────────
STAGED_KT=$(git diff --cached --name-only --diff-filter=ACMR | grep '\.kt$'    || true)
STAGED_SWIFT=$(git diff --cached --name-only --diff-filter=ACMR | grep '\.swift$' || true)
STAGED_ALL=$(git diff --cached --name-only --diff-filter=ACMR || true)

KT_COUNT=$(echo "$STAGED_KT"    | grep -c . || true)
SW_COUNT=$(echo "$STAGED_SWIFT" | grep -c . || true)
ALL_COUNT=$(echo "$STAGED_ALL"  | grep -c . || true)

info "Staged: $ALL_COUNT files ($KT_COUNT Kotlin, $SW_COUNT Swift)"

if [ "$ALL_COUNT" -eq 0 ]; then
    warn "Nothing staged — skipping review."; exit 0
fi

# ═════════════════════════════════════════════════════════════════════════════
# 1. SENSITIVE DATA & FORBIDDEN FILES
# ═════════════════════════════════════════════════════════════════════════════
section "Sensitive Data & Forbidden Files"

# dev.properties must never be committed
if echo "$STAGED_ALL" | grep -q 'dev\.properties'; then
    err "dev.properties is staged — must NOT be committed (contains secrets, Rule SEC-2)"
    inc_error
fi

# Check for hardcoded secrets in diffs
SENSITIVE_RE='(password\s*=\s*["'"'"'][^"'"'"']{4,}|api_key\s*=\s*["'"'"']|secret\s*=\s*["'"'"']|Bearer\s+[A-Za-z0-9._-]{20,}|-----BEGIN\s+(RSA\s+)?PRIVATE KEY)'
while IFS= read -r file; do
    [ -z "$file" ] && continue; [ ! -f "$file" ] && continue
    matches=$(git diff --cached "$file" | grep '^+' | grep -v 'const val KEY_' | grep -iE "$SENSITIVE_RE" || true)
    if [ -n "$matches" ]; then
        err "Potential secret in $file:"; echo "$matches" | head -3
        inc_error
    fi
done <<< "$STAGED_ALL"

# Build / generated artifacts
FORBIDDEN_RE='^(build/|\.gradle/|androidApp/build/|shared/build/|.*\.class$|.*\.jar$|.*-release\.apk$|.*-debug\.apk$|.*\.xcarchive/|.*\.ipa$)'
if echo "$STAGED_ALL" | grep -qE "$FORBIDDEN_RE"; then
    err "Build/generated files staged:"; echo "$STAGED_ALL" | grep -E "$FORBIDDEN_RE"
    inc_error
fi

# Large binaries > 500 KB
while IFS= read -r file; do
    [ -z "$file" ] && continue; [ ! -f "$file" ] && continue
    size=$(wc -c < "$file" 2>/dev/null || echo 0)
    if [ "$size" -gt 524288 ]; then
        warn "Large file staged: $file ($(( size / 1024 )) KB)"
        inc_warn
    fi
done <<< "$STAGED_ALL"

[ "$ERRORS" -eq 0 ] && ok "No sensitive data or forbidden files"

# ═════════════════════════════════════════════════════════════════════════════
# 2. KOTLIN — CODING STANDARDS
# ═════════════════════════════════════════════════════════════════════════════
if [ -n "$STAGED_KT" ]; then
section "Kotlin — Coding Standards"

while IFS= read -r file; do
    [ -z "$file" ] && continue; [ ! -f "$file" ] && continue
    ADDED=$(git diff --cached "$file" | grep '^+' | grep -v '^+++')

    # Debug output (Rule LOG-1)
    if echo "$ADDED" | grep -qE '^\+\s*(println|print)\s*\('; then
        warn "$file: debug println — remove before merging (Rule LOG-1)"
        inc_warn
    fi

    # CancellationException not rethrown (Rules EH-1, CR-2)
    if echo "$ADDED" | grep -qE 'catch\s*\(\s*e\s*:\s*(Exception|Throwable)\s*\)'; then
        if ! echo "$ADDED" | grep -q 'CancellationException'; then
            err "$file: catch(Exception) without CancellationException rethrow (Rule EH-1)"
            inc_error
        fi
    fi

    # MockK in commonTest (Rule T-1)
    if echo "$file" | grep -q 'commonTest'; then
        if echo "$ADDED" | grep -qE '\bmockk\b|every\s*\{|coEvery\s*\{'; then
            err "$file: MockK in commonTest — use fake implementations (Rule T-1)"
            inc_error
        fi
    fi

    # java.time in commonMain (Rule A-2, DT-2)
    if echo "$file" | grep -q 'commonMain'; then
        if echo "$ADDED" | grep -qE 'import java\.time'; then
            err "$file: java.time in commonMain — use kotlinx.datetime (Rule A-2)"
            inc_error
        fi
    fi

    # Domain importing data layer (Rule A-1)
    if echo "$file" | grep -q '/domain/'; then
        if echo "$ADDED" | grep -qE 'import.*\.(data\.(network|repository|db)|ktor|sqldelight)'; then
            err "$file: domain layer imports data — layer separation violation (Rule A-1)"
            inc_error
        fi
    fi

    # GlobalScope (Rule CR-6)
    if echo "$ADDED" | grep -qE '\bGlobalScope\b'; then
        err "$file: GlobalScope forbidden — use viewModelScope (Rule CR-6)"
        inc_error
    fi

    # Exposed MutableStateFlow (Rule SM-2)
    if echo "$ADDED" | grep -qE 'val\s+[a-z]\w*\s*=\s*MutableStateFlow'; then
        warn "$file: possible public MutableStateFlow — use private _backing field (Rule SM-2)"
        inc_warn
    fi

    # Hardcoded dimensions in view files (Rule UI-1a)
    if echo "$file" | grep -qE '/(view|Screen)'; then
        DIMS=$(echo "$ADDED" | grep -oE '[1-9][0-9]*\.(dp|sp)' || true)
        if [ -n "$DIMS" ]; then
            warn "$file: hardcoded dimension(s) $(echo "$DIMS" | tr '\n' ' ')— use Dimens tokens (Rule UI-1a)"
            inc_warn
        fi
    fi

    # Hardcoded color literals (Rule UI-3)
    if echo "$ADDED" | grep -qE 'Color\(0x[0-9A-Fa-f]'; then
        warn "$file: hardcoded Color literal — use design token (Rule UI-3)"
        inc_warn
    fi

    # Inline validation logic in ViewModel (Rule A-9)
    if echo "$file" | grep -q 'ViewModel'; then
        if echo "$ADDED" | grep -qE 'fun\s+isValid\w*\(|fun\s+validate\w*\('; then
            warn "$file: inline validation in ViewModel — move to shared Validators.kt (Rule A-9)"
            inc_warn
        fi
    fi

    # TODO / FIXME
    if echo "$ADDED" | grep -qiE '(TODO|FIXME|HACK|XXX)\s*[:(]'; then
        warn "$file: unresolved TODO/FIXME in staged diff"
        inc_warn
    fi

    # Excessive commented-out code
    COMMENTED=$(echo "$ADDED" | grep -cE '^\+\s*//' || echo 0)
    [ "$COMMENTED" -gt 5 ] && { warn "$file: $COMMENTED comment lines in diff — review for dead code"; inc_warn; }

done <<< "$STAGED_KT"

ok "Kotlin checks complete"
fi

# ═════════════════════════════════════════════════════════════════════════════
# 3. SWIFT — CODING STANDARDS
# ═════════════════════════════════════════════════════════════════════════════
if [ -n "$STAGED_SWIFT" ]; then
section "Swift — Coding Standards"

while IFS= read -r file; do
    [ -z "$file" ] && continue; [ ! -f "$file" ] && continue
    ADDED=$(git diff --cached "$file" | grep '^+' | grep -v '^+++')

    # Debug output (Rule LOG-1)
    if echo "$ADDED" | grep -qE '^\+\s*print(ln)?\s*\('; then
        warn "$file: debug print — remove before merging (Rule LOG-1)"
        inc_warn
    fi

    # DispatchQueue in ViewModelWrapper (Rule FW-2)
    if echo "$file" | grep -q 'ViewModelWrapper'; then
        if echo "$ADDED" | grep -qE 'DispatchQueue\.main\.async'; then
            err "$file: DispatchQueue.main.async in ViewModelWrapper — use Task { @MainActor } (Rule FW-2)"
            inc_error
        fi
    fi

    # FlowWatcher missing in ViewModelWrapper (Rule FW-1)
    if echo "$file" | grep -q 'ViewModelWrapper'; then
        FULL=$(cat "$file")
        if ! echo "$FULL" | grep -q 'FlowWatcher'; then
            warn "$file: ViewModelWrapper missing FlowWatcher — required by Rule FW-1"
            inc_warn
        fi
        if ! echo "$FULL" | grep -q '@MainActor'; then
            warn "$file: ViewModelWrapper missing @MainActor annotation (Rule FW-1)"
            inc_warn
        fi
    fi

    # Force unwrap (Rule UI-10)
    UNWRAPS=$(echo "$ADDED" | grep -cE '[a-zA-Z0-9_]\!' || echo 0)
    [ "$UNWRAPS" -gt 0 ] && { warn "$file: $UNWRAPS force-unwrap(s) — use guard/if let/?? (Rule UI-10)"; inc_warn; }

    # Inline DateFormatter (Rule DT-5)
    if echo "$ADDED" | grep -qE 'DateFormatter\(\)'; then
        if ! echo "$file" | grep -q 'DateUtil'; then
            err "$file: inline DateFormatter — use DateUtil.swift (Rule DT-5)"
            inc_error
        fi
    fi

    # Hardcoded dimensions in Features/ (Rule UI-1a)
    if echo "$file" | grep -q '/Features/'; then
        if echo "$ADDED" | grep -qE '\.(padding|frame|cornerRadius|spacing:)\s*[^(]*\b[1-9][0-9]+\b'; then
            warn "$file: possible hardcoded dimension — use Spacing/Sizing/Radius tokens (Rule UI-1a)"
            inc_warn
        fi
    fi

    # TODO / FIXME
    if echo "$ADDED" | grep -qiE '(TODO|FIXME|HACK)\s*[:(]'; then
        warn "$file: unresolved TODO/FIXME in staged diff"
        inc_warn
    fi

done <<< "$STAGED_SWIFT"

ok "Swift checks complete"
fi

# ═════════════════════════════════════════════════════════════════════════════
# 4. LOCALIZATION (Rules L-2, L-5)
# ═════════════════════════════════════════════════════════════════════════════
section "Localization"

if echo "$STAGED_ALL" | grep -q 'twine\.txt'; then
    # Check for empty translations
    MISSING=$(awk -F'=' '/^\s*(en|fr|de|lb)\s*=/ && ($2 ~ /^\s*$/) {print NR": "$0}' strings/twine.txt | head -5 || true)
    if [ -n "$MISSING" ]; then
        err "twine.txt has empty translations (Rule L-2):"; echo "$MISSING"
        inc_error
    else
        ok "twine.txt: all four languages present"
    fi
    warn "Run 'make strings' to regenerate Android resources after editing twine.txt"
fi

# Hardcoded user-visible strings in Kotlin view files
if [ -n "$STAGED_KT" ]; then
    while IFS= read -r file; do
        [ -z "$file" ] && continue; [ ! -f "$file" ] && continue
        if echo "$file" | grep -qE '/(view|Screen)'; then
            ADDED=$(git diff --cached "$file" | grep '^+' | grep -v '^+++')
            HARDCODED=$(echo "$ADDED" | grep -oE 'Text\(\s*"[A-Z][a-zA-Z ]{4,}"' | head -3 || true)
            if [ -n "$HARDCODED" ]; then
                warn "$file: hardcoded string literal in Text() — use stringResource() (Rule UI-2)"
                inc_warn
            fi
        fi
    done <<< "$STAGED_KT"
fi

# ═════════════════════════════════════════════════════════════════════════════
# 5. PLATFORM PARITY (Rule PP-1)
# ═════════════════════════════════════════════════════════════════════════════
section "Platform Parity"

if [ -n "$STAGED_KT" ]; then
    while IFS= read -r file; do
        [ -z "$file" ] && continue
        BASENAME=$(basename "$file" .kt)
        if echo "$BASENAME" | grep -qE 'Screen$'; then
            VIEW_NAME="${BASENAME/Screen/View}.swift"
            if ! find "$PROJECT_ROOT/iosApp" -name "$VIEW_NAME" -type f 2>/dev/null | grep -q .; then
                warn "Android $BASENAME staged but no iOS $VIEW_NAME found (Rule PP-1)"
                inc_warn
            fi
        fi
    done <<< "$STAGED_KT"
fi

if [ -n "$STAGED_SWIFT" ]; then
    while IFS= read -r file; do
        [ -z "$file" ] && continue
        BASENAME=$(basename "$file" .swift)
        if echo "$BASENAME" | grep -qE 'View$' && ! echo "$BASENAME" | grep -qE 'Wrapper$|Extension'; then
            SCREEN_NAME="${BASENAME/View/Screen}.kt"
            if ! find "$PROJECT_ROOT/androidApp" -name "$SCREEN_NAME" -type f 2>/dev/null | grep -q .; then
                warn "iOS $BASENAME staged but no Android $SCREEN_NAME found (Rule PP-1)"
                inc_warn
            fi
        fi
    done <<< "$STAGED_SWIFT"
fi

[ "$ERRORS" -eq 0 ] && ok "Platform parity looks good"

# ═════════════════════════════════════════════════════════════════════════════
# FINAL SUMMARY
# ═════════════════════════════════════════════════════════════════════════════
echo ""
echo -e "${BOLD}╔══════════════════════════════════════════════════════╗${RESET}"
echo -e "${BOLD}║                   Review Summary                    ║${RESET}"
echo -e "${BOLD}╚══════════════════════════════════════════════════════╝${RESET}"
echo -e "  Files reviewed : $ALL_COUNT ($KT_COUNT Kotlin, $SW_COUNT Swift)"
echo -e "  Errors         : ${RED}${BOLD}$ERRORS${RESET}"
echo -e "  Warnings       : ${YELLOW}$WARNINGS${RESET}"

if   [ "$ERRORS" -gt 0 ]; then RISK="CRITICAL"; RC="$RED"
elif [ "$WARNINGS" -gt 5 ]; then RISK="HIGH";     RC="$YELLOW"
elif [ "$WARNINGS" -gt 2 ]; then RISK="MEDIUM";   RC="$YELLOW"
else                              RISK="LOW";      RC="$GREEN"
fi

echo -e "  Risk level     : ${RC}${BOLD}$RISK${RESET}"
echo ""

if [ "$ERRORS" -gt 0 ]; then
    echo -e "${RED}${BOLD}COMMIT BLOCKED — fix $ERRORS critical error(s) above.${RESET}"
    echo -e "To override: ${CYAN}git commit --no-verify${RESET}  (use sparingly)"
    echo ""; exit 1
else
    [ "$WARNINGS" -gt 0 ] && echo -e "${YELLOW}$WARNINGS warning(s) — review before merging to main.${RESET}"
    echo -e "${GREEN}${BOLD}Commit approved.${RESET}"; echo ""; exit 0
fi
