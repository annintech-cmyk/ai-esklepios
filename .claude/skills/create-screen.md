# Skill: Create / Rebuild Screen

Single skill that handles **new screen creation**, **refactoring an existing screen to project standards**, and **replacing raw UI primitives** with project wrappers — on both platforms.

**Usage:**
```
/create-screen <Name> form|gradient <area> [description]   ← new screen
/create-screen <ScreenName>                                ← refactor an existing screen
```

When invoked with a single `<ScreenName>` argument matching an existing file (e.g. `LoginScreen`, `HomeView`), the skill runs in **rebuild mode** and skips the template-substitution + wiring steps — applying only the checklist and Bad-vs-Good guidance to bring the existing screen up to standards, including replacing raw primitives (`Text`, `Icon`, `IconButton`, `Spacer`, `Button`, `Tab`, etc.) with project wrappers.

---

## Existing Components — USE THESE FIRST

Read the actual file before writing any code. Never invent component names.

### Screen Shells

| Component | Platform | Signature | Use when |
|-----------|----------|-----------|----------|
| `AppFormScreen` | Android | `(title, onNavigateBack?, error?, onErrorDismissed, content)` | Any scrollable form/detail/auth screen |
| `AppScreen` | iOS | `(title:, onBack:?, error:?, onErrorDismissed:?, content:)` | Same |
| `AppGradientHeader` | Android | `(roundedBottom, leadingAction, centerAction, trailingAction, textBlock?, profile?, search?)` | Gradient hero — **always use this, never raw `GradientHeader` in screens** |
| `AppGradientHeaderView` | iOS | `(roundedBottom:, leading:, center:, trailing:, textBlock:?, profile:?, search:?)` | Same |
| `GradientHeader` | Android | `(modifier, roundedBottom, content)` | Internal primitive only — do not use directly in screens |
| `GradientHeader` | iOS | `(minHeight:, onBack:?, trailingAction:?, trailingIcon:?, content:)` | Internal primitive only |

`AppFormScreen` / `AppScreen` already include: toolbar, back nav, snackbar/alert, background, scroll, padding. Do not rebuild these manually.

### Buttons

| Android | iOS | Style |
|---------|-----|-------|
| `PrimaryButton(text, onClick, modifier, enabled, isLoading)` | `PrimaryButton(title:, icon:?, isLoading:, isEnabled:, action:)` | Gradient pill |
| `SecondaryButton(text, onClick, modifier, enabled)` | `SecondaryButton(title:, icon:?, isLoading:, isEnabled:, action:)` | Outlined pill |
| `GhostButton(text, onClick, modifier)` | `GhostButton(title:, action:)` | Text only |

Files: `Buttons.kt` / `PrimaryButton.swift`, `SecondaryButton.swift`, `GhostButton.swift`

### Inputs

| Component | Signature | Use when |
|-----------|-----------|----------|
| `FormField` (Android) | `(label, value, onValueChange, placeholder, isRequired, isPassword, leadingIcon?, errorMessage?, keyboardType, modifier)` | Any labeled form input |
| `SearchInputField` (Android) | `(value, onValueChange, placeholder, leadingIcon, modifier, iconTint, variant)` | Search fields |
| `SearchCard` (Android) | `(searchQuery, onSearchQueryChange, locationQuery, onLocationQueryChange, onSearchClick, variant, modifier)` | Specialty + location search combo |
| `SearchInputField` (iOS) | `(systemIcon:, placeholder:, text:, iconColor:?, variant:, onSubmit:?)` | Search fields |
| `SearchCard` (iOS) | `(searchQuery:, locationQuery:, onSearchTap:, variant:)` | Search combo |

`SearchInputVariant`: `.Light` = surface fill + dark text · `.Dark` = white-opacity fill + white text (use inside `GradientHeader`)

Files: `FormField.kt` · `SearchInputField.kt` · `SearchCard.kt` / `SearchInputField.swift` · `SearchCard.swift`

### State Views

| State | Android | iOS |
|-------|---------|-----|
| Loading | `LoadingIndicator(modifier, message?)` | `LoadingView(message:)` |
| Error | `ErrorView(modifier, message, onRetry?)` | `ErrorView(message:, retry:?)` |
| Empty | `EmptyStateView(modifier, icon, title, subtitle, actionLabel?, onAction?)` | `EmptyStateView(icon:, title:, message:, actionLabel:?, onAction:?)` |

Every screen with async data must handle all three.

