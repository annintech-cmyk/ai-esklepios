# GitHub Push Automation Scripts

Two automated scripts to handle authentication and push changes to GitHub.

---

## Quick Start

### Using Python (Recommended)

```bash
cd /Users/anna.felix/projects/esklepios
python3 push-to-github.py
```

### Using Bash

```bash
cd /Users/anna.felix/projects/esklepios
./push-to-github.sh
```

---

## What These Scripts Do

✅ Verify you're in a git repository  
✅ Check for uncommitted changes  
✅ Offer three authentication methods  
✅ Push to remote with proper credentials  
✅ Verify push succeeded  
✅ Display commit details and GitHub link  

---

## Authentication Methods

### 1. Personal Access Token (Recommended for CI/CD)

**Best for:** One-time pushes, GitHub Actions, scripting

**Steps:**
1. Go to: https://github.com/settings/tokens
2. Click "Generate new token" → "Generate new token (classic)"
3. Select scopes: `repo`, `workflow`
4. Copy the token
5. Run script and select option **1**
6. Enter GitHub username and token when prompted

**Pros:**
- Works on any machine
- Can be revoked easily
- No SSH key needed

**Cons:**
- Expires (must regenerate)
- Must paste token each time

---

### 2. SSH Key (Recommended for local development)

**Best for:** Regular development, local machines

**Steps:**
1. Check if you have an SSH key:
   ```bash
   ls -la ~/.ssh/
   ```

2. If not, create one:
   ```bash
   ssh-keygen -t ed25519 -C "your_email@example.com"
   ```

3. Add public key to GitHub:
   - Copy: `cat ~/.ssh/id_ed25519.pub`
   - Go to: https://github.com/settings/keys
   - Click "New SSH key"
   - Paste and save

4. Test SSH connection:
   ```bash
   ssh -T git@github.com
   ```

5. Run script and select option **2**

**Pros:**
- No password required after setup
- More secure
- Works offline

**Cons:**
- Requires initial setup
- Only works on configured machines

---

### 3. Existing Credentials

**Best for:** If you've already authenticated

**Steps:**
1. Requires previous authentication with `git`
2. Run script and select option **3**
3. Git will use cached credentials

**Pros:**
- No setup needed if already configured

**Cons:**
- Only works if credentials cached
- May fail if credentials expired

---

## Script Comparison

| Feature | Python | Bash |
|---------|--------|------|
| Cross-platform | ✅ macOS, Linux, Windows | ⚠️ Mostly macOS/Linux |
| Error handling | ✅ Better | ⚠️ Basic |
| User prompts | ✅ Cleaner | ✅ Works |
| Sensitive data | ✅ Hidden | ✅ Hidden |
| Dependencies | ✅ Built-in | ⚠️ Needs bash |

**Recommendation:** Use Python for reliability

---

## Step-by-Step Example

### Using Python Script

```bash
$ python3 push-to-github.py

🚀 GitHub Push Script
==================================================

Repository: https://github.com/annintech-cmyk/ai-esklepios.git
Branch: main

⚠️  Uncommitted changes detected:
M  .claude/rules/architecture-rules.md
A  .github/GITHUB_SETUP.md
Continue anyway? (y/n): y

Authentication Methods:
1. Personal Access Token (GitHub)
2. SSH Key
3. Existing credentials

Choose method (1-3): 1

Personal Access Token Method
Create a token at: https://github.com/settings/tokens
Scopes needed: repo, workflow

GitHub Username: anna-felix
GitHub Personal Access Token: ••••••••••••••••••

→ git push https://anna-felix:••••••••••••••••••@github.com/annintech-cmyk/ai-esklepios.git main

Verifying push...

✅ Push verified! Local and remote are in sync

📊 Commit Details:
8bd65b7 chore: audit and consolidate rules; add GitHub integration

🔗 View on GitHub:
   https://github.com/annintech-cmyk/ai-esklepios.git/commits/main

Done!
```

---

## Troubleshooting

### "Not in a git repository"
```bash
# Make sure you're in the project directory
cd /Users/anna.felix/projects/esklepios
```

### "Personal Access Token not working"
- Verify token has `repo` and `workflow` scopes
- Check token hasn't expired
- Try creating a new token

### "SSH key not found"
```bash
# Create one
ssh-keygen -t ed25519 -C "your_email@example.com"
# Then add to GitHub at https://github.com/settings/keys
```

### "Push failed - authentication error"
- Try method 1 (Personal Access Token)
- Ensure you have push access to the repository
- Check GitHub permissions

### "Remote changed since we started"
```bash
# Pull latest and retry
git pull origin main
python3 push-to-github.py
```

---

## Security Best Practices

✅ **Do:**
- Use SSH keys for local development
- Use Personal Access Tokens for CI/CD
- Regenerate tokens monthly
- Restrict token scopes to minimum needed
- Never commit tokens to Git

❌ **Don't:**
- Share tokens via email or chat
- Commit tokens to repository
- Use the same token across projects
- Store tokens in plain text files

---

## Automating with Environment Variables

For CI/CD systems, you can skip prompts:

```bash
# Using environment variables (Bash)
export GITHUB_USER="your-username"
export GITHUB_TOKEN="your-token"

# Then the script can auto-detect and use them
```

---

## Next Steps

1. ✅ Run the script:
   ```bash
   python3 push-to-github.py
   ```

2. ✅ Choose your authentication method

3. ✅ Verify push at:
   ```
   https://github.com/annintech-cmyk/ai-esklepios/commits/main
   ```

4. ✅ Set up SSH for future pushes (optional):
   ```bash
   ssh-keygen -t ed25519 -C "your_email@example.com"
   ```

---

## Support

If you encounter issues:

1. Check script output for error messages
2. Verify GitHub credentials at https://github.com/settings/
3. Ensure you have push access to the repository
4. Try manual push:
   ```bash
   git push origin main
   ```

---

**Created:** 2026-05-26  
**Scripts:** `push-to-github.py` (Python), `push-to-github.sh` (Bash)
