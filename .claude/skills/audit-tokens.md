# Skill: Audit Design Tokens

Finds and fixes hardcoded dimension, color, and font-size literals that should be design tokens.

## Usage
```
/audit-tokens
```

## Enforcement Commands

### Android — Hardcoded Dimensions (expect 0)
```bash
grep -rEn '[0-9]+\.dp|[0-9]+\.sp' androidApp/src/main/kotlin --include="*.kt" \
  | grep -v "/theme/" | grep -v "screenHeightDp.dp"
```

### Android — Hardcoded Colors (expect 0)
```bash
grep -rEn 'Color\(0x[A-Fa-f0-9]' androidApp/src/main/kotlin --include="*.kt" \
  | grep -v "/theme/"
```

### iOS — Hardcoded Font Sizes (expect 0)
```bash
grep -rEn '\.font\(\.system\(size:\s*[0-9]' iosApp/eSklepios --include="*.swift" \
  | grep -vE 'Dimens\.|Spacing\.|Radius\.|Sizing\.'
```

### iOS — Hardcoded Spacing in Stacks (expect 0)
```bash
grep -rEn '(HStack|VStack)\([^)]*spacing:\s*[0-9]' iosApp/eSklepios --include="*.swift" \
  | grep -vE 'Dimens\.|Spacing\.|Radius\.|Sizing\.'
```

### iOS — Hardcoded Hex Colors (expect 0 outside AppColors.swift)
```bash
grep -rEn 'Color\(hex: "[A-F0-9]' iosApp/eSklepios --include="*.swift" \
  | grep -v "/Theme/AppColors.swift"
```

## Fix Procedure

### For hardcoded dimensions
1. Find the nearest existing token in `Dimens.kt` (Android) or `AppDimens.swift` / `Spacing` / `Radius` / `Sizing` (iOS) within ±1pt.
2. If no match exists, add a semantic token to the theme file.
3. Replace the literal with the token.
4. Re-run the grep — expect 0 violations.

### For hardcoded colors
1. Check if a semantically equivalent color already exists in `Color.kt` (Android) or `AppColors.swift` (iOS).
2. Add the color to the theme file if missing, with a semantic name.
3. Replace the inline `Color(hex:)` / `Color(0xFF...)` with the token reference.

## Token Files
| Platform | File | What it contains |
|---------|------|-----------------|
| Android | `androidApp/.../core/ui/theme/Dimens.kt` | All dp/sp values |
| Android | `androidApp/.../core/ui/theme/Color.kt` | All color values |
| iOS | `iosApp/.../Core/UI/Theme/AppDimens.swift` | `Dimens`, `Spacing`, `Sizing`, `Radius` structs |
| iOS | `iosApp/.../Core/UI/Theme/AppColors.swift` | All `Color(hex:)` values |
| iOS | `iosApp/.../Core/UI/Theme/AppFonts.swift` | Font size tokens |