### Typography

**All text must use AppText components. Raw `Text` styling is forbidden in screen files.**

| Android | iOS | Use for |
|---------|-----|---------|
| `AppTitleText(text, modifier?, color?, textAlign?, maxLines?)` | `AppTitleText(text:, color:?, alignment:?, maxLines:?)` | Screen/dialog headings |
| `AppSubtitleText(text, modifier?, color?, textAlign?, maxLines?)` | `AppSubtitleText(text:, color:?, alignment:?, maxLines:?)` | Card titles, section names |
| `AppBodyText(text, modifier?, color?, textAlign?, maxLines?)` | `AppBodyText(text:, color:?, alignment:?, maxLines:?)` | Body copy, descriptions |
| `AppCaptionText(text, modifier?, color?, textAlign?, maxLines?)` | `AppCaptionText(text:, color:?, alignment:?, maxLines:?)` | Captions, metadata, secondary text |
| `AppToolbarTitle(text, modifier?, color?)` | `AppToolbarTitle(text:, color:?)` | Toolbar / screen titles |
| `AppButtonText(text, modifier?, color?)` | `AppButtonText(text:, color:?)` | Button labels |
| `AppLabelText(text, modifier?, color?, textAlign?)` | `AppLabelText(text:, color:?, alignment:?)` | Form labels, chips, badges |
| `AppErrorText(text, modifier?, textAlign?)` | `AppErrorText(text:, alignment:?)` | Inline error messages |
| `FormFieldLabel(label, required, modifier?)` | `FormFieldLabel(label:, required:)` | Label + optional red asterisk |
| *(use `AppErrorText`)* | `ValidationCaption(text:, isValid:)` | Icon + caption — inline field validation |

**Default colors:** `AppBodyText`/`AppCaptionText` → `TextSecondary`/`.appTextSecondary`. All others → `TextPrimary`/`.appTextPrimary`. Pass `color:` to override.
**White-on-gradient:** pass `color = Color.White` / `color: .white`.

Files: `core/ui/components/Typography.kt` / `Core/UI/Components/AppTypography.swift`

### Navigation Links & Dividers

| Component | Android | iOS | Use when |
|-----------|---------|-----|----------|
| `AppTextLink` | `AppTextLink(text, onClick, modifier)` | `AppTextLink(text:, action:)` | Inline text link — replaces `TextButton { Text(Primary) }` |
| `DividerWithLabel` | `DividerWithLabel(label, modifier)` | `DividerWithLabel(label:)` | "or" divider in auth screens |

Files: `AppTextLink.kt` / `AppTextLink.swift`

### Other Components

`AppToolbar` · `AvatarCircle` · `StatusBadge` · `FilterChip` · `PractitionerCard` · `InfoRow` · `MapPreviewCard`

Social sign-in: `GoogleSignInButton(onClick, modifier)` and `AppleSignInButton(onClick, modifier)` — no generic `SocialSignInButton` exists.

---

## Raw Primitives → Project Wrappers

### Hard Rule

A screen or feature file may contain **only** these primitive types:
- Layout primitives: `Column`, `Row`, `Box`, `LazyColumn`, `LazyRow`, `VStack`, `HStack`, `ZStack`, `ScrollView`, `LazyVStack`.
- Project wrappers from `core/ui/components/`.

Raw `Text`, `Icon`, `IconButton`, `Spacer`, `Button`, `OutlinedButton`, `TextButton`, `Tab`, `Image(systemName:)` are forbidden in `view/` / `Features/` directories. They appear only inside `core/ui/components/`, where the wrapper itself is defined.

When no existing wrapper fits: **create the wrapper first**, then refactor the call site. Document the new component in this file.

---

### 1. Text → `App*Text`

See the Typography table above for all components. Bad → Good:

```kotlin
// BAD
Text(text, style = MaterialTheme.typography.bodySmall, color = Warning)
TextButton(onClick) { Text(text, color = Primary, style = labelLarge) }
Row { HorizontalDivider(Modifier.weight(1f)); Text("  or  "); HorizontalDivider(Modifier.weight(1f)) }

// GOOD
AppCaptionText(text = text, color = Warning)
AppTextLink(text = text, onClick = onClick)
DividerWithLabel(label = stringResource(R.string.login_or))
```

```swift
// BAD
Text(title).font(.heading2).foregroundColor(.appTextPrimary)
Button(text) {}.font(.heading5).foregroundColor(.appPrimary)

// GOOD
AppTitleText(text: title)
AppTextLink(text: text) { action() }
```

