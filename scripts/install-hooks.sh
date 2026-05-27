#!/usr/bin/env bash
# scripts/install-hooks.sh
# One-command setup: installs all eSklepios Git hooks for a developer workstation.
#
# Run once after cloning:
#   bash scripts/install-hooks.sh

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
HOOKS_DIR="$PROJECT_ROOT/.git/hooks"

GREEN='\033[0;32m'; CYAN='\033[0;36m'; YELLOW='\033[1;33m'; RED='\033[0;31m'
BOLD='\033[1m'; RESET='\033[0m'

ok()   { echo -e "${GREEN}[OK]${RESET}    $*"; }
info() { echo -e "${CYAN}[INFO]${RESET}  $*"; }
warn() { echo -e "${YELLOW}[WARN]${RESET}  $*"; }
err()  { echo -e "${RED}[ERROR]${RESET} $*"; }

echo -e "\n${BOLD}╔══════════════════════════════════════════════════════╗${RESET}"
echo -e "${BOLD}║         eSklepios Git Hooks Installer                ║${RESET}"
echo -e "${BOLD}╚══════════════════════════════════════════════════════╝${RESET}\n"

# ─── Verify we are in a git repo ──────────────────────────────────────────────
if [ ! -d "$HOOKS_DIR" ]; then
    err "Not a git repository or .git/hooks directory not found."; exit 1
fi

# ─── Install pre-commit ───────────────────────────────────────────────────────
PRE_COMMIT="$HOOKS_DIR/pre-commit"
cat > "$PRE_COMMIT" << 'HOOK'
#!/usr/bin/env bash
REPO_ROOT="$(git rev-parse --show-toplevel)"
exec "$REPO_ROOT/scripts/pre-commit-review.sh"
HOOK
chmod +x "$PRE_COMMIT"
ok "Installed pre-commit hook → scripts/pre-commit-review.sh"

# ─── Install pre-push ─────────────────────────────────────────────────────────
PRE_PUSH="$HOOKS_DIR/pre-push"
cat > "$PRE_PUSH" << 'HOOK'
#!/usr/bin/env bash
REPO_ROOT="$(git rev-parse --show-toplevel)"
exec "$REPO_ROOT/scripts/pre-push.sh"
HOOK
chmod +x "$PRE_PUSH"
ok "Installed pre-push hook → scripts/pre-push.sh"

# ─── Ensure scripts are executable ───────────────────────────────────────────
chmod +x "$PROJECT_ROOT/scripts/pre-commit-review.sh"
chmod +x "$PROJECT_ROOT/scripts/pre-push.sh"
ok "Scripts are executable"

# ─── Check optional tooling ───────────────────────────────────────────────────
echo ""
info "Checking optional tooling …"

if command -v swiftlint &>/dev/null; then
    ok "SwiftLint: $(swiftlint version)"
else
    warn "SwiftLint not found — iOS lint will be skipped in pre-push"
    warn "Install with: brew install swiftlint"
fi

if command -v twine &>/dev/null; then
    ok "Twine: $(twine --version 2>&1 | head -1)"
else
    warn "Twine not found — 'make strings' will not work"
    warn "Install with: gem install twine"
fi

if [[ "$(uname)" == "Darwin" ]]; then
    if command -v xcodebuild &>/dev/null; then
        ok "Xcode CLI tools available"
    else
        warn "xcodebuild not found — iOS build check skipped"
        warn "Install with: xcode-select --install"
    fi
fi

echo ""
echo -e "${GREEN}${BOLD}Git hooks installed successfully!${RESET}"
echo ""
echo "  pre-commit  → validates code quality on each commit"
echo "  pre-push    → runs full build + tests before each push"
echo ""
echo "To skip hooks in an emergency:"
echo "  git commit --no-verify   (skip pre-commit)"
echo "  git push   --no-verify   (skip pre-push)"
echo ""
