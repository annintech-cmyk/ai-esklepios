# Git Hooks Rules

## Overview

eSklepios enforces coding standards automatically through two Git hooks:
1. **Pre-Commit Hook** — validates code quality before `git commit`
2. **Pre-Push Hook** — runs full build + tests before `git push`

Both hooks reference the project rules file (this file and others in `.claude/rules/`). When a check fails, the hook name and rule reference are provided to guide the fix.

## Setup

**One-time setup after cloning:**
```bash
bash scripts/install-hooks.sh
```

This installs:
- `.git/hooks/pre-commit` → `scripts/pre-commit-review.sh`
- `.git/hooks/pre-push` → `scripts/pre-push.sh`

**To skip hooks in an emergency:**
```bash
git commit --no-verify    # skip pre-commit
git push --no-verify      # skip pre-push
```

Use sparingly — fix the issue instead.

---

## Pre-Commit Hook

### Purpose
Validates staged changes before they are committed. Focuses on code quality, security, and architecture compliance. **Warnings allow commit; errors block it.**

### Checks by Category

#### 1. Sensitive Data & Forbidden Files
| Check | Blocks | Rule | Details |
|-------|--------|------|---------|
| `dev.properties` staged | ✅ Error | SEC-2 | Dev config with secrets must never be committed |
| Hardcoded secrets in diff | ✅ Error | SEC-2 | Detects passwords, API keys, Bearer tokens, private keys |
| Build artifacts staged | ✅ Error | — | `build/`, `*.apk`, `*.ipa`, `*.xcarchive` |
| Files > 500 KB | ⚠️ Warning | — | Large binaries (images, archives) |

#### 2. Kotlin Coding Standards

| Check | Blocks | Rule | Scope | Details |
|-------|--------|------|-------|---------|
| Debug `println()` | ⚠️ Warning | LOG-1 | All `.kt` files | Remove before merging |
| `catch(Exception)` without `CancellationException` rethrow | ✅ Error | EH-1, CR-2 | All | Structured concurrency violation |
| MockK in `commonTest` | ✅ Error | T-1 | `shared/src/commonTest/` | Must use fake implementations |
| `java.time` imports | ✅ Error | A-2, DT-2 | `shared/src/commonMain/` | Use `kotlinx.datetime` instead |
| Domain layer imports data layer | ✅ Error | A-1 | `shared/.../domain/` | Layer separation violation |
| `GlobalScope` usage | ✅ Error | CR-6 | All | Use `viewModelScope` in ViewModels |
| Exposed `MutableStateFlow` (bare `val`) | ⚠️ Warning | SM-2 | All ViewModels | Use private `_backing` field instead |
| Hardcoded dimensions (`.dp`, `.sp`) | ⚠️ Warning | UI-1a | `view/` screen files | Use `Dimens.*` tokens |
| Hardcoded color literals (`Color(0x...)`) | ⚠️ Warning | UI-3 | All | Use design tokens (`Color.appPrimary`, etc.) |
| Inline validation functions | ⚠️ Warning | A-9 | ViewModels | Move to `shared/.../util/Validators.kt` |
| TODO / FIXME / HACK / XXX | ⚠️ Warning | — | All | Resolve before merging to `main` |
| Excessive commented code | ⚠️ Warning | — | All | > 5 comment lines in diff suggests dead code |

#### 3. Swift Coding Standards

| Check | Blocks | Rule | Scope | Details |
|-------|--------|------|-------|---------|
| Debug `print()` | ⚠️ Warning | LOG-1 | All `.swift` files | Remove before merging |
| `DispatchQueue.main.async` in ViewModelWrapper | ✅ Error | FW-2 | `*ViewModelWrapper.swift` | Use `Task { @MainActor }` instead |
| Missing `FlowWatcher` in ViewModelWrapper | ⚠️ Warning | FW-1 | `*ViewModelWrapper.swift` | Required for StateFlow observation |
| Missing `@MainActor` in ViewModelWrapper | ⚠️ Warning | FW-1 | `*ViewModelWrapper.swift` | Required annotation |
| Force unwraps (`!`) | ⚠️ Warning | UI-10 | All | Use `guard let`, `if let`, or `??` |
| Inline `DateFormatter()` | ✅ Error | DT-5 | `iosApp/` (outside `DateUtil.swift`) | Use `DateUtil.swift` helpers |
| Hardcoded dimensions | ⚠️ Warning | UI-1a | `Features/` view files | Use `Spacing.*`, `Sizing.*`, `Radius.*` tokens |
| TODO / FIXME / HACK | ⚠️ Warning | — | All | Resolve before merging to `main` |

