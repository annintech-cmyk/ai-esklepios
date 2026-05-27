# Prompt — Recreate a Healthcare Booking KMM App From Scratch

> Copy everything between the `<PROMPT>` and `</PROMPT>` markers and paste it as the **first message** to a coding assistant (e.g. Claude Code) in an empty repository. The prompt is self-contained: folder layout, theme, screens, workflows, architecture, and rules are all included. Replace the **PROJECT IDENTITY** values at the top to retarget the prompt to a different domain or brand.

---

<PROMPT>

You are a senior mobile architect. Build a production-grade **Kotlin Multiplatform Mobile (KMM)** application from scratch in the current working directory, faithfully matching the **architecture, folder layout, theme, UI components, and user workflows** described below. Do not ask clarifying questions — every decision is specified. Output code, not commentary. Generate all files in one pass, ordered so the project builds end-to-end.

═══════════════════════════════════════════════════════════════════
PROJECT IDENTITY  (change these values to retarget the prompt)
═══════════════════════════════════════════════════════════════════

- **App name:** eSklepios
- **Domain:** Healthcare appointment booking for Luxembourg
- **Package / Bundle ID:** `lu.esklepios.app`
- **Android target:** minSdk 26, targetSdk 35
- **iOS target:** iOS 17+
- **Languages:** English (en), French (fr), German (de), Luxembourgish (lb)
- **API base (prod):** `https://api.esklepios.lu`
- **Brand primary color:** `#3B4FE8`

═══════════════════════════════════════════════════════════════════
1. TECH STACK & VERSIONS  (use exactly these — pin in `gradle/libs.versions.toml`)
═══════════════════════════════════════════════════════════════════

| Tool / Lib | Version |
|---|---|
| Kotlin | 2.0.21 |
| Android Gradle Plugin | 8.5.2 |
| Compose BOM | 2024.12.01 |
| Ktor | 3.0.3 |
| Koin | 4.0.0 |
| SQLDelight | 2.0.2 |
| kotlinx-coroutines | 1.9.0 |
| kotlinx-serialization-json | 1.7.3 |
| kotlinx-datetime | 0.6.1 |
| AndroidX Lifecycle ViewModel | 2.8.7 |
| AndroidX Navigation Compose | 2.8.5 |
| Coil Compose | 2.7.0 |
| Multiplatform Settings | 1.2.0 |
| BuildKonfig | 0.15.2 |
| Security Crypto (Android) | 1.1.0-alpha06 |
| Turbine | 1.2.0 |
| MockK (Android-only) | 1.13.13 |

iOS uses **SwiftUI** (no UIKit screens), MapKit, Security.framework (Keychain), Swift 5.9+, Xcode 15+.

═══════════════════════════════════════════════════════════════════
2. ARCHITECTURE (Clean Architecture + MVVM, ViewModels in shared KMM)
═══════════════════════════════════════════════════════════════════

```
UI (Compose / SwiftUI)
    ↓
ViewModel (shared, extends androidx.lifecycle.ViewModel)
    ↓
UseCase (shared/domain)
    ↓
Repository Interface (shared/domain)
    ↓ implemented by
Repository Impl (shared/data)
    ↓
ApiService (Ktor) / SQLDelight / TokenStorage (platform-specific)
```

**Hard rules — enforce in every file you generate:**

- **A-1** Dependencies point inward. Domain never imports data. ViewModels never import Compose/SwiftUI/Ktor/SQLDelight.
- **A-2** `commonMain` may only use kotlin-stdlib, kotlinx-*, Koin core, project domain/data interfaces. No `android.*`, `java.*`, `UIKit`, `SwiftUI`.
- **A-3** ViewModels hold no `Context`, `Bitmap`, `UIImage`, Compose `State`, or SwiftUI `Binding`.
- **A-4** Use cases are single-responsibility, expose only `operator fun invoke(...)`.
- **A-5** Repository interface lives in `domain/repository/`, implementation in `data/repository/`.
- **A-6** One ViewModel per screen. Shared state goes through a repository.
- **A-7** Use `StateFlow<UiState>`. No `LiveData` in commonMain.
- **A-8** All Koin bindings in module files only: `SharedModule.kt`, `AndroidModule.kt`, iOS Koin module via `KoinHelper`.

═══════════════════════════════════════════════════════════════════
3. FULL FOLDER STRUCTURE  (create every directory; one class per file)
═══════════════════════════════════════════════════════════════════

