# Reusable Template — Rebuild Screen

Generic template for refactoring a single screen + its iOS sibling against any combination of project rules. Substitute the placeholders in the kickoff message; the body below runs verbatim for any screen and any combination of refactor goals.

## How to use

`@`-reference this file into a fresh session and provide five substitutions in your kickoff message. Three are always required (the two file paths and the screen name); two are the refactor scope:

| Placeholder | What to fill in | Examples |
|---|---|---|
| `<<SCREEN_NAME>>` | PascalCase screen name | `PractitionerDetailScreen` · `EditProfileScreen` · `HomeScreen` |
| `<<ANDROID_PATH>>` | Path from project root to the `.kt` screen | `androidApp/src/main/kotlin/lu/esklepios/app/view/dashboard/home/HomeScreen.kt` |
| `<<IOS_PATH>>` | Path from project root to the `.swift` view | `iosApp/eSklepios/Features/Dashboard/Home/HomeView.swift` |
| `<<GOALS>>` | One or more refactor goals (comma-separated) | `primitives` · `strings` · `dimensions` · `colors` · `validation` · `dialcodes` · `gender` · `datefilter` · `cns` · `all` |
| `<<RULES>>` | Rule IDs the agent must enforce | `UI-1a, UI-2, UI-14` · `A-12, A-13` · `all` |

### Goal → rules cheat-sheet

| `<<GOALS>>` token | Enforces | Source skill |
|---|---|---|
| `primitives` | UI-14 (no raw `Text` / `Icon` / `IconButton` / `Button` / `Tab` / inline `AvatarCircle` / `CheckRow`) | `rebuild-primitives.md` |
| `strings` | UI-2 (no hardcoded user-visible strings) — every string goes through Twine | `add-string.md` |
| `dimensions` | UI-1a (no `.dp` / `.sp` / `CGFloat` literals — `Dimens.*` / `Spacing.*` / `Sizing.*` / `Radius.*`) | `audit-tokens.md` |
| `colors` | UI-1 + UI-17 (no hex literals outside theme files — semantic `Color.appX` tokens only) | `audit-tokens.md` |
| `validation` | A-12 (email / password rules go through `ValidationUtil`) | `centralize-utilities.md` |
| `dialcodes` | A-13 (phone-prefix lists from `supportedDialCodes` + `PhoneParser`) | `centralize-utilities.md` |
| `gender` | A-13 (gender from `Gender` enum + Twine `gender_*` keys) | `centralize-utilities.md` |
| `datefilter` | A-13 (date filters from `DateFilter` enum) | `centralize-utilities.md` |
| `cns` | A-13 (CNS masking through `CnsFormatter.mask`) | `centralize-utilities.md` |
| `all` | every rule above | every skill above |

### Kickoff template

```
@.claude/prompts/rebuild-screen-primitives.md

Run this for:
SCREEN_NAME  = <PascalCaseScreenName>
ANDROID_PATH = <path/to/Screen.kt>
IOS_PATH     = <path/to/View.swift>
GOALS        = <comma-separated goals>
RULES        = <comma-separated rule IDs>
```

### Example kickoffs

```
# Primitives-only refactor (single goal)
SCREEN_NAME  = PractitionerListScreen
ANDROID_PATH = androidApp/src/main/kotlin/lu/esklepios/app/view/dashboard/home/practitioners/PractitionerListScreen.kt
IOS_PATH     = iosApp/eSklepios/Features/Dashboard/Home/PractitionerList/PractitionerListView.swift
GOALS        = primitives
RULES        = UI-14
```

```
# Multi-goal refactor (primitives + strings + colors)
SCREEN_NAME  = HomeScreen
ANDROID_PATH = androidApp/src/main/kotlin/lu/esklepios/app/view/dashboard/home/HomeScreen.kt
IOS_PATH     = iosApp/eSklepios/Features/Dashboard/Home/HomeView.swift
GOALS        = primitives, strings, colors
RULES        = UI-1, UI-2, UI-14, UI-17
```

```
# Full audit + refactor (everything)
SCREEN_NAME  = EditProfileScreen
ANDROID_PATH = androidApp/src/main/kotlin/lu/esklepios/app/view/dashboard/profile/profile_edit/EditProfileScreen.kt
IOS_PATH     = iosApp/eSklepios/Features/Dashboard/Profile/ProfileEdit/EditProfileView.swift
GOALS        = all
RULES        = all
```

The agent reads the prompt below, substitutes the five values, and executes end-to-end.

