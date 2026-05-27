# Localization Rules

## Rule L-1: twine.txt Is the Single Source of Truth
`strings/twine.txt` is the master file for all user-visible strings. Never edit generated files (`strings.xml`, `Localizable.strings`) directly — they are overwritten by `make strings`.

## Rule L-2: All Four Languages Are Required
Every new key in `twine.txt` must have values in all four supported languages: `en`, `fr`, `de`, `lb`. A key with any language empty is a localization violation.

```
[home.search_placeholder]
en = Search by name or specialty
fr = Rechercher par nom ou spécialité
de = Nach Name oder Fachgebiet suchen
lb = Sichen no Numm oder Fachgebitt
```

## Rule L-3: Key Format
- Section prefix: snake_case — `home.search_placeholder`, `profile.change_email`
- Section headers in double brackets: `[[home]]`, `[[profile]]`
- Android resource name: underscore-joined — `home_search_placeholder`
- Generate: `make strings` (requires `gem install twine`)

## Rule L-4: Accessibility Keys Use cd.* Prefix
All accessibility description strings live under the `[[cd]]` section:
```
[cd.back]
en = Go back
fr = Retour
de = Zurück
lb = Zréck
```

## Rule L-5: No Hardcoded User-Visible Strings in UI Code
Every string shown to the user must come from:
- Android: `stringResource(R.string.key_name)`  
- iOS: `NSLocalizedString("key_name", comment: "")` or `Text("key.name")`

**Exceptions:** Debug-only labels, developer console output, log messages.

## Rule L-6: Plurals Use Platform-Native Mechanisms
Quantity strings (`N appointments`) use:
- Android: `plurals` resource in `res/values/plurals.xml` (and translated variants)
- iOS: `Localizable.stringsdict`

Do not hand-roll plural logic (`if count == 1 { "appointment" } else { "appointments" }`).

## Rule L-7: Run make strings Before Committing
When `twine.txt` changes, always run `make strings` to regenerate Android string resources before committing. The CI pipeline enforces this by running `make strings` and checking for diff.