```
esklepios/
├── build.gradle.kts                  (root)
├── settings.gradle.kts
├── gradle.properties
├── gradle/libs.versions.toml         (version catalog — values from §1)
├── dev.properties                    (BASE_URL=https://dev-api.esklepios.lu, ENABLE_LOGGING=true)
├── prod.properties                   (BASE_URL=https://api.esklepios.lu, ENABLE_LOGGING=false)
├── Makefile                          (`make strings` → twine generate)
├── strings/twine.txt                 (single source of truth for all 4 languages)
│
├── androidApp/
│   ├── build.gradle.kts
│   └── src/main/
│       ├── AndroidManifest.xml
│       └── kotlin/lu/esklepios/app/
│           ├── ESklepiosApp.kt                    (Application — startKoin)
│           ├── MainActivity.kt                    (single-Activity host)
│           ├── di/AndroidModule.kt
│           ├── storage/SecureStorage.kt           (EncryptedSharedPreferences → TokenStorage)
│           ├── core/
│           │   ├── navigation/
│           │   │   ├── NavDestination.kt          (sealed class with route strings)
│           │   │   └── AppNavGraph.kt             (NavHost + composable() entries)
│           │   └── ui/
│           │       ├── theme/
│           │       │   ├── Color.kt
│           │       │   ├── Dimens.kt
│           │       │   ├── Gradients.kt
│           │       │   ├── Typography.kt
│           │       │   └── AppTheme.kt
│           │       └── components/
│           │           ├── AppDrawer.kt
│           │           ├── AppFormScreen.kt
│           │           ├── AppToolbar.kt
│           │           ├── AvailabilityChip.kt
│           │           ├── AvatarCircle.kt
│           │           ├── Buttons.kt              (PrimaryButton, SecondaryButton, GhostButton, Google/AppleSignInButton)
│           │           ├── Cards.kt                (AppCard wrappers)
│           │           ├── EmptyStateView.kt
│           │           ├── ErrorView.kt
│           │           ├── FilterChip.kt
│           │           ├── FormField.kt
│           │           ├── GradientHeader.kt
│           │           ├── InfoRow.kt
│           │           ├── LoadingIndicator.kt
│           │           ├── MapPreviewCard.kt
│           │           ├── PractitionerCard.kt
│           │           ├── SearchCard.kt
│           │           ├── SearchInputField.kt
│           │           ├── StatusBadge.kt
│           │           └── Typography.kt           (text style wrappers)
│           └── features/
│               ├── mainhome/
│               │   ├── splash/SplashScreen.kt
│               │   ├── landing/LandingScreen.kt
│               │   ├── home/HomeScreen.kt
│               │   ├── search/SearchResultsScreen.kt
│               │   └── practitionerdetail/PractitionerDetailScreen.kt
│               ├── auth/
│               │   ├── login/LoginScreen.kt
│               │   ├── register/RegisterScreen.kt
│               │   └── forgotpassword/ForgotPasswordScreen.kt
│               ├── appointment/
│               │   ├── book/BookAppointmentScreen.kt
│               │   └── success/AppointmentSuccessScreen.kt
│               ├── appointments/MyAppointmentsScreen.kt
│               ├── profile/ProfileScreen.kt
│               └── profileedit/
│                   ├── EditProfileScreen.kt
│                   ├── ChangeEmailScreen.kt
│                   └── ChangePasswordScreen.kt
│
├── iosApp/
│   ├── eSklepios.xcodeproj/                       (generate scheme: eSklepios)
│   ├── eSklepios/
│   │   ├── eSklepiosApp.swift                     (@main, calls KoinHelper.startKoin)
│   │   ├── Info.plist
│   │   ├── Assets.xcassets/
│   │   ├── Storage/KeychainStorage.swift          (Security.framework → TokenStorage)
│   │   ├── Core/
│   │   │   ├── DI/KoinHelper.swift                (startKoin(baseUrl:, enableLogging:), generic get())
│   │   │   ├── Navigation/
│   │   │   │   ├── RootView.swift                 (auth gate: Splash → Landing or AppTabView)
│   │   │   │   └── AppTabView.swift               (TabView + NavigationStack per tab)
│   │   │   └── UI/
│   │   │       ├── Theme/
│   │   │       │   ├── AppColors.swift            (Color extensions: appPrimary, etc.)
│   │   │       │   ├── AppDimens.swift
│   │   │       │   ├── AppFonts.swift
│   │   │       │   └── ThemeManager.swift
│   │   │       └── Components/
│   │   │           ├── AppCard.swift              (ViewBuilder content)
│   │   │           ├── AppScreen.swift            (background + safe-area wrapper)
│   │   │           ├── AppToolbar.swift
│   │   │           ├── AppointmentCard.swift
│   │   │           ├── AvatarCircle.swift
│   │   │           ├── EmptyStateView.swift
│   │   │           ├── ErrorView.swift
│   │   │           ├── FilterChip.swift
│   │   │           ├── GhostButton.swift
│   │   │           ├── GradientHeader.swift       (init: minHeight, onBack, trailingAction, trailingIcon, content)
│   │   │           ├── InfoRow.swift
│   │   │           ├── LoadingView.swift
│   │   │           ├── MapPreviewCard.swift       (use new closure-based Map API, iOS 17+)
│   │   │           ├── PractitionerCard.swift
│   │   │           ├── PrimaryButton.swift
│   │   │           ├── SearchCard.swift
│   │   │           ├── SearchInputField.swift
│   │   │           ├── SecondaryButton.swift
│   │   │           ├── SectionTitle.swift
│   │   │           ├── StatusBadge.swift
│   │   │           └── ViewExtensions.swift       (single `placeholder()` definition — do NOT duplicate)
│   │   └── Features/
│   │       ├── MainHome/
│   │       │   ├── Splash/{SplashView.swift, SplashViewModelWrapper.swift}
│   │       │   ├── Landing/LandingView.swift
│   │       │   ├── Home/{HomeView.swift, HomeViewModelWrapper.swift}
│   │       │   ├── Search/SearchResultsView.swift
│   │       │   └── PractitionerDetail/{PractitionerDetailView.swift, PractitionerDetailViewModelWrapper.swift}
│   │       ├── Auth/
│   │       │   ├── Login/{LoginView.swift, AuthViewModelWrapper.swift}
│   │       │   ├── Register/RegisterView.swift
│   │       │   └── ForgotPassword/ForgotPasswordView.swift
│   │       ├── Appointment/
│   │       │   ├── Book/{BookAppointmentView.swift, BookAppointmentViewModelWrapper.swift}
│   │       │   └── Success/{AppointmentSuccessView.swift, AppointmentSuccessViewModelWrapper.swift}
│   │       ├── Appointments/{MyAppointmentsView.swift, MyAppointmentsViewModelWrapper.swift}
│   │       ├── Profile/{ProfileView.swift, ProfileViewModelWrapper.swift}
│   │       └── ProfileEdit/
│   │           ├── EditProfileView.swift + EditProfileViewModelWrapper.swift
│   │           ├── ChangeEmailView.swift + ChangeEmailViewModelWrapper.swift
│   │           └── ChangePasswordView.swift + ChangePasswordViewModelWrapper.swift
│   └── eSklepiosTests/
│       ├── AuthViewModelTests.swift
│       ├── HomeViewModelTests.swift
│       ├── BookAppointmentViewModelTests.swift
│       ├── KeychainStorageTests.swift              (must clean up keys in tearDown)
│       └── ThemeManagerTests.swift
│
└── shared/
    ├── build.gradle.kts                            (KMM: androidTarget, iosX64/iosArm64/iosSimulatorArm64; XCFramework name "shared")
    └── src/
        ├── commonMain/kotlin/lu/esklepios/app/
        │   ├── data/
        │   │   ├── db/DatabaseDriverFactory.kt     (expect class)
        │   │   ├── network/
        │   │   │   ├── ApiService.kt               (interface — all endpoints return Result<T>)
        │   │   │   ├── KtorApiService.kt           (impl)
        │   │   │   ├── HttpClientFactory.kt        (Ktor config + Auth/Logging/ContentNegotiation)
        │   │   │   ├── DTOs.kt                     (@Serializable request/response classes)
        │   │   │   ├── Mappers.kt                  (DTO → domain model)
        │   │   │   └── TokenStorage.kt             (interface: setToken/setRefreshToken/getToken/getRefreshToken/clear)
        │   │   └── repository/
        │   │       ├── AuthRepositoryImpl.kt
        │   │       ├── PractitionerRepositoryImpl.kt
        │   │       ├── AppointmentRepositoryImpl.kt
        │   │       └── UserRepositoryImpl.kt
        │   ├── di/SharedModule.kt                  (factoryOf for every ViewModel + UseCase, single for repos/HttpClient/ApiService)
        │   ├── domain/
        │   │   ├── model/
        │   │   │   ├── User.kt + ProfileType enum
        │   │   │   ├── Practitioner.kt
        │   │   │   ├── Appointment.kt + AppointmentStatus enum
        │   │   │   ├── AppointmentSlot.kt
        │   │   │   └── ScheduleDay.kt
        │   │   ├── repository/
        │   │   │   ├── AuthRepository.kt
        │   │   │   ├── PractitionerRepository.kt
        │   │   │   ├── AppointmentRepository.kt
        │   │   │   └── UserRepository.kt
        │   │   └── usecase/
        │   │       ├── LoginUseCase.kt
        │   │       ├── RegisterUseCase.kt
        │   │       ├── LogoutUseCase.kt
        │   │       ├── ForgotPasswordUseCase.kt
        │   │       ├── ChangeEmailUseCase.kt
        │   │       ├── ChangePasswordUseCase.kt
        │   │       ├── GetProfileUseCase.kt
        │   │       ├── UpdateProfileUseCase.kt
        │   │       ├── SearchPractitionersUseCase.kt
        │   │       ├── GetPractitionerDetailUseCase.kt
        │   │       ├── ToggleFavoriteUseCase.kt
        │   │       ├── CreateAppointmentUseCase.kt
        │   │       ├── CancelAppointmentUseCase.kt
        │   │       ├── ModifyAppointmentUseCase.kt
        │   │       ├── GetUpcomingAppointmentsUseCase.kt
        │   │       └── GetPastAppointmentsUseCase.kt
        │   ├── presentation/viewmodel/
        │   │   ├── SplashViewModel.kt + SplashUiState
        │   │   ├── AuthViewModel.kt + AuthUiState        (login, register, forgot)
        │   │   ├── HomeViewModel.kt + HomeUiState
        │   │   ├── PractitionerDetailViewModel.kt + UiState
        │   │   ├── BookAppointmentViewModel.kt + UiState
        │   │   ├── AppointmentSuccessViewModel.kt + UiState
        │   │   ├── MyAppointmentsViewModel.kt + UiState
        │   │   ├── ProfileViewModel.kt + UiState
        │   │   ├── EditProfileViewModel.kt + UiState
        │   │   ├── ChangeEmailViewModel.kt + UiState
        │   │   └── ChangePasswordViewModel.kt + UiState
        │   └── util/FlowExtensions.kt
        ├── androidMain/kotlin/lu/esklepios/app/
        │   ├── data/db/DatabaseDriverFactory.kt          (AndroidSqliteDriver)
        │   └── data/network/HttpClientEngine.kt          (OkHttp)
        ├── iosMain/kotlin/lu/esklepios/app/
        │   ├── data/db/DatabaseDriverFactory.kt          (NativeSqliteDriver)
        │   ├── data/network/HttpClientEngine.kt          (Darwin)
        │   ├── data/preferences/IosTokenStorage.kt
        │   ├── di/IosModule.kt
        │   └── di/IosKoinInit.kt                         (top-level fun doInitKoin(baseUrl, enableLogging))
        ├── commonTest/kotlin/lu/esklepios/app/
        │   ├── AuthRepositoryTest.kt
        │   ├── PractitionerRepositoryTest.kt
        │   └── AppointmentRepositoryTest.kt
        └── commonMain/sqldelight/lu/esklepios/app/db/
            └── ESklepiosDatabase.sq                       (favorites, recent_searches tables)
```