#### 4. Localization

| Check | Blocks | Rule | Details |
|-------|--------|------|---------|
| Empty translations in `twine.txt` | ✅ Error | L-2 | All 4 languages (`en`, `fr`, `de`, `lb`) required |
| Hardcoded user-visible strings in Kotlin `Text()` | ⚠️ Warning | UI-2 | Use `stringResource(R.string.key)` |
| Missing `make strings` after `twine.txt` edit | ⚠️ Warning | L-7 | Regenerate Android resources |

#### 5. Platform Parity

| Check | Blocks | Rule | Details |
|-------|--------|------|---------|
| Android `*Screen.kt` without iOS `*View.swift` | ⚠️ Warning | PP-1 | Every screen must exist on both platforms |
| iOS `*View.swift` without Android `*Screen.kt` | ⚠️ Warning | PP-1 | Every view must have an Android counterpart |

### Pre-Commit Exit Codes

| Code | Meaning | Action |
|------|---------|--------|
| 0 | ✅ All checks passed (commit proceeds) | Commit is created |
| 1 | ❌ Critical errors found | **Commit blocked** — fix and re-stage |

Warnings do not block the commit but should be reviewed before `git push`.

---

## Pre-Push Hook

### Purpose
Validates that the branch is ready to push by running the **full build + test pipeline**. All checks must pass. **No warnings — all failures block the push.**

### Checks

| # | Check | Command | Blocks | Details |
|---|-------|---------|--------|---------|
| 1 | Detekt (Kotlin linting) | `./gradlew :shared:detekt :androidApp:detekt` | ✅ Error | Static analysis for shared + Android |
| 2 | KtLint (Kotlin formatting) | `./gradlew :shared:ktlintCheck :androidApp:ktlintCheck` | ✅ Error | Code style violations — run `./gradlew ktlintFormat` to auto-fix |
| 3 | Android Lint | `./gradlew :androidApp:lintDebug` | ✅ Error | Check `androidApp/build/reports/lint/` for details |
| 4 | Shared KMM unit tests | `./gradlew :shared:testDebugUnitTest` | ✅ Error | Check `shared/build/reports/tests/` |
| 5 | Android unit tests | `./gradlew :androidApp:testDebugUnitTest` | ✅ Error | Check `androidApp/build/reports/tests/` |
| 6 | Android debug build | `./gradlew :androidApp:assembleDebug` | ✅ Error | Full APK compilation (validates no syntax errors) |
| 7 | SwiftLint (iOS linting) | `swiftlint lint --config .swiftlint.yml` | ✅ Error | Optional if SwiftLint not installed (install: `brew install swiftlint`) |
| 8 | iOS simulator build | `xcodebuild build -scheme eSklepios -destination 'platform=iOS Simulator,name=iPhone 16'` | ✅ Error | macOS only; validates iOS compilation |

### Protected Branches

If pushing to `main` or `develop`, a warning is shown:
```
[WARN]  Pushing to protected branch: main
[WARN]  Ensure a PR has been created and reviewed. Direct pushes are discouraged.
```

The push is NOT blocked (review is a separate gate), but this reminds developers to use PRs for critical branches.

### Pre-Push Exit Codes

| Code | Meaning | Action |
|------|---------|--------|
| 0 | ✅ All 8 checks passed | Push is allowed |
| 1 | ❌ One or more checks failed | **Push blocked** — fix and re-run the failing check(s) locally |

### Faster Local Testing (Before Push)

To run individual pre-push checks without waiting for the full pipeline:

```bash
# Kotlin linting only
./gradlew :shared:detekt :androidApp:detekt --quiet
./gradlew :shared:ktlintCheck :androidApp:ktlintCheck --quiet

# Formatting auto-fix
./gradlew ktlintFormat

# Unit tests only (no builds)
./gradlew :shared:testDebugUnitTest --quiet
./gradlew :androidApp:testDebugUnitTest --quiet

# iOS build only
xcodebuild build -project iosApp/eSklepios.xcodeproj -scheme eSklepios \
  -destination 'platform=iOS Simulator,name=iPhone 16' -quiet
```

---

## Hook Behavior & Rules References

