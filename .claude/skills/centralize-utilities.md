# Skill: Centralize Utilities

Extracts duplicated validation, formatting, and option-list logic from screens and ViewModels into shared utility files. Applies Rules A-12, A-13, A-14, A-15.

## Usage
```
/centralize-utilities [scope]
```
Scope examples: `validation`, `formatters`, `options`, `all`

---

## Step 1 — Find Duplicated Logic

### Validation (inline logic in screens or ViewModels)
```bash
# Kotlin — inline email/password checks
grep -rn "contains(\"@\")\|length < 8\|isBlank()\|isEmpty()" \
  shared/src/commonMain/kotlin/lu/esklepios/app/presentation \
  androidApp/src/main/kotlin --include="*.kt"

# Swift — inline validation
grep -rn "contains(\"@\")\|count < 8\|isEmpty\|isBlank" \
  iosApp/eSklepios/Features --include="*.swift"
```

### Formatting (inline display logic in screens)
```bash
# Kotlin
grep -rn "prefix\|substring\|padStart\|format\|mask" \
  androidApp/src/main/kotlin/lu/esklepios/app/view --include="*.kt"

# Swift — masking, initials, display formatting
grep -rn "prefix\|suffix\|\.count >" iosApp/eSklepios/Features --include="*.swift"
```

### Hardcoded option lists
```bash
# Kotlin — local val/var with listOf() in screens
grep -rn "val.*=.*listOf\|private val.*=.*listOf" \
  androidApp/src/main/kotlin/lu/esklepios/app/view --include="*.kt"

# Swift — local let arrays inside View body or computed properties
grep -rn "let.*=.*\[(" iosApp/eSklepios/Features --include="*.swift"
```

---

## Step 2 — Create Shared Util Files

### Validators
File: `shared/src/commonMain/kotlin/lu/esklepios/app/util/Validators.kt`
```kotlin
package lu.esklepios.app.util

fun isValidEmail(email: String): Boolean =
    email.contains("@") && email.contains(".") && email.indexOf("@") < email.lastIndexOf(".")

fun isValidPhone(phone: String): Boolean =
    phone.replace(Regex("[\\s\\-+()]"), "").length in 7..15

fun isValidCns(cns: String): Boolean =
    cns.replace(" ", "").length == 13

fun passwordStrength(password: String): Int {
    if (password.isEmpty()) return 0
    val hasMinLength = password.length >= 12
    val hasMixedCase = password.any { it.isUppercase() } && password.any { it.isLowercase() }
    val hasNumAndSymbol = password.any { it.isDigit() } && password.any { !it.isLetter() && !it.isDigit() }
    if (hasMinLength && hasMixedCase && hasNumAndSymbol) return 4
    if (password.length >= 10 && (password.any { it.isDigit() } || password.any { it.isUppercase() })) return 3
    if (password.length >= 8) return 2
    return 1
}
```

### Formatters
File: `shared/src/commonMain/kotlin/lu/esklepios/app/util/Formatters.kt`
```kotlin
package lu.esklepios.app.util

fun maskCns(cns: String): String =
    if (cns.length > 9) "${cns.take(9)} ••••" else cns

fun formatPhone(phone: String): String = phone.trim()
```

### Options
File: `shared/src/commonMain/kotlin/lu/esklepios/app/util/Options.kt`
```kotlin
package lu.esklepios.app.util

data class LanguageOption(val code: String, val displayLabel: String)

val LANGUAGE_OPTIONS = listOf(
    LanguageOption("fr", "🇫🇷  French"),
    LanguageOption("en", "🇬🇧  English"),
    LanguageOption("de", "🇩🇪  German"),
    LanguageOption("lb", "🇱🇺  Luxembourgish")
)

val GENDER_OPTIONS = listOf("Male", "Female", "Other")

val DATE_FILTER_OPTIONS = listOf("All", "Today", "Within 3 Days")
```

---

## Step 3 — Write Tests First
File: `shared/src/commonTest/kotlin/lu/esklepios/app/ValidatorsTest.kt`
```kotlin
class ValidatorsTest {
    @Test fun `valid email passes`() = assertTrue(isValidEmail("a@b.com"))
    @Test fun `email without at fails`() = assertFalse(isValidEmail("notanemail"))
    @Test fun `strong password scores 4`() = assertEquals(4, passwordStrength("Abc123!defgh"))
    @Test fun `empty password scores 0`() = assertEquals(0, passwordStrength(""))
    @Test fun `maskCns masks after 9 chars`() = assertEquals("123456789 ••••", maskCns("1234567891234"))
}
```

---

## Step 4 — Replace Call Sites

### Kotlin ViewModels
Before:
```kotlin
private fun isValidEmail(email: String): Boolean =
    email.contains("@") && email.contains(".")
```
After:
```kotlin
import lu.esklepios.app.util.isValidEmail
// (remove private function, call imported one)
```

### Swift Views
Before:
```swift
private var maskedCns: String {
    guard let cns = viewModel.uiState.user?.cnsNumber, !cns.isEmpty else { return "—" }
    return cns.count > 9 ? "\(cns.prefix(9)) ••••" : cns
}
```
After:
```swift
// In a Swift shim (iosApp/.../Core/Utils/Formatters.swift)
import shared
func maskCns(_ cns: String) -> String { FormattersKt.maskCns(cns: cns) }
// In View
private var maskedCns: String {
    guard let cns = viewModel.uiState.user?.cnsNumber, !cns.isEmpty else { return "—" }
    return maskCns(cns)
}
```

> **Note:** Kotlin util functions in `commonMain` are accessible in Swift as top-level functions via the shared framework using their `Kt`-suffixed file name (e.g., `FormattersKt.maskCns(...)`). For ergonomics, create thin Swift wrappers in `Core/Utils/`.

---

## Step 5 — Verify
```bash
# No inline validation remains in ViewModels
grep -rn "contains(\"@\")" shared/src/commonMain/kotlin/lu/esklepios/app/presentation --include="*.kt"
# expect 0

# No hardcoded option lists in screens
grep -rn "\"🇫🇷\|\"French\"\|\"German\"\|\"Luxembourgish\"" \
  androidApp/src/main/kotlin/lu/esklepios/app/view --include="*.kt"
# expect 0

grep -rn "\"🇫🇷\|French\|German\|Luxembourgish" \
  iosApp/eSklepios/Features --include="*.swift"
# expect 0
```

---

## Step 6 — Update Rules and Skills
After the refactor, if a new reusable pattern was discovered:
- Add it as a **Rule** if it's a permanent architectural constraint (Rule A-16).
- Add it as a **Skill** step if it's a repeatable process.

---

## Checklist
- [ ] Inline validation removed from ViewModels — calls shared `Validators.kt`
- [ ] Inline formatting removed from screens — calls shared `Formatters.kt`
- [ ] Option lists removed from screens — reference `Options.kt`
- [ ] Tests written for all shared utility functions
- [ ] iOS shim wrappers created in `Core/Utils/` for Kotlin utilities
- [ ] Grep verifies 0 violations remain
- [ ] Rule/skill files updated if new pattern was promoted