---

### 2. Icon → `AppIcon`

**Status:** `AppIcon` does NOT exist yet — create on first refactor.

**Android** (`core/ui/components/AppIcon.kt`):
```kotlin
@Composable
fun AppIcon(
    imageVector: ImageVector,
    contentDescription: String?,    // REQUIRED — pass null only when adjacent Text labels the icon (a11y rule AC-2)
    modifier: Modifier = Modifier,
    tint: Color = TextSecondary,
    size: Dp = Dimens.iconSizeMd,
) {
    Icon(
        imageVector = imageVector,
        contentDescription = contentDescription,
        tint = tint,
        modifier = modifier.size(size),
    )
}
```

**iOS** (`Core/UI/Components/AppIcon.swift`):
```swift
struct AppIcon: View {
    let systemName: String
    var accessibilityLabel: String? = nil   // nil ⇒ decorative — caller must group with labelled Text
    var tint: Color = .appTextSecondary
    var size: CGFloat = Dimens.iconMd

    var body: some View {
        let img = Image(systemName: systemName)
            .font(.system(size: size, weight: .regular))
            .foregroundColor(tint)
        if let label = accessibilityLabel {
            img.accessibilityLabel(label)
        } else {
            img.accessibilityHidden(true)
        }
    }
}
```

Bad → Good:
```kotlin
// BAD
Icon(Icons.Filled.Email, contentDescription = null, tint = Primary, modifier = Modifier.size(Dimens.iconSizeMd))

// GOOD — decorative (labelled by adjacent Text)
AppIcon(Icons.Filled.Email, contentDescription = null, tint = Primary)

// GOOD — informative
AppIcon(Icons.Filled.Warning, contentDescription = stringResource(R.string.cd_warning), tint = Danger)
```

```swift
// BAD
Image(systemName: "envelope").font(.system(size: Dimens.iconMd)).foregroundColor(.appPrimary)

// GOOD
AppIcon(systemName: "envelope", tint: .appPrimary)
AppIcon(systemName: "exclamationmark.triangle", accessibilityLabel: NSLocalizedString("cd_warning", value: "Warning", comment: ""), tint: .appDanger)
```

---

### 3. IconButton → `AppIconButton`

**Status:** `AppIconButton` does NOT exist yet — create on first refactor.

**Android** (`core/ui/components/AppIconButton.kt`):
```kotlin
@Composable
fun AppIconButton(
    icon: ImageVector,
    contentDescription: String,     // REQUIRED — IconButtons are always interactive ⇒ never null (a11y rule AC-1)
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tint: Color = TextPrimary,
    iconSize: Dp = Dimens.iconSizeMd,
    enabled: Boolean = true,
) {
    IconButton(onClick = onClick, modifier = modifier, enabled = enabled) {
        AppIcon(imageVector = icon, contentDescription = contentDescription, tint = tint, size = iconSize)
    }
}
```

**iOS** (`Core/UI/Components/AppIconButton.swift`):
```swift
struct AppIconButton: View {
    let systemName: String
    let accessibilityLabel: String      // REQUIRED
    let action: () -> Void
    var tint: Color = .appTextPrimary
    var iconSize: CGFloat = Dimens.iconMd

    var body: some View {
        Button(action: action) {
            Image(systemName: systemName)
                .font(.system(size: iconSize, weight: .semibold))
                .foregroundColor(tint)
        }
        .accessibilityLabel(accessibilityLabel)
        .frame(minWidth: Dimens.toolbarSlot, minHeight: Dimens.toolbarSlot)   // 44pt tap target — rule AC-3
    }
}
```

Bad → Good:
```kotlin
// BAD
IconButton(onClick = { /* back */ }) {
    Icon(Icons.Filled.ArrowBack, contentDescription = stringResource(R.string.cd_back))
}

// GOOD
AppIconButton(
    icon = Icons.Filled.ArrowBack,
    contentDescription = stringResource(R.string.cd_back),
    onClick = { /* back */ },
)
```

---

### 4. Button / TextButton / OutlinedButton → Project buttons

| Raw primitive | Replace with |
|---|---|
| `Button(...) { Text(...) }` (filled) | `PrimaryButton(text, onClick, isLoading?, enabled?)` |
| `OutlinedButton(...) { Text(...) }` | `SecondaryButton(text, onClick, enabled?)` |
| `TextButton(...) { Text(...) }` (no nav semantics) | `GhostButton(text, onClick)` |
| `TextButton(...) { Text(...) }` (inline nav link) | `AppTextLink(text, onClick)` |
| Google / Apple SSO buttons | `GoogleSignInButton(onClick)`, `AppleSignInButton(onClick)` |