---

## Prompt (the agent should follow this verbatim after substituting placeholders)

Refactor **<<SCREEN_NAME>>** to comply with project rules **<<RULES>>**. Scope of refactor: **<<GOALS>>**. Both platforms in lock-step. Working directory: `/Users/anna.felix/projects/esklepios`.

Skills you may need (load only the ones the active `<<GOALS>>` reference — don't pull all of them blindly):
- `.claude/skills/rebuild-primitives.md` — primitives
- `.claude/skills/add-string.md` — strings (Twine workflow)
- `.claude/skills/audit-tokens.md` — dimensions + colors
- `.claude/skills/centralize-utilities.md` — validation / dialcodes / gender / datefilter / cns

Rules you may need:
- `.claude/rules/ui-rules.md` — UI-1, UI-1a, UI-2, UI-3, UI-14, UI-17
- `.claude/rules/architecture-rules.md` — A-12 (ValidationUtil), A-13 (form options)
- `.claude/rules/platform-parity-rules.md` — always read

═══════════════════════════════════════════════════════════════════
FILES TO REFACTOR
═══════════════════════════════════════════════════════════════════

- **ANDROID:** `<<ANDROID_PATH>>`
- **iOS:** `<<IOS_PATH>>`

═══════════════════════════════════════════════════════════════════
STEP 0 — SCOUT (run only the checks matching <<GOALS>>)
═══════════════════════════════════════════════════════════════════

Run the following bash block. Each section is gated by a goal — execute the ones the kickoff lists in `<<GOALS>>` (or all of them if `<<GOALS>> = all`).

```bash
ANDROID="<<ANDROID_PATH>>"
IOS="<<IOS_PATH>>"
GOALS="<<GOALS>>"

contains() { [[ "$GOALS" == *"$1"* ]] || [[ "$GOALS" == "all" ]]; }

echo "═══ SCOUT: $ANDROID ($(wc -l < $ANDROID) lines)  /  $IOS ($(wc -l < $IOS) lines) ═══"
echo

if contains primitives; then
  echo "── primitives ───────────────────────────────────────────"
  echo "Android: Text=$(grep -cE '^\s*Text\(' $ANDROID)  Icon=$(grep -cE '^\s*Icon\(' $ANDROID)  IconButton=$(grep -cE 'IconButton\(' $ANDROID)  Button/Outl/Text=$(grep -cE '\bButton\(|\bOutlinedButton\(|\bTextButton\(' $ANDROID)  Spacer=$(grep -cE 'Spacer\(Modifier\.(height|width)' $ANDROID)  Tab=$(grep -cE '\bTab\(' $ANDROID)"
  echo "iOS:     Text=$(grep -cE '\bText\(' $IOS)  Image(systemName)=$(grep -cE 'Image\(systemName:' $IOS)  Button(action:)=$(grep -cE 'Button\(action:' $IOS)"
fi

if contains strings; then
  echo "── strings (UI-2) ───────────────────────────────────────"
  echo "Android hardcoded English strings (Text(\"...\") missing stringResource):"
  grep -nE 'Text\("[A-Z]' $ANDROID | grep -v stringResource
  grep -nE 'text = "[A-Z]' $ANDROID | grep -v stringResource
  echo "iOS hardcoded English strings (Text(\"...\") missing NSLocalizedString):"
  grep -nE 'Text\("[A-Z]' $IOS | grep -v NSLocalizedString | head -10
fi

if contains dimensions; then
  echo "── dimensions (UI-1a) ───────────────────────────────────"
  echo "Android hardcoded dp/sp: $(grep -cE '[0-9]+\.dp|[0-9]+\.sp' $ANDROID)"
  echo "iOS hardcoded CGFloat in sizing calls:"
  grep -nE '\.(frame|padding|cornerRadius|offset|spacing)\([^)]*[0-9]' $IOS \
    | grep -vE 'Dimens\.|Spacing\.|Radius\.|Sizing\.|\.infinity|maxWidth:' | head -10
fi

if contains colors; then
  echo "── colors (UI-17) ───────────────────────────────────────"
  echo "Android hex literals: $(grep -cE 'Color\(0x[A-F0-9]' $ANDROID)"
  echo "iOS hex literals: $(grep -cE 'Color\(hex:' $IOS)"
  grep -nE 'Color\(0x[A-F0-9]' $ANDROID | head -5
  grep -nE 'Color\(hex:' $IOS | head -5
fi

if contains validation; then
  echo "── validation (A-12) ────────────────────────────────────"
  echo "Android inline email/password checks (should use ValidationUtil):"
  grep -nE 'contains\("@"\)|indexOf\("@"\)|hasMinLength|hasMixedCase|hasNumAndSymbol' $ANDROID
  echo "iOS inline email/password checks:"
  grep -nE 'contains\("@"\)|firstIndex\(of: "@"\)|lastIndex\(of: "."\)|hasMinLength|hasMixedCase' $IOS
fi

if contains dialcodes; then
  echo "── dialcodes (A-13) ─────────────────────────────────────"
  grep -nE '"\+352"|"\+33"|"\+49"|"\+32"|"\+44"' $ANDROID
  grep -nE '"\+352"|"\+33"|"\+49"|"\+32"|"\+44"' $IOS
fi

if contains gender; then
  echo "── gender (A-13) ────────────────────────────────────────"
  grep -nE 'listOf\("(Male|Man|Female|Woman|Other)"|\["(Male|Man|Female|Woman|Other)"' $ANDROID $IOS
fi

if contains datefilter; then
  echo "── datefilter (A-13) ────────────────────────────────────"
  grep -nE '"All" to|"Today" to|"Within 3 Days" to|\("All",|\("Today",|\("Within' $ANDROID $IOS
fi

if contains cns; then
  echo "── cns (A-13) ───────────────────────────────────────────"
  grep -nE 'cns\.prefix\(9\)|cnsNumber\.take\(9\)|cnsNumber\.substring\(0' $ANDROID $IOS
fi
```

Use the scout output to drive the refactor — do not refactor blind.

═══════════════════════════════════════════════════════════════════
PRE-WORK — CREATE MISSING WRAPPERS  (only when `<<GOALS>>` includes `primitives`)
═══════════════════════════════════════════════════════════════════

If the scout shows raw primitives in the screen and the wrapper component for that primitive doesn't exist yet, create it before refactoring call sites. Check first:

```bash
ls androidApp/src/main/kotlin/lu/esklepios/app/core/ui/components/AppIcon.kt 2>&1
ls androidApp/src/main/kotlin/lu/esklepios/app/core/ui/components/AppIconButton.kt 2>&1
ls androidApp/src/main/kotlin/lu/esklepios/app/core/ui/components/Spacers.kt 2>&1
ls androidApp/src/main/kotlin/lu/esklepios/app/core/ui/components/AppTabs.kt 2>&1
ls androidApp/src/main/kotlin/lu/esklepios/app/core/ui/components/CheckRow.kt 2>&1
ls iosApp/eSklepios/Core/UI/Components/AppIcon.swift 2>&1
ls iosApp/eSklepios/Core/UI/Components/AppIconButton.swift 2>&1
ls iosApp/eSklepios/Core/UI/Components/AppTabRow.swift 2>&1
ls iosApp/eSklepios/Core/UI/Components/CheckRow.swift 2>&1
```

For each "No such file" that the screen **actually needs**, create it using the exact signatures from `rebuild-primitives.md` (§2 AppIcon, §3 AppIconButton, §5 Spacers, §6 AppTabRow, §8 CheckRow). Both platforms in the same commit. Include `@Preview` / `#Preview` with at least 2 states.

═══════════════════════════════════════════════════════════════════
REFACTOR PROCEDURE
═══════════════════════════════════════════════════════════════════

Execute the steps whose goal is in `<<GOALS>>` (skip the rest).

### 1. Strings  (goal: `strings`)

For every hardcoded English string the scout found, add a key to `strings/twine.txt` in all 4 languages (en/fr/de/lb) and run `make strings`. Key naming: `<area>_<element>_<role>` (e.g. `practitioner_detail_not_found`). Also add the matching `<string name="...">` to `androidApp/src/main/res/values/strings.xml` so the Android build doesn't break before Twine regenerates.

Replace each Android `Text("Literal", …)` with `Text(stringResource(R.string.<key>), …)` (or the equivalent inside the `App*Text` wrapper if `primitives` is also active).
Replace each iOS `Text("Literal")` with `Text(NSLocalizedString("<key>", value: "Literal", comment: ""))`.

### 2. Primitives  (goal: `primitives`)

**Android** — work the file top-to-bottom replacing each violation per the UI-14 mapping table:

| Raw | Wrapper |
|---|---|
| `Text(value, style=…, color=…)` | one of `AppTitleText / AppSubtitleText / AppBodyText / AppCaptionText / AppLabelText` |
| `Icon(imageVector, contentDescription, tint, modifier=Modifier.size(token))` | `AppIcon(imageVector, contentDescription, tint, size = token)` |
| `IconButton { Icon(...) }` | `AppIconButton(icon, contentDescription, onClick, tint)` |
| `Button(...) { Text(...) }` | `PrimaryButton` (filled) / `SecondaryButton` (outlined) / `GhostButton` (no border) |
| `TextButton { Text(Primary) }` (inline link) | `AppTextLink(text, onClick)` |
| `Tab(selected, onClick, text = { ... })` × N | `AppTabRow(selectedIndex, tabs = listOf(AppTabItem(...)))` |
| `Spacer(Modifier.height(token))` × N inside a Column | move to `Column(verticalArrangement = Arrangement.spacedBy(token))`; or use `VSpace(token)` for one-off gaps |
| inline circle + background + initials | `AvatarCircle(initials, size)` |
| inline label-value row | `CheckRow(label, value, isLast?, leadingIcon?)` |

**iOS** — equivalent:

| Raw | Wrapper |
|---|---|
| `Text(value).font(...).foregroundColor(...)` | `App*Text(text: value, color: ...)` |
| `Image(systemName:).font(.system(size: token)).foregroundColor(tint)` | `AppIcon(systemName, tint: tint, size: token)` |
| `Button(action:) { Image(systemName:) }` | `AppIconButton(systemName, accessibilityLabel, action)` |
| `Button(action:) { Text("Save") }` | `PrimaryButton(title, action)` |
| `Button(action:) { Text(linkLabel) }` for inline links | `AppTextLink(text, action)` |

### 3. Dimensions  (goal: `dimensions`)

Replace every `.dp` / `.sp` (Android) or bare `CGFloat` (iOS) inside sizing calls with a token from `Dimens.kt` / `AppDimens.swift`. If no semantic token fits, **add one to the theme file first**, then use it. Even `0` goes through `Dimens.paddingNone` / `Spacing.none` / `Radius.none`. See Rule UI-1a for the full mapping.

### 4. Colors  (goal: `colors`)

Replace every `Color(0xFF…)` (Android) / `Color(hex: "…")` (iOS) outside `Color.kt` / `AppColors.swift` with a semantic token (`Color.appPrimary`, `Color.appFavoriteRed`, etc.). If a hex value has no token, add a named token to the theme file first.

### 5. Validation  (goal: `validation`)

Replace inline email-format checks with `ValidationUtil.isValidEmail(...)`.
Replace inline password strength logic with `ValidationUtil.passwordStrength(...)` + `ValidationUtil.passwordCriteria(...)`.
The strength meter reads `PasswordStrength.percent`; the criteria checklist reads `PasswordCriterion.MIN_LENGTH` / `.MIXED_CASE` / `.NUM_AND_SYMBOL`.

### 6. Dial codes  (goal: `dialcodes`)

Replace inline `listOf("+352" to "🇱🇺 +352", ...)` and `knownPrefixes` arrays with iteration over `supportedDialCodes` (Android: `lu.esklepios.app.util.supportedDialCodes`; iOS: bridged from KMP). Parsing goes through `PhoneParser.parse(phone)`.

### 7. Gender  (goal: `gender`)

Replace `listOf("Male", "Female", "Other")` / `listOf("Man", "Woman", "Other")` / inline `switch gender.lowercased()` with `Gender.entries` and `Gender.fromApiString(value)`. Labels read from Twine via each enum's `labelKey`.

### 8. Date filter  (goal: `datefilter`)

Replace inline date-filter pair lists with `DateFilter.entries.map { it to stringResource(it.labelKey) }` (Android) or the equivalent on iOS.

### 9. CNS masking  (goal: `cns`)

Replace inline `cns.prefix(9)` / `cnsNumber.take(9)` with `CnsFormatter.mask(cns)`.

### 10. Always

- **Preserve appearance** — do not change colors, sizes, paddings, or layouts beyond the token swap. The refactor swaps construction style, not visuals.
- **Accessibility (Rule AC-2)** — every `AppIcon` that was `contentDescription = null` keeps `null` AND carries the `// a11y: decorative — labelled by adjacent Text` comment. Informative icons get a localized `stringResource(R.string.cd_*)` / `NSLocalizedString("cd_*", ...)`.

═══════════════════════════════════════════════════════════════════
EXEMPTIONS — leave these as raw primitives
═══════════════════════════════════════════════════════════════════

- `Text(...).tag(...)` inside `Picker` / `TabView`
- `Text(...)` in `.alert { } message:` slot
- Compound `Text(...) + Text(...)` concatenation (annotated-string equivalent)
- Compose `buildAnnotatedString { withStyle { } }`
- `HeaderAction.TitleAction(style:)` inside `AppGradientHeader`
- Proportional font sizes computed from layout geometry
- `Icon` inside Material `OutlinedTextField`'s `leadingIcon=`/`trailingIcon=` slots
- `Spacer(Modifier.weight(1f))` — weight-based flex, not a fixed gap

═══════════════════════════════════════════════════════════════════
VERIFICATION  (run the blocks matching <<GOALS>>)
═══════════════════════════════════════════════════════════════════

```bash
ANDROID="<<ANDROID_PATH>>"
IOS="<<IOS_PATH>>"
GOALS="<<GOALS>>"
contains() { [[ "$GOALS" == *"$1"* ]] || [[ "$GOALS" == "all" ]]; }

if contains primitives; then
  grep -nE '^\s*(Text|Icon|IconButton|Button|OutlinedButton|TextButton)\(' $ANDROID \
    | grep -vE 'Modifier\.weight|spacedBy'                    # expect: empty
  grep -nE '^\s*(Image\(systemName:|Button\(action:|Text\()' $IOS \
    | grep -vE '\.tag\(|\.alert.*message:|Text\("\\\(|\+ Text\('  # expect: empty
fi

if contains strings; then
  grep -nE 'Text\("[A-Z]|text = "[A-Z]' $ANDROID | grep -v stringResource    # expect: empty
  grep -nE 'Text\("[A-Z]' $IOS | grep -v NSLocalizedString                   # expect: empty
fi

if contains dimensions; then
  grep -cE '[0-9]+\.dp|[0-9]+\.sp' $ANDROID                                  # expect: 0
  grep -nE '\.(frame|padding|cornerRadius|offset|spacing)\([^)]*[0-9]' $IOS \
    | grep -vE 'Dimens\.|Spacing\.|Radius\.|Sizing\.|\.infinity|maxWidth:'   # expect: empty
fi

if contains colors; then
  grep -nE 'Color\(0x[A-F0-9]' $ANDROID                                      # expect: empty
  grep -nE 'Color\(hex:' $IOS                                                # expect: empty
fi

if contains validation; then
  grep -nE 'contains\("@"\)|hasMinLength|hasMixedCase|hasNumAndSymbol' $ANDROID $IOS  # expect: empty
fi

if contains dialcodes; then
  grep -nE '"\+352"|"\+33"|"\+49"|"\+32"|"\+44"' $ANDROID $IOS               # expect: empty
fi

if contains gender; then
  grep -nE 'listOf\("(Male|Man|Female|Woman|Other)"|\["(Male|Man|Female|Woman|Other)"' $ANDROID $IOS  # expect: empty
fi

if contains datefilter; then
  grep -nE '"All" to|"Today" to|"Within 3 Days" to|\("All",|\("Today",|\("Within' $ANDROID $IOS  # expect: empty
fi

if contains cns; then
  grep -nE 'cns\.prefix\(9\)|cnsNumber\.take\(9\)|cnsNumber\.substring\(0' $ANDROID $IOS  # expect: empty
fi

# Both builds must pass regardless of goals
./gradlew :androidApp:assembleDebug
xcodebuild -project iosApp/eSklepios.xcodeproj -scheme eSklepios \
  -destination 'platform=iOS Simulator,name=iPhone 16' build
```

═══════════════════════════════════════════════════════════════════
DO NOT
═══════════════════════════════════════════════════════════════════

- Change business logic, ViewModel signatures, or `uiState` field names.
- Modify the screen shell (`AppFormScreen`/`AppScreen` or `AppGradientHeader`/`AppGradientHeaderView`).
- Touch the corresponding `*ViewModel.kt` — pure UI refactor.
- Introduce a duplicate component (e.g. another local `CheckRow`) — extract to `core/ui/components/` if needed.
- Pull in goals outside `<<GOALS>>` — stay scoped.

═══════════════════════════════════════════════════════════════════
REPORT (at the end)
═══════════════════════════════════════════════════════════════════

- Files changed with line-count delta
- New wrappers created (if any) with their signatures
- New Twine keys added with their 4-language values (if `strings` was a goal)
- New design-system tokens added (if `dimensions` or `colors` was a goal)
- Final grep output for the verification blocks matching `<<GOALS>>` (all expected to be empty)
