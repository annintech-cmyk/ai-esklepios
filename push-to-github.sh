#!/bin/bash

# Push to GitHub automation script
# This script handles authentication and pushes changes to the remote repository

set -e

echo "🚀 GitHub Push Script"
echo "====================="
echo ""

# Colors for output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Check if we're in a git repository
if ! git rev-parse --git-dir > /dev/null 2>&1; then
    echo -e "${RED}❌ Error: Not in a git repository${NC}"
    exit 1
fi

# Get repo info
REPO_URL=$(git remote get-url origin)
BRANCH=$(git rev-parse --abbrev-ref HEAD)
COMMIT_COUNT=$(git rev-list --count HEAD..origin/$BRANCH 2>/dev/null || echo "0")

echo -e "${BLUE}Repository:${NC} $REPO_URL"
echo -e "${BLUE}Branch:${NC} $BRANCH"
echo ""

# Check for uncommitted changes
if [ -n "$(git status --porcelain)" ]; then
    echo -e "${YELLOW}⚠️  You have uncommitted changes:${NC}"
    git status --short
    echo ""
    read -p "Continue anyway? (y/n) " -n 1 -r
    echo
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        echo "Aborted."
        exit 1
    fi
fi

# Check authentication method
echo -e "${BLUE}Authentication Methods:${NC}"
echo "1. Personal Access Token (GitHub)"
echo "2. SSH Key"
echo "3. Existing credentials"
echo ""
read -p "Choose method (1-3): " AUTH_METHOD

case $AUTH_METHOD in
    1)
        echo ""
        echo -e "${BLUE}Personal Access Token Method${NC}"
        echo "Create a token at: https://github.com/settings/tokens"
        echo "Scopes needed: repo, workflow"
        echo ""
        read -sp "GitHub Username: " GITHUB_USER
        echo ""
        read -sp "GitHub Personal Access Token: " GITHUB_TOKEN
        echo ""

        # Configure git credentials temporarily
        echo "Configuring git credentials..."
        git config --local credential.helper store

        # Push with token auth
        echo "Pushing to $REPO_URL..."
        if echo "https://${GITHUB_USER}:${GITHUB_TOKEN}@$(echo $REPO_URL | sed 's/https:\/\///')" | \
           git -c credential.helper=store push origin $BRANCH; then
            echo -e "${GREEN}✅ Push successful!${NC}"
        else
            echo -e "${RED}❌ Push failed${NC}"
            exit 1
        fi
        ;;
    2)
        echo ""
        echo -e "${BLUE}SSH Key Method${NC}"

        # Check if SSH key exists
        if [ ! -f ~/.ssh/id_rsa ] && [ ! -f ~/.ssh/id_ed25519 ]; then
            echo -e "${RED}❌ SSH key not found${NC}"
            echo "Create one with: ssh-keygen -t ed25519 -C 'your_email@example.com'"
            exit 1
        fi

        # Check if it's already SSH
        if [[ $REPO_URL == git@github.com* ]]; then
            echo "Already using SSH"
        else
            echo "Converting to SSH URL..."
            GITHUB_REPO=$(echo $REPO_URL | sed 's/https:\/\/github.com\///' | sed 's/.git$//')
            git remote set-url origin "git@github.com:${GITHUB_REPO}.git"
            echo "Remote updated to: git@github.com:${GITHUB_REPO}.git"
        fi

        echo "Pushing to remote..."
        if git push origin $BRANCH; then
            echo -e "${GREEN}✅ Push successful!${NC}"
        else
            echo -e "${RED}❌ Push failed${NC}"
            exit 1
        fi
        ;;
    3)
        echo ""
        echo -e "${BLUE}Using Existing Credentials${NC}"
        echo "Pushing to $REPO_URL..."
        if git push origin $BRANCH; then
            echo -e "${GREEN}✅ Push successful!${NC}"
        else
            echo -e "${RED}❌ Push failed (no credentials found)${NC}"
            echo "Try method 1 or 2 to set up authentication"
            exit 1
        fi
        ;;
    *)
        echo -e "${RED}Invalid option${NC}"
        exit 1
        ;;
esac

# Verify push
echo ""
echo -e "${BLUE}Verifying push...${NC}"
sleep 2

# Check if push reached remote
if git rev-parse origin/$BRANCH > /dev/null 2>&1; then
    LOCAL_HEAD=$(git rev-parse HEAD)
    REMOTE_HEAD=$(git rev-parse origin/$BRANCH)

    if [ "$LOCAL_HEAD" = "$REMOTE_HEAD" ]; then
        echo -e "${GREEN}✅ Push verified! Local and remote are in sync${NC}"
        echo ""
        echo "📊 Commit Details:"
        git log -1 --oneline
        echo ""
        echo "🔗 View on GitHub:"
        echo "   $REPO_URL/commits/$BRANCH"
    else
        echo -e "${YELLOW}⚠️  Push may not have completed fully${NC}"
    fi
else
    echo -e "${YELLOW}⚠️  Could not verify push${NC}"
fi

echo ""
echo -e "${GREEN}Done!${NC}"