Bad → Good:
```kotlin
// BAD
Button(onClick = ..., shape = RoundedCornerShape(Dimens.radiusPill)) {
    Text("Save", color = Color.White, style = MaterialTheme.typography.labelLarge)
}

// GOOD
PrimaryButton(text = stringResource(R.string.action_save), onClick = ..., modifier = Modifier.fillMaxWidth())
```

```kotlin
// BAD
TextButton(onClick = { navController.navigate("forgot") }) {
    Text(stringResource(R.string.login_forgot_password), color = Primary, style = labelLarge)
}

// GOOD
AppTextLink(
    text = stringResource(R.string.login_forgot_password),
    onClick = { navController.navigate("forgot") },
)
```

**Exception:** `TextButton`/`Button` may stay raw inside `core/ui/components/Buttons.kt` itself, where the wrapper is implemented.

---

### 5. Spacer → `VSpace` / `HSpace`

**Status:** wrappers do NOT exist yet — create on first refactor. **Soft rule:** the current `Spacer(Modifier.height(Dimens.paddingL))` pattern is token-compliant, just verbose. Migrate opportunistically.

**Android** (`core/ui/components/Spacers.kt`):
```kotlin
/** Vertical space using a Dimens token. Prefer Arrangement.spacedBy() in Columns when possible. */
@Composable
fun VSpace(height: Dp) { Spacer(Modifier.height(height)) }

/** Horizontal space using a Dimens token. */
@Composable
fun HSpace(width: Dp) { Spacer(Modifier.width(width)) }
```

**iOS:** Not needed — use `VStack(spacing: Spacing.l)` per Rule UI-1a.

Bad → Good (Android):
```kotlin
// VERBOSE (current)
Spacer(modifier = Modifier.height(Dimens.paddingL))

// PREFERRED
VSpace(Dimens.paddingL)

// BEST inside a Column with a fixed gap between every child:
Column(verticalArrangement = Arrangement.spacedBy(Dimens.paddingL)) { ... }
```

---

### 6. Tab / TabRow → `AppTabRow`

**Status:** wrappers do NOT exist yet — create on first refactor.

**Android** (`core/ui/components/AppTabs.kt`):
```kotlin
@Composable
fun AppTabRow(
    selectedIndex: Int,
    tabs: List<AppTabItem>,
    modifier: Modifier = Modifier,
) {
    TabRow(
        selectedTabIndex = selectedIndex,
        modifier = modifier,
        containerColor = Surface,
        contentColor = Primary,
    ) {
        tabs.forEachIndexed { index, tab ->
            Tab(
                selected = selectedIndex == index,
                onClick = tab.onClick,
                text = {
                    AppCaptionText(
                        text = tab.label,
                        color = if (selectedIndex == index) Primary else TextSecondary,
                    )
                },
            )
        }
    }
}

data class AppTabItem(val label: String, val onClick: () -> Unit)
```

**iOS:** Use `Picker(...).pickerStyle(.segmented)` or a custom `HStack` of pills; codify in `Core/UI/Components/AppTabRow.swift`.

Bad → Good:
```kotlin
// BAD
TabRow(selectedTabIndex = uiState.selectedTab, ...) {
    Tab(selected = uiState.selectedTab == 0, onClick = { viewModel.selectTab(0) }, text = {
        AppCaptionText(text = stringResource(R.string.appointments_upcoming), ...)
    })
    Tab(selected = uiState.selectedTab == 1, onClick = { viewModel.selectTab(1) }, text = {
        AppCaptionText(text = stringResource(R.string.appointments_past), ...)
    })
}

// GOOD
AppTabRow(
    selectedIndex = uiState.selectedTab,
    tabs = listOf(
        AppTabItem(stringResource(R.string.appointments_upcoming)) { viewModel.selectTab(0) },
        AppTabItem(stringResource(R.string.appointments_past))     { viewModel.selectTab(1) },
    ),
)
```

---

### 7. AvatarCircle — already a project component

**Status:** `AvatarCircle` exists on both platforms (`AvatarCircle.kt` / `AvatarCircle.swift`).