═══════════════════════════════════════════════════════════════════
4. DESIGN SYSTEM  (token names are literal — use them everywhere; no raw values)
═══════════════════════════════════════════════════════════════════

### Colors (Kotlin `Color.kt` / Swift `AppColors.swift`)

| Token | Hex |
|---|---|
| Primary | `#3B4FE8` |
| PrimaryDark | `#1A2580` |
| PrimaryMid | `#6B7FF0` |
| PrimaryLight | `#E8EBFD` |
| Background | `#F4F6FB` |
| Surface | `#FFFFFF` |
| TextPrimary | `#1A1A2E` |
| TextSecondary | `#6B7280` |
| TextHint | `#9CA3AF` |
| Border | `#E5E7EB` |
| BorderLight | `#F3F4F6` |
| Success | `#10B981` |
| SuccessBackground | `#D1FAE5` |
| Danger | `#EF4444` |
| DangerBackground | `#FEE2E2` |
| Warning | `#F59E0B` |
| WarningBackground | `#FEF3C7` |

Android exposes as `Color.appPrimary`, `Color.appBackground`, etc. via extension properties on `androidx.compose.ui.graphics.Color`. iOS exposes as identically named `Color` extensions in `AppColors.swift`.

### Gradient
**AppGradient** = linear from `Primary` → `PrimaryDark`, top-leading → bottom-trailing. Used on Splash, Landing hero, GradientHeader.

