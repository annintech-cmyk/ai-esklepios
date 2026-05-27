# Localization Rules

## Rule L-1: No Hardcoded User-Facing Strings

Every string visible to the user must come from the centralized localization system — never hardcoded inline. This applies to all screens, components, dialogs, snackbars, and tooltips.

**Android (Compose)**
```kotlin
// CORRECT
Text(stringResource(R.string.home_greeting))
snackbarHostState.showSnackbar(stringResource(R.string.edit_saved_snackbar))

// FORBIDDEN
Text("Hello")
snackbarHostState.showSnackbar("Profile saved!")
```

**iOS (SwiftUI)**
```swift
// CORRECT
Text(String(localized: "home_greeting"))
AppToolbar(title: String(localized: "screen_change_email"), ...)

// FORBIDDEN
Text("Hello")
AppToolbar(title: "Change Email", ...)
```

**Acceptable exceptions** (intentionally not localized):
- Language names shown in their native script: "English", "Français", "Deutsch", "Lëtzebuergesch" — always displayed in the native language for user self-identification
- Placeholder/example data in form fields that is not language-specific (e.g., phone number formats like "+352 000 000 000")
- Debug-only labels not shipped to users

## Rule L-2: Strings Live in twine.txt, Generated Files Are Derived

`strings/twine.txt` is the single source of truth for all localized strings. Do not edit `strings.xml` or `Localizable.strings` directly — regenerate them with `make strings`.

```
# Add to strings/twine.txt
[new_section.key_name]
en = English text
fr = Texte en français
de = Deutscher Text
lb = Lëtzebuergeschen Text

# Then run:
make strings
```

## Rule L-3: All Four Languages Required

Every key in `strings/twine.txt` must have values for all four languages: `en`, `fr`, `de`, `lb`. A key with an empty value for any language is a violation of Rule PP-7.

## Rule L-4: Use Consistent Key Naming

String keys follow the pattern `section_subsection_name` in `snake_case`. See `strings/twine.txt` for existing section prefixes:

| Prefix | Screen / area |
|--------|--------------|
| `landing_` | LandingScreen / LandingView |
| `home_` | HomeScreen / HomeView |
| `appointments_` | MyAppointmentsScreen / MyAppointmentsView |
| `profile_` | ProfileScreen / ProfileView |
| `edit_` | EditProfileScreen / EditProfileView |
| `change_email_` | ChangeEmailScreen / ChangeEmailView |
| `change_password_` | ChangePasswordScreen / ChangePasswordView |
| `drawer_` | AppDrawer / menu sheet |
| `nav_` | Bottom navigation tabs |
| `action_` | Reusable action labels (Save, Cancel, OK…) |
| `label_` | Reusable field labels |
| `status_` | Appointment status badges |
| `gender_` | Gender display values |
| `error_` | Error messages |
| `cd_` | Accessibility content descriptions |
| `screen_` | Screen / toolbar titles |

## Rule L-5: Resolve String Resources Before LaunchedEffect (Android)

`stringResource()` must be called in a `@Composable` scope, not inside `LaunchedEffect` or coroutine blocks. Resolve strings as `val` before any `LaunchedEffect` that uses them.

```kotlin
// CORRECT
val successMessage = stringResource(R.string.change_email_success)
LaunchedEffect(uiState.isSuccess) {
    if (uiState.isSuccess) snackbarHostState.showSnackbar(successMessage)
}

// FORBIDDEN
LaunchedEffect(uiState.isSuccess) {
    if (uiState.isSuccess) snackbarHostState.showSnackbar(stringResource(R.string.change_email_success))
}
```

## Rule L-6: Separate Display Labels from Stored Values

When a UI element drives both a display label and a stored/API value (e.g., filter chips, gender chips), the stored value must stay language-neutral and the display label must be localized.

```kotlin
// CORRECT — key is English constant, label is localized
val genderOptions = listOf(
    "Male" to R.string.edit_gender_man,
    "Female" to R.string.edit_gender_woman,
    "Other" to R.string.edit_gender_other
)
genderOptions.forEach { (value, labelRes) ->
    FilterChip(
        selected = uiState.gender == value,
        onClick = { viewModel.updateField(ProfileField.GENDER, value) },
        label = { Text(stringResource(labelRes)) }
    )
}

// FORBIDDEN — stored and displayed value are the same hardcoded string
listOf("Male", "Female", "Other").forEach { g ->
    FilterChip(selected = uiState.gender == g, onClick = { ... }, label = { Text(g) })
}
```

## Rule L-7: Use Locale-Aware Date Formatting

Do not hardcode month name arrays. Use platform APIs that respect the device locale.

**Android:**
```kotlin
import java.time.Month
import java.time.format.TextStyle
import java.util.Locale

val monthName = Month.of(monthNumber).getDisplayName(TextStyle.FULL, Locale.getDefault())
```

**iOS:**
```swift
let formatter = DateFormatter()
formatter.dateStyle = .long
formatter.locale = Locale.current
let display = formatter.string(from: date)
```