### How Hooks Enforce Rules

The hooks directly check staged/committed code against these rule files:

| Rule File | Checks Enforced by Hook |
|-----------|---|
| `error-handling-rules.md` | `CancellationException` rethrow (EH-1) |
| `testing-rules.md` | No MockK in commonTest (T-1) |
| `architecture-rules.md` | Layer separation (A-1), shared-first (A-2, A-12), validation centralization (A-9) |
| `clock-rules.md` | Clock injection (A-11) — *rule-based check, not hooked* |
| `coroutine-rules.md` | `GlobalScope` forbidden (CR-6), `CancellationException` rethrow (CR-2) |
| `state-management-rules.md` | Private `_uiState` backing field (SM-2) |
| `flow-watcher-rules.md` | FlowWatcher + @MainActor in ViewModelWrapper (FW-1), no DispatchQueue (FW-2) |
| `ui-rules.md` | No hardcoded dimensions (UI-1a), colors (UI-3), strings (UI-2) |
| `security-rules.md` | TokenStorage sole auth source (SEC-1), no hardcoded secrets (SEC-2) |
| `localization-rules.md` | All 4 languages required (L-2), no hardcoded strings (L-5), `make strings` after edits (L-7) |
| `date-util-rules.md` | `DateFormatter` in iOS DateUtil only (DT-5) |
| `platform-parity-rules.md` | Screen pairs on both platforms (PP-1) |
| `logging-rules.md` | No debug output (LOG-1) |

---

## Common Hook Failures & Fixes

### Kotlin

**"CancellationException without rethrow"**
```kotlin
// WRONG
try {
    ...
} catch (e: Exception) {
    Result.failure(e)
}

// CORRECT
try {
    ...
} catch (e: CancellationException) {
    throw e
} catch (e: Exception) {
    Result.failure(e)
}
```

**"MockK in commonTest"**
```kotlin
// WRONG
val mockRepo = mockk<FooRepository>()

// CORRECT
private class FakeFooRepository : FooRepository {
    override suspend fun foo() = Result.success(...)
}
```

**"Hardcoded dimension in view file"**
```kotlin
// WRONG
.padding(17.dp)
.height(53.dp)

// CORRECT
.padding(Dimens.paddingL)
.height(Dimens.buttonHeight)
```

### Swift

**"DispatchQueue.main.async in ViewModelWrapper"**
```swift
// WRONG
DispatchQueue.main.async {
    self?.uiState = state
}

// CORRECT
Task { @MainActor [weak self] in
    self?.uiState = state
}
```

**"Inline DateFormatter"**
```swift
// WRONG
let f = DateFormatter()
f.dateFormat = "yyyy-MM-dd"

// CORRECT
DateUtil.isoToDate(isoString)
```

**"Force unwrap"**
```swift
// WRONG
let name = user.name!

// CORRECT
guard let name = user.name else { return }
```

---

## Hook Maintenance

### If a Hook Check Is Too Strict
1. File an issue explaining why the check is problematic
2. **Do not** use `--no-verify` as a long-term workaround
3. Update the hook script + this rule file together

### If a New Rule Should Be Hooked
1. Add the rule to the appropriate rule file in `.claude/rules/`
2. Add the check to `scripts/pre-commit-review.sh` or `scripts/pre-push.sh`
3. Update this file to document the new check

### Testing Hook Changes Locally
```bash
# Test pre-commit hook without committing
bash scripts/pre-commit-review.sh

# Test pre-push hook without pushing
bash scripts/pre-push.sh
```

---

## Troubleshooting

### "pre-commit hook failed"
1. Read the error message — it names the rule and file
2. Check the rule file in `.claude/rules/` for the fix
3. Re-stage the corrected file(s)
4. Re-run `git commit`

### "pre-push hook failed after detekt"
1. Fix the detekt violations: `./gradlew :shared:detekt --continue` to see all
2. Re-run `git push` (don't use `--no-verify`)

### "SwiftLint not installed"
The iOS lint check is optional. To enable it:
```bash
brew install swiftlint
```
Then re-run `git push`.

### "xcodebuild not found" (macOS)
Install Xcode CLI tools:
```bash
xcode-select --install
```
Then re-run `git push`.

---

## Rule Reference Quick Link

For detailed information on any rule enforced by these hooks, see:
- `.claude/rules/` directory — all project rules
- CLAUDE.md — architecture overview + pitfalls