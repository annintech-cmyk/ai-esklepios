# UI Rules

## General

### Rule UI-1: Design System First
Always use design system tokens — never raw values:
- Colors: `Color.appPrimary`, `Color.appBackground`, etc. (Kotlin); `Color.appPrimary` (Swift extension)
- Spacing: `Dimens.paddingM`, `Dimens.paddingL`, etc.
- Corner radius: `Dimens.radiusLg`, `Dimens.radiusPill`, etc.
- Typography: defined in `Theme.kt` (Android) and `ThemeManager` / font extensions (iOS)

### Rule UI-1a: Never Introduce Hardcoded Dimensions — Always Use Design Tokens
**This is a hard rule with no exceptions outside the token files themselves.**

A "dimension" is any numeric measurement used for layout: padding, margin, spacing, width, height, corner radius, offset, border width, elevation, icon size, font size, avatar size, toolbar / sheet / card height.

| Platform | Forbidden in UI code | Required source |
|---|---|---|
| Android (Compose) | `17.dp`, `13.sp`, `0.dp`, `2.dp` literals; `PaddingValues(8.dp)`; `RoundedCornerShape(12.dp)`; `Modifier.size(40.dp)`; `Modifier.offset(2.dp, 2.dp)`; `border(1.dp, ...)`; `elevation = 4.dp` | `androidApp/.../core/ui/theme/Dimens.kt` |
| iOS (SwiftUI) | bare `CGFloat` literals in `.frame()`, `.padding()`, `.cornerRadius()`, `.offset()`, `.spacing:`, `.lineWidth:`, `.font(.system(size: N))` | `iosApp/.../Core/UI/Theme/AppDimens.swift` (`Dimens`, `Spacing`, `Sizing`, `Radius`) and `AppFonts.swift` for font sizes |

**Rules:**
1. Replace every hardcoded dimension with a design token. **Reuse existing tokens** whenever possible. Create new tokens only when no existing one fits.
2. Use **semantic names**, not numeric ones: `Dimens.paddingScreen`, `Dimens.iconChevron`, `Dimens.orbXl` — never `Dimens.padding17` or `Dimens.size40`.
3. The values `0` (for "no padding/elevation/corner") still go through tokens: `Dimens.paddingNone`, `Dimens.cornerNone`, `Dimens.elevationNone`, `Spacing.none`, `Radius.none`.
4. If a similar value already exists within ±1pt/dp, **prefer the existing token** for consistency over preserving 1pt drift. Document the consolidation in the PR.
5. Token files (`Dimens.kt`, `AppDimens.swift`, `AppFonts.swift`, `Typography.kt`) are the only places where `.dp`, `.sp`, or `CGFloat` literals may appear.

**Examples:**

```kotlin
// FORBIDDEN
.padding(17.dp)
.height(53.dp)
RoundedCornerShape(12.dp)
border(width = 1.dp, color = ...)

// REQUIRED
.padding(Dimens.paddingL)
.height(Dimens.buttonHeight)
RoundedCornerShape(Dimens.radiusMd)
border(width = Dimens.borderThin, color = ...)
```

```swift
// FORBIDDEN
.padding(.horizontal, 14)
.frame(height: 52)
.cornerRadius(10)
.font(.system(size: 13, weight: .medium))

// REQUIRED
.padding(.horizontal, Spacing.plus)
.frame(height: Sizing.inputHeight)
.cornerRadius(Radius.input)
.font(.label)
```

**Enforcement (run locally before merging):**
```bash
# Android — should return 0
grep -rEn '[0-9]+\.dp|[0-9]+\.sp' androidApp/src/main/kotlin --include="*.kt" \
  | grep -v "/theme/" | grep -v "screenHeightDp.dp"

# iOS — should return 0 for each
grep -rEn '\.font\(\.system\(size:\s*[0-9]' iosApp/eSklepios --include="*.swift" \
  | grep -vE 'Dimens\.|Spacing\.|Radius\.|Sizing\.'
grep -rEn '(HStack|VStack)\([^)]*spacing:\s*[0-9]' iosApp/eSklepios --include="*.swift" \
  | grep -vE 'Dimens\.|Spacing\.|Radius\.|Sizing\.'
```

### Rule UI-2: No Hardcoded Strings in UI
Every user-visible string must come from:
- Android: `stringResource(R.string.key)`
- iOS: `Text("key")` backed by `NSLocalizedString` (or directly from Twine-generated `.strings`)

Exception: Debug-only labels and developer error messages.

### Rule UI-3: Brand Colors
| Token | Hex | Usage |
|-------|-----|-------|
| Primary | #3B4FE8 | CTAs, active tabs, accent |
| PrimaryDark | #1A2580 | Gradient end, dark variant |
| PrimaryLight | #E8EBFD | Button backgrounds, subtle backgrounds |
| Background | #F4F6FB | Screen background |
| Surface | #FFFFFF | Card backgrounds |
| TextPrimary | #1A1A2E | Main text |
| TextSecondary | #6B7280 | Secondary labels |

## Android Compose

### Rule UI-4: Stateless Composables Preferred
Prefer composables that receive data as parameters over composables that access ViewModels directly. Only the screen-level composable calls `koinViewModel()`.

```kotlin
// PREFERRED — stateless, testable
@Composable
fun PractitionerCard(data: PractitionerCardData, onClick: () -> Unit) { ... }

// DISCOURAGED at component level
@Composable
fun PractitionerCard(viewModel: HomeViewModel = koinViewModel()) { ... }
```

### Rule UI-5: Modifier Parameter in Reusable Components
Every reusable composable should accept a `modifier: Modifier = Modifier` parameter and pass it to the root element.