| Platform | Signature |
|---|---|
| Android | `AvatarCircle(initials: String, size: Dp = Dimens.avatarSizeMd, fontSize: TextUnit = Dimens.fontSizeBase)` |
| iOS | `AvatarCircle(initials: String, size: CGFloat = Dimens.avatarMd)` |

Never roll your own `Box(Modifier.size(...).clip(CircleShape).background(...))` — every avatar goes through `AvatarCircle`. Pass `size` from one of the avatar tokens (`avatarSm/Md/Lg/Xl` or `detailAvatarSize`).

```kotlin
// BAD
Box(modifier = Modifier.size(Dimens.avatarSizeLg).clip(CircleShape).background(brush = Brush.linearGradient(...)),
    contentAlignment = Alignment.Center) {
    Text(initials.uppercase(), color = PrimaryDark, fontWeight = FontWeight.Bold)
}

// GOOD
AvatarCircle(initials = initials, size = Dimens.avatarSizeLg)
```

---

### 8. CheckRow — promote to `core/ui/components/`

**Status:** Currently a private function in `BookingScreen.kt`. Extract before any other screen needs it.

**Android** (`core/ui/components/CheckRow.kt`):
```kotlin
@Composable
fun CheckRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    isLast: Boolean = false,
    leadingIcon: ImageVector? = null,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(vertical = Dimens.paddingM),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (leadingIcon != null) {
                AppIcon(imageVector = leadingIcon, contentDescription = null, tint = Primary)
                HSpace(Dimens.paddingM)
            }
            AppLabelText(text = label, modifier = Modifier.weight(1f), color = TextSecondary)
            AppBodyText(text = value, color = TextPrimary)
        }
        if (!isLast) HorizontalDivider(color = BorderColor)
    }
}
```

**iOS** (`Core/UI/Components/CheckRow.swift`):
```swift
struct CheckRow: View {
    let label: String
    let value: String
    var leadingIcon: String? = nil
    var isLast: Bool = false

    var body: some View {
        VStack(spacing: Spacing.none) {
            HStack {
                if let icon = leadingIcon { AppIcon(systemName: icon, tint: .appPrimary) }
                AppLabelText(text: label, color: .appTextSecondary)
                Spacer()
                AppBodyText(text: value, color: .appTextPrimary)
            }
            .padding(.vertical, Spacing.m)
            if !isLast { Divider().background(Color.appBorder) }
        }
    }
}
```

Bad → Good:
```kotlin
// BAD — local function inside a screen file
@Composable
private fun CheckRow(label: String, value: String, isLast: Boolean = false) { ... }

// GOOD — single component in core/ui/components, imported everywhere
CheckRow(label = "Reason", value = "Consultation")
CheckRow(label = "Date & time", value = "$dateLabel · $timeLabel")
CheckRow(label = "Institute", value = clinicName, isLast = true)
```

---

### Exemptions — leave these as raw primitives

| Context | Why |
|---|---|
| `Text(...).tag(N)` in `Picker` / `TabView` segments | SwiftUI system API — `.tag()` requires a raw `Text` |
| `Text(...)` in `.alert { } message:` slot | System alert message slot ignores custom view styling |
| Compound `Text(...) + Text(...)` concatenation | Annotated-string equivalent — cannot be wrapped |
| Compose `buildAnnotatedString { withStyle { } }` | Annotated strings require raw `Text(annotated)` |
| `HeaderAction.TitleAction(style:)` in `AppGradientHeader` | Dynamic `TextStyle` parameter — resolved at runtime |
| Proportional / computed font sizes | Size derived from layout geometry, not a fixed token |
| `Icon` inside `OutlinedTextField`'s `leadingIcon=`/`trailingIcon=` slots | Material slot expects the bare `Icon` type signature |
| `Icon` / `Image` inside a wrapper component body | The wrapper IS the styling layer |
| `Spacer(Modifier.weight(1f))` (no fixed size) | Weight-based flex, not a fixed gap |
| `Button` / `TextButton` inside `core/ui/components/Buttons.kt` | Source of the wrappers themselves |

### Audit a screen file

```bash
# Should return 0 in any view/* or Features/* file:
grep -nE '^\s*Icon\(|^\s*IconButton\(|^\s*Spacer\(Modifier\.(height|width)|^\s*Text\(' <file>
grep -nE '^\s*Button\(|^\s*OutlinedButton\(|^\s*TextButton\(' <file>
grep -nE '\.font\(\.system\(|\.foregroundColor\(.*\)\.font\(' <file>
```

