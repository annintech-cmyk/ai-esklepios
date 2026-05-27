# KtLint Rules & Standards

## Overview

KtLint enforces Kotlin code style and formatting across the eSklepios project. Some violations can be auto-corrected (`./gradlew ktlintFormat`), while others require manual fixes. This document categorizes common violations and prevention strategies.

---

## Rule KL-1: No Wildcard Imports — Always Explicit

**Violation:** `standard:no-wildcard-imports`
**Auto-Fixable:** ❌ No — requires manual intervention
**Enforcement:** Pre-push hook will block

### Problem
Wildcard imports hide which specific classes/functions are being used, making code review harder and obscuring dependencies.

```kotlin
// ❌ WRONG — cannot determine what's imported
import androidx.compose.foundation.*
import androidx.compose.material.icons.*
import androidx.compose.runtime.*
```

### Solution
Always list explicit imports. When adding new code, be deliberate about what you import.

```kotlin
// ✅ CORRECT — dependencies are visible
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
```

### IDE Shortcut
Most IDEs can auto-expand wildcard imports:
- **Android Studio/IntelliJ:** Right-click import → "Optimize Imports" (⌃⌥O on Mac)

### Prevention
- Never write `import com.example.*`
- When copy-pasting code, manually import only what's needed
- Use IDE's "Add import" feature rather than typing wildcard imports

---

## Rule KL-2: Comment Placement in Function Arguments

**Violation:** `standard:discouraged-comment-location`
**Auto-Fixable:** ❌ No — requires manual intervention
**Enforcement:** Pre-push hook will block

### Problem
Comments placed inline with function arguments are flagged as `discouraged-comment-location`. The style guide requires comments to be on their own line above the argument they describe.

### Wrong Patterns

```kotlin
// ❌ Inline comment at end of argument
Icon(
    imageVector = leadingIcon,
    contentDescription = null, // a11y: decorative — labelled by adjacent Text
    tint = Primary,
)

// ❌ Inline comment with parameter list
fun PractitionerCard(
    practitioner: Practitioner, // ViewModel receives this
    onBook: (slotId: String) -> Unit, // Callback for booking
) { }
```

### Correct Patterns

**Option 1: Comment above the argument (preferred for a11y labels)**
```kotlin
// ✅ Comment on separate line above argument
Icon(
    imageVector = leadingIcon,
    // a11y: decorative — labelled by adjacent Text
    contentDescription = null,
    tint = Primary,
)
```

**Option 2: Explicit documentation comment (for complex arguments)**
```kotlin
// ✅ Explicit comment in function signature
fun PractitionerCard(
    // The practitioner data to display
    practitioner: Practitioner,
    // Callback triggered when user clicks "Book" button
    onBook: (slotId: String) -> Unit,
) { }
```

**Option 3: Move complex logic out (best for maintainability)**
```kotlin
// ✅ Extract to variable instead of inline comment
val decorativeIconContentDescription = null // a11y: decorative — labelled by adjacent Text

Icon(
    imageVector = leadingIcon,
    contentDescription = decorativeIconContentDescription,
    tint = Primary,
)
```

### Accessibility Comment Standard
For a11y comments, use this format consistently:

```kotlin
// Preferred format:
// a11y: decorative — labelled by adjacent Text

// Alternative if comment is longer:
// a11y: Decorative icon. The label is provided by the adjacent Text element above.
```

### Prevention
- When writing Icon/Image composables, place a11y comments on their own line **above** `contentDescription`
- In function parameters, avoid trailing comments — if needed, use block-level comments before parameters
- Use IDE formatting: "Reformat Code" (⌥⌘L) sometimes helps, but manual fix is often required

---

## Rule KL-3: Composable Function Naming (PascalCase)

**Violation:** `standard:function-naming`  
**Status:** ⚠️ False Positive — KtLint incorrectly flags correct Compose conventions
**Auto-Fixable:** ❌ No — and should NOT be fixed
**Enforcement:** Pre-push hook flags this, but DO NOT FIX

### Context
Jetpack Compose requires all `@Composable` functions to use `PascalCase` (like class names) because they are semantically similar to class constructors. KtLint's `function-naming` rule is unaware of this and flags `PascalCase` composables as violations.

### What You'll See
```
[ERROR] Function name should start with a lowercase letter (except factory methods) 
        and use camel case (standard:function-naming)
  at HomeScreen.kt:29:5
```

### Correct Implementation (DO NOT CHANGE)
```kotlin
// ✅ CORRECT — Composable functions MUST use PascalCase
@Composable
fun HomeScreen(navController: NavController) { }

@Composable
fun PractitionerCard(practitioner: Practitioner) { }

@Composable
private fun NewPatientsToggle(checked: Boolean) { }

// ❌ WRONG — this would break Compose conventions
@Composable
fun homeScreen(navController: NavController) { } // Not a valid Composable pattern
```

