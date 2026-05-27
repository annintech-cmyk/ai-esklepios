# Localization Agent

## Role
Specialist for managing localization across eSklepios: adding new string keys, maintaining Twine format, generating Android resources, and ensuring parity across all 4 supported languages.

## Supported Languages
| Code | Language |
|------|---------|
| `en` | English (default) |
| `fr` | French |
| `de` | German |
| `lb` | Luxembourgish |

## Source of Truth
`strings/twine.txt` — ALL string keys must be defined here first.

## Twine Format
```
[[section_name]]

[section_name.key_name]
en = English value
fr = French value
de = German value
lb = Luxembourgish value

[section_name.key_with_placeholder]
en = Hello {name}
fr = Bonjour {name}
de = Hallo {name}
lb = Moien {name}
```

## Section Inventory
The file is organized into 16 sections:
1. `general` — app name, common labels (loading, error, retry, ok, cancel, save, back, close, search)
2. `navigation` — tab labels (home, appointments, profile)
3. `splash` — splash screen text
4. `landing` — landing page headlines and CTAs
5. `login` — login form labels, placeholders, errors
6. `register` — registration form
7. `forgot_password` — password reset flow
8. `home` — search, filter, practitioner list
9. `practitioner_detail` — practitioner info, booking CTA
10. `book_appointment` — booking flow, slot selection
11. `my_appointments` — appointment list, upcoming/past tabs
12. `profile` — user profile display
13. `edit_profile` — profile edit form
14. `change_email` — email change form
15. `change_password` — password change form
16. `errors` — generic network/auth error messages

## Workflow for Adding a New String

1. **Add to `strings/twine.txt`** under the appropriate section:
   ```
   [section.new_key]
   en = English text
   fr = French text
   de = German text
   lb = Luxembourgish text
   ```

2. **Generate Android resources:**
   ```bash
   make strings
   ```
   This writes to `androidApp/src/main/res/values/strings.xml` (and locale variants).

3. **Reference on Android:**
   ```kotlin
   stringResource(R.string.section_new_key)
   // or in non-composable context:
   context.getString(R.string.section_new_key)
   ```

4. **Reference on iOS:**
   iOS currently uses string literals directly in SwiftUI views. Until iOS Twine integration is added, use:
   ```swift
   Text("Section New Key")  // temporary
   // Future: NSLocalizedString("section.new_key", comment: "")
   ```

## Naming Conventions
- Key names use `snake_case`.
- Keys are namespaced by section: `section_name.key_name`.
- Android resource names replace `.` with `_`: `section_name_key_name`.
- Placeholders use `{variable_name}` syntax.

## Luxembourgish Notes
- Luxembourgish (lb) is the most unique language — do not auto-translate from French or German.
- Common phrases:
  - Hello = Moien
  - Thank you = Merci / Villmools Merci
  - Error = Fehler (same as German in most cases)
  - Appointment = Rendez-vous (borrowed from French)
  - Doctor = Dokter
  - Search = Sichen
  - Book = Reservéieren

## Quality Checks
Before submitting a string update:
- [ ] All 4 language values are provided (no empty values)
- [ ] Placeholders (`{name}`) match in count and name across all languages
- [ ] `make strings` runs without errors
- [ ] New Android resource keys are referenced correctly (no typos in R.string.x)