---

## UI Rules (always enforced)

- **Tokens only** — `Dimens.*` / `Spacing.*` / `Sizing.*` / `Radius.*` for all dimensions. No `.dp`, `.sp`, or bare `CGFloat` literals in UI files.
- **No hardcoded colors** — use `Color.appPrimary` / `.appPrimary` and the full token set.
- **Localization** — all user-visible strings in `strings/twine.txt` → Android: `stringResource(R.string.key)` · iOS: `NSLocalizedString("key", value: "...", comment: "")`
- **State** — Android `collectAsStateWithLifecycle()` · iOS `@StateObject` at ownership view, `.task { }` for async loading.
- **Shell** — form/detail/auth → `AppFormScreen`/`AppScreen` · gradient hero → `AppGradientHeader`/`AppGradientHeaderView`. Never raw `GradientHeader` + custom layout.
- **Extract** repeated sub-layouts to private composables/ViewBuilders in the same file.

---

## Bad vs Good

### Screen shell — Android
```kotlin
// Bad
Scaffold { AppToolbar(...); Column(Modifier.verticalScroll(...).padding(20.dp).background(Color(0xFFF4F6FB))) { ... } }

// Good
AppFormScreen(title = stringResource(R.string.screen_foo), onNavigateBack = ...,
    error = uiState.error, onErrorDismissed = { viewModel.clearError() }) { ... }
```

### Search on gradient — Android
```kotlin
// Bad — hardcoded white-opacity colors on OutlinedTextField
// Good
SearchCard(searchQuery = q, onSearchQueryChange = { viewModel.updateSearchQuery(it) },
    ..., variant = SearchInputVariant.Dark)
```

### Dimensions
```kotlin
// Bad
.padding(17.dp); .height(53.dp); RoundedCornerShape(22.dp); border(1.dp, ...)

// Good
.padding(Dimens.paddingL); .height(Dimens.buttonHeight); RoundedCornerShape(Dimens.radiusXl); border(Dimens.borderThin, ...)
```
```swift
// Bad
.padding(.horizontal, 14); .frame(height: 52); .cornerRadius(10); .font(.system(size: 13))

// Good
.padding(.horizontal, Spacing.plus); .frame(height: Sizing.inputHeight); .cornerRadius(Radius.input); .font(.label)
```

### Form label — iOS
```swift
// Bad
HStack { Text(label).font(.label).foregroundColor(.appTextPrimary); if required { Text(" *").foregroundColor(.appDanger) } }

// Good
FormFieldLabel(label: label, required: required)
```

---

## New Screen: Archetypes

| Archetype | When to use | Android shell | iOS shell |
|---|---|---|---|
| **`form`** | Auth, editors, settings, single-action flows | `AppFormScreen { … }` | `AppScreen { … }` |
| **`gradient`** | Dashboards, feeds, profile, detail screens | `AppGradientHeader` + body | `AppGradientHeaderView` + body |

**Form examples:** `LoginScreen`, `RegisterScreen`, `ForgotPasswordScreen`, `EditProfileScreen`, `ChangeEmailScreen`, `ChangePasswordScreen`.

**Gradient examples:** `HomeScreen`, `MyAppointmentsScreen`, `ProfileScreen`, `PractitionerDetailScreen`.

## New Screen: Template Substitution

