#!/usr/bin/env bash
# lint-check.sh
# Run Android lint and Kotlin Detekt for code quality checks.
# Usage: .claude/hooks/lint-check.sh [--fix]

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/../.." && pwd)"
FIX_MODE=false

for arg in "$@"; do
    if [ "$arg" == "--fix" ]; then
        FIX_MODE=true
    fi
done

echo "=== eSklepios Lint Check ==="
echo ""

cd "$PROJECT_ROOT"

ERRORS=0

# ─── Android Lint ─────────────────────────────────────────────────────────────
echo "Running Android lint..."
if ./gradlew :androidApp:lintDebug --quiet; then
    echo "[OK] Android lint passed"
else
    echo "[FAIL] Android lint found issues. Check androidApp/build/reports/lint-results-debug.html"
    ERRORS=$((ERRORS + 1))
fi

# ─── Kotlin Detekt (if configured) ───────────────────────────────────────────
if ./gradlew tasks --quiet | grep -q "detekt"; then
    echo "Running Detekt..."
    if $FIX_MODE; then
        if ./gradlew detektMain --auto-correct --quiet; then
            echo "[OK] Detekt passed (with auto-correct)"
        else
            echo "[WARN] Detekt found issues not fixable automatically"
        fi
    else
        if ./gradlew detekt --quiet; then
            echo "[OK] Detekt passed"
        else
            echo "[FAIL] Detekt found issues. Run with --fix to auto-correct."
            ERRORS=$((ERRORS + 1))
        fi
    fi
else
    echo "[SKIP] Detekt not configured"
fi

# ─── Swift Lint (if installed) ────────────────────────────────────────────────
if command -v swiftlint &>/dev/null; then
    echo "Running SwiftLint..."
    if $FIX_MODE; then
        if (cd "$PROJECT_ROOT/iosApp" && swiftlint --fix --quiet); then
            echo "[OK] SwiftLint passed (with auto-correct)"
        else
            echo "[WARN] SwiftLint found unfixable issues"
        fi
    else
        if (cd "$PROJECT_ROOT/iosApp" && swiftlint --quiet); then
            echo "[OK] SwiftLint passed"
        else
            echo "[FAIL] SwiftLint found issues. Run with --fix to auto-correct."
            ERRORS=$((ERRORS + 1))
        fi
    fi
else
    echo "[SKIP] swiftlint not installed. Install with: brew install swiftlint"
fi

# ─── Summary ─────────────────────────────────────────────────────────────────
echo ""
echo "=== Summary ==="
if [ "$ERRORS" -gt 0 ]; then
    echo "LINT CHECK FAILED — $ERRORS issue(s) found. Fix before committing."
    exit 1
else
    echo "Lint check passed."
    exit 0
fi