### Spacing & Radius (`Dimens.kt` / `AppDimens.swift`)

| Token | Value |
|---|---|
| paddingXS | 4 |
| paddingS | 8 |
| paddingM | 12 |
| paddingL | 16 |
| paddingXL | 24 |
| paddingXXL | 32 |
| radiusSm | 8 |
| radiusMd | 12 |
| radiusLg | 16 |
| radiusXL | 24 |
| radiusPill | 100 |

### Typography
- **Android Material3:** headlineLarge 32sp/Bold, headlineMedium 28sp/SemiBold, titleLarge 22sp/SemiBold, titleMedium 16sp/Medium, bodyLarge 16sp, bodyMedium 14sp, labelLarge 14sp/Medium, labelMedium 12sp/Medium, labelSmall 11sp.
- **iOS:** map to `.largeTitle`, `.title`, `.title2`, `.headline`, `.body`, `.subheadline`, `.caption`, `.caption2`, `.footnote` (Dynamic Type — never hardcode point sizes).

### Component specs
- **PrimaryButton** — 52pt high, `radiusPill`, white text `.headline`/`labelLarge`, Primary background (solid or gradient). Disabled = 50% opacity. Loading = inline spinner.
- **SecondaryButton** — outlined, Primary border, Primary text.
- **GhostButton** — no border, Primary text only.
- **AppTextField / FormField** — 1pt Border default, 2pt Primary on focus, 2pt Danger on error. `radiusSm`. Label above, error caption below.
- **AppCard** — Surface bg, 4pt elevation/shadow, `radiusLg`, `paddingL` default content padding.
- **StatusBadge** — Confirmed→Success/SuccessBg; Pending→Warning/WarningBg; Cancelled→Danger/DangerBg.
- **FilterChip** — inactive: Surface bg + Border; active: PrimaryLight bg + Primary border & text. `radiusPill`, 36pt high.
- **GradientHeader** — AppGradient bg, min height configurable (default 120), white back chevron, white title, content slot. **iOS signature must be** `init(minHeight:onBack:trailingAction:trailingIcon:content:)`.
- **AvatarCircle** — circular, initials fallback when no URL, sizes: 32/40/56/72.

### Bottom Nav / Tab Bar (3 tabs)
Home (house icon), Appointments (calendar icon), Profile (person icon). Active = Primary; inactive = TextSecondary.

═══════════════════════════════════════════════════════════════════
5. SCREENS  (15 total — implement BOTH Android and iOS for each)
═══════════════════════════════════════════════════════════════════

