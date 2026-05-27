# Release & Versioning Workflow

**Version:** 1.0  
**Last Updated:** 2026-05-26  
**Project:** eSklepios (Kotlin Multiplatform Mobile)

---

## Versioning Scheme

eSklepios uses **Semantic Versioning (SemVer)**: `MAJOR.MINOR.PATCH`

### Version Format
```
MAJOR.MINOR.PATCH[-PRERELEASE][+BUILD]

Examples:
1.0.0           — First stable release
1.0.1           — Patch (bug fix)
1.1.0           — Minor (new feature)
2.0.0           — Major (breaking change)
1.0.0-beta.1    — Beta release
1.0.0-rc.1      — Release candidate
```

### When to Increment

| Increment | When | Example |
|-----------|------|---------|
| **MAJOR** | Breaking changes to API or behavior | 1.0.0 → 2.0.0 |
| **MINOR** | New features (backward compatible) | 1.0.0 → 1.1.0 |
| **PATCH** | Bug fixes (backward compatible) | 1.0.0 → 1.0.1 |

### Pre-release Versions

Used for testing before full release:

```
1.0.0-alpha.1   — Early development, unstable
1.0.0-beta.1    — Feature-complete, may have bugs
1.0.0-rc.1      — Release candidate, ready to release
```

**Progression:** alpha → beta → rc → final release

---

## Release Timeline

### Phase 1: Development (2-4 weeks)
```
develop branch receives:
- New features (feature/* branches)
- Bug fixes (bugfix/* branches)
- Regular integration testing
```

### Phase 2: Release Preparation (1 week)
```
develop → release/* branch
- Version bumps
- Final bug fixes only
- Testing & QA
- Documentation updates
```

### Phase 3: Release (1 day)
```
release/* → main branch
- Create GitHub Release
- Tag with version
- Deploy to app stores
- Announce release
```

### Phase 4: Post-Release (ongoing)
```
main ← back-merge from release/*
develop ← back-merge from release/*
- Hotfixes to main if needed
- Development continues on develop
```

---

## Step-by-Step Release Process

### Step 1: Prepare Release Branch

**When:** Features for release are complete on `develop`

```bash
# 1. Create release branch from develop
git checkout develop
git pull origin develop
git checkout -b release/1.0.0

# 2. Update version numbers
# Files to update:
#   - build.gradle.kts (android/shared version)
#   - iosApp/eSklepios/Info.plist (iOS version)
#   - buildkonfig section in build.gradle.kts

# Android: build.gradle.kts
versionCode = 1        # Increment by 1 for each release
versionName = "1.0.0"  # Match semantic version

# iOS: Info.plist
CFBundleShortVersionString = "1.0.0"
CFBundleVersion = "1"

git add build.gradle.kts iosApp/eSklepios/Info.plist
git commit -m "chore(release): bump version to 1.0.0"

# 3. Push release branch
git push -u origin release/1.0.0
```

### Step 2: Testing & Stabilization

**Duration:** 3-7 days

```bash
# On release/* branch, fix bugs only (no new features)

# Bug fix example:
git checkout -b bugfix/timezone-issue
# ... fix bug ...
git commit -m "fix(appointments): correct timezone handling"
git push origin bugfix/timezone-issue

# Create PR to release/1.0.0
# After approval and merge, verify:
# ✓ All tests pass
# ✓ Manual testing on real devices
# ✓ Performance acceptable
# ✓ No regressions
```

### Step 3: Update Changelog

**File:** `CHANGELOG.md`

```markdown
## [1.0.0] - 2026-05-26

### Added
- Practitioner search with filtering
- Appointment booking system
- User profile management
- Multi-language support (en, fr, de, lb)

### Fixed
- Login token refresh issue
- Timezone handling in appointments
- Memory leak in home screen

### Changed
- Improved search performance
- Redesigned home screen layout

### Security
- Added certificate pinning for API

### Deprecated
- Old API v1 endpoints (deprecated, use v2)

[Full release notes](https://github.com/annintech-cmyk/ai-esklepios/releases/tag/v1.0.0)
```

### Step 4: Create Release PR

**Create PR:** `release/1.0.0` → `main`

