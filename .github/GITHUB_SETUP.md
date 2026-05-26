# GitHub Integration Setup Guide

This guide walks you through setting up Claude Code GitHub integration for the eSklepios project.

---

## Prerequisites

- ✅ GitHub account with admin access to the repository
- ✅ Anthropic API key (from https://console.anthropic.com/)
- ✅ Repository: `https://github.com/annintech-cmyk/ai-esklepios` or your fork of eSklepios

---

## Step 1: Install Claude GitHub App

1. Go to: **https://github.com/apps/claude**
2. Click **"Install"**
3. Select your organization/account
4. Choose **"Only select repositories"** and select:
   - `ai-esklepios` (or your eSklepios fork)
5. Click **"Install"**

---

## Step 2: Add Repository Secrets

Navigate to your repository on GitHub:  
**Settings → Secrets and variables → Actions**

### **Option A: Single API Key (Simplest)**

| Name | Value |
|------|-------|
| `ANTHROPIC_API_KEY` | Your Anthropic API key (starts with `sk-ant-...`) |
| `GITHUB_TOKEN` | Auto-populated by GitHub Actions |

**How to get your API key:**
1. Visit: https://console.anthropic.com/
2. Go to **API Keys**
3. Create or copy your key
4. Keep it secure (rotate regularly)

### **Option B: Environment-Specific Keys (Recommended)**

If you use different API keys for dev vs. prod:

| Name | Value | Environment |
|------|-------|-------------|
| `ANTHROPIC_API_KEY_DEV` | Dev API key | development |
| `ANTHROPIC_API_KEY_PROD` | Prod API key | production |
| `GITHUB_TOKEN` | Auto-populated | All |

**To set up environments:**
1. Go to **Settings → Environments**
2. Click **"New environment"**
3. Name it: `development` or `production`
4. Add the corresponding secret under that environment

### **Option C: OAuth Token (If Using Claude Pro/Max)**

1. Run locally: `claude setup-token`
2. Copy the token
3. Add to repository secrets:

| Name | Value |
|------|-------|
| `CLAUDE_CODE_OAUTH_TOKEN` | Token from `claude setup-token` |

---

## Step 3: Workflow Files Configuration

Three workflows are provided in `.github/workflows/`:

### **claude-code.yml** (General Purpose)
- Runs on all pull requests
- Uses `ANTHROPIC_API_KEY`
- Basic analysis and comments

**When to use:** For simple feedback on any PR

### **claude-code-dev.yml** (Development)
- Runs on `develop`, `feature/*`, `fix/*` branches
- Uses `ANTHROPIC_API_KEY_DEV` (dev environment)
- Includes test and lint steps
- Uses Claude 3.5 Sonnet (balanced)

**When to use:** For feature/bug fix branches during development

### **claude-code-prod.yml** (Production)
- Runs on `main` / `master` branches only
- Uses `ANTHROPIC_API_KEY_PROD` (prod environment)
- Includes all tests, security checks, and builds
- Uses Claude 3.5 Opus (most powerful)

**When to use:** For release branches and main PRs

---

## Step 4: Branch Protection Rules (Optional but Recommended)

To require Claude Code analysis before merging:

1. Go to **Settings → Branches**
2. Click **"Add rule"** under Branch protection rules
3. Name pattern: `main` (or your main branch)
4. Enable:
   - ✅ **Require a pull request before merging**
   - ✅ **Require status checks to pass before merging**
   - ✅ Select: `claude-code-prod` (or `claude-code`)
5. Click **"Create"**

---

## Step 5: Test the Integration

1. Create a test branch:
   ```bash
   git checkout -b test/github-setup
   ```

2. Make a small change (e.g., update README)

3. Push and create a PR:
   ```bash
   git push origin test/github-setup
   ```

4. Go to your PR on GitHub and look for:
   - ✅ Claude Code workflow running
   - ✅ Comments from Claude
   - ✅ Status check showing pass/fail

5. If successful, delete the test branch:
   ```bash
   git branch -D test/github-setup
   ```

---

## Troubleshooting

### **Workflow not running?**
- [ ] Check that Claude app is installed (Settings → Integrations & services)
- [ ] Verify secrets are added (Settings → Secrets and variables → Actions)
- [ ] Check workflow file syntax (`.github/workflows/*.yml`)
- [ ] Look at Actions tab for error logs

### **"API key not found" error?**
- [ ] Verify `ANTHROPIC_API_KEY` secret is added
- [ ] Check that the key starts with `sk-ant-`
- [ ] Regenerate key at https://console.anthropic.com/ if needed
- [ ] Ensure secret name matches exactly (case-sensitive)

### **PR comment not appearing?**
- [ ] Check that workflow completed successfully (Actions tab)
- [ ] Verify `GITHUB_TOKEN` permission is set to `write`
- [ ] Check pull request permissions in the workflow file

### **"Rate limited" errors?**
- [ ] Check Anthropic console for API quota
- [ ] Consider using environment-specific keys to isolate usage
- [ ] Space out workflow runs (avoid triggering multiple simultaneously)

---

## Security Best Practices

✅ **Do:**
- Rotate API keys monthly
- Use environment-specific keys if possible
- Restrict branch protection rules to main/release branches only
- Keep workflow files in version control
- Review Claude Code output before merging

❌ **Don't:**
- Commit API keys to Git
- Share secrets via email or Slack
- Use the same key across multiple repositories
- Disable required status checks for production branches
- Run workflows on every commit (use strategic branch filters)

---

## Advanced Configuration

### **Customize Model Selection**

Edit the workflow files to specify which Claude model to use:

```yaml
- uses: anthropics/claude-code-action@v1
  with:
    anthropic_api_key: ${{ secrets.ANTHROPIC_API_KEY }}
    model: claude-3-5-opus  # or claude-3-5-sonnet, claude-3-haiku
```

| Model | Speed | Cost | Use Case |
|-------|-------|------|----------|
| `claude-3-5-haiku` | Fast | Cheapest | Quick reviews, simple tasks |
| `claude-3-5-sonnet` | Balanced | Moderate | Most PRs, feature work |
| `claude-3-5-opus` | Thorough | Most expensive | Production, security-critical |

### **Run Claude Code on Demand**

The workflows support manual triggering:

1. Go to **Actions** tab
2. Select **"Claude Code"** workflow
3. Click **"Run workflow"** → **"Run workflow"**
4. Optionally specify a PR number

---

## Next Steps

1. ✅ [Complete Step 1-5 above](#step-1-install-claude-github-app)
2. ✅ Test with a pull request
3. ✅ Set up branch protection rules (optional)
4. ✅ Configure environment-specific secrets (optional)
5. ✅ Invite team members to review Claude Code feedback

---

## Support & Documentation

- **Claude Code GitHub Action:** https://github.com/anthropics/claude-code-action
- **Anthropic API Docs:** https://docs.anthropic.com/
- **Claude Console:** https://console.anthropic.com/

---

## Quick Reference

**Workflow Matrix:**

| Workflow | Trigger | Branch | Secrets | Tests | Model |
|----------|---------|--------|---------|-------|-------|
| `claude-code.yml` | PR | Any | `ANTHROPIC_API_KEY` | No | Sonnet |
| `claude-code-dev.yml` | PR | develop, feature/*, fix/* | `ANTHROPIC_API_KEY_DEV` | Yes | Sonnet |
| `claude-code-prod.yml` | PR | main/master | `ANTHROPIC_API_KEY_PROD` | All | Opus |

---

**Setup completed:** `2026-05-26`  
**Last updated:** This document