Run from project root:
```bash
NAME="$1"
archetype="$2"       # "form" or "gradient"
area="$3"            # e.g. "dashboard", "auth", "dashboard/profile/profile_edit"
desc="${4:-TODO add description}"

name_snake=$(echo "$NAME" | sed -E 's/([a-z0-9])([A-Z])/\1_\2/g' | tr '[:upper:]' '[:lower:]')
name_camel=$(echo "$NAME" | awk '{ print tolower(substr($0,1,1)) substr($0,2) }')

case "$archetype" in
  form)     android_tpl="FormScreen.kt.template";     ios_tpl="FormScreen.swift.template" ;;
  gradient) android_tpl="GradientScreen.kt.template"; ios_tpl="GradientScreen.swift.template" ;;
  *) echo "archetype must be 'form' or 'gradient'"; exit 1 ;;
esac

android_out="androidApp/src/main/kotlin/lu/esklepios/app/view/${area}/${name_snake}/${NAME}Screen.kt"
ios_area=$(echo "$area" | awk -F/ '{
    out = ""
    for (i = 1; i <= NF; i++) {
        n = split($i, w, "_")
        seg = ""
        for (j = 1; j <= n; j++) { seg = seg toupper(substr(w[j], 1, 1)) substr(w[j], 2) }
        out = (i > 1 ? out "/" seg : seg)
    }
    print out
}')
ios_out="iosApp/eSklepios/Features/${ios_area}/${NAME}/${NAME}View.swift"

mkdir -p "$(dirname "$android_out")" "$(dirname "$ios_out")"

sed -e "s/{{NAME}}/$NAME/g" -e "s/{{name_snake}}/$name_snake/g" \
    -e "s/{{name_camel}}/$name_camel/g" -e "s|{{area}}|${area//\//.}|g" \
    -e "s|{{DESCRIPTION}}|$desc|g" \
    ".claude/templates/android-compose/$android_tpl" > "$android_out"

sed -e "s/{{NAME}}/$NAME/g" -e "s/{{name_snake}}/$name_snake/g" \
    -e "s/{{name_camel}}/$name_camel/g" -e "s|{{DESCRIPTION}}|$desc|g" \
    ".claude/templates/ios-swiftui/$ios_tpl" > "$ios_out"

echo "Generated: $android_out  $ios_out"
```

---

## New Screen: Full Wiring

### 1. Shared ViewModel
File: `shared/src/commonMain/kotlin/lu/esklepios/app/presentation/viewmodel/<Name>ViewModel.kt`
```kotlin
class FooViewModel(private val repo: FooRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(FooUiState())
    val uiState: StateFlow<FooUiState> = _uiState.asStateFlow()

    fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            repo.getFoo()
                .onSuccess { data -> _uiState.update { it.copy(isLoading = false, items = data) } }
                .onFailure { e -> _uiState.update { it.copy(isLoading = false, error = e.message) } }
        }
    }

    fun clearError() { _uiState.update { it.copy(error = null) } }
}

data class FooUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val items: List<FooItem> = emptyList()
)
```
Register in `SharedModule.kt`: `factoryOf(::FooViewModel)`

### 2. Android Navigation
`NavDestination.kt`: `object Foo : NavDestination("foo")`
`AppNavGraph.kt`: `composable(NavDestination.Foo.route) { FooScreen() }`

### 3. Android Screen
File: `androidApp/.../view/<area>/<name>/FooScreen.kt`
```kotlin
@Composable
fun FooScreen(viewModel: FooViewModel = koinViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) { viewModel.load() }

    AppFormScreen(
        title = stringResource(R.string.foo_screen_title),
        onNavigateBack = { /* navController.popBackStack() */ },
        error = uiState.error,
        onErrorDismissed = { viewModel.clearError() }
    ) {
        when {
            uiState.isLoading -> LoadingIndicator()
            uiState.items.isEmpty() -> EmptyStateView(icon = Icons.Filled.Info, title = ..., subtitle = ...)
            else -> { /* content */ }
        }
    }
}
```

### 4. iOS ViewModelWrapper
File: `iosApp/.../Features/<Area>/<Name>/FooViewModelWrapper.swift`
```swift
@MainActor
class FooViewModelWrapper: ObservableObject {
    let viewModel: FooViewModel
    @Published var uiState: FooUiState
    private var stateObserver: FlowWatcher?

    init(viewModel: FooViewModel = KoinHelper.get()) {
        self.viewModel = viewModel
        uiState = viewModel.uiState.value as! FooUiState
        stateObserver = FlowExtensionsKt.watch(viewModel.uiState) { [weak self] state in
            guard let state = state as? FooUiState else { return }
            Task { @MainActor [weak self] in self?.uiState = state }
        }
    }

    deinit { stateObserver?.close() }
}
```

### 5. iOS View
File: `iosApp/.../Features/<Area>/<Name>/FooView.swift`
```swift
struct FooView: View {
    @StateObject private var viewModel = FooViewModelWrapper()

    var body: some View {
        AppScreen(
            title: NSLocalizedString("foo_screen_title", value: "Foo", comment: ""),
            onBack: { /* dismiss() */ },
            error: viewModel.uiState.error,
            onErrorDismissed: { viewModel.viewModel.clearError() }
        ) {
            if viewModel.uiState.isLoading {
                LoadingView()
            } else if viewModel.uiState.items.isEmpty {
                EmptyStateView(icon: "info.circle", title: "...", message: "...")
            } else {
                // content
            }
        }
        .task { viewModel.viewModel.load() }
    }
}
```

