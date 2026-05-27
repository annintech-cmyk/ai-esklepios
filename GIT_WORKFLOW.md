# Git Version Control Workflow

**Version:** 1.0  
**Last Updated:** 2026-05-26  
**Project:** eSklepios (Kotlin Multiplatform Mobile)

---

## Overview

This document defines the professional Git workflow for the eSklepios project. It ensures:
- ✅ `main` branch is always stable and production-ready
- ✅ Features are developed in isolation before merging
- ✅ Code review happens before integration
- ✅ Clean, meaningful commit history
- ✅ Scalable release process

---

## Branch Strategy

### Main Branches

#### 1. **main** (Production)
- **Purpose:** Production-ready code only
- **Protection:** Branch protection enabled
  - ✅ Require pull request reviews (minimum 1 approval)
  - ✅ Dismiss stale PR approvals
  - ✅ Require status checks to pass
  - ✅ Require branches to be up to date
  - ✅ No direct pushes allowed
  - ✅ No force pushes allowed
- **Access:** Lead developers + release manager only
- **Merge source:** `release/*` branches or `develop` (with explicit approval)

#### 2. **develop** (Integration)
- **Purpose:** Integration branch for features; staging for releases
- **Protection:** Branch protection enabled
  - ✅ Require pull request reviews (minimum 1 approval)
  - ✅ Require status checks to pass
  - ✅ No direct pushes allowed
- **Access:** All developers (via pull requests)
- **Merge source:** `feature/*`, `bugfix/*`, `hotfix/*` branches

### Support Branches

#### 3. **feature/\<feature-name\>** (Development)
- **Purpose:** Develop new features
- **Naming:** `feature/user-authentication`, `feature/home-screen-redesign`
- **Created from:** `develop`
- **Merged back into:** `develop` (via pull request)
- **Lifetime:** Short-lived (days to weeks)
- **Example:**
  ```bash
  git checkout develop
  git pull origin develop
  git checkout -b feature/practitioner-search-filters
  ```

#### 4. **bugfix/\<bug-name\>** (Development)
- **Purpose:** Fix bugs found in development
- **Naming:** `bugfix/login-crash`, `bugfix/appointment-timezone-issue`
- **Created from:** `develop`
- **Merged back into:** `develop` (via pull request)
- **Lifetime:** Short-lived (hours to days)
- **Example:**
  ```bash
  git checkout develop
  git pull origin develop
  git checkout -b bugfix/auth-token-expiration
  ```

#### 5. **hotfix/\<issue-name\>** (Emergency)
- **Purpose:** Fix critical issues in production
- **Naming:** `hotfix/security-vulnerability`, `hotfix/payment-processing-down`
- **Created from:** `main`
- **Merged back into:** `main` AND `develop`
- **Lifetime:** Very short (minutes to hours)
- **Example:**
  ```bash
  git checkout main
  git pull origin main
  git checkout -b hotfix/critical-security-patch
  # After fix, merge to main AND develop
  ```

#### 6. **release/\<version\>** (Release)
- **Purpose:** Prepare releases; allow only bug fixes
- **Naming:** `release/1.0.0`, `release/2.1.0-beta`
- **Created from:** `develop`
- **Merged back into:** `main` (tagged), then `develop`
- **Lifetime:** Short (1-2 weeks)
- **Allowed changes:** Version bumps, bug fixes, documentation only
- **Example:**
  ```bash
  git checkout develop
  git pull origin develop
  git checkout -b release/1.0.0
  # Update version numbers, fix bugs
  # Create PR to main, then back-merge to develop
  ```

---

## Workflow Process

### Creating a Feature Branch

```bash
# 1. Start from develop
git checkout develop
git pull origin develop

# 2. Create feature branch
git checkout -b feature/my-feature-name

# 3. Work on your feature
# ... make commits ...
git add .
git commit -m "feat(scope): clear description"
git push -u origin feature/my-feature-name

# 4. Create Pull Request on GitHub
# See PULL_REQUEST_GUIDELINES.md
```

### Working on Your Branch

**Commit Frequently:**
```bash
# Good commits (small, focused)
git commit -m "feat(auth): add email validation"
git commit -m "fix(profile): correct timezone display"
git commit -m "test(appointments): add filtering tests"

# Bad commits (too large, vague)
git commit -m "updates"
git commit -m "various fixes and improvements"
```

**Keep Sync with Develop:**
```bash
# Before opening PR or when develop is updated
git fetch origin
git rebase origin/develop

# Or if you prefer merge
git merge origin/develop
```

### Submitting a Pull Request

**All pull requests must:**
1. ✅ Have a clear title and description
2. ✅ Reference related issues/tickets
3. ✅ Include test coverage
4. ✅ Pass all status checks (CI/CD)
5. ✅ Have meaningful commit history
6. ✅ Be reviewed by at least 1 team member