| # | Screen | Android file | iOS file | Key elements |
|---|---|---|---|---|
| 1 | **Splash** | `features/mainhome/splash/SplashScreen.kt` | `Features/MainHome/Splash/SplashView.swift` | Gradient bg, logo, tagline, 300ms fade, checks `tokenStorage.getToken()` then navigates to Home or Landing |
| 2 | **Landing** | `features/mainhome/landing/LandingScreen.kt` | `Features/MainHome/Landing/LandingView.swift` | Hero GradientHeader, 3 feature bullets, "Sign In" PrimaryButton, "Create account" SecondaryButton, Google + Apple SSO buttons |
| 3 | **Login** | `features/auth/login/LoginScreen.kt` | `Features/Auth/Login/LoginView.swift` | Email + password (toggle visibility), Login PrimaryButton, "Forgot password?" link, Google + Apple SSO |
| 4 | **Register** | `features/auth/register/RegisterScreen.kt` | `Features/Auth/Register/RegisterView.swift` | First/last name, email, password, confirm password, terms checkbox, Submit |
| 5 | **Forgot Password** | `features/auth/forgotpassword/ForgotPasswordScreen.kt` | `Features/Auth/ForgotPassword/ForgotPasswordView.swift` | Email field, Submit, success confirmation message |
| 6 | **Home** | `features/mainhome/home/HomeScreen.kt` | `Features/MainHome/Home/HomeView.swift` | SearchCard (name + location), horizontal FilterChip row, "Recommended practitioners" PractitionerCard list |
| 7 | **Search Results** | `features/mainhome/search/SearchResultsScreen.kt` | `Features/MainHome/Search/SearchResultsView.swift` | Result count, sort menu, PractitionerCard list, empty state |
| 8 | **Practitioner Detail** | `features/mainhome/practitionerdetail/PractitionerDetailScreen.kt` | `Features/MainHome/PractitionerDetail/PractitionerDetailView.swift` | GradientHeader w/ avatar + name + specialty, InfoRow grid (phone, address, payments), MapPreviewCard, "Available slots" chip grid, "Book appointment" PrimaryButton |
| 9 | **Book Appointment** | `features/appointment/book/BookAppointmentScreen.kt` | `Features/Appointment/Book/BookAppointmentView.swift` | Practitioner summary, selected slot card, optional notes FormField, Confirm PrimaryButton |
| 10 | **Appointment Success** | `features/appointment/success/AppointmentSuccessScreen.kt` | `Features/Appointment/Success/AppointmentSuccessView.swift` | Success icon, appointment details card, "View my appointments" + "Back to home" buttons |
| 11 | **My Appointments** | `features/appointments/MyAppointmentsScreen.kt` | `Features/Appointments/MyAppointmentsView.swift` | Upcoming / Past tab row, AppointmentCard list with StatusBadge, cancel action w/ confirm dialog |
| 12 | **Profile** | `features/profile/ProfileScreen.kt` | `Features/Profile/ProfileView.swift` | Avatar + name + email card, settings rows (Edit profile, Change email, Change password, Language), Logout (Danger) |
| 13 | **Edit Profile** | `features/profileedit/EditProfileScreen.kt` | `Features/ProfileEdit/EditProfileView.swift` | First/last name, phone, save button |
| 14 | **Change Email** | `features/profileedit/ChangeEmailScreen.kt` | `Features/ProfileEdit/ChangeEmailView.swift` | New email, current password, submit |
| 15 | **Change Password** | `features/profileedit/ChangePasswordScreen.kt` | `Features/ProfileEdit/ChangePasswordView.swift` | Current password, new password, confirm new password, submit |

Every screen MUST render three states from its UiState: `isLoading` (LoadingView), `error` (ErrorView with onRetry), and content. Both platforms must show errors and loaders — never silently swallow on one side (Platform Parity rule PP-4/PP-5).

═══════════════════════════════════════════════════════════════════
6. USER WORKFLOWS  (navigation graphs — wire on both platforms)
═══════════════════════════════════════════════════════════════════

### Auth flow
```
Splash → [token valid?] → Home (AppTabView)
                       └→ Landing → Login | Register | ForgotPassword → Home
```

### Booking flow
```
Home → SearchResults → PractitionerDetail → BookAppointment → AppointmentSuccess
                                                            └→ Home or MyAppointments
```

### Appointments flow
```
MyAppointments (tabs: Upcoming | Past)
  → tap appointment → PractitionerDetail (read-only)
  → swipe / button → cancel → confirm dialog → API → list refresh
```

### Profile flow
```
Profile → EditProfile | ChangeEmail | ChangePassword | Logout
Logout clears TokenStorage and pops back to Landing.
```

**Android navigation:** single `MainActivity` hosts a `NavHost`. `NavDestination` is a sealed class — one object per route with a `route: String`. `AppNavGraph` registers every screen as `composable(NavDestination.X.route) { XScreen() }`. Bottom nav only on Home / MyAppointments / Profile.

**iOS navigation:** `RootView` is the auth gate. `AppTabView` is a `TabView` with three `NavigationStack`s — each declares `.navigationDestination(for: AppDestination.self)` and routes by an enum case.

═══════════════════════════════════════════════════════════════════
7. DOMAIN MODELS  (in `domain/model/`)
═══════════════════════════════════════════════════════════════════

