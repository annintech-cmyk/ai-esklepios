# UI Rules

## General

### Rule UI-1: Design System First
Always use design system tokens — never raw values:
- Colors: `Color.appPrimary`, `Color.appBackground`, etc. (Kotlin); `Color.appPrimary` (Swift extension)
- Spacing: `Dimens.paddingM`, `Dimens.paddingL`, etc.
- Corner radius: `Dimens.radiusLg`, `Dimens.radiusPill`, etc.
- Typography: defined in `Theme.kt` (Android) and `ThemeManager` / font extensions (iOS)

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
