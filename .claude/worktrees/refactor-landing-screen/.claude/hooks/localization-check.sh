#!/usr/bin/env bash
# localization-check.sh
# Verify localization completeness: all keys in twine.txt have values in all 4 languages.
# Usage: .claude/hooks/localization-check.sh

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/../.." && pwd)"
TWINE_FILE="$PROJECT_ROOT/strings/twine.txt"
LANGUAGES=("en" "fr" "de" "lb")

echo "=== eSklepios Localization Check ==="
echo ""

if [ ! -f "$TWINE_FILE" ]; then
    echo "[ERROR] strings/twine.txt not found at $TWINE_FILE"
    exit 1
fi

ERRORS=0
CURRENT_KEY=""

while IFS= read -r line; do
    # Detect key lines: [section.key]
    if [[ "$line" =~ ^\[([a-z_]+\.[a-z_]+)\]$ ]]; then
        CURRENT_KEY="${BASH_REMATCH[1]}"
        # Reset language tracking for this key
        for lang in "${LANGUAGES[@]}"; do
            eval "SEEN_${lang}=0"
        done
    fi

    # Detect language value lines: en = ...
    for lang in "${LANGUAGES[@]}"; do
        if [[ "$line" =~ ^${lang}[[:space:]]*=[[:space:]]*(.+)$ ]]; then
            VALUE="${BASH_REMATCH[1]}"
            if [ -n "$VALUE" ]; then
                eval "SEEN_${lang}=1"
            fi
        fi
    done

    # Check completeness at blank lines after a key block
    if [[ -z "$line" ]] && [[ -n "$CURRENT_KEY" ]]; then
        for lang in "${LANGUAGES[@]}"; do
            SEEN=$(eval "echo \$SEEN_${lang}")
            if [ "$SEEN" -eq 0 ]; then
                echo "[MISSING] Key '$CURRENT_KEY' has no '$lang' value"
                ERRORS=$((ERRORS + 1))
            fi
        done
        CURRENT_KEY=""
    fi
done < "$TWINE_FILE"

# Check last key in file (if file doesn't end with blank line)
if [[ -n "$CURRENT_KEY" ]]; then
    for lang in "${LANGUAGES[@]}"; do
        SEEN=$(eval "echo \$SEEN_${lang}")
        if [ "$SEEN" -eq 0 ]; then
            echo "[MISSING] Key '$CURRENT_KEY' has no '$lang' value"
            ERRORS=$((ERRORS + 1))
        fi
    done
fi

echo ""
echo "=== Summary ==="
echo "Missing translations: $ERRORS"
echo ""

if [ "$ERRORS" -gt 0 ]; then
    echo "LOCALIZATION CHECK FAILED — fill in missing translations before building."
    exit 1
else
    echo "Localization check passed. All keys have values in en, fr, de, lb."
    exit 0
fi
