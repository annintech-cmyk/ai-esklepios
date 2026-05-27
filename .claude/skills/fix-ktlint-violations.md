# Skill: Fix KtLint Violations

## Overview
KtLint violations can be categorized into two types: **auto-fixable** (use `./gradlew ktlintFormat`) and **manual** (require code changes). This skill provides a systematic approach to identify, categorize, and fix violations.

## When to Use This Skill
- Pre-push hook blocks due to KtLint violations
- You've added new code and want to ensure KtLint compliance
- You're refactoring code and need to maintain style standards
- You want to prevent KtLint violations in new files

## Prerequisites
- Project is built and compiles
- Git is clean (no uncommitted changes you want to keep)
- You can run Gradle commands

## Workflow

### Step 1: Generate KtLint Report

Run the format command to identify violations:
```bash
./gradlew ktlintFormat --info 2>&1 | tee ktlint-report.txt
```

This generates a detailed report in:
```
androidApp/build/reports/ktlint/ktlintMainSourceSetFormat/ktlintMainSourceSetFormat.txt
```

### Step 2: Categorize Violations

Read the report and categorize by type:

```bash
# Extract manually-fixable violations
grep "no-wildcard-imports\|discouraged-comment-location" ktlint-report.txt | head -20

# Extract auto-fixable violations (everything else)
grep -v "no-wildcard-imports\|discouraged-comment-location" ktlint-report.txt | head -20
```

### Step 3: Auto-Fix Fixable Violations

```bash
./gradlew ktlintFormat --quiet
```

This fixes:
- Indentation
- Spacing around operators
- Line length
- Trailing commas
- Most import ordering (except wildcard imports)

### Step 4: Manually Fix Non-Fixable Violations

#### For Wildcard Imports
Find all wildcard imports in the error report:
```bash
grep "no-wildcard-imports" androidApp/build/reports/ktlint/ktlintMainSourceSetFormat/ktlintMainSourceSetFormat.txt
```

For each file listed, expand the wildcard imports:

**Before:**
```kotlin
import androidx.compose.foundation.*
import androidx.compose.material.icons.*
import androidx.compose.runtime.*
```

**After (use IDE's optimize imports or expand manually):**
```kotlin
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
```

#### For Comment Placement
Find all comment placement issues:
```bash
grep "discouraged-comment-location" androidApp/build/reports/ktlint/ktlintMainSourceSetFormat/ktlintMainSourceSetFormat.txt
```

For each violation, move the comment to its own line above the argument:

**Before:**
```kotlin
Icon(
    imageVector = Icons.Default.Person,
    contentDescription = null, // a11y: decorative — labelled by adjacent Text
    tint = Primary,
)
```

**After:**
```kotlin
Icon(
    imageVector = Icons.Default.Person,
    // a11y: decorative — labelled by adjacent Text
    contentDescription = null,
    tint = Primary,
)
```

#### For Composable Function Naming (Do NOT Fix)
If you see `standard:function-naming` errors on `@Composable` functions with PascalCase names:
- **This is a false positive** — Composable functions MUST use PascalCase
- Do NOT rename the function
- Document in your PR if needed that it's following Compose conventions

### Step 5: Verify All Violations Are Fixed

Run the check again:
```bash
./gradlew :androidApp:ktlintMainSourceSetCheck --quiet && echo "✅ All KtLint violations fixed" || echo "❌ Violations remain"
```

### Step 6: Commit Your Changes

```bash
git add -A
git commit -m "style(kotlin): fix KtLint violations

- Expand wildcard imports to explicit imports
- Move inline comments above function arguments
- Auto-format code (indentation, spacing, trailing commas)

Co-Authored-By: Claude Sonnet 4.6 <noreply@anthropic.com>"
```

### Step 7: Re-Run Pre-Push Hook

```bash
bash scripts/pre-push.sh
```

If it passes:
```bash
git push
```

If it fails, return to Step 1 and identify new violations.

## Prevention Checklist

When writing new code, avoid these violations upfront:

- [ ] No wildcard imports — use explicit imports for all used classes/functions
- [ ] Comments in function arguments on their own line, above the argument
- [ ] Composable functions use PascalCase (e.g., `HomeScreen`, not `homeScreen`)
- [ ] Run `./gradlew ktlintFormat` before committing
- [ ] Check IDE's import organization feature (Ctrl+Alt+O / Cmd+Option+O)

## Common Patterns to Avoid

### Wildcard Imports
```kotlin
// ❌ AVOID
import androidx.compose.foundation.*
import androidx.compose.material.icons.*
import androidx.compose.runtime.*

// ✅ PREFER
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
```

### Inline Comments in Arguments
```kotlin
// ❌ AVOID
Button(
    onClick = { viewModel.submit() }, // Submit the form
    modifier = Modifier.fillMaxWidth(), // Take full width
) { }

// ✅ PREFER
Button(
    // Submit the form
    onClick = { viewModel.submit() },
    // Take full width
    modifier = Modifier.fillMaxWidth(),
) { }
```

## Troubleshooting

### "KtLint found code style violations. Please see the following reports"

1. Check the report file:
   ```bash
   cat androidApp/build/reports/ktlint/ktlintMainSourceSetFormat/ktlintMainSourceSetFormat.txt
   ```

2. Identify violation types:
   - `no-wildcard-imports` → expand manually
   - `discouraged-comment-location` → move comments above arguments
   - Others → should be fixed by `ktlintFormat`

3. Run format again:
   ```bash
   ./gradlew ktlintFormat
   ```

### "All violations remaining are non-fixable"

This means the report only has `no-wildcard-imports` and `discouraged-comment-location` violations. You must fix these manually by editing the source files directly.

### "Pre-push still fails after fixing"

Run the full pre-push check locally:
```bash
bash scripts/pre-push.sh 2>&1 | grep -A 5 "KtLint"
```

This ensures you've caught all violations before pushing.

## Related Rules & Documentation
- **`.claude/rules/ktlint-rules.md`** — Detailed rules for each violation type
- **`.claude/CLAUDE.md` Pitfall #15** — Quick reference for KtLint issues
- **Git Hooks** — See `.claude/rules/git-hooks-rules.md` for pre-push enforcement