# Pull Request Guidelines

**Version:** 1.0  
**Last Updated:** 2026-05-26

---

## Overview

This document defines standards for creating, reviewing, and merging pull requests in the eSklepios project.

---

## Before Creating a Pull Request

### ✅ Checklist

- [ ] Branch created from correct base (`develop` for features, `main` for hotfixes)
- [ ] Branch name follows convention (`feature/`, `bugfix/`, `hotfix/`)
- [ ] All commits follow **Conventional Commits** format
- [ ] Code tested locally (unit + integration tests pass)
- [ ] Code follows project style guides
- [ ] No debugging code left (console.logs, print statements)
- [ ] No hardcoded credentials or secrets
- [ ] Documentation updated if needed
- [ ] Related issues/tickets referenced
- [ ] Branch is up to date with base branch

### Code Quality Checks

**Before pushing:**

```bash
# Run tests
./gradlew :shared:commonTest
./gradlew :androidApp:test
xcodebuild test -project iosApp/eSklepios.xcodeproj

# Run linter
./gradlew :androidApp:lint
./gradlew :shared:detekt

# Check for secrets
git diff --cached | grep -i "password\|secret\|token\|api"
```

---

## Creating a Pull Request

### Title Format

```
<type>(<scope>): <short description>
```

**Examples:**
- ✅ `feat(home): add practitioner search with filters`
- ✅ `fix(auth): correct token refresh logic`
- ✅ `test(appointments): add booking flow tests`
- ✅ `docs(strings): add Luxembourgish translations`

**Bad titles:**
- ❌ `update code`
- ❌ `fix`
- ❌ `WIP`
- ❌ `Various changes`

### Description Template

Use this template for all PRs:

```markdown
## Description
Brief overview of what this PR does.

## Related Issues
Fixes #123
Related to #456

## Type of Change
- [ ] New feature
- [ ] Bug fix
- [ ] Documentation
- [ ] Refactoring (no behavior change)
- [ ] Dependency update
- [ ] Breaking change

## Testing
- [ ] Unit tests added/updated
- [ ] Integration tests added/updated
- [ ] Manual testing completed
- [ ] No regression found

## Screenshots (if UI change)
Attach images showing before/after if applicable.

## Checklist
- [ ] Code follows project coding standards
- [ ] Comments added for complex logic
- [ ] Documentation updated
- [ ] Tests pass locally
- [ ] No console.logs or debug code
- [ ] No hardcoded secrets
- [ ] Ready for review
```

### Example PR Description

```markdown
## Description
Implement practitioner search functionality with filtering by specialty, 
experience level, and availability. Includes real-time search and cached results.

## Related Issues
Fixes #89
Implements feature request from #75

## Type of Change
- [x] New feature
- [ ] Bug fix
- [ ] Documentation
- [ ] Refactoring
- [ ] Dependency update
- [ ] Breaking change

## Testing
- [x] Unit tests added (PractitionerRepositoryTest, SearchViewModelTest)
- [x] Integration tests added (end-to-end search flow)
- [x] Manual testing completed on:
  - Android (API 26, 30, 33)
  - iOS (iPhone 13, 15)
- [x] Verified search performance with 1000+ practitioners
- [x] No regressions found in existing tests

## Screenshots
**Android Search:**
[Screenshot 1]

**iOS Search Results:**
[Screenshot 2]

## Checklist
- [x] Code follows project coding standards
- [x] Rule UI-1a applied (no hardcoded dimensions)
- [x] Rule L-2 applied (all strings from Twine)
- [x] Comments added for search algorithm
- [x] README updated with search feature
- [x] All tests pass locally
- [x] No console.logs or debug code
- [x] No API keys or secrets committed
- [x] Ready for review
```

---

## Keeping PR Manageable

### Size Guidelines

**Good PR sizes:**
- ✅ 100-500 lines of changes
- ✅ Focused on one feature or fix
- ✅ Can be reviewed in 30 minutes

**Too large:**
- ❌ 1000+ lines of changes
- ❌ Multiple unrelated features
- ❌ Refactoring + feature together

**If your PR is too large:**
1. Break into smaller PRs
2. Merge smaller PRs first
3. Rebase larger PR on latest develop
4. Opens each in sequence

### Commit Structure

**Each commit should:**
- ✅ Be a logical unit of work
- ✅ Have a meaningful message
- ✅ Be testable independently
- ✅ Not break existing tests

**Example good commit sequence:**
```
feat(home): add search input field
feat(home): implement search API call
feat(home): add result filtering
test(home): add comprehensive search tests
docs(home): update feature documentation
```

**Bad commit sequence:**
```
✗ Work in progress
✗ Updates
✗ Fix
✗ More fixes
✗ Final version
```

---

## During Review

### For Authors

