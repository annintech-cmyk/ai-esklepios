#!/usr/bin/env bash
# pre-build-check.sh
# Run before building to catch common issues early.
# Usage: .claude/hooks/pre-build-check.sh

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/../.." && pwd)"

echo "=== eSklepios Pre-Build Check ==="
echo ""

ERRORS=0
WARNINGS=0

# ─── Check 1: dev.properties exists ─────────────────────────────────────────
if [ ! -f "$PROJECT_ROOT/dev.properties" ]; then
    echo "[ERROR] dev.properties not found at project root."
    echo "        Copy dev.properties.example (if it exists) and fill in your values."
    ERRORS=$((ERRORS + 1))
else
    echo "[OK] dev.properties found"
fi

# ─── Check 2: prod.properties exists ────────────────────────────────────────
if [ ! -f "$PROJECT_ROOT/prod.properties" ]; then
    echo "[WARN] prod.properties not found. Release builds may fail."
    WARNINGS=$((WARNINGS + 1))
else
    echo "[OK] prod.properties found"
fi

# ─── Check 3: Twine is installed ─────────────────────────────────────────────
if ! command -v twine &>/dev/null; then
    echo "[WARN] 'twine' CLI not found. Run 'gem install twine' to generate localizations."
    WARNINGS=$((WARNINGS + 1))
else
    echo "[OK] twine CLI found: $(twine --version 2>&1 | head -1)"
fi

# ─── Check 4: strings/twine.txt exists ───────────────────────────────────────
if [ ! -f "$PROJECT_ROOT/strings/twine.txt" ]; then
    echo "[ERROR] strings/twine.txt not found."
    ERRORS=$((ERRORS + 1))
else
    echo "[OK] strings/twine.txt found"
fi

# ─── Check 5: Android strings.xml exists ─────────────────────────────────────
STRINGS_XML="$PROJECT_ROOT/androidApp/src/main/res/values/strings.xml"
if [ ! -f "$STRINGS_XML" ]; then
    echo "[WARN] androidApp/src/main/res/values/strings.xml not found."
    echo "       Run 'make strings' to generate it from strings/twine.txt"
    WARNINGS=$((WARNINGS + 1))
else
    echo "[OK] Android strings.xml found"
fi

# ─── Check 6: AndroidManifest.xml exists ─────────────────────────────────────
MANIFEST="$PROJECT_ROOT/androidApp/src/main/AndroidManifest.xml"
if [ ! -f "$MANIFEST" ]; then
    echo "[ERROR] AndroidManifest.xml not found at $MANIFEST"
    ERRORS=$((ERRORS + 1))
else
    echo "[OK] AndroidManifest.xml found"
fi

# ─── Check 7: gradle wrapper exists ──────────────────────────────────────────
if [ ! -f "$PROJECT_ROOT/gradlew" ]; then
    echo "[ERROR] gradlew not found. Run 'gradle wrapper' to generate it."
    ERRORS=$((ERRORS + 1))
else
    echo "[OK] gradlew found"
fi

# ─── Summary ─────────────────────────────────────────────────────────────────
echo ""
echo "=== Summary ==="
echo "Errors:   $ERRORS"
echo "Warnings: $WARNINGS"
echo ""

if [ "$ERRORS" -gt 0 ]; then
    echo "PRE-BUILD CHECK FAILED — fix errors above before building."
    exit 1
else
    echo "Pre-build check passed."
    exit 0
fi