```kotlin
data class User(
    val id: String, val firstName: String, val lastName: String,
    val email: String, val phone: String, val gender: String,
    val dateOfBirth: String, val cnsNumber: String,
    val profileType: ProfileType, val language: String
) {
    val fullName: String get() = "$firstName $lastName"
    val initials: String get() = "${firstName.firstOrNull() ?: ""}${lastName.firstOrNull() ?: ""}".uppercase()
}
enum class ProfileType { PATIENT, PRACTITIONER }

data class Practitioner(
    val id: String, val firstName: String, val lastName: String,
    val specialty: String, val clinicName: String, val address: String,
    val city: String, val phone: String, val email: String,
    val latitude: Double, val longitude: Double,
    val acceptingNewPatients: Boolean,
    val availableSlots: List<AppointmentSlot>,
    val schedule: List<ScheduleDay>,
    val paymentMethods: List<String>,
    val diplomas: List<String>,
    val isFavorite: Boolean
) {
    val fullName: String get() = "Dr. $firstName $lastName"
    val initials: String get() = "${firstName.firstOrNull() ?: ""}${lastName.firstOrNull() ?: ""}".uppercase()
}

data class Appointment(
    val id: String, val practitionerId: String, val practitionerName: String,
    val clinicName: String, val dateTime: String /* ISO 8601 */,
    val status: AppointmentStatus, val notes: String?
)
enum class AppointmentStatus { PENDING, CONFIRMED, CANCELLED, COMPLETED, NO_SHOW }

data class AppointmentSlot(val id: String, val practitionerId: String, val dateTime: String, val isAvailable: Boolean, val durationMinutes: Int)
data class ScheduleDay(val dayOfWeek: String, val openTime: String, val closeTime: String, val isClosed: Boolean)
```

Domain models contain **no** `@Serializable` annotations; that lives on DTOs in `data/network/DTOs.kt`. Repositories map DTO ↔ domain via `data/network/Mappers.kt`.

═══════════════════════════════════════════════════════════════════
8. API CONTRACT  (single source of truth: `ApiService.kt`)
═══════════════════════════════════════════════════════════════════

| Endpoint | Method | Auth | Notes |
|---|---|---|---|
| `/auth/login` | POST | No | LoginRequest → AuthResponse |
| `/auth/register` | POST | No | RegisterRequest → AuthResponse |
| `/auth/refresh` | POST | Refresh token | called by Ktor BearerTokenPlugin only |
| `/auth/forgot-password` | POST | No |  |
| `/auth/change-email` | POST | Yes |  |
| `/auth/change-password` | POST | Yes |  |
| `/practitioners` | GET | Yes | query: q, location, specialty |
| `/practitioners/{id}` | GET | Yes |  |
| `/practitioners/{id}/slots` | GET | Yes |  |
| `/appointments` | GET | Yes | filter: upcoming/past |
| `/appointments` | POST | Yes | book |
| `/appointments/{id}` | DELETE | Yes | cancel |
| `/users/me` | GET | Yes |  |
| `/users/me` | PUT | Yes |  |

**API rules — non-negotiable:**
- Every ApiService and repository method returns `Result<T>`. No exceptions propagate to ViewModels.
- DTOs are `@Serializable`; domain models are not.
- Bearer tokens are injected by Ktor `Auth { bearer { … } }` plugin. **Never** add `Authorization` headers manually.
- Refresh logic lives in the Ktor client config — not in repositories.
- BASE_URL comes from `BuildKonfig.BASE_URL` (no hardcoded URLs).
- JSON config: `ignoreUnknownKeys = true`, `isLenient = true`.
- Logging only when `BuildKonfig.ENABLE_LOGGING == true`.
- Timeouts: connect 30s, request 30s, socket 30s.
- Server error bodies are parsed (not just HTTP status text).

═══════════════════════════════════════════════════════════════════
9. STATE MANAGEMENT PATTERN  (replicate exactly in every ViewModel)
═══════════════════════════════════════════════════════════════════

```kotlin
data class FooUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val data: List<Item> = emptyList()
)

class FooViewModel(private val repo: FooRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(FooUiState())
    val uiState: StateFlow<FooUiState> = _uiState.asStateFlow()

    fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            repo.getData()
                .onSuccess { r -> _uiState.update { it.copy(isLoading = false, data = r) } }
                .onFailure { e -> _uiState.update { it.copy(isLoading = false, error = e.message) } }
        }
    }
}
```

- **SM-1** Single UiState per screen.
- **SM-2** Private `_uiState`; public `StateFlow` only.
- **SM-3** Always mutate via `.update { it.copy(...) }` (thread-safe).
- **SM-4** UiState fields are `val` only.
- **SM-5** `error: String?` (null = no error).
- **SM-6** Clear error on every new action.
- **SM-7** ViewModels never reference each other.

**Android:** `val uiState by viewModel.uiState.collectAsStateWithLifecycle()`. One-time loads in `LaunchedEffect(Unit)`. Use callbacks like `onClick = { viewModel.foo() }` — never launch coroutines from composable callbacks directly.