**Respond to feedback:**
- ✅ Address all comments
- ✅ Explain decisions if disagreeing
- ✅ Request re-review after changes
- ✅ Keep commits organized (don't force push messily)

**Communication:**
- ✅ Be respectful of feedback
- ✅ Ask clarifying questions if unclear
- ✅ Provide context when needed
- ✅ Update PR description if scope changes

### For Reviewers

**Review checklist:**
- [ ] Code quality and logic sound
- [ ] Tests are adequate and pass
- [ ] Commits are meaningful
- [ ] Follows project standards
- [ ] No security/performance issues
- [ ] Documentation updated
- [ ] No breaking changes (unless intentional)

**Provide constructive feedback:**
- ✅ "Why" not just "what"
- ✅ Suggest improvements, don't demand
- ✅ Praise good code
- ✅ Approve when satisfied

**Example feedback:**
```
✅ Great implementation of search filtering! 
   Suggestion: Could we cache results to improve performance on large lists?
```

Not:
```
❌ Bad code
```

---

## Common Issues & Solutions

### Issue: PR is Too Large

**Solution:**
```
Split into:
1. PR-1: Search input field
2. PR-2: API integration
3. PR-3: Result filtering
4. PR-4: Tests and docs
```

### Issue: Merge Conflicts

**Solution:**
```bash
git fetch origin
git rebase origin/develop
# Fix conflicts in your editor
git add .
git rebase --continue
git push origin branch --force-with-lease
```

### Issue: Changes Requested, Multiple Commits

**Option 1: Keep history (preferred)**
```bash
# Make new commits addressing feedback
git commit -m "refactor(search): improve performance"
git push origin branch
# Request re-review
```

**Option 2: Squash (if many small fixes)**
```bash
git rebase -i origin/develop
# Mark commits to squash
git push origin branch --force-with-lease
```

### Issue: Accidentally Pushed to Main

**Don't panic!** Branch protection prevents this. Revert the PR instead:
```bash
git revert <commit-hash>
git push origin main
```

---

## Approval & Merging

### Approval Requirements

**For develop:**
- ✅ Minimum 1 approval
- ✅ All checks passing
- ✅ Up to date with develop
- ✅ No conversations pending

**For main (releases):**
- ✅ Minimum 1 approval (or 2 for critical)
- ✅ All checks passing
- ✅ Version numbers updated
- ✅ Changelog updated
- ✅ Tagged with version

### Merge Process

**Before merging:**
```bash
# Ensure branch is up to date
git fetch origin
git rebase origin/develop  (or main)
git push origin branch
```

**Merge options on GitHub:**
- **Squash & Merge** — Combine all commits (good for features)
- **Create a Merge Commit** — Keep full history (good for releases)
- **Rebase & Merge** — Linear history (use carefully)

**After merge:**
```bash
# Delete branch locally
git branch -D branch-name

# Delete remote branch (or GitHub auto-deletes)
git push origin --delete branch-name
```

---

## Post-Merge

### Monitor CI/CD

After merge to develop/main:
- ✅ Watch CI/CD pipeline run
- ✅ Check all tests pass
- ✅ Verify deployments (if applicable)
- ✅ Monitor for issues/errors

### If Issues Found

**If develop breaks:**
```bash
# Option 1: Revert PR
git revert <merge-commit-hash>
git push origin develop

# Option 2: Fix with new PR
# Create hotfix PR to develop
```

**If main breaks (production issue):**
```bash
# Create hotfix from main
git checkout main
git checkout -b hotfix/issue-name
# Fix, commit, push, create PR
```

---

## PR Checklist (Quick Reference)

Copy this to use before submitting:

```markdown
## Pre-PR Checklist

Code Quality:
- [ ] Tests pass locally (./gradlew :shared:commonTest, etc.)
- [ ] Linter passes (./gradlew lint, detekt)
- [ ] No console.logs or debug code
- [ ] No hardcoded secrets/credentials
- [ ] Code follows project standards

Documentation:
- [ ] Strings in Twine (not hardcoded)
- [ ] Design tokens used (no hardcoded dimensions)
- [ ] Comments for complex logic
- [ ] README/docs updated
- [ ] CHANGELOG updated (for releases)

Git Hygiene:
- [ ] Branch name correct (feature/bugfix/hotfix/release)
- [ ] Commits follow Conventional Commits
- [ ] Up to date with base branch
- [ ] Meaningful commit history
- [ ] No merge commits from base branch

PR Content:
- [ ] Title follows format
- [ ] Description complete and clear
- [ ] Related issues referenced
- [ ] Screenshots attached (if UI change)
- [ ] Checklist items reviewed
```

---

## FAQ

**Q: Can I request a specific reviewer?**
A: Yes, use GitHub's request reviewer feature. But anyone can review.

**Q: How long should review take?**
A: Aim for 24 hours for features, urgent for hotfixes. But depends on PR size.

**Q: Can I keep PR in draft while working?**
A: Yes. Mark as "Draft" if not ready. Convert to "Ready for review" when done.

**Q: What if reviewer is unavailable?**
A: Request another reviewer. All reviewers should review within 24h.

**Q: Can I merge my own PR?**
A: No. Another team member must approve and merge.

---

## Examples

### ✅ Good PR

**Title:** `feat(appointments): implement booking cancellation with confirmation`

**Description:**
```markdown
## Description
Allow users to cancel appointments with optional reason. Shows confirmation dialog
before processing.

## Related Issues
Fixes #456

## Type of Change
- [x] New feature

## Testing
- [x] Unit tests: AppointmentRepositoryTest (cancellation logic)
- [x] UI tests: CancelAppointmentScreenTest
- [x] Manual: Tested on Android + iOS
- [x] Verified refund processing

## Screenshots
[Shows cancel dialog, confirmation flow]

## Checklist
- [x] Tests pass
- [x] No debug code
- [x] Documentation updated
- [x] Strings from Twine
```

**Commits:**
```
feat(shared): add CancelAppointmentUseCase
feat(android): implement cancellation UI
feat(ios): add cancellation flow
test(shared): add cancellation tests
docs(appointments): update cancellation guide
```

### ❌ Bad PR

**Title:** `Fix and updates`

**Description:** (mostly empty)

**Commits:**
```
Updates
WIP
Fixed stuff
More fixes
Final version
```

**Issues:**
- Title too vague
- Description empty
- Commit messages meaningless
- Too large (500+ files)
- No tests
- Mixed concerns (feature + refactor)

---

**Remember:** Good PRs = faster reviews = faster merges = faster shipping! 🚀