### Why It Matters
- Composables are composable scope builders — semantically similar to class constructors
- Calling a composable function looks like instantiating a class: `HomeScreen()` vs `homeScreen()`
- Lowercase would suggest a regular utility function, confusing readers

### Handling the False Positive
If KtLint blocks on this:
1. **Do NOT rename the function to camelCase**
2. The pre-push hook is aware this is a false positive
3. Document in your PR that Composable naming follows Compose conventions, not standard Kotlin function rules

---

## Rule KL-4: Formatting Best Practices

### Before Committing
Run auto-format to catch fixable issues:
```bash
./gradlew ktlintFormat
```

This fixes:
- Indentation
- Spacing around operators
- Line length (within reason)
- Trailing commas
- Most import organization issues (except wildcards)

### What Won't Auto-Fix
These require manual intervention:
- Wildcard imports → must expand manually
- Comment placement in arguments → must move above argument
- Function naming (if incorrectly flagged on Composables) → should not fix

### Handling Pre-Push Failures
If `./gradlew :androidApp:ktlintMainSourceSetFormat` fails:

1. **Check the error report:**
   ```bash
   cat androidApp/build/reports/ktlint/ktlintMainSourceSetFormat/ktlintMainSourceSetFormat.txt | grep "discouraged-comment\|no-wildcard"
   ```

2. **Fix manually if flagged:**
   - Wildcard imports: expand to explicit imports
   - Comment placement: move comments above arguments

3. **Re-run format:**
   ```bash
   ./gradlew ktlintFormat
   ```

4. **Commit and re-push:**
   ```bash
   git add .
   git commit -m "style(kotlin): fix KtLint violations"
   git push
   ```

---

## Rule KL-5: Import Organization in New Files

When creating new files:

1. **Standard library imports first**
   ```kotlin
   import kotlin.math.abs
   ```

2. **Third-party imports (Compose, Koin, etc.)**
   ```kotlin
   import androidx.compose.foundation.background
   import androidx.compose.runtime.Composable
   ```

3. **Project imports (shared, domain, data layers)**
   ```kotlin
   import lu.esklepios.app.domain.model.Practitioner
   import lu.esklepios.app.presentation.viewmodel.HomeViewModel
   ```

4. **Android/platform-specific imports**
   ```kotlin
   import android.content.Context
   ```

Example (full ordering):
```kotlin
package lu.esklepios.app.view.dashboard.home

// Standard library
import kotlin.collections.emptyList

// Third-party
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle

// Project
import lu.esklepios.app.R
import lu.esklepios.app.core.navigation.NavDestination
import lu.esklepios.app.core.ui.components.*
import lu.esklepios.app.core.ui.theme.*
import lu.esklepios.app.presentation.viewmodel.HomeViewModel

// No wildcard imports at the end
```

**Never mix approaches:**
- ❌ `import androidx.compose.foundation.*` mixed with explicit imports
- ✅ All explicit or all organized by section

---

## Troubleshooting

### "KtLint found code style violations"
1. Run `./gradlew ktlintFormat` to auto-fix what can be fixed
2. Check the report for `discouraged-comment-location` or `no-wildcard-imports`
3. Manually fix those two violation types
4. Re-run format and commit

### "Composable function name flagged as wrong"
- This is a known false positive
- Do NOT rename the function — Composables must use PascalCase
- The pre-push hook is configured to allow this

### ktlintFormat exits with error
- Some violations cannot be auto-fixed
- Read the error message carefully
- Most commonly: wildcard imports or comment placement
- Fix manually and re-run

---

## Quick Reference

| Violation | Auto-Fixable | Action |
|-----------|--------------|--------|
| Wildcard imports | ❌ | Expand to explicit imports manually |
| Comment placement in arguments | ❌ | Move comment above argument on separate line |
| Composable PascalCase | ⚠️ False positive | Do NOT fix — it's correct |
| Indentation | ✅ | `./gradlew ktlintFormat` |
| Spacing | ✅ | `./gradlew ktlintFormat` |
| Line length | ✅ | `./gradlew ktlintFormat` |
| Trailing commas | ✅ | `./gradlew ktlintFormat` |

---

## Related Rules
- **Rule A-2:** No platform code in commonMain (applies to imports)
- **Rule UI-14:** No raw primitives in screen files (relates to Composable function naming)
- **Rule A11Y-1:** Accessibility labels must be explicit (a11y comment placement)