### 6. Localization
Add to `strings/twine.txt`:
```
[foo.screen_title]
en = Foo
fr = …
de = …
lb = …
```
Run `make strings`.

### 7. Test
File: `shared/src/commonTest/kotlin/lu/esklepios/app/FooViewModelTest.kt`
```kotlin
@OptIn(ExperimentalCoroutinesApi::class)
class FooViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    @BeforeTest fun setUp() { Dispatchers.setMain(dispatcher) }
    @AfterTest fun tearDown() { Dispatchers.resetMain() }

    private val fakeRepo = object : FooRepository {
        var result: Result<List<FooItem>> = Result.success(emptyList())
        override suspend fun getFoo() = result
    }
    private val viewModel = FooViewModel(fakeRepo)

    @Test fun `load success populates items`() = runTest {
        fakeRepo.result = Result.success(listOf(FooItem("1")))
        viewModel.load()
        dispatcher.scheduler.advanceUntilIdle()
        assertEquals(1, viewModel.uiState.value.items.size)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test fun `load failure sets error`() = runTest {
        fakeRepo.result = Result.failure(Exception("network error"))
        viewModel.load()
        dispatcher.scheduler.advanceUntilIdle()
        assertNotNull(viewModel.uiState.value.error)
    }
}
```

---

## When to Extend the Wrapper System

A new wrapper is added when the same primitive shape appears **3+ times** across screens. Process:

1. Add `core/ui/components/<Name>.kt` (Android) **and** `Core/UI/Components/<Name>.swift` (iOS).
2. Both files use `Dimens.*` / `Spacing.*` tokens only (Rule UI-1a).
3. Both files include a `@Preview` / `#Preview` covering at least 2 states.
4. Add a new row in this file under the right section.
5. Refactor all existing call sites to the new wrapper in the same PR.

All wrappers must:
- Use design-system tokens for every dimension and color.
- Support localization (no hardcoded user-visible strings).
- Default `contentDescription` / `accessibilityLabel` correctly (informative ⇒ required, decorative ⇒ `null` with the `// a11y: decorative — labelled by adjacent Text` comment).

---

## Checklist

**Always (new and existing screens):**
- [ ] Screen shell: `AppFormScreen`/`AppScreen` or `AppGradientHeader`/`AppGradientHeaderView` — not raw `Scaffold`/`ScrollView`/`GradientHeader`
- [ ] Zero hardcoded dimensions — no `.dp`, `.sp`, or bare `CGFloat` literals; all from `Dimens.kt` / `AppDimens.swift` / `AppFonts.swift`
- [ ] No hardcoded colors — use `Color.appPrimary` and friends
- [ ] All strings via `stringResource`/`NSLocalizedString`, keys in `twine.txt` (4 languages)
- [ ] Loading + error + empty states all handled
- [ ] All `Text` uses named `AppText*` components — no raw `Text(str).font(...).foregroundColor(...)`
- [ ] `FormField` / `FormFieldLabel` + `ValidationCaption`/`AppErrorText` for form inputs
- [ ] `PrimaryButton`/`SecondaryButton`/`GhostButton` for actions — no custom button styling
- [ ] `AppTextLink` for text navigation links — no inline `TextButton { Text }` or `Button.foregroundColor(.appPrimary)`
- [ ] `DividerWithLabel` for "or" dividers — no inline `Row { Divider + Text + Divider }`
- [ ] `SearchInputField`/`SearchCard` for search — not raw `OutlinedTextField`/`TextField`
- [ ] No duplicate extensions (iOS: `placeholder()`, custom `cornerRadius` → `ViewExtensions.swift`)
- [ ] Both platforms implemented (PP-1)
- [ ] Decorative `Icon`/`Image` have `contentDescription = null // a11y: decorative — ...` (Android) or `.accessibilityHidden(true)` (iOS)

**New screens only:**
- [ ] `FooUiState` fields are `val` only; has `isLoading: Boolean = false` and `error: String? = null`
- [ ] ViewModel registered with `factoryOf(::FooViewModel)` in `SharedModule.kt`
- [ ] iOS `ViewModelWrapper` uses `@MainActor` + `FlowWatcher` + `deinit { stateObserver?.close() }`
- [ ] `NavDestination` added + wired in `AppNavGraph.kt`
- [ ] iOS `navigationDestination` case added
- [ ] Twine keys added for all 4 languages, `make strings` run
- [ ] ViewModel test in `shared/src/commonTest/`
