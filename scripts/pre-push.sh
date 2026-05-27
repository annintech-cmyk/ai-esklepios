#!/usr/bin/env bash
# scripts/pre-push.sh
# Full build + test gate run before every git push.
# Blocks push if any build, lint, or test fails.
#
# Exit codes:
#   0  — all checks passed
#   1  — one or more checks failed; push blocked
#
# To bypass in a genuine emergency: git push --no-verify
# (This should be extremely rare — fix the issue instead.)

set -uo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
cd "$PROJECT_ROOT"

# ─── Colour helpers ───────────────────────────────────────────────────────────
RED='\033[0;31m'; YELLOW='\033[1;33m'; GREEN='\033[0;32m'
CYAN='\033[0;36m'; BOLD='\033[1m'; RESET='\033[0m'

info()    { echo -e "${CYAN}[INFO]${RESET}  $*"; }
ok()      { echo -e "${GREEN}[OK]${RESET}    $*"; }
warn()    { echo -e "${YELLOW}[WARN]${RESET}  $*"; }
err()     { echo -e "${RED}[ERROR]${RESET} $*"; }
section() { echo -e "\n${BOLD}── $* ──${RESET}"; }

ERRORS=0
fail() { err "$1"; ERRORS=$((ERRORS + 1)); }

echo -e "\n${BOLD}╔══════════════════════════════════════════════════════╗${RESET}"
echo -e "${BOLD}║            eSklepios Pre-Push Validation             ║${RESET}"
echo -e "${BOLD}╚══════════════════════════════════════════════════════╝${RESET}"

# ─── Determine what branches are being pushed ─────────────────────────────────
PROTECTED_BRANCH=0
while read -r _local_ref _local_sha _remote_ref _remote_sha; do
    BRANCH=$(echo "$_remote_ref" | sed 's|refs/heads/||')
    if [[ "$BRANCH" == "main" || "$BRANCH" == "develop" ]]; then
        PROTECTED_BRANCH=1
        warn "Pushing to protected branch: $BRANCH"
        warn "Ensure a PR has been created and reviewed. Direct pushes are discouraged."
    fi
done

# ═════════════════════════════════════════════════════════════════════════════
# 1. KOTLIN — DETEKT STATIC ANALYSIS
# ═════════════════════════════════════════════════════════════════════════════
section "Kotlin — Detekt Static Analysis"

info "Running detekt on shared + androidApp …"
if ./gradlew :shared:detekt :androidApp:detekt --quiet 2>&1; then
    ok "Detekt passed"
else
    fail "Detekt found violations — fix before pushing"
fi

# ═════════════════════════════════════════════════════════════════════════════
# 2. KOTLIN — KTLINT FORMATTING
# ═════════════════════════════════════════════════════════════════════════════
section "Kotlin — KtLint Formatting"

info "Running ktlint check …"
if ./gradlew :shared:ktlintCheck :androidApp:ktlintCheck --quiet 2>&1; then
    ok "KtLint passed"
else
    fail "KtLint formatting violations — run './gradlew ktlintFormat' to auto-fix"
fi

# ═════════════════════════════════════════════════════════════════════════════
# 3. ANDROID — LINT
# ═════════════════════════════════════════════════════════════════════════════
section "Android — Lint"

info "Running Android lint …"
if ./gradlew :androidApp:lintDebug --quiet 2>&1; then
    ok "Android lint passed"
else
    fail "Android lint found issues — check androidApp/build/reports/lint/"
fi

# ═════════════════════════════════════════════════════════════════════════════
# 4. SHARED KMM — UNIT TESTS
# ═════════════════════════════════════════════════════════════════════════════
section "Shared KMM — Unit Tests"

info "Running shared common tests …"
if ./gradlew :shared:testDebugUnitTest --quiet 2>&1; then
    ok "Shared KMM tests passed"
else
    fail "Shared KMM tests failed — check shared/build/reports/tests/"
fi

# ═════════════════════════════════════════════════════════════════════════════
# 5. ANDROID — UNIT TESTS
# ═════════════════════════════════════════════════════════════════════════════
section "Android — Unit Tests"

info "Running Android unit tests …"
if ./gradlew :androidApp:testDebugUnitTest --quiet 2>&1; then
    ok "Android unit tests passed"
else
    fail "Android unit tests failed — check androidApp/build/reports/tests/"
fi

# ═════════════════════════════════════════════════════════════════════════════
# 6. ANDROID — DEBUG BUILD
# ═════════════════════════════════════════════════════════════════════════════
section "Android — Debug Build"

info "Building Android debug APK …"
if ./gradlew :androidApp:assembleDebug --quiet 2>&1; then
    ok "Android debug build succeeded"
else
    fail "Android debug build failed"
fi

# ═════════════════════════════════════════════════════════════════════════════
# 7. iOS — SWIFTLINT
# ═════════════════════════════════════════════════════════════════════════════
section "iOS — SwiftLint"

if command -v swiftlint &>/dev/null; then
    info "Running SwiftLint …"
    if swiftlint lint --config .swiftlint.yml --quiet 2>&1; then
        ok "SwiftLint passed"
    else
        fail "SwiftLint found violations — check output above"
    fi
else
    warn "SwiftLint not installed — skipping iOS lint (install: brew install swiftlint)"
fi

# ═════════════════════════════════════════════════════════════════════════════
# 8. iOS — BUILD (macOS only)
# ═════════════════════════════════════════════════════════════════════════════
section "iOS — Build"

if [[ "$(uname)" == "Darwin" ]]; then
    if command -v xcodebuild &>/dev/null; then
        info "Building iOS app (simulator) …"
        if xcodebuild build \
            -project iosApp/eSklepios.xcodeproj \
            -scheme eSklepios \
            -destination 'platform=iOS Simulator,name=iPhone 16' \
            CODE_SIGNING_ALLOWED=NO \
            -quiet 2>&1; then
            ok "iOS build succeeded"
        else
            fail "iOS build failed — check Xcode output"
        fi
    else
        warn "xcodebuild not found — skipping iOS build"
    fi
else
    warn "Not on macOS — skipping iOS build checks"
fi

# ═════════════════════════════════════════════════════════════════════════════
# FINAL SUMMARY
# ═════════════════════════════════════════════════════════════════════════════
echo ""
echo -e "${BOLD}╔══════════════════════════════════════════════════════╗${RESET}"
echo -e "${BOLD}║                Pre-Push Summary                      ║${RESET}"
echo -e "${BOLD}╚══════════════════════════════════════════════════════╝${RESET}"
echo -e "  Total failures: ${ERRORS}"
echo ""

if [ "$ERRORS" -gt 0 ]; then
    echo -e "${RED}${BOLD}PUSH BLOCKED — $ERRORS check(s) failed above.${RESET}"
    echo -e "Fix the issues and re-push. To override: ${CYAN}git push --no-verify${RESET}"
    echo ""
    exit 1
else
    echo -e "${GREEN}${BOLD}All checks passed — push approved.${RESET}"
    echo ""
    exit 0
fi