```markdown
## Release PR: v1.0.0

This PR merges the release into production and creates the v1.0.0 release.

### Release Highlights
- ✅ Practitioner search
- ✅ Appointment booking
- ✅ User profiles
- ✅ Multi-language support

### Release Notes
See CHANGELOG.md for full details.

### Testing Completed
- ✅ All tests pass
- ✅ Manual testing on physical devices
- ✅ Performance testing
- ✅ Security review passed

### Deployment Plan
1. Merge to main
2. Tag v1.0.0
3. Build release APK/IPA
4. Submit to Play Store & App Store
5. Monitor for issues

### Rollback Plan
If critical issues: hotfix/issue-name from main
```

### Step 5: Merge to Main (with Approval)

**Only proceed after explicit approval from release manager/lead**

```bash
# Get approval on PR, then merge on GitHub
# Or manually:

git checkout main
git pull origin main
git merge --no-ff release/1.0.0

# Verify
git log --oneline -5
```

### Step 6: Tag Release

```bash
# Create annotated tag (preferred)
git tag -a v1.0.0 -m "Release version 1.0.0

Features:
- Practitioner search with filtering
- Appointment booking system
- User profile management

See CHANGELOG.md for full details."

# Push tag
git push origin v1.0.0

# Verify
git tag -l
git show v1.0.0
```

### Step 7: Build & Deploy

```bash
# Build Android Release
./gradlew :androidApp:assembleRelease
# Output: androidApp/build/outputs/apk/release/androidApp-release.apk

# Build iOS Archive
xcodebuild archive \
  -project iosApp/eSklepios.xcodeproj \
  -scheme eSklepios \
  -archivePath build/eSklepios.xcarchive \
  -configuration Release

# ExportOptions.plist for App Store
# Then export:
xcodebuild -exportArchive \
  -archivePath build/eSklepios.xcarchive \
  -exportPath build/eSklepios.ipa \
  -exportOptionsPlist ExportOptions.plist
```

### Step 8: Back-Merge to Develop

```bash
# Important: Bring develop up to date with main

git checkout develop
git pull origin develop
git merge --no-ff main

# If conflicts, resolve them
git add .
git commit -m "Merge main (v1.0.0) back to develop"
git push origin develop
```

### Step 9: Clean Up Release Branch

```bash
# Delete local branch
git branch -d release/1.0.0

# Delete remote branch
git push origin --delete release/1.0.0

# Verify
git branch -a
```

### Step 10: Create GitHub Release

On GitHub:

1. Go to: `Releases` → `Draft a new release`
2. Choose tag: `v1.0.0`
3. Title: `Release 1.0.0`
4. Description:
   ```markdown
   # eSklepios v1.0.0
   
   ## Features
   - Practitioner search with filtering
   - Appointment booking system
   - User profile management
   - Multi-language support (en, fr, de, lb)
   
   ## Improvements
   - Search performance optimized
   - Home screen redesigned
   - Token refresh improved
   
   ## Bug Fixes
   - Fixed timezone handling
   - Fixed memory leak
   - Corrected login flow
   
   See [CHANGELOG](./CHANGELOG.md) for full details.
   
   **Download:**
   - [Android APK](link-to-apk)
   - [iOS IPA](link-to-ipa)
   ```
5. Click `Publish release`

---

## Hotfix Process

### When to Use Hotfix

**Create hotfix when:**
- ✅ Critical bug in production
- ✅ Security vulnerability
- ✅ Cannot wait for next release

**Don't use hotfix for:**
- ❌ Minor issues (include in next release)
- ❌ Features (use normal release process)

### Hotfix Steps

```bash
# 1. Create from main (production)
git checkout main
git pull origin main
git checkout -b hotfix/critical-security-issue

# 2. Fix the issue
# ... make changes ...
git commit -m "fix(critical): patch security vulnerability"

# 3. Version bump (patch increment)
# build.gradle.kts: versionName = "1.0.1"
git commit -m "chore(hotfix): bump version to 1.0.1"

# 4. Merge to main
git checkout main
git merge --no-ff hotfix/critical-security-issue
git tag -a v1.0.1 -m "Hotfix: security patch"
git push origin main
git push origin v1.0.1

# 5. Back-merge to develop
git checkout develop
git merge --no-ff hotfix/critical-security-issue
git push origin develop

# 6. Delete hotfix branch
git branch -d hotfix/critical-security-issue
git push origin --delete hotfix/critical-security-issue
```

---

## Version Files Location

Update these files for each release:

### Kotlin/Android
```
build.gradle.kts (root)
├── versionCode    (increment for each build)
└── versionName    (match semantic version)

buildkonfig {
    defaultConfigs {
        stringField("VERSION_NAME", "1.0.0")
    }
}
```