**Example PR description:**
```markdown
## Description
Implement practitioner search with filtering by specialty and availability.

## Related Issues
Fixes #123, Related to #124

## Type of Change
- [x] New feature
- [ ] Bug fix
- [ ] Breaking change
- [ ] Documentation

## Testing
- [x] Unit tests added
- [x] Integration tests added
- [x] Manual testing completed

## Screenshots (if UI change)
[Attach images]

## Checklist
- [x] Code follows project style
- [x] Self-review completed
- [x] Documentation updated
- [x] No new warnings generated
```

### Code Review & Approval

**Reviewer responsibilities:**
- ✅ Check code quality and logic
- ✅ Verify tests are adequate
- ✅ Ensure commits are meaningful
- ✅ Validate against project standards
- ✅ Test locally if needed

**Author responsibilities:**
- ✅ Address all review comments
- ✅ Provide context for decisions
- ✅ Keep PR focused and manageable
- ✅ Update documentation as needed

### Merging to Develop

**Once approved:**
1. ✅ Ensure branch is up to date with `develop`
2. ✅ All status checks pass
3. ✅ Minimum 1 approval received
4. ✅ Merge with meaningful commit message

**Merge options:**
- **Squash & Merge:** For multiple small commits (preferred for features)
- **Create a Merge Commit:** Keep full history (preferred for releases)
- **Rebase & Merge:** Linear history (use carefully)

---

## Release Process

### Creating a Release Branch

**Step 1: Prepare Release Branch**
```bash
# Create from develop when ready to release
git checkout develop
git pull origin develop
git checkout -b release/1.0.0
```

**Step 2: Update Version Numbers**
```
# Update in these files:
- build.gradle.kts (Android/Shared)
- iosApp/eSklepios/Info.plist (iOS)
- package.json (if applicable)
- CHANGELOG.md
- README.md (if version mentioned)

git commit -m "chore(release): bump version to 1.0.0"
```

**Step 3: Create PR to Main**
```bash
git push -u origin release/1.0.0
# Create PR: release/1.0.0 → main
# Title: "Release 1.0.0"
# In description: list all features/fixes in this release
```

**Step 4: Merge to Main (with Approval)**
```bash
# After approval and review:
git checkout main
git pull origin main
git merge --no-ff release/1.0.0
git tag -a v1.0.0 -m "Release version 1.0.0"
git push origin main
git push origin v1.0.0
```

**Step 5: Back-Merge to Develop**
```bash
git checkout develop
git pull origin develop
git merge --no-ff release/1.0.0
git push origin develop

# Delete release branch
git branch -d release/1.0.0
git push origin --delete release/1.0.0
```

---

## Hotfix Process

**For critical production issues:**

```bash
# 1. Create hotfix from main
git checkout main
git pull origin main
git checkout -b hotfix/critical-issue

# 2. Fix the issue
# ... make commits ...
git commit -m "fix(critical): resolve production issue"

# 3. Merge to main with tag
git checkout main
git pull origin main
git merge --no-ff hotfix/critical-issue
git tag -a v1.0.1 -m "Hotfix: critical issue"
git push origin main
git push origin v1.0.1

# 4. Back-merge to develop
git checkout develop
git pull origin develop
git merge --no-ff hotfix/critical-issue
git push origin develop

# 5. Delete hotfix branch
git branch -d hotfix/critical-issue
git push origin --delete hotfix/critical-issue
```

---

## Commit Message Standards

Follow **Conventional Commits** format:

```
<type>(<scope>): <subject>

<body>

<footer>
```

### Types
- `feat` — New feature
- `fix` — Bug fix
- `refactor` — Code refactoring without behavior change
- `test` — Test additions or fixes
- `docs` — Documentation only
- `chore` — Build, dependencies, tooling
- `style` — Code style (formatting, missing semicolons, etc.)
- `perf` — Performance improvements
- `ci` — CI/CD configuration

### Scopes (Project-Specific)
- `shared` — Shared KMM code
- `android` — Android app
- `ios` — iOS app
- `auth` — Authentication
- `home` — Home screen
- `appointments` — Appointments feature
- `profile` — User profile
- `navigation` — Navigation
- `di` — Dependency injection
- `network` — API/network layer
- `db` — Database layer
- `strings` — Localization
- `ci` — CI/CD

### Examples

**Good commits:**
```
feat(shared): add HomeViewModel with search and filter support

This commit introduces the HomeViewModel which manages:
- Practitioner search
- Filtering by specialty
- Loading states

Fixes #123
```

```
fix(android): correct SecureStorage method names

SecureStorage methods were named setToken/setRefreshToken but TokenStorage
interface expects saveToken/saveRefreshToken. Updated to match interface.

Fixes #456
```

