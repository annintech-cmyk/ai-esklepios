#!/usr/bin/env python3
"""
GitHub Push Automation Script
Handles authentication and pushes changes to remote repository
"""

import os
import sys
import subprocess
import getpass
from pathlib import Path

# Colors for terminal output
class Colors:
    GREEN = '\033[0;32m'
    BLUE = '\033[0;34m'
    YELLOW = '\033[1;33m'
    RED = '\033[0;31m'
    NC = '\033[0m'  # No Color

def run_command(cmd, capture=False, sensitive=False):
    """Run a shell command and return result."""
    try:
        if sensitive:
            # Don't print sensitive commands
            result = subprocess.run(cmd, shell=True, capture_output=True, text=True)
        else:
            print(f"{Colors.BLUE}→{Colors.NC} {cmd}")
            result = subprocess.run(cmd, shell=True, capture_output=True, text=True)

        if result.returncode != 0:
            if result.stderr:
                print(f"{Colors.RED}Error: {result.stderr}{Colors.NC}")
            return None if capture else False

        if capture:
            return result.stdout.strip()
        return True
    except Exception as e:
        print(f"{Colors.RED}❌ Error executing command: {e}{Colors.NC}")
        return None if capture else False

def check_git_repo():
    """Check if we're in a git repository."""
    if run_command("git rev-parse --git-dir > /dev/null 2>&1") is False:
        print(f"{Colors.RED}❌ Not in a git repository{Colors.NC}")
        sys.exit(1)
    return True

def get_repo_info():
    """Get repository information."""
    repo_url = run_command("git remote get-url origin", capture=True)
    branch = run_command("git rev-parse --abbrev-ref HEAD", capture=True)
    return repo_url, branch

def check_uncommitted_changes():
    """Check for uncommitted changes."""
    output = run_command("git status --porcelain", capture=True)
    if output:
        print(f"{Colors.YELLOW}⚠️  Uncommitted changes detected:{Colors.NC}")
        print(output)
        response = input(f"{Colors.YELLOW}Continue anyway? (y/n): {Colors.NC}")
        if response.lower() != 'y':
            print("Aborted.")
            sys.exit(0)
    return True

def push_with_token(repo_url, branch):
    """Push using Personal Access Token."""
    print(f"{Colors.BLUE}Personal Access Token Method{Colors.NC}")
    print("Create a token at: https://github.com/settings/tokens")
    print("Scopes needed: repo, workflow")
    print()

    username = input("GitHub Username: ")
    token = getpass.getpass("GitHub Personal Access Token: ")

    # Parse repo URL
    if "github.com/" in repo_url:
        repo_path = repo_url.replace("https://github.com/", "").replace(".git", "")
    else:
        print(f"{Colors.RED}❌ Unsupported repository URL{Colors.NC}")
        return False

    # Create authenticated URL
    auth_url = f"https://{username}:{token}@github.com/{repo_path}.git"

    print(f"{Colors.BLUE}Pushing to {repo_url}...{Colors.NC}")

    # Push with credentials
    cmd = f'git push {auth_url.replace("github.com", "github.com")} {branch}'
    if run_command(f'git push https://{username}:{token}@github.com/{repo_path}.git {branch}', sensitive=True):
        return True
    return False

def push_with_ssh(repo_url, branch):
    """Push using SSH Key."""
    print(f"{Colors.BLUE}SSH Key Method{Colors.NC}")

    # Check if SSH key exists
    ssh_key = Path.home() / ".ssh" / "id_rsa"
    ssh_key_ed = Path.home() / ".ssh" / "id_ed25519"

    if not ssh_key.exists() and not ssh_key_ed.exists():
        print(f"{Colors.RED}❌ SSH key not found{Colors.NC}")
        print("Create one with: ssh-keygen -t ed25519 -C 'your_email@example.com'")
        return False

    # Convert to SSH if needed
    if not repo_url.startswith("git@github.com"):
        repo_path = repo_url.replace("https://github.com/", "").replace(".git", "")
        ssh_url = f"git@github.com:{repo_path}.git"
        print(f"Converting to SSH URL: {ssh_url}")
        run_command(f"git remote set-url origin {ssh_url}")

    print(f"{Colors.BLUE}Pushing to remote...{Colors.NC}")
    if run_command(f"git push origin {branch}"):
        return True
    return False

def push_with_existing():
    """Push using existing credentials."""
    print(f"{Colors.BLUE}Using Existing Credentials{Colors.NC}")
    branch = run_command("git rev-parse --abbrev-ref HEAD", capture=True)

    print(f"{Colors.BLUE}Pushing...{Colors.NC}")
    if run_command(f"git push origin {branch}"):
        return True
    return False

def verify_push(branch):
    """Verify that push was successful."""
    print()
    print(f"{Colors.BLUE}Verifying push...{Colors.NC}")

    local_head = run_command("git rev-parse HEAD", capture=True)
    remote_head = run_command(f"git rev-parse origin/{branch}", capture=True)

    if local_head and remote_head and local_head == remote_head:
        print(f"{Colors.GREEN}✅ Push verified! Local and remote are in sync{Colors.NC}")

        print()
        print(f"{Colors.BLUE}📊 Commit Details:{Colors.NC}")
        run_command("git log -1 --oneline")

        repo_url = run_command("git remote get-url origin", capture=True)
        print()
        print(f"{Colors.BLUE}🔗 View on GitHub:{Colors.NC}")
        print(f"   {repo_url}/commits/{branch}")

        return True
    else:
        print(f"{Colors.YELLOW}⚠️  Could not verify push{Colors.NC}")
        return False

def main():
    """Main function."""
    print(f"{Colors.GREEN}🚀 GitHub Push Script{Colors.NC}")
    print("=" * 50)
    print()

    # Check if in git repo
    check_git_repo()

    # Get repo info
    repo_url, branch = get_repo_info()
    print(f"{Colors.BLUE}Repository:{Colors.NC} {repo_url}")
    print(f"{Colors.BLUE}Branch:{Colors.NC} {branch}")
    print()

    # Check for uncommitted changes
    check_uncommitted_changes()

    # Choose authentication method
    print(f"{Colors.BLUE}Authentication Methods:{Colors.NC}")
    print("1. Personal Access Token (GitHub)")
    print("2. SSH Key")
    print("3. Existing credentials")
    print()

    choice = input("Choose method (1-3): ")

    success = False

    if choice == "1":
        print()
        success = push_with_token(repo_url, branch)
    elif choice == "2":
        print()
        success = push_with_ssh(repo_url, branch)
    elif choice == "3":
        print()
        success = push_with_existing()
    else:
        print(f"{Colors.RED}Invalid option{Colors.NC}")
        sys.exit(1)

    if success:
        verify_push(branch)
        print()
        print(f"{Colors.GREEN}Done!{Colors.NC}")
    else:
        print()
        print(f"{Colors.RED}Push failed. Please try again.{Colors.NC}")
        sys.exit(1)

if __name__ == "__main__":
    main()
