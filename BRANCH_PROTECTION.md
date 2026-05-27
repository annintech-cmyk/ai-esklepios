# GitHub Branch Protection Setup

**Version:** 1.0  
**Last Updated:** 2026-05-26

---

## Overview

This document explains how to set up GitHub branch protection rules for `main` and `develop` branches to enforce the Git workflow.

---

## Why Branch Protection?

✅ Prevents accidental commits to main/develop  
✅ Requires code review before merge  
✅ Ensures all tests pass before merge  
✅ Maintains clean commit history  
✅ Enforces workflow standards  

---

## Setup: Main Branch Protection

### Step 1: Go to Repository Settings

1. Navigate to: **https://github.com/annintech-cmyk/ai-esklepios**
2. Click **Settings** (top navigation)
3. Click **Branches** (left sidebar)
4. Click **Add rule** under "Branch protection rules"

### Step 2: Configure Protection Rule

**Branch name pattern:** `main`

### Step 3: Enable Protections

#### ✅ Require a pull request before merging
- Check: **Require a pull request before merging**
- Set: **Require 1 approval** (or 2 for critical changes)
- Check: **Dismiss stale pull request approvals when new commits are pushed**

#### ✅ Require status checks to pass
- Check: **Require status checks to pass before merging**
- Require branches to be up to date before merging
- Select status checks:
  - `Claude Code` (if using Claude code analysis)
  - `build` (if CI/CD configured)
  - `tests` (if automated tests configured)

#### ✅ Require branches to be up to date
- Check: **Require branches to be up to date before merging**

#### ✅ Restrict who can push
- Check: **Restrict who can push to matching branches**
- Select: **Only the following people** (release manager + lead developers)

#### ✅ Prevent force pushes
- Check: **Allow force pushes** → Select **Do not allow force pushes**

#### ✅ Prevent deletions
- Check: **Allow deletions** → Select **Do not allow deletions**

#### ✅ Include administrators
- Check: **Include administrators** (so rules apply to everyone)

### Step 4: Save

Click **Create** to save the rule.

---

## Setup: Develop Branch Protection

### Step 1: Add Another Rule

Click **Add rule** again

### Step 2: Configure Protection Rule

**Branch name pattern:** `develop`

### Step 3: Enable Protections

#### ✅ Require a pull request before merging
- Check: **Require a pull request before merging**
- Set: **Require 1 approval**
- Check: **Dismiss stale pull request approvals when new commits are pushed**

#### ✅ Require status checks to pass
- Check: **Require status checks to pass before merging**
- Check: **Require branches to be up to date before merging**
- Select status checks (same as main)

#### ✅ Require branches to be up to date
- Check: **Require branches to be up to date before merging**

#### ✅ Prevent force pushes
- Check: **Allow force pushes** → Select **Do not allow force pushes**

#### ✅ Prevent deletions
- Check: **Allow deletions** → Select **Do not allow deletions**

#### ✅ Include administrators
- Check: **Include administrators**

**Note:** Don't restrict pushers on develop (all team members should be able to create PRs)

### Step 4: Save

Click **Create** to save the rule.

---

## Verify Protection Rules

### Check Rules Are Enabled

1. Go to **Settings → Branches**
2. You should see:
   - **main** protection rule ✅
   - **develop** protection rule ✅

### Test Protection (Don't Actually Do This!)

To verify:
```bash
# This will fail (as expected):
git checkout main
echo "test" >> README.md
git add README.md
git commit -m "test"
git push origin main

# Error: "remote: error: GH006: Protected branch push rejected"
```

---

## Complete Protection Configuration

### Main Branch Rules

| Setting | Configuration |
|---------|---------------|
| **Pattern** | `main` |
| **Require PR** | ✅ Yes (1 approval) |
| **Status checks** | ✅ All checks must pass |
| **Dismiss stale PRs** | ✅ Yes |
| **Require up to date** | ✅ Yes |
| **Restrict pushes** | ✅ Release manager + leads only |
| **Force push** | ❌ Not allowed |
| **Deletions** | ❌ Not allowed |
| **Apply to admins** | ✅ Yes |

### Develop Branch Rules

| Setting | Configuration |
|---------|---------------|
| **Pattern** | `develop` |
| **Require PR** | ✅ Yes (1 approval) |
| **Status checks** | ✅ All checks must pass |
| **Dismiss stale PRs** | ✅ Yes |
| **Require up to date** | ✅ Yes |
| **Restrict pushes** | ⏸️ Not restricted (all via PR) |
| **Force push** | ❌ Not allowed |
| **Deletions** | ❌ Not allowed |
| **Apply to admins** | ✅ Yes |

---

## Behavior With Protection Enabled

### Normal Workflow (Works Fine)

```bash
# ✅ This works - using a feature branch
git checkout develop
git checkout -b feature/my-feature
# ... make changes ...
git push origin feature/my-feature

# Create PR on GitHub
# After approval, merge from GitHub UI
# PR merge to develop succeeds ✅
```

### Forbidden Actions (Protected)

```bash
# ❌ This will fail - direct push to main
git checkout main
git commit -m "quick fix"
git push origin main
# Error: GH006: Protected branch push rejected

# ❌ This will fail - direct push to develop
git checkout develop
git commit -m "new feature"
git push origin develop
# Error: GH006: Protected branch push rejected

# ❌ This will fail - force push to main
git push origin main --force
# Error: GH006: Protected branch push rejected

# ❌ This will fail - delete main/develop locally
git branch -D main
git push origin --delete main
# Error: GH006: Protected branch deletion rejected
```