### iOS
```
iosApp/eSklepios/Info.plist
├── CFBundleShortVersionString    (app version: 1.0.0)
└── CFBundleVersion               (build number: 1)
```

### Documentation
```
CHANGELOG.md       (detailed release notes)
README.md          (if version mentioned)
docs/RELEASES.md   (if exists)
```

---

## Release Checklist

Copy and use for each release:

```markdown
## Release Checklist: v1.0.0

### Preparation
- [ ] All features complete on develop
- [ ] All tests passing
- [ ] No known critical bugs
- [ ] Code review completed

### Release Branch
- [ ] release/1.0.0 created from develop
- [ ] Version numbers updated:
  - [ ] build.gradle.kts
  - [ ] Info.plist
  - [ ] BuildKonfig
- [ ] CHANGELOG.md updated
- [ ] README.md updated (if needed)

### Testing
- [ ] All tests pass on release branch
- [ ] Manual testing on Android device
- [ ] Manual testing on iOS device
- [ ] Performance testing completed
- [ ] Security testing completed

### Merge & Tag
- [ ] PR to main created
- [ ] PR approved by release manager
- [ ] PR merged to main
- [ ] Version tag created (v1.0.0)
- [ ] Tag pushed to remote

### Build & Deploy
- [ ] Android APK built
- [ ] iOS IPA built
- [ ] Submitted to Play Store
- [ ] Submitted to App Store

### Post-Release
- [ ] Back-merged to develop
- [ ] Release branch deleted
- [ ] GitHub Release created
- [ ] Announcement posted
- [ ] Monitoring active for issues

### Rollback (if needed)
- [ ] Issue identified
- [ ] Hotfix created
- [ ] New version released
```

---

## FAQ

**Q: Who can release?**
A: Only designated release manager or lead developer. Others must request approval.

**Q: What if a feature isn't ready?**
A: Don't include it. Keep it in develop for next release.

**Q: Can I release on Friday?**
A: Better to release mid-week so team can monitor over weekend.

**Q: How often should we release?**
A: Every 2-4 weeks typically. Can be more/less depending on needs.

**Q: What if a critical bug is found after release?**
A: Create hotfix/issue-name from main, fix, tag new version.

**Q: Can I skip beta releases?**
A: For major releases, beta/rc recommended. For patches, can skip.

**Q: How long do we support old versions?**
A: Typically last 2 versions. Define this in your project policy.

---

## Tools & Integration

### GitHub
- **Releases:** Create GitHub releases with tag
- **Actions:** Auto-build on tag push (optional)
- **Protection:** Release branch has protections

### Version Control
```bash
# View all tags
git tag -l

# View specific tag
git show v1.0.0

# Delete tag (if mistake)
git tag -d v1.0.0
git push origin --delete v1.0.0
```

### CI/CD
Consider automating:
- ✅ Building on tag push
- ✅ Running release tests
- ✅ Creating GitHub release
- ✅ Uploading artifacts

---

## Example Release Timeline

### Day 1: Release Prep
- [ ] 9:00 AM - Code review complete on develop
- [ ] 10:00 AM - Create release/1.0.0 branch
- [ ] 11:00 AM - Update version numbers, create PR

### Day 2-5: QA & Testing
- [ ] Manual testing on physical devices
- [ ] Bug fixes as needed
- [ ] Performance & security review

### Day 6: Release Day
- [ ] 9:00 AM - Final approval from release manager
- [ ] 10:00 AM - Merge PR to main
- [ ] 11:00 AM - Tag v1.0.0, build release artifacts
- [ ] 12:00 PM - Submit to app stores
- [ ] 3:00 PM - Announce release

### Day 7+: Monitoring
- [ ] Monitor for issues
- [ ] Prepare hotfix if needed
- [ ] Back-merge to develop
- [ ] Plan next release

---

## Related Documents

- [`GIT_WORKFLOW.md`](./GIT_WORKFLOW.md) — Branch strategy and workflow
- [`PULL_REQUEST_GUIDELINES.md`](./PULL_REQUEST_GUIDELINES.md) — PR standards
- [`CHANGELOG.md`](./CHANGELOG.md) — Release notes
- [Semantic Versioning](https://semver.org/) — Official SemVer spec

---

**Version:** 1.0  
**Status:** Active  
**Last Reviewed:** 2026-05-26
