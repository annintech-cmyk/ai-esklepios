# eSklepios — Design System

## Overview

eSklepios uses a custom design system shared conceptually between Android (Material3-based) and iOS (SwiftUI native). The system prioritizes clarity, accessibility, and a trustworthy healthcare aesthetic.

**Design Personality:** Professional, modern, approachable. The brand color (#3B4FE8) is a bold blue evoking trust and technology, contrasted against a soft background (#F4F6FB) for a calm, clean feel.

---

## Brand Colors

| Token | Hex | Usage |
|-------|-----|-------|
| Primary | `#3B4FE8` | CTAs, active elements, icons, tabs |
| Primary Dark | `#1A2580` | Gradient end, dark variant for press states |
| Primary Light | `#E8EBFD` | Chip backgrounds, subtle highlights, button tint |
| Primary Mid | `#6B7FF0` | Secondary accent, intermediate gradient point |
| Background | `#F4F6FB` | Screen backgrounds |
| Surface | `#FFFFFF` | Card and modal backgrounds |
| Text Primary | `#1A1A2E` | Main body text, headings |
| Text Secondary | `#6B7280` | Labels, captions, secondary info |
| Text Hint | `#9CA3AF` | Placeholder text, disabled states |
| Border | `#E5E7EB` | Dividers, input borders |
| Border Light | `#F3F4F6` | Subtle dividers |
| Success | `#10B981` | Confirmed status, success messages |
| Success Background | `#D1FAE5` | Success badge background |
| Danger | `#EF4444` | Errors, cancelled status, destructive actions |
| Danger Background | `#FEE2E2` | Error badge background |
| Warning | `#F59E0B` | Pending status, warnings |
| Warning Background | `#FEF3C7` | Warning badge background |

---

## Gradients

### Primary Gradient
Linear gradient from Primary (`#3B4FE8`) to Primary Dark (`#1A2580`).
Used in: `GradientHeader`, the `Landing` page hero, `SplashView` background.

**Android:**
```kotlin
val AppGradient = Brush.linearGradient(
    colors = listOf(Primary, PrimaryDark)
)
```

**iOS:**
```swift
LinearGradient(
    gradient: Gradient(colors: [Color.appPrimary, Color.appPrimaryDark]),
    startPoint: .topLeading,
    endPoint: .bottomTrailing
)
```

---

## Typography

### Android (Material3 Typography)
| Style | Usage | Weight | Size |
|-------|-------|--------|------|
| headlineLarge | Screen titles | Bold | 32sp |
| headlineMedium | Section headers | SemiBold | 28sp |
| titleLarge | Card titles | SemiBold | 22sp |
| titleMedium | Subsections | Medium | 16sp |
| bodyLarge | Body copy | Regular | 16sp |
| bodyMedium | Secondary body | Regular | 14sp |
| labelLarge | Button labels | Medium | 14sp |
| labelMedium | Chips, badges | Medium | 12sp |
| labelSmall | Captions | Regular | 11sp |

**Font Family:** System default (Roboto on Android).

### iOS (Dynamic Type)
| Style | Usage |
|-------|-------|
| `.largeTitle` | Screen titles |
| `.title` / `.title2` | Section headers |
| `.headline` | Card headings |
| `.body` | Main body |
| `.subheadline` | Secondary labels |
| `.caption` / `.caption2` | Small labels, badges |
| `.footnote` | Legal, fine print |

**Font Family:** SF Pro (system default on iOS).

---

## Spacing (Dimens)

| Token | Value | Usage |
|-------|-------|-------|
| `paddingXS` | 4dp/pt | Tight internal spacing |
| `paddingS` | 8dp/pt | Small gaps |
| `paddingM` | 12dp/pt | Standard gaps between elements |
| `paddingL` | 16dp/pt | Card internal padding, section margins |
| `paddingXL` | 24dp/pt | Screen horizontal padding |
| `paddingXXL` | 32dp/pt | Large vertical spacing |
| `radiusSm` | 8dp/pt | Small corners (inputs, small cards) |
| `radiusMd` | 12dp/pt | Medium corners |
| `radiusLg` | 16dp/pt | Cards, modals |
| `radiusXL` | 24dp/pt | Large rounded containers |
| `radiusPill` | 100dp/pt | Pills, chips, badges |

---

## Component Specifications

### AppButton
- Background: Primary gradient or solid Primary
- Text: White, labelLarge / .headline
- Corner: radiusPill
- Height: 52dp/pt
- Loading state: shows `CircularProgressIndicator` / `ProgressView`
- Disabled state: 50% opacity

### AppTextField
- Border: 1dp BorderColor
- Active border: 2dp Primary
- Error border: 2dp Danger
- Corner: radiusSm
- Label: bodyMedium / .subheadline, textSecondary
- Error message: labelSmall / .caption, Danger color

### AppCard
- Background: Surface (#FFFFFF)
- Shadow: 4dp elevation / iOS shadow 4pt
- Corner: radiusLg
- Default padding: paddingL

### StatusBadge
| Status | Text Color | Background |
|--------|-----------|-----------|
| Confirmed | Success | Success Background |
| Pending / Reserved | Warning | Warning Background |
| Cancelled | Danger | Danger Background |

### PractitionerCard
- Avatar: 56dp AvatarCircle (initials fallback)
- Title: titleMedium
- Subtitle: bodyMedium, TextSecondary
- Rating: star icon + decimal
- Accepting badge: Success/Danger StatusBadge

### FilterChip
- Inactive: Surface bg, BorderColor border
- Active: Primary Light bg, Primary border + text
- Corner: radiusPill
- Size: labelMedium, 36dp/pt height

### GradientHeader
- Background: Primary gradient (top to bottom)
- Min height: configurable (default 120dp/pt)
- Back button: chevron.left / ArrowBack, white icon
- Title: white text
- Content: `@ViewBuilder` / `@Composable` slot inside gradient area

---

## Navigation Components

### Bottom Navigation Bar (Android) / Tab Bar (iOS)
| Tab | Icon | Label |
|-----|------|-------|
| Home | house / Home | Home |
| My Appointments | calendar / CalendarToday | Appointments |
| Profile | person / Person | Profile |

Active state: Primary color icon + indicator
Inactive state: TextSecondary color

---

## Animation

| Type | Duration | Easing |
|------|---------|--------|
| Screen transition (Android) | 300ms | easeInOut |
| Splash fade | 300ms | easeInOut |
| Auth state change | 300ms | easeInOut |
| Filter chip toggle | 150ms | spring |
| Loading skeleton | Infinite shimmer | linear |

---

## Accessibility

- Minimum contrast ratio: 4.5:1 for normal text, 3:1 for large text
- Minimum tap target: 48dp (Android) / 44pt (iOS)
- All interactive elements must have a content description / accessibility label
- Support for Dynamic Type on iOS (all font sizes should scale)
- Support for font scale on Android (SP units, not DP for text)
- VoiceOver / TalkBack navigation must be logical and complete

---

## Screen Design Inventory

| Screen | Key Elements |
|--------|-------------|
| Splash | Gradient background, app logo, tagline, fade transition |
| Landing | Hero gradient header, feature bullets, Login + Register CTAs, Google/Apple SSO |
| Login | Email field, password field (toggle visibility), login button, social SSO, forgot password link |
| Register | Name fields, email, password, confirm password, terms checkbox |
| Forgot Password | Email field, submit button, success confirmation |
| Home | Search bar, location field, filter chips, practitioner card list |
| Search Results | Filtered list with count, sort option |
| Practitioner Detail | GradientHeader with avatar, info rows, slot picker, Book CTA |
| Book Appointment | Selected slot display, notes field, confirm button |
| Appointment Success | Success animation/icon, appointment details, Home + Appointments CTAs |
| My Appointments | Upcoming/Past tab bar, appointment card list, cancel action |
| Profile | Avatar, user info card, settings links (edit, email, password), logout |
| Edit Profile | First name, last name, phone fields, save button |
| Change Email | New email field, current password, submit |
| Change Password | Current password, new password, confirm new password, submit |

---

## Dark Mode

Dark mode support is **not in scope for v0.1.0**. `ThemeManager` is structured to support it in the future by switching the color set. All tokens reference the light palette for now.

Future approach:
- Android: `isSystemInDarkTheme()` in `Theme.kt` with dark color scheme
- iOS: `@Environment(\.colorScheme)` with conditional color set in `AppColors`
