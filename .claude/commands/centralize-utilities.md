# Centralize Utilities

Audit a screen (both Android and iOS counterparts) for logic that violates the centralization rules
(A-12 Validation, A-13 Options, A-14 Formatting, A-15 Shared-First, L-5 No Hardcoded Strings,
UI-10 No Force Unwrap on iOS). Report every violation with a file:line reference, classify it, and
propose the exact destination (utility file + function signature).

## Argument

`$ARGUMENTS` — the screen base name, e.g. `PractitionerDetailScreen`, `HomeScreen`, `BookingScreen`.

## Steps

### 1. Locate both platform files

- **Android:** search `androidApp/src/main/kotlin/**/view/**/<name>.kt`
  (drop the `Screen` suffix for the folder search if needed, e.g. `PractitionerDetail`).
- **iOS:** search `iosApp/eSklepios/Features/**/<name>View.swift`
  (replace `Screen` with `View` in the filename).
- Also read the shared ViewModel: `shared/src/commonMain/**/presentation/viewmodel/<name prefix>ViewModel.kt`.

### 2. Read existing shared utilities

Check whether these already exist — their current contents define what is already centralized:

- `shared/src/commonMain/.../util/Formatters.kt`
- `shared/src/commonMain/.../util/Validators.kt`
- `shared/src/commonMain/.../util/Options.kt`
- `iosApp/eSklepios/Core/Utils/Formatters.swift`
- `androidApp/.../utils/DateUtil.kt` (date/slot formatting only)

### 3. Audit each file for violations

For every finding record: rule violated, file path, line number(s), the offending code snippet,
and the proposed fix destination.

#### A-12 — Validation logic in the screen/view

Look for: email `.contains("@")`, regex matches, password length/character checks,
CNS/IBAN format checks, non-empty guards that encode business rules.
**Destination:** `shared/.../util/Validators.kt` (+ mirrored in `Formatters.swift` if UI-only display).

#### A-13 — Hardcoded option lists

Look for: `listOf("Cash", "Card", ...)`, `["Cash", "Card", ...]`, inline enum-to-display-string maps,
language code arrays, gender options, filter label arrays defined inside the screen.
**Destination:** `shared/.../util/Options.kt`.

#### A-14 — Inline formatting in the screen/view

Look for:
- String interpolation that assembles a display value: `"${a}, ${b}, ${c}"`, `"\(a)\n\(b)"`.
- Phone sanitization: `.replacingOccurrences(of:...)` chains, `replace()` on raw phone strings.
- Address concatenation, name initials computation, CNS masking, price formatting.
- Inline `DateFormatter` / `Calendar.current` / `Date()` outside `DateUtil`.
- Inline `slotId.split(...)` time extraction.
**Destination:** `shared/.../util/Formatters.kt` and `iosApp/.../Core/Utils/Formatters.swift`
(or `DateUtil` for date/slot logic).

#### A-15 — Logic present on one platform but not shared

Compare the Android and iOS implementations. If one platform has logic the other lacks (or the
logic should live in the shared ViewModel / use case), flag it.

#### L-5 — Hardcoded user-visible strings

Look for: string literals passed to `Text(...)`, `AppXxxText(text: "...")`, `cardTitle("...")`,
accessibility labels as plain string literals (not `stringResource` / `NSLocalizedString`).
**Destination:** `strings/twine.txt` new key, then `stringResource(R.string.key)` /
`NSLocalizedString("key", comment: "")`.

#### UI-10 — Force unwrap in iOS UI code

Look for `!` on URL construction, optional casts, or any `as!` / `try!` in the view file.
**Destination:** rewrite with `guard let` or `if let`.

#### PP-3 — Platform navigation/link parity

Compare URLs, phone numbers, deep-link targets, and external resources opened on each platform.
Flag mismatches.

### 4. Produce the audit report

Output a table of all findings:

| # | Rule | Platform | File:Line | Snippet | Proposed fix |
|---|------|----------|-----------|---------|--------------|
| … | A-14 | Android  | `FooScreen.kt:42` | `"${a}, ${b}"` | Move to `Formatters.formatAddress()` |

Then list the **new utility functions** that need to be created (with proposed Kotlin and Swift
signatures), grouped by destination file.

### 5. Ask before fixing

After presenting the report, ask the user: "Fix all of the above now?" If yes, apply every fix
in this order:
1. Create / update shared utility files (`Formatters.kt`, `Validators.kt`, `Options.kt`).
2. Create / update iOS mirror utilities (`Formatters.swift`).
3. Update the Android screen to call the new utilities.
4. Update the iOS view to call the new utilities.
5. Add missing twine keys and run `make strings`.
6. Verify no raw literals or inline logic remain (re-run the enforcement greps from the rules).

## Notes

- Do NOT fix hardcoded `.dp` / `.sp` / `CGFloat` dimension literals in this command — those are
  handled by `/rebuild-screen`.
- Do NOT refactor typography raw `Text(...)` primitives — those are handled by `/rebuild-text`.
- Do NOT touch the ViewModel or repository layer unless the fix is a pure extraction of a
  pure function (no side effects, no DI changes needed).
- After all fixes, update `CLAUDE.md` under "Common Pitfalls" if the violation pattern is novel.