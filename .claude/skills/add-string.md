# Skill: Add Localized String

Adds a new string key to the project with all 4 language translations, then regenerates Android resources.

## Usage
```
/add-string <section.key> <en> <fr> <de> <lb>
```
Example: `/add-string home.welcome_back "Welcome back" "Bienvenue" "Willkommen zurück" "Wëllkomm zréck"`

## Steps

### 1. Find the Right Section in twine.txt
File: `strings/twine.txt`

Strings are grouped under `[[section]]` headers. Add the new key inside the correct section:
```
[home.welcome_back]
en = Welcome back
fr = Bienvenue
de = Willkommen zurück
lb = Wëllkomm zréck
```

If a new section is needed, add a `[[section_name]]` header first.

### 2. For Accessibility Strings — Use cd.* Prefix
Accessibility labels live under `[[cd]]`:
```
[cd.close_dialog]
en = Close dialog
fr = Fermer la boîte de dialogue
de = Dialog schließen
lb = Dialog zoumaachen
```

### 3. Regenerate Android Resources
```bash
make strings
# gem install twine  ← if not installed
```
This writes to `androidApp/src/main/res/values/strings.xml` and `values-fr/`, `values-de/`, `values-lb/`.

### 4. Reference in Code

**Android:**
```kotlin
Text(stringResource(R.string.home_welcome_back))
// Note: dots in key become underscores in Android resource name
```

**iOS (current — string literals):**
```swift
Text(NSLocalizedString("home.welcome_back", comment: ""))
// or directly: Text("Welcome back")
```

## Checklist
- [ ] Key added to `strings/twine.txt` with all 4 languages (en, fr, de, lb)
- [ ] No language value is empty
- [ ] Key follows `section.key` snake_case format
- [ ] `make strings` run successfully
- [ ] Referenced with `stringResource(R.string.key_name)` on Android
