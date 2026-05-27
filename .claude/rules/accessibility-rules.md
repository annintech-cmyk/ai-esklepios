# Accessibility Rules

## Rule A11Y-1: Decorative Icons Must Be Explicitly Labeled
Every `Icon()` or `Image()` composable on Android and every `Image(systemName:)` on iOS must have an explicit accessibility label. Decorative icons that are fully described by adjacent text use a specific comment pattern.

**Android:**
```kotlin
// Decorative: skip with contentDescription = null
Icon(
    imageVector = Icons.Default.Person,
    contentDescription = null // a11y: decorative — labelled by adjacent Text
)

// Informative: must have a description
Icon(
    imageVector = Icons.Default.Warning,
    contentDescription = stringResource(R.string.cd_warning)
)
```

**iOS:**
```swift
// Decorative
Image(systemName: "person.fill")
    .accessibilityHidden(true)

// Informative
Image(systemName: "exclamationmark.triangle")
    .accessibilityLabel(Text("Warning"))
```

## Rule A11Y-2: Accessibility String Keys Live in Twine Under cd.*
All user-facing accessibility labels that are not purely decorative must be defined as `cd.*` keys in `strings/twine.txt` and generated via `make strings`.

```
[cd.back]
en = Go back
fr = Retour
de = Zurück
lb = Zréck
```

Reference on Android: `stringResource(R.string.cd_back)`
Reference on iOS: `NSLocalizedString("cd_back", comment: "")`

## Rule A11Y-3: Minimum Touch Target Is 44×44pt/dp
Interactive elements (buttons, toggles, list rows) must have a minimum tap target of 44dp on Android and 44pt on iOS. Use `Modifier.minimumInteractiveComponentSize()` (Compose M3) or `.contentShape(Rectangle())` with `.frame(minWidth: 44, minHeight: 44)` on iOS.

## Rule A11Y-4: Custom Components Must Merge Semantics
When a composable contains multiple semantic elements that form one logical unit (e.g., icon + label in a row), use `Modifier.semantics(mergeDescendants = true) {}` on the container. On iOS, group with `.accessibilityElement(children: .combine)`.

## Rule A11Y-5: contentDescription = null Without Comment Is a Violation
A bare `contentDescription = null` with no `// a11y:` comment is a linting violation. The comment documents intent — either `// a11y: decorative — labelled by adjacent Text` or `// a11y: decorative — purely visual`.
