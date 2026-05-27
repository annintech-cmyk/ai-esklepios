# eSklepios — Project Status

**Last Updated:** 2025-05-21
**Current Version:** 0.1.0 (Initial Scaffold)
**Overall Status:** In Development — Scaffold Complete

---

## Phase Completion

| Phase | Description | Status |
|-------|-------------|--------|
| Phase 1 | Shared ViewModels (11 total) | COMPLETE |
| Phase 2 | Shared Koin DI Module | COMPLETE |
| Phase 3 | Android platform files (Manifest, Storage, DI, App, Activity) | COMPLETE |
| Phase 4 | Android Navigation (NavDestination, AppNavGraph) | COMPLETE |
| Phase 5 | Android Compose Screens (15 total) | COMPLETE |
| Phase 6 | Android Unit Tests (5 files) | COMPLETE |
| Phase 7 | Shared Common Tests (3 files) | COMPLETE |
| Phase 8 | iOS Components (11 SwiftUI components) | COMPLETE |
| Phase 9 | iOS Views (15 SwiftUI screens) | COMPLETE |
| Phase 10 | iOS Navigation (AppTabView, RootView) | COMPLETE |
| Phase 11 | iOS App Entry Point (eSklepiosApp.swift) | COMPLETE |
| Phase 12 | iOS XCTest Files (5 test files) | COMPLETE |
| Phase 13 | Localization (strings/twine.txt — EN/FR/DE/LB) | COMPLETE |
| Phase 14 | .claude/ support files | COMPLETE |
| Phase 15 | docs/ files | COMPLETE |
| Phase 16 | README.md | COMPLETE |

---

## Feature Status

### Authentication
| Feature | Status | Notes |
|---------|--------|-------|
| Email/password login | Scaffolded | Needs real API integration |
| User registration | Scaffolded | Needs real API integration |
| Password reset | Scaffolded | Needs email trigger implementation |
| Google Sign-In | UI only | OAuth flow not implemented |
| Apple Sign-In | UI only | Sign in with Apple not implemented |
| Token refresh | Ktor plugin configured | Needs real refresh endpoint |
| Secure token storage | COMPLETE | EncryptedSharedPreferences (Android), Keychain (iOS) |

### Practitioner Discovery
| Feature | Status | Notes |
|---------|--------|-------|
| Practitioner search | Scaffolded | Needs API connection |
| Category filters | Scaffolded | Filter logic in HomeViewModel |
| Practitioner detail | Scaffolded | Needs real data |
| Favorites | Scaffolded | Toggle in UI, not persisted to backend |
| Map preview | COMPLETE | MapKit on iOS, placeholder on Android |

### Appointment Booking
| Feature | Status | Notes |
|---------|--------|-------|
| Slot selection | Scaffolded | Needs real slot data |
| Booking confirmation | Scaffolded | Needs API call |
| Success screen | Scaffolded | Needs appointment ID from API |

### My Appointments
| Feature | Status | Notes |
|---------|--------|-------|
| Upcoming appointments | Scaffolded | Needs API connection |
| Past appointments | Scaffolded | Needs API connection |
| Cancel appointment | Scaffolded | Needs API call + confirmation dialog |

### Profile
| Feature | Status | Notes |
|---------|--------|-------|
| View profile | Scaffolded | Needs authenticated user data |
| Edit profile | Scaffolded | Needs PUT /users/me |
| Change email | Scaffolded | Needs backend endpoint |
| Change password | Scaffolded | Needs backend endpoint |
| Logout | Scaffolded | Clears tokens, resets navigation |

---

## Known Issues

| ID | Issue | Severity | Status |
|----|-------|----------|--------|
| BUG-001 | `placeholder()` ViewModifier extension may be duplicated in multiple iOS view files | Medium | Open — move to `Components/ViewExtensions.swift` |
| BUG-002 | `Map(coordinateRegion:annotationItems:)` deprecated in iOS 17+ | Low | Open — migrate to new Map API |
| BUG-003 | Android StatusBadge uses different AppointmentStatus enum than domain model | Medium | Mitigated — bridge composable used in screens |
| BUG-004 | No real API integration — all data is mocked/stubbed | High | Planned for v0.2.0 |

---

## Test Coverage

| Area | Coverage |
|------|---------|
| Shared ViewModel state transitions | Scaffolded |
| Shared Repository (fake API) | Basic paths covered |
| Android ViewModel (MockK) | Basic paths covered |
| iOS XCTest | Instantiation + theme |
| Integration tests | None yet |
| UI/E2E tests | None yet |

---

## Dependencies Status

| Library | Version | Status |
|---------|---------|--------|
| Kotlin | 2.0.21 | Current |
| Ktor | 3.0.3 | Current |
| Koin | 4.0.0 | Current |
| SQLDelight | 2.0.2 | Current |
| Compose BOM | 2024.12.01 | Current |
| AndroidX Lifecycle | 2.8.7 | Current |

---

## Next Milestones

### v0.2.0 — API Integration
- [ ] Connect all screens to real backend API
- [ ] Implement Google OAuth flow
- [ ] Implement Apple Sign In
- [ ] End-to-end booking flow with real data
- [ ] Fix BUG-001 (duplicate ViewExtensions)

### v0.3.0 — Polish + Release Prep
- [ ] Push notifications
- [ ] CI/CD pipeline (GitHub Actions)
- [ ] Performance optimization
- [ ] Accessibility audit
- [ ] App Store / Play Store submission preparation