---

## Troubleshooting

### "GH006: Protected branch push rejected"

**Cause:** Trying to push directly to protected branch

**Solution:**
```bash
# Use a feature branch instead
git checkout develop
git checkout -b feature/fix-name
# ... make changes ...
git push origin feature/fix-name
# Create PR, get approval, merge from GitHub
```

### PR Won't Merge: Status Checks Failed

**Cause:** Tests/checks haven't passed

**Solution:**
```bash
# Fix the code
git commit -m "fix: address test failures"
git push origin branch

# Wait for checks to rerun
# Once all green, PR can merge
```

### PR Won't Merge: Requires Approval

**Cause:** No approving reviews yet

**Solution:**
1. Ensure PR description is complete
2. Tag reviewers: `@username`
3. Wait for review
4. Address feedback if requested
5. Request re-review after fixes

### "Branch is behind main"

**Cause:** Branch hasn't been updated with latest main changes

**Solution:**
```bash
git fetch origin
git rebase origin/main
# or
git merge origin/main
git push origin branch
```

---

## Best Practices

### DO

✅ **Always use feature branches**
```bash
git checkout develop
git checkout -b feature/new-thing
```

✅ **Keep PRs focused**
- One feature/fix per PR
- Related changes only

✅ **Address review feedback**
- Push new commits to same branch
- Request re-review
- PR updates automatically

✅ **Merge only when ready**
- All checks green
- Approval received
- Branch up to date

### DON'T

❌ **Try to bypass protection**
- Don't use `--force` or `-f`
- Can't work on protected branches directly

❌ **Create massive PRs**
- Hard to review
- More likely to be rejected
- Break into smaller PRs

❌ **Merge your own code**
- Another developer must approve
- Catch mistakes, improve quality

❌ **Leave PRs stale**
- Update when asked
- Merge or close promptly

---

## Managing Protection Rules

### View Current Rules

1. **Settings → Branches**
2. Scroll to "Branch protection rules"
3. Click rule name to view/edit

### Edit a Rule

1. Click **Edit** button on the rule
2. Make changes
3. Click **Save**

### Delete a Rule

1. Click **Edit** button
2. Scroll to bottom
3. Click **Delete** (with confirmation)

**⚠️ Warning:** Only delete with team consensus

---

## Required Review Approvals

By default, we require **1 approval**. For critical changes, you can increase to **2**.

### Who Can Approve?

Anyone with "Write" access to the repository:
- ✅ Repository owner
- ✅ Collaborators (write access)
- ❌ Contributors (pull request only)
- ❌ Self-approval doesn't count

### Dismiss Stale Approvals?

✅ **Recommended: Enable**

When enabled:
- Old approvals dismissed if new commits pushed
- New approvals needed after changes
- Prevents sneaky changes after approval

---

## For Hotfixes

Hotfixes still require approval but follow fast track:

```bash
# Create hotfix from main
git checkout main
git checkout -b hotfix/critical-issue

# Push & create PR
git push origin hotfix/critical-issue

# Create PR: hotfix → main
# Mark as "Critical" or "Urgent" in title
# Get expedited review (target: <30 min)
# Merge and tag for release
```

---

## Integration with CI/CD

Protection rules work with GitHub Actions:

```yaml
# .github/workflows/tests.yml
name: Tests

on:
  pull_request:
    branches: [main, develop]

jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - run: ./gradlew test
      # If this fails, PR can't merge
```

---

## Checklist: Branch Protection Setup

- [ ] Navigate to repository Settings → Branches
- [ ] Create rule for `main` branch
  - [ ] Require PR (1 approval)
  - [ ] Require status checks
  - [ ] Require branches up to date
  - [ ] Restrict pushes (leaders only)
  - [ ] Prevent force pushes
  - [ ] Prevent deletions
  - [ ] Include administrators
- [ ] Create rule for `develop` branch
  - [ ] Require PR (1 approval)
  - [ ] Require status checks
  - [ ] Require branches up to date
  - [ ] Do NOT restrict pushes
  - [ ] Prevent force pushes
  - [ ] Prevent deletions
  - [ ] Include administrators
- [ ] Test: Verify can't push directly
- [ ] Test: Verify can create PRs
- [ ] Document in team wiki/docs
- [ ] Inform team of new restrictions

---

## FAQ

**Q: Can I push directly to develop?**
A: No. Use a feature branch and create a PR.

**Q: What if I need to push urgently?**
A: Still need PR + approval. Expedite review for urgent changes.

**Q: Can the repo owner override protection?**
A: Yes, but shouldn't. Maintain the process for everyone.

**Q: How do I update main with develop?**
A: Create a PR from develop to main for release management.

**Q: What if tests fail?**
A: Fix the code, push new commits. PR updates automatically.

**Q: Can my team disable protection?**
A: Collectively, yes. But it's recommended to keep it.

---

## Related Documents

- [`GIT_WORKFLOW.md`](./GIT_WORKFLOW.md) — Complete Git workflow
- [`PULL_REQUEST_GUIDELINES.md`](./PULL_REQUEST_GUIDELINES.md) — PR standards
- [`RELEASE_WORKFLOW.md`](./RELEASE_WORKFLOW.md) — Release process

---

**Version:** 1.0  
**Status:** Active  
**Last Reviewed:** 2026-05-26
