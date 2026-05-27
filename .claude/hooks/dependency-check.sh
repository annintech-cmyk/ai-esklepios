#!/usr/bin/env bash
# dependency-check.sh
# Check for known dependency issues and print version information.
# Usage: .claude/hooks/dependency-check.sh

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/../.." && pwd)"

echo "=== eSklepios Dependency Check ==="
echo ""

cd "$PROJECT_ROOT"

WARNINGS=0

# ─── Java Version ─────────────────────────────────────────────────────────────
echo "--- Java ---"
if command -v java &>/dev/null; then
    JAVA_VERSION=$(java -version 2>&1 | head -1)
    echo "  $JAVA_VERSION"
    # Warn if not Java 17+
    JAVA_MAJOR=$(java -version 2>&1 | grep -oP '(?<=version ")[\d]+' | head -1)
    if [ -n "$JAVA_MAJOR" ] && [ "$JAVA_MAJOR" -lt 17 ]; then
        echo "  [WARN] Java 17+ recommended. Current: $JAVA_MAJOR"
        WARNINGS=$((WARNINGS + 1))
    fi
else
    echo "  [WARN] Java not found in PATH"
    WARNINGS=$((WARNINGS + 1))
fi

# ─── Gradle ───────────────────────────────────────────────────────────────────
echo ""
echo "--- Gradle ---"
if [ -f "$PROJECT_ROOT/gradlew" ]; then
    GRADLE_VERSION=$(./gradlew --version --quiet 2>/dev/null | grep "^Gradle" || echo "unknown")
    echo "  $GRADLE_VERSION"
else
    echo "  [WARN] gradlew not found"
    WARNINGS=$((WARNINGS + 1))
fi

# ─── Kotlin version from gradle ───────────────────────────────────────────────
echo ""
echo "--- Kotlin ---"
if [ -f "$PROJECT_ROOT/gradle/libs.versions.toml" ]; then
    KOTLIN_VERSION=$(grep 'kotlin' "$PROJECT_ROOT/gradle/libs.versions.toml" | grep -v '#' | head -1)
    echo "  libs.versions.toml: $KOTLIN_VERSION"
else
    echo "  [SKIP] gradle/libs.versions.toml not found"
fi

# ─── Xcode ────────────────────────────────────────────────────────────────────
echo ""
echo "--- Xcode ---"
if command -v xcodebuild &>/dev/null; then
    XCODE_VERSION=$(xcodebuild -version 2>&1 | head -2 | tr '\n' ' ')
    echo "  $XCODE_VERSION"
else
    echo "  [SKIP] Xcode not available on this system"
fi

# ─── Ruby / Twine ─────────────────────────────────────────────────────────────
echo ""
echo "--- Twine (Localization) ---"
if command -v ruby &>/dev/null; then
    RUBY_VERSION=$(ruby --version)
    echo "  Ruby: $RUBY_VERSION"
fi
if command -v twine &>/dev/null; then
    TWINE_VERSION=$(twine --version 2>&1 | head -1)
    echo "  Twine: $TWINE_VERSION"
else
    echo "  [WARN] twine not installed. Run: gem install twine"
    WARNINGS=$((WARNINGS + 1))
fi

# ─── SwiftLint (optional) ────────────────────────────────────────────────────
echo ""
echo "--- SwiftLint ---"
if command -v swiftlint &>/dev/null; then
    SWIFTLINT_VERSION=$(swiftlint version)
    echo "  SwiftLint $SWIFTLINT_VERSION"
else
    echo "  [INFO] SwiftLint not installed (optional). Install: brew install swiftlint"
fi

# ─── Known Version Pins ───────────────────────────────────────────────────────
echo ""
echo "--- Expected versions (from CLAUDE.md) ---"
echo "  Kotlin:      2.0.21"
echo "  Ktor:        3.0.3"
echo "  Koin:        4.0.0"
echo "  SQLDelight:  2.0.2"
echo "  Compose BOM: 2024.12.01"
echo ""

# ─── Summary ─────────────────────────────────────────────────────────────────
echo "=== Summary ==="
echo "Warnings: $WARNINGS"
echo ""
if [ "$WARNINGS" -gt 0 ]; then
    echo "Dependency check completed with warnings. Review items above."
    exit 0
else
    echo "Dependency check passed."
    exit 0
fi
