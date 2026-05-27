# Changelog

All notable changes to eSklepios are documented here.
Format follows [Keep a Changelog](https://keepachangelog.com/en/1.0.0/).
Versioning follows [Semantic Versioning](https://semver.org/).

---

## [0.1.0] - 2025-05-21 - Initial KMM scaffold

### Added

**Shared (commonMain)**
- `SplashViewModel`, `AuthViewModel`, `HomeViewModel`, `PractitionerDetailViewModel`, `BookAppointmentViewModel`, `AppointmentSuccessViewModel`, `MyAppointmentsViewModel`, `ProfileViewModel`, `EditProfileViewModel`, `ChangeEmailViewModel`, `ChangePasswordViewModel` — complete ViewModels with UiState, StateFlow, and full action coverage
- `SharedModule.kt` — Koin DI wiring for all network, repository, use case, and ViewModel bindings
- Domain models: `User` (with `ProfileType.PATIENT/PRACTITIONER`), `Appointment` (with `AppointmentStatus.PENDING/CONFIRMED/CANCELLED/COMPLETED/NO_SHOW`), `Practitioner`, `Slot`
- Repository interfaces: `AuthRepository`, `PractitionerRepository`, `AppointmentRepository`
- Use cases: `LoginUseCase`, `RegisterUseCase`, `ForgotPasswordUseCase`, `LogoutUseCase`, `GetPractitionersUseCase`, `GetPractitionerDetailUseCase`, `GetAvailableSlotsUseCase`, `BookAppointmentUseCase`, `GetUserAppointmentsUseCase`, `CancelAppointmentUseCase`, `GetCurrentUserUseCase`, `UpdateProfileUseCase`, `ChangeEmailUseCase`, `ChangePasswordUseCase`
- `TokenStorage` interface with `setToken`, `setRefreshToken`, `getToken`, `getRefreshToken`, `clear`
- `ApiService` interface covering all v1 API endpoints
- Ktor `ApiServiceImpl` with OkHttp/Darwin engines, BearerTokenPlugin, content negotiation, logging

**Android App**
- `AndroidManifest.xml` with `ESklepiosApp`, `MainActivity`, and `INTERNET` permission
- `ESklepiosApp.kt` — Application class with Koin initialization
- `MainActivity.kt` — Single activity host for Compose NavHost
- `SecureStorage.kt` — `TokenStorage` implementation using `EncryptedSharedPreferences`
- `AndroidModule.kt` — Platform DI bindings (SecureStorage, SQLite driver)
- `NavDestination.kt` — Sealed class routing for all 15 screens
- `AppNavGraph.kt` — `NavHost` with all routes + `AppBottomNavBar`
- 15 Compose screens: Splash, Landing, Login, Register, ForgotPassword, Home, SearchResults, PractitionerDetail, BookAppointment, AppointmentSuccess, MyAppointments, Profile, EditProfile, ChangeEmail, ChangePassword
- Reusable components: `AppCard`, `AppButton`, `AppTextField`, `StatusBadge`, `GoogleSignInButton`, `AppleSignInButton`, `GradientHeader`, `MapPreviewCard`
- Theme: `Color.kt` (brand palette), `Theme.kt` (Material3), `Typography.kt`, `Dimens.kt`
- `strings.xml` with 60+ resource keys for all screens

**iOS App**
- `eSklepiosApp.swift` — App entry calling `KoinHelper.startKoin(baseUrl:enableLogging:)`
- `RootView.swift` — Auth gate routing to Splash → Landing/Home
- `AppTabView.swift` — `TabView` with Home, My Appointments, and Profile tabs
- 11 reusable SwiftUI components: `AvatarCircle`, `StatusBadge`, `FilterChip`, `InfoRow`, `PractitionerCard`, `AppointmentCard`, `MapPreviewCard`, `LoadingView`, `EmptyStateView`, `ErrorView`, `SectionTitle`
- 15 SwiftUI views matching all Android screens
- ViewModelWrappers for all 11 shared ViewModels
- `ThemeManager`, `AppColors`, `AppGradient`, `Dimens` for iOS design system
- `KeychainStorage` implementing `TokenStorage` via iOS Security.framework

**Localization**
- `strings/twine.txt` — Master localization file with 16 sections, 100+ string keys in EN/FR/DE/LB
- Android `strings.xml` generated from Twine

**Testing**
- Android ViewModel tests: `HomeViewModelTest`, `AuthViewModelTest`, `BookAppointmentViewModelTest`, `MyAppointmentsViewModelTest`, `ProfileViewModelTest`
- Shared common tests: `PractitionerRepositoryTest`, `AppointmentRepositoryTest`, `AuthRepositoryTest`
- iOS XCTest files: `HomeViewModelTests`, `AuthViewModelTests`, `BookAppointmentViewModelTests`, `KeychainStorageTests`, `ThemeManagerTests`

**Developer Tooling**
- `.claude/CLAUDE.md` — 400+ line project intelligence document
- `.claude/agents/` — 7 specialist agent guides
- `.claude/rules/` — 7 rule documents (architecture, naming, state management, platform parity, API, UI, testing)
- `.claude/skills/` — 7 skill playbooks (create-screen, create-repository, create-api, create-usecase, create-viewmodel, create-test, create-navigation)
- `.claude/hooks/` — 4 shell scripts (pre-build-check, lint-check, localization-check, dependency-check)
- `.claude/templates/` — 7 code templates (Android screen, component, test; iOS view, wrapper, test; shared ViewModel)
- `.claude/commands/update-docs-and-commit.md`
- `docs/projects_specs.md`, `docs/architecture.md`, `docs/changelog.md`, `docs/project_status.md`, `docs/project_design.md`
- `README.md` at project root

---

## [Unreleased]

### Planned for v0.2.0
- Backend API integration (replace mock responses)
- Google Sign-In OAuth flow implementation
- Apple Sign-In implementation
- Push notifications (FCM for Android, APNs for iOS)
- CI/CD pipeline (GitHub Actions)
- Practitioner search with real-time suggestions
- Appointment slot availability calendar view
- Profile avatar upload