### Rule UI-6: Preview Annotations
Every non-screen composable should have a `@Preview` annotation. Screens may have previews but are not required.

### Rule UI-7: Loading State Uses LoadingView or CircularProgressIndicator
```kotlin
if (uiState.isLoading) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = Color.appPrimary)
    }
}
```

### Rule UI-8: Error State Has Retry
When showing an error, provide a retry action:
```kotlin
if (uiState.error != null) {
    ErrorView(
        message = uiState.error!!,
        onRetry = { viewModel.load() }
    )
}
```

## iOS SwiftUI

### Rule UI-9: @StateObject at View Ownership Level
Only the view that owns the ViewModel lifecycle uses `@StateObject`. Child views use `@ObservedObject`.

### Rule UI-10: Avoid Force Unwrapping in UI Code
Use `if let`, `guard let`, or nil-coalescing (`??`) instead of `!` for optionals in SwiftUI views.

### Rule UI-11: Components Use ViewBuilder Where Appropriate
Components that need custom content use `@ViewBuilder`:
```swift
struct AppCard<Content: View>: View {
    let padding: CGFloat
    @ViewBuilder let content: () -> Content
}
```

### Rule UI-12: No Duplicate Extensions
View extensions (`placeholder()`, `cornerRadius(_:corners:)`, etc.) must be defined in exactly one file. Prefer `Components/ViewExtensions.swift` for shared extensions.

### Rule UI-13: Accessibility
- All images must have descriptive `accessibilityLabel` values.
- Interactive elements must be reachable via VoiceOver.
- Minimum tap target: 44x44pt.

## Cross-platform

### Rule UI-14: Raw UI Primitives Are Forbidden in Screen / Feature Files
**Hard rule — no exceptions outside `core/ui/components/`.**

Screen files (`androidApp/src/main/kotlin/lu/esklepios/app/view/**/*.kt`) and feature view files (`iosApp/eSklepios/Features/**/*.swift`) may use only:
1. Layout primitives — `Column` / `Row` / `Box` / `LazyColumn` / `LazyRow` (Compose) and `VStack` / `HStack` / `ZStack` / `ScrollView` / `LazyVStack` (SwiftUI).
2. Project wrappers from `core/ui/components/`.

The following raw primitives MUST appear only inside `core/ui/components/` (where the wrapper is defined). Outside that directory each must be replaced with its named wrapper:

| Raw primitive | Required wrapper |
|---|---|
| `Text(...)` with styling | `AppTitleText` / `AppSubtitleText` / `AppBodyText` / `AppCaptionText` / `AppLabelText` / `AppToolbarTitle` / `AppButtonText` / `AppErrorText` / `FormFieldLabel` / `ValidationCaption` |
| `Icon` (Compose) / `Image(systemName:)` (SwiftUI) | `AppIcon(imageVector / systemName, contentDescription / accessibilityLabel, tint, size)` |
| `IconButton` | `AppIconButton(icon, contentDescription, onClick, tint)` — `contentDescription` / `accessibilityLabel` is **non-nullable** |
| `Button` (filled) | `PrimaryButton` |
| `OutlinedButton` | `SecondaryButton` |
| `TextButton` — no-nav action | `GhostButton` |
| `TextButton` — inline link | `AppTextLink` |
| `Tab` / `TabRow` | `AppTabRow(selectedIndex, tabs)` + `AppTabItem(label, onClick)` |
| `Spacer(Modifier.height/width(...))` | `VSpace(token)` / `HSpace(token)` — or `Arrangement.spacedBy(token)` / `VStack(spacing:)` on the parent |
| Inline circle + background + initials | `AvatarCircle(initials, size)` |
| Inline label-value summary row | `CheckRow(label, value, isLast?, leadingIcon?)` |

**Exemptions** — these stay as raw primitives because of platform-API constraints:
- `Text(...).tag(...)` inside `Picker` / `TabView` segments (SwiftUI requires bare `Text`)
- `Text(...)` in `.alert { } message:` slot
- Annotated-string concatenation (`Text(...) + Text(...)` / `buildAnnotatedString { withStyle { ... } }`)
- `Icon` in Material `OutlinedTextField`'s `leadingIcon =` / `trailingIcon =` slot
- `HeaderAction.TitleAction(style:)` — dynamic `TextStyle` parameter resolved at runtime
- Proportional font sizes computed from layout geometry (e.g. avatar initials sized as `parentSize * 0.33`)
- `Spacer(Modifier.weight(1f))` — weight-based flex, not a fixed gap

When no existing wrapper fits, **create one in `core/ui/components/` for BOTH platforms before introducing a third call site**. See `/rebuild-primitives` for the refactor procedure and full wrapper specs (including create-ready Kotlin + Swift signatures for the wrappers that don't exist yet: `AppIcon`, `AppIconButton`, `VSpace` / `HSpace`, `AppTabRow`, `CheckRow`).

**Enforcement** (run locally before merging):
```bash
# Android — should return 0 (inside view/ directories)
grep -rEn '^\s*(Text|Icon|IconButton|Button|OutlinedButton|TextButton)\(' \
  androidApp/src/main/kotlin/lu/esklepios/app/view --include="*.kt" \
  | grep -vE 'Modifier\.weight|spacedBy'

# iOS — should return 0 (inside Features/ directories)
grep -rEn '^\s*(Image\(systemName:|Button\(action:|Text\()' \
  iosApp/eSklepios/Features --include="*.swift" \
  | grep -vE '\.tag\(|\.alert.*message:'
```
