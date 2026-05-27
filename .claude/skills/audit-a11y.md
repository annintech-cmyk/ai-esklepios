# Skill: Audit Accessibility

Finds accessibility violations in Android Compose and iOS SwiftUI code.

## Usage
```
/audit-a11y
```

## Enforcement Commands

### Android — contentDescription = null Without Comment (expect 0)
```bash
grep -rn "contentDescription = null" androidApp/src/main/kotlin --include="*.kt" \
  | grep -v "// a11y:"
```
Fix: Add `// a11y: decorative — labelled by adjacent Text` or `// a11y: decorative — purely visual`.

### Android — Missing contentDescription on informative icons
```bash
grep -rn "Icon(" androidApp/src/main/kotlin --include="*.kt" \
  | grep -v "contentDescription"
```
Fix: Add `contentDescription = stringResource(R.string.cd_key)` or `contentDescription = null // a11y: decorative`.

### iOS — Images Without accessibilityLabel or accessibilityHidden
```bash
grep -rn 'Image(systemName:' iosApp/eSklepios --include="*.swift" \
  | grep -v "accessibilityLabel\|accessibilityHidden\|// a11y:"
```
Fix: Add `.accessibilityHidden(true)` (decorative) or `.accessibilityLabel(Text("description"))` (informative).

## Fix Procedure

### Decorative icon (purely visual, described by nearby text)
**Android:**
```kotlin
Icon(
    imageVector = Icons.Default.Person,
    contentDescription = null // a11y: decorative — labelled by adjacent Text
)
```
**iOS:**
```swift
Image(systemName: "person.fill")
    .accessibilityHidden(true)
```

### Informative icon (carries information not present in nearby text)
1. Add a `cd.*` key to `strings/twine.txt` in the `[[cd]]` section (all 4 languages).
2. Run `make strings`.
3. Reference: `stringResource(R.string.cd_close)` (Android) or `NSLocalizedString("cd.close", comment: "")` (iOS).

## Checklist
- [ ] 0 `contentDescription = null` without `// a11y:` comment (Android)
- [ ] 0 `Image(systemName:)` without `.accessibilityHidden` or `.accessibilityLabel` (iOS)
- [ ] All informative `cd.*` keys defined in `strings/twine.txt` with 4 translations
- [ ] Interactive elements have minimum 44dp/pt touch target
