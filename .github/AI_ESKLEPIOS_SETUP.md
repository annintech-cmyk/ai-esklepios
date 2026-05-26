# Setup Guide: ai-esklepios GitHub Integration

This guide is for setting up Claude Code on the separate `ai-esklepios` repository:  
**https://github.com/annintech-cmyk/ai-esklepios**

---

## Quick Start (5 minutes)

### 1. Install Claude App
- Go to: https://github.com/apps/claude
- Select `ai-esklepios` repository
- Click "Install"

### 2. Add Secrets
Navigate to: `https://github.com/annintech-cmyk/ai-esklepios/settings/secrets/actions`

Add secret:
```
Name:  ANTHROPIC_API_KEY
Value: sk-ant-xxxxxxxxxxxxxxxxxxxx (from https://console.anthropic.com/)
```

### 3. Create Workflow File
Create file: `.github/workflows/claude-code.yml`

```yaml
name: Claude Code

on:
  pull_request:
  workflow_dispatch:

permissions:
  contents: read
  pull-requests: write

jobs:
  claude-code:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: anthropics/claude-code-action@v1
        with:
          anthropic_api_key: ${{ secrets.ANTHROPIC_API_KEY }}
```

### 4. Push and Test
```bash
git add .github/workflows/claude-code.yml
git commit -m "chore: add Claude Code GitHub workflow"
git push
```

Create a test PR and watch Claude Code analyze it!

---

## Full Setup with Branch Rules

### Repository-Specific Settings

**ai-esklepios is likely a Python/AI project**, so customize for that:

```yaml
# .github/workflows/claude-code.yml
name: Claude Code

on:
  pull_request:
  workflow_dispatch:

permissions:
  contents: read
  pull-requests: write

jobs:
  claude-code:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
        with:
          fetch-depth: 0

      - uses: actions/setup-python@v4
        with:
          python-version: '3.11'

      - name: Run Claude Code
        uses: anthropics/claude-code-action@v1
        with:
          anthropic_api_key: ${{ secrets.ANTHROPIC_API_KEY }}

      - name: Lint with Ruff
        run: |
          pip install ruff
          ruff check .

      - name: Type Check with Mypy
        run: |
          pip install mypy
          mypy . --ignore-missing-imports

      - name: Comment on PR
        if: always()
        uses: actions/github-script@v7
        with:
          github-token: ${{ secrets.GITHUB_TOKEN }}
          script: |
            github.rest.issues.createComment({
              issue_number: context.issue.number,
              owner: context.repo.owner,
              repo: context.repo.repo,
              body: '✅ Claude Code analysis completed.'
            })
```

### Branch Protection Rules (Optional)

To require Claude Code before merging:

1. Go to: `https://github.com/annintech-cmyk/ai-esklepios/settings/branches`
2. Add rule for branch: `main`
3. Enable: **Require status checks to pass** → Select `claude-code`
4. Click "Create"

---

## Verify Setup

1. Create test branch:
   ```bash
   git checkout -b test/claude-setup
   echo "# Test" >> README.md
   git add README.md
   git commit -m "test: verify claude setup"
   git push origin test/claude-setup
   ```

2. Open PR on GitHub

3. Watch Actions tab → Should see "Claude Code" workflow running

4. Check PR comments → Should see Claude's feedback

5. If successful:
   ```bash
   git checkout main
   git branch -D test/claude-setup
   git push origin --delete test/claude-setup
   ```

---

## Troubleshooting

| Problem | Solution |
|---------|----------|
| Workflow not running | Check that Claude app is installed in repository Settings |
| "Secret not found" error | Verify `ANTHROPIC_API_KEY` is added (Settings → Secrets → Actions) |
| No PR comments | Check Actions log for errors; verify `GITHUB_TOKEN` permissions |
| Rate limit errors | Check Anthropic console quota; stagger workflow triggers |

---

## Both Repositories Setup Summary

| Item | eSklepios (Local) | ai-esklepios (GitHub) |
|------|-------------------|----------------------|
| Install Claude App | ✅ Required | ✅ Required |
| Add API Key Secret | ✅ Required | ✅ Required |
| Workflow Files | ✅ 3 files created | ✅ 1 file needed |
| Branch Rules | ⭕ Optional | ⭕ Optional |
| Test PR | ✅ Recommended | ✅ Recommended |

---

## Next Steps

1. ✅ Install Claude app on both repositories
2. ✅ Add `ANTHROPIC_API_KEY` secret to both
3. ✅ Copy workflow files:
   - **eSklepios:** Files already in `.github/workflows/`
   - **ai-esklepios:** Copy the workflow from this guide
4. ✅ Test with a pull request
5. ✅ Enable branch protection (optional)

---

Need help? See the main setup guide: [`.github/GITHUB_SETUP.md`](./GITHUB_SETUP.md)