**iOS:** `@StateObject` at the view that owns the wrapper; `@ObservedObject` for children. Wrapper observes KMM `StateFlow` via `.watch { }` and dispatches updates to main:
```swift
viewModel.uiState.watch { [weak self] state in
    guard let state else { return }
    DispatchQueue.main.async { self?.uiState = state }
}
```
Use `.task { viewModel.viewModel.load() }`, not `.onAppear`.

═══════════════════════════════════════════════════════════════════
10. DEPENDENCY INJECTION  (Koin 4.0)
═══════════════════════════════════════════════════════════════════

`shared/.../di/SharedModule.kt` registers:
- `single { HttpClientFactory.create(get(), get()) }` → HttpClient
- `single<ApiService> { KtorApiService(get()) }`
- `single<AuthRepository> { AuthRepositoryImpl(get(), get()) }`
- … same for Practitioner / Appointment / User repositories
- One `factoryOf(::FooUseCase)` per use case
- One `factoryOf(::FooViewModel)` per ViewModel

**Android** `AndroidModule.kt` adds: `single<TokenStorage> { SecureStorage(androidContext()) }`, `single<SqlDriver> { AndroidSqliteDriver(ESklepiosDatabase.Schema, androidContext(), "esklepios.db") }`.

**iOS** `IosModule.kt` adds: `single<TokenStorage> { IosTokenStorage() }`, native SQL driver. `IosKoinInit.kt` exposes `fun doInitKoin(baseUrl: String, enableLogging: Boolean)` for Swift to call.

`KoinHelper.swift`:
```swift
class KoinHelper {
    static func startKoin(baseUrl: String, enableLogging: Bool) {
        IosKoinInitKt.doInitKoin(baseUrl: baseUrl, enableLogging: enableLogging)
    }
    static func get<T: AnyObject>() -> T { /* KoinKt.getKoin().get(...) bridge */ }
}
```

Wrappers call `viewModel: HomeViewModel = KoinHelper.get()` in their `init`.

═══════════════════════════════════════════════════════════════════
11. SECURE STORAGE  (TokenStorage interface — methods must match exactly)
═══════════════════════════════════════════════════════════════════

```kotlin
interface TokenStorage {
    fun setToken(token: String)
    fun setRefreshToken(token: String)
    fun getToken(): String?
    fun getRefreshToken(): String?
    fun clear()
}
```

- **Android `SecureStorage`** wraps `EncryptedSharedPreferences` (MasterKey AES256-GCM).
- **iOS `KeychainStorage`** wraps `SecItemAdd/Copy/Delete` with `kSecClassGenericPassword`.

═══════════════════════════════════════════════════════════════════
12. LOCALIZATION  (Twine)
═══════════════════════════════════════════════════════════════════

Master file: `strings/twine.txt`. Format:
```
[[General]]
app.name
    en = eSklepios
    fr = eSklepios
    de = eSklepios
    lb = eSklepios
```
`Makefile` target `strings` runs `twine generate-all-localization-files strings/twine.txt androidApp/src/main/res --format android --tags android` to produce `values/strings.xml`, `values-fr/`, `values-de/`, `values-lb/`. iOS uses `--format apple` into `iosApp/eSklepios/Resources/`.

**Rule UI-2:** No hardcoded user-visible strings. Android: `stringResource(R.string.key)`. iOS: `Text("key")` backed by NSLocalizedString.

Required section headers in `twine.txt`: General, Navigation, Auth, Home, Search, PractitionerDetail, BookAppointment, AppointmentSuccess, MyAppointments, Profile, EditProfile, ChangeEmail, ChangePassword, Errors.

═══════════════════════════════════════════════════════════════════
13. BUILD CONFIG  (BuildKonfig)
═══════════════════════════════════════════════════════════════════

In `shared/build.gradle.kts`:
```kotlin
buildkonfig {
    packageName = "lu.esklepios.app"
    val isProd = (project.findProperty("flavor") ?: "dev") == "prod"
    val props = Properties().apply {
        load(rootProject.file(if (isProd) "prod.properties" else "dev.properties").reader())
    }
    defaultConfigs {
        buildConfigField(STRING, "BASE_URL", props.getProperty("BASE_URL"))
        buildConfigField(BOOLEAN, "ENABLE_LOGGING", props.getProperty("ENABLE_LOGGING"))
    }
}
```
Access: `BuildKonfig.BASE_URL`, `BuildKonfig.ENABLE_LOGGING`.

═══════════════════════════════════════════════════════════════════
14. TESTING
═══════════════════════════════════════════════════════════════════

- **commonTest:** kotlin.test, kotlinx-coroutines-test, Turbine. **No MockK** (JVM-only). Use hand-written fake interface implementations.
- **androidApp/src/test:** MockK + Turbine + JUnit4 for ViewModel state-transition tests. Always `Dispatchers.resetMain()` in `@After`.
- **iOSTests:** XCTest. Keychain tests must `delete(key:)` in `tearDown()`.
- Cover **every state transition** of every ViewModel (loading → success, loading → error, retry clears error).
- Don't test SQLDelight generated code, BuildKonfig fields, or Koin wiring.

═══════════════════════════════════════════════════════════════════
15. NAMING CONVENTIONS  (strict)
═══════════════════════════════════════════════════════════════════