```
test(shared): add PractitionerRepositoryTest

Added comprehensive tests for:
- Fetching practitioners
- Filtering by specialty
- Caching behavior

Related to #789
```

**Bad commits:**
```
✗ "updates" — too vague
✗ "fix bug" — doesn't describe what
✗ "WIP" — work in progress shouldn't be pushed
✗ "aaa" — meaningless
```

---

## Branch Protection Rules

### Main Branch Protection

```
✅ Require a pull request before merging
✅ Require approvals: 1 (or 2 for critical)
✅ Dismiss stale pull request approvals when new commits are pushed
✅ Require status checks to pass before merging
   - All CI/CD checks must pass
   - Code review approved
✅ Require branches to be up to date before merging
✅ Restrict who can push to matching branches
   - Only release manager + lead developers
✅ Prevent force pushes
✅ Prevent deletions
```

### Develop Branch Protection

```
✅ Require a pull request before merging
✅ Require approvals: 1
✅ Require status checks to pass before merging
✅ Restrict who can push to matching branches
   - Team members via PR only
✅ Prevent force pushes
✅ Prevent deletions
```

---

## Best Practices

### DO

✅ **Create small, focused branches**
- One feature/fix per branch
- Short-lived (days, not weeks)

✅ **Write meaningful commit messages**
- Describe the "why" not just the "what"
- Include issue references

✅ **Test before pushing**
- Run tests locally
- Verify on target platform
- Check for regressions

✅ **Keep develop stable**
- Never merge broken code
- Only merge tested, reviewed code

✅ **Pull before pushing**
- Always sync with remote before pushing
- Resolve conflicts locally

✅ **Delete merged branches**
- Keep repository clean
- Delete local and remote after merge

### DON'T

❌ **Commit directly to main or develop**
- Always use pull requests
- Never force push

❌ **Make unrelated changes in one PR**
- Keep PRs focused
- One feature per PR

❌ **Commit sensitive data**
- Never commit API keys, passwords, tokens
- Use environment variables instead

❌ **Ignore failing tests**
- All tests must pass before merge
- Don't skip CI/CD checks

❌ **Leave long-lived feature branches**
- Merge or delete regularly
- Stale branches cause conflicts

❌ **Rewrite public history**
- Don't force push to shared branches
- Use revert for fixing public commits

---

## Troubleshooting

### Branch is Behind Main/Develop

```bash
# Option 1: Rebase (preferred for feature branches)
git fetch origin
git rebase origin/develop

# Option 2: Merge (safer if others use your branch)
git fetch origin
git merge origin/develop
```

### Accidentally Committed to Wrong Branch

```bash
# Move last commit to correct branch
git reset HEAD~1              # Undo last commit (keeps changes)
git stash                     # Save changes
git checkout correct-branch
git stash pop                 # Apply changes
git commit -m "correct message"
```

### Need to Fix Last Commit Message

```bash
git commit --amend -m "new message"
git push origin branch-name --force-with-lease  # Only if not merged yet
```

### Merge Conflict

```bash
# 1. See conflicts
git status

# 2. Edit conflicted files (remove <<<, ===, >>>)
# 3. Mark as resolved
git add .

# 4. Complete merge
git commit -m "Merge resolve: branch-name"
```

---

## Tools & Integration

### GitHub Branch Protection
- **Setting:** Settings → Branches → Branch protection rules
- **Configure:** Main and Develop branches
- **Enforce:** Status checks, PR reviews, dismissal rules

### GitHub Actions (CI/CD)
- Workflows defined in `.github/workflows/`
- Run on: PR, push to main/develop
- Required checks: tests, lint, builds

### Pre-commit Hooks
Consider adding to `.git/hooks/pre-commit`:
- Run linter
- Check for secrets
- Verify commit message format

---

## FAQ

**Q: Can I work on multiple features at once?**
A: Yes, create separate branches for each. Switch between them with `git checkout`.

**Q: What if my PR gets rejected?**
A: Address feedback, make new commits, push. PR auto-updates. Repeat until approved.

**Q: Can I merge my own PR?**
A: No. Code review requires another team member's approval.

**Q: How often should I push?**
A: At least daily, or when a logical unit of work is complete.

**Q: What's the difference between merge and rebase?**
A: Merge keeps history, rebase linearizes it. Use merge for main, rebase for feature branches.

**Q: How do I stay updated with develop changes?**
A: Run `git pull origin develop` before starting work, and before opening a PR.

---

## Contact & Questions

For questions about this workflow:
1. Check this document
2. Review `PULL_REQUEST_GUIDELINES.md`
3. Ask team lead or release manager

---

**Version:** 1.0  
**Status:** Active  
**Last Reviewed:** 2026-05-26