- Kotlin classes/objects/interfaces/enums: `PascalCase` (no `I` prefix on interfaces).
- Enum values: `SCREAMING_SNAKE_CASE`.
- Functions / props: `camelCase`. Private backing fields: `_camelCase`.
- Companion constants: `SCREAMING_SNAKE_CASE`.
- Swift types: `PascalCase`; enum cases: `camelCase`; private props: no underscore.
- One class per file; filename matches class name.
- Twine keys: `section.snake_case`. Android resource name: same with `_`.
- Git branches: `feature/<desc>`, `fix/<desc>`, `refactor/<desc>`, `chore/<desc>`. Conventional Commits for messages: `feat(scope): …`, scopes are `shared|android|ios|auth|home|appointments|profile|navigation|di|network|db|strings`.

═══════════════════════════════════════════════════════════════════
16. PLATFORM PARITY  (every screen on both platforms — same UiState contract)
═══════════════════════════════════════════════════════════════════

- All 15 screens exist on Android **and** iOS, backed by the same shared ViewModel.
- Navigation flows must match across platforms.
- Loading and error states are rendered on both.
- TokenStorage semantics identical (same method names, same erase-on-`clear()` behavior).
- All 4 languages translated for every key in `twine.txt`.

═══════════════════════════════════════════════════════════════════
17. PITFALLS  (avoid these; they cost real time)
═══════════════════════════════════════════════════════════════════

1. **`AppointmentStatus` collision:** the domain enum has `PENDING, CONFIRMED, CANCELLED, COMPLETED, NO_SHOW`. If a UI component needs a different value set, define a separate local enum — do not redefine `AppointmentStatus` in UI code.
2. **TokenStorage method names** are `setToken / setRefreshToken / clear` — **not** `saveToken / clearAll`. Read the interface before implementing.
3. **MockK is JVM-only** — cannot be used in `commonTest`. Use fakes.
4. **`ProfileType`** has only `PATIENT` and `PRACTITIONER` — no FAMILY_MEMBER, CAREGIVER, etc.
5. **iOS `placeholder()` extension** — define in ONE file (`ViewExtensions.swift`) only; duplicate definitions break compilation.
6. **KoinHelper.startKoin** takes `(baseUrl: String, enableLogging: Bool)` — always pass both.
7. **No generic `SocialSignInButton`** — split into `GoogleSignInButton(onClick:modifier:)` and `AppleSignInButton(onClick:modifier:)`.
8. **`GradientHeader` iOS init:** `(minHeight:onBack:trailingAction:trailingIcon:content:)`. All five always named; pass `nil` when unused.
9. **iOS Map API:** target iOS 17+, use the closure-based `Map { ... }`, not the deprecated `Map(coordinateRegion:annotationItems:)`.
10. **SQLDelight on iOS:** instantiate `NativeSqliteDriver` **inside** the iOS Koin module, not before `startKoin`.

═══════════════════════════════════════════════════════════════════
18. DELIVERABLES & ORDER OF GENERATION
═══════════════════════════════════════════════════════════════════

Generate files in this order so the project builds incrementally:

1. Root Gradle (`settings.gradle.kts`, `build.gradle.kts`, `gradle.properties`, `libs.versions.toml`).
2. Property files (`dev.properties`, `prod.properties`) and `Makefile` + `strings/twine.txt` skeleton.
3. `shared/build.gradle.kts` with KMM, BuildKonfig, SQLDelight, serialization plugins.
4. `shared/commonMain` — domain models → repository interfaces → use cases → DTOs → ApiService interface + Ktor impl + HttpClientFactory + TokenStorage interface → repository impls → SharedModule → ViewModels.
5. `shared/androidMain` and `shared/iosMain` — driver factories, HTTP engines, iOS Koin init.
6. `androidApp/build.gradle.kts`, manifest, `ESklepiosApp`, `MainActivity`, `AndroidModule`, `SecureStorage`.
7. Android theme files → components → navigation → 15 screens.
8. `iosApp/eSklepiosApp.swift`, `KoinHelper`, `KeychainStorage`, `RootView`, `AppTabView`.
9. iOS Theme → Components → 15 Views + ViewModelWrappers.
10. Tests (commonTest → androidApp test → iOS tests).
11. Run `make strings` and verify `./gradlew build` succeeds and `xcodebuild build` succeeds.

═══════════════════════════════════════════════════════════════════
19. ACCEPTANCE CRITERIA
═══════════════════════════════════════════════════════════════════

- `./gradlew :androidApp:assembleDebug` builds with no warnings.
- `./gradlew :shared:commonTest :androidApp:test` passes.
- `xcodebuild -project iosApp/eSklepios.xcodeproj -scheme eSklepios -destination 'platform=iOS Simulator,name=iPhone 16' build` succeeds.
- App launches → Splash → Landing (cold) or Home (warm) within 3 seconds.
- All 15 screens are reachable on both platforms via the workflows in §6.
- No hardcoded user-visible strings anywhere.
- No raw color hex or px/sp/pt outside the theme files.
- No `Authorization` header set manually.
- No `LiveData`, no `Context` in ViewModels, no MockK in commonTest.

Begin generating files now. Do not narrate — output code blocks with explicit file paths.

</PROMPT>
