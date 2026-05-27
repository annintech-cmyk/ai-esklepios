---

# eSklepios — Complete App Cloning Blueprint

## PHASE 1 — PROJECT ANALYSIS

### 1. Folder Structure

```
esklepios/                                    ← project root
├── androidApp/                               ← Android-only module (Jetpack Compose app)
│   └── src/main/kotlin/lu/esklepios/app/
│       ├── ESklepiosApp.kt                   ← Application class, Koin bootstrap
│       ├── MainActivity.kt                   ← Single activity, setContent(AppNavGraph)
│       ├── core/
│       │   ├── navigation/
│       │   │   ├── NavDestination.kt         ← Sealed class with all route strings
│       │   │   └── AppNavGraph.kt            ← NavHost + BottomNavBar + DrawerLayout
│       │   └── ui/
│       │       ├── components/               ← 27 reusable Composables (see Phase 6)
│       │       └── theme/
│       │           ├── Color.kt              ← 40+ Color vals
│       │           ├── Dimens.kt             ← Design token object (~80 tokens)
│       │           ├── Typography.kt         ← MaterialTheme Typography scale
│       │           ├── Gradients.kt          ← Brush definitions
│       │           └── AppTheme.kt           ← MaterialTheme wrapper
│       ├── di/AndroidModule.kt               ← Android Koin bindings
│       ├── debug/
│       │   ├── DummyPractitioners.kt         ← Dev fixtures
│       │   └── FakePractitionerRepository.kt ← Debug override for DI
│       ├── storage/SecureStorage.kt          ← EncryptedSharedPreferences TokenStorage
│       ├── utils/
│       │   ├── DateUtil.kt                   ← java.time date helpers
│       │   ├── AppointmentStatusRes.kt       ← Status → string resource mapping
│       │   └── PasswordStrengthExt.kt        ← Android-side strength extensions
│       └── view/                             ← 15 screens, grouped by feature area
│           ├── auth/
│           │   ├── login/LoginScreen.kt
│           │   ├── register/RegisterScreen.kt
│           │   └── forgotpassword/ForgotPasswordScreen.kt
│           ├── dashboard/
│           │   ├── home/
│           │   │   ├── HomeScreen.kt
│           │   │   ├── HomePractitionerCardMapper.kt
│           │   │   ├── practitioners/PractitionerListScreen.kt
│           │   │   └── practitioner_data/PractitionerDetailScreen.kt
│           │   ├── appointments/
│           │   │   ├── MyAppointmentsScreen.kt
│           │   │   └── booking/
│           │   │       ├── BookingScreen.kt
│           │   │       └── AppointmentSuccessScreen.kt
│           │   └── profile/
│           │       ├── ProfileScreen.kt
│           │       └── profile_edit/
│           │           ├── EditProfileScreen.kt
│           │           ├── ChangeEmailScreen.kt
│           │           └── ChangePasswordScreen.kt
│           ├── landing/LandingScreen.kt
│           └── splash/SplashScreen.kt
│
├── iosApp/
│   └── eSklepios/
│       ├── eSklepiosApp.swift                ← @main App struct, KoinHelper.start()
│       ├── Storage/KeychainStorage.swift     ← Keychain wrapper (native, not Koin)
│       ├── Core/
│       │   ├── DI/KoinHelper.swift           ← Singleton ViewModel factory facade
│       │   ├── Navigation/
│       │   │   ├── RootView.swift            ← Splash → Landing/AppTabView switcher
│       │   │   └── AppTabView.swift          ← TabView(Home, Appointments, Profile)
│       │   ├── UI/
│       │   │   ├── Components/               ← 22 reusable SwiftUI structs (see Phase 6)
│       │   │   └── Theme/
│       │   │       ├── AppColors.swift       ← Color extension with all tokens
│       │   │       ├── AppDimens.swift       ← Dimens/Spacing/Radius/Sizing enums
│       │   │       ├── AppFonts.swift        ← Font extension with named styles
│       │   │       └── ThemeManager.swift    ← Observable theme state
│       │   └── Utils/
│       │       ├── DateUtil.swift            ← Foundation date helpers
│       │       └── PasswordStrengthExt.swift ← iOS-side strength extensions
│       ├── Features/                         ← 15 views + wrappers mirroring Android
│       │   ├── Auth/
│       │   ├── Dashboard/
│       │   ├── Landing/
│       │   └── Splash/
│       └── eSklepiosTests/                   ← 5 XCTest files
│
├── shared/                                   ← KMM module — pure Kotlin, no platform deps
│   └── src/
│       ├── commonMain/kotlin/lu/esklepios/app/
│       │   ├── data/
│       │   │   ├── db/DatabaseDriverFactory.kt   ← expect declaration
│       │   │   ├── network/
│       │   │   │   ├── ApiService.kt             ← interface (13 suspend fns)
│       │   │   │   ├── DTOs.kt                   ← 14 @Serializable data classes
│       │   │   │   ├── HttpClientFactory.kt       ← Ktor client builder
│       │   │   │   ├── KtorApiService.kt          ← ApiService impl + safeCall
│       │   │   │   ├── Mappers.kt                ← DTO ↔ domain extension fns
│       │   │   │   └── TokenStorage.kt           ← interface (5 methods)
│       │   │   └── repository/                   ← 4 *RepositoryImpl files
│       │   ├── di/SharedModule.kt                ← Koin module (all registrations)
│       │   ├── domain/
│       │   │   ├── model/                        ← 5 domain model files
│       │   │   ├── repository/                   ← 4 repository interfaces
│       │   │   └── usecase/                      ← 15 single-method use cases
│       │   ├── presentation/viewmodel/           ← 11 ViewModels + UiState classes
│       │   └── util/                             ← 8 shared utility files
│       ├── androidMain/kotlin/                   ← actual DatabaseDriverFactory + OkHttp engine
│       ├── iosMain/kotlin/                       ← actual DatabaseDriverFactory + Darwin engine + IosModule + IosKoinInit
│       └── commonTest/kotlin/                    ← 11 test files (fakes, no MockK)
│
├── strings/twine.txt                            ← master localization (en/fr/de/lb)
├── Makefile                                     ← `make strings` → generates strings.xml
├── build.gradle.kts                             ← root: applies detekt, ktlint to all subprojects
├── settings.gradle.kts                          ← includes :shared, :androidApp
├── prod.properties                              ← production BuildKonfig values
├── dev.properties                               ← dev values (gitignored)
└── .claude/                                     ← AI rules and skills
    ├── CLAUDE.md
    ├── rules/                                   ← 20 rule files
    └── skills/                                  ← 5 skill files
```

### 2. Architecture

The architecture enforces strict inward-only dependency flow across four layers:

**Layer 1 — Domain (innermost, pure Kotlin):**
`shared/domain/model/` contains platform-agnostic data classes. `shared/domain/repository/` contains Kotlin interfaces. `shared/domain/usecase/` contains single-responsibility operators. Nothing here imports Ktor, SQLDelight, Compose, or SwiftUI.

**Layer 2 — Data (outer shell of shared):**
`shared/data/network/` implements `ApiService` via Ktor. `shared/data/repository/` implements domain interfaces. `shared/data/db/` bridges to SQLDelight-generated code. This layer knows about the domain layer but the domain never imports from here.

**Layer 3 — Presentation (shared ViewModels):**
`shared/presentation/viewmodel/` contains 11 ViewModels extending `androidx.lifecycle.ViewModel` (the KMM-compatible artifact). Each ViewModel owns one `UiState` data class and exposes it as `StateFlow`. ViewModels import domain use cases and repository interfaces only.

**Layer 4 — Platform UI (platform-specific):**
`androidApp/view/` (Compose screens) and `iosApp/Features/` (SwiftUI views) consume ViewModels directly (Android via `koinViewModel()`, iOS via `ViewModelWrapper`). They import nothing from the data layer.

### 3. KMP Modules

**`:shared`** (Kotlin Multiplatform Library)
- `commonMain`: all domain + data + presentation + util code
- `androidMain`: `DatabaseDriverFactory` (AndroidSqliteDriver), `HttpClientEngine` (OkHttp)
- `iosMain`: `DatabaseDriverFactory` (NativeSqliteDriver), `HttpClientEngine` (Darwin), `IosModule.kt`, `IosKoinInit.kt`
- `commonTest`: 11 test files using hand-written fakes

**`:androidApp`** (Android Application)
- Depends on `:shared`
- Contains Compose UI, navigation, DI wiring, SecureStorage, utility extensions
- Output: APK/AAB

**`iosApp`** (Xcode project, not a Gradle module)
- Consumes `:shared` as an XCFramework
- SwiftUI UI, KoinHelper, KeychainStorage, iOS-native utilities
- Output: IPA

### 4. Navigation Architecture

**Android — NavGraph + BottomNav + Drawer:**

`NavDestination.kt` is a sealed class with 14 `object` members, each holding a route string. Parameterized routes use path segments: `"practitioner_detail/{practitionerId}"`. Helper `createRoute()` functions avoid string formatting at call sites.

`AppNavGraph.kt` wraps a `NavHost` inside `ModalNavigationDrawer` inside `Scaffold` inside a `SharedTransitionLayout`. The `startDestination` is `NavDestination.Splash.route`. Three routes (`Home`, `MyAppointments`, `Profile`) are bottom-nav items. `HomeViewModel` is scoped activity-wide (not per-composable) so search queries survive navigation.

Bottom navigation uses `NavigationBar` / `NavigationBarItem` with `popUpTo(Home) { saveState = true }` and `restoreState = true` to preserve back-stack state per tab.

**iOS — RootView + AppTabView + NavigationStack:**

`RootView.swift` holds two `@State` booleans: `isReady` and `isAuthenticated`. When `isReady == false`, it shows `SplashView`. When ready and authenticated → `AppTabView`. Otherwise → `LandingView`. Transitions use `.animation(.easeInOut(duration: 0.3))`.

`AppTabView.swift` is a `TabView` with three tab items (Home, Appointments, Profile). Each tab's root view uses `NavigationStack` internally to push detail screens. `.accentColor(.appPrimary)` applies the brand color to all tabs.

Navigation destinations within each tab use SwiftUI's `.navigationDestination(for:)` typed navigation.

### 5. Dependency Injection

**Android bootstrap** (`ESklepiosApp.kt`):
```kotlin
startKoin {
    androidLogger(Level.ERROR)
    androidContext(this@ESklepiosApp)
    modules(sharedModule(), androidModule())
}
```
`sharedModule()` is loaded first; `androidModule()` last so Android bindings override shared ones (the `FakePractitionerRepository` override pattern demonstrates this).

**`SharedModule.kt`** registers:
- `Clock.System` as `single<Clock>`
- Database driver and `ESklepiosDatabase` as singletons
- `HttpClientFactory` → creates `HttpClient` (singleton)
- `KtorApiService` bound as `ApiService` (singleton)
- 4 repositories as singletons bound to their interfaces
- 15 use cases as `factoryOf` (new instance per injection)
- 11 ViewModels as `factoryOf` (new instance per screen)

**`AndroidModule.kt`** registers:
- `SecureStorage` bound as `TokenStorage`
- `DatabaseDriverFactory` with `androidContext()`
- `FakePractitionerRepository` override (dev only — remove for production)

**iOS bootstrap** (`eSklepiosApp.swift`):
```swift
KoinHelper.shared.start(enableLogging: false)
```
`KoinHelper.start()` calls `IosKoinInitKt.doInitKoin(enableLogging:)` which runs `startKoin { modules(iosModule(), sharedModule()) }`.

**`IosModule.kt`** registers:
- `IosTokenStorage` bound as `TokenStorage`
- `DatabaseDriverFactory()` with no args

**`IOSViewModelFactory`** in `IosKoinInit.kt` is a `KoinComponent` that `by inject()`s all 11 ViewModels. `KoinHelper.swift` wraps this factory and exposes typed getters.

### 6. ViewModel Pattern

All 11 ViewModels extend `androidx.lifecycle.ViewModel` from `libs.androidx.lifecycle.viewmodel` (the KMM-compatible multiplatform artifact — NOT the Android-only `lifecycle-viewmodel-ktx`).

**Canonical pattern:**
```kotlin
class FooViewModel(private val fooUseCase: FooUseCase) : ViewModel() {
    private val _uiState = MutableStateFlow(FooUiState())
    val uiState: StateFlow<FooUiState> = _uiState.asStateFlow()

    fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            fooUseCase().onSuccess { data ->
                _uiState.update { it.copy(isLoading = false, result = data) }
            }.onFailure { e ->
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun clearError() { _uiState.update { it.copy(error = null) } }
}

data class FooUiState(
    val isLoading: Boolean = false,
    val error: String? = null
)
```

**iOS ViewModelWrapper** (canonical):
```swift
@MainActor
class FooViewModelWrapper: ObservableObject {
    let viewModel: FooViewModel
    @Published var uiState: FooUiState
    private var stateObserver: FlowWatcher?

    init() {
        self.viewModel = KoinHelper.shared.fooViewModel()
        self.uiState = viewModel.uiState.value as! FooUiState
        stateObserver = FlowExtensionsKt.watch(viewModel.uiState) { [weak self] anyState in
            guard let state = anyState as? FooUiState else { return }
            Task { @MainActor [weak self] in self?.uiState = state }
        }
    }

    deinit { stateObserver?.close(); viewModel.onCleared() }
}
```

**FlowWatcher** (`shared/util/FlowExtensions.kt`) bridges Kotlin's `StateFlow.collect { }` to Swift. It creates a `CoroutineScope(Dispatchers.Main + SupervisorJob())` and returns a `FlowWatcher` handle. Calling `.close()` cancels the scope.

### 7. Repository Pattern

Interfaces live in `shared/domain/repository/`. Implementations live in `shared/data/repository/`. The rule is: interface methods return `Result<T>` (never throw), implementations use `runCatching { }` for DB calls and `safeCall { }` (defined in `KtorApiService.kt`) for network calls.

**`AuthRepository`** interface: `login`, `register`, `forgotPassword`, `refreshToken`, `logout`, `isLoggedIn()`, `getCurrentUser()`.

**`PractitionerRepository`** interface: `searchPractitioners`, `getPractitionerById`, `toggleFavorite`.

**`AppointmentRepository`** interface: mixed — some `suspend fun ... : Result<T>`, plus `fun getAppointments(): Flow<List<Appointment>>` for real-time DB observation.

**`UserRepository`** interface: `getProfile`, `updateProfile`, `changeEmail`, `changePassword`.

**`AuthRepositoryImpl`** demonstrates the full pattern: API call → parse response → store token in `TokenStorage` → cache user in SQLDelight → return domain model. Logout uses `database.transaction { }` to atomically clear all three tables.

### 8. API Layer

**`ApiService`** interface: 13 suspend functions covering auth (login, register, forgotPassword, refreshToken), practitioners (search, getById), appointments (create, getAll, modify, cancel), and profile (get, update, changeEmail, changePassword).

**`KtorApiService`** implements `ApiService`. Every function wraps its Ktor call in `safeCall { }`, which catches specific exception types in order: `ClientRequestException`, `ServerResponseException`, `ConnectTimeoutException`, `SocketTimeoutException`, `CancellationException` (rethrown), then generic `Exception`.

**`HttpClientFactory`** configures the Ktor client with:
- `ContentNegotiation` with `ignoreUnknownKeys = true`, `isLenient = true`
- `HttpTimeout`: all three timeouts at 30,000ms
- `Logging` gated by `BuildKonfig.ENABLE_LOGGING`
- `Auth { bearer { } }` loading tokens from `TokenStorage`
- `defaultRequest` with `Content-Type: application/json` and `X-App-Platform: mobile`

**`TokenStorage`** interface: `getToken()`, `setToken(String)`, `getRefreshToken()`, `setRefreshToken(String)`, `clear()`.

**Android implementation** (`SecureStorage`): `EncryptedSharedPreferences` with AES256-GCM master key. Keys: `auth_token`, `auth_refresh_token`.

**iOS implementation** (`IosTokenStorage`, wraps `KeychainStorage`): `kSecClassGenericPassword` items under service `lu.esklepios.app`.

**BuildKonfig** injects `BASE_API_URL`, `API_TIMEOUT_SECONDS`, `ENABLE_LOGGING`, `MAPS_API_KEY` from `dev.properties` / `prod.properties` at compile time.

### 9. State Management

Android screens collect state:
```kotlin
val uiState by viewModel.uiState.collectAsStateWithLifecycle()
```

One-shot side effects use `LaunchedEffect`:
```kotlin
LaunchedEffect(uiState.isLoggedOut) {
    if (uiState.isLoggedOut) navController.navigate(NavDestination.Landing.route) {
        popUpTo(0) { inclusive = true }
    }
}
```

iOS views use `@StateObject` at the owning view, `.task { }` for async loading, and receive `@ObservedObject` references in child views.

All UiState classes are `data class` with `val` fields only, ensuring immutability. Mutations go through `_uiState.update { it.copy(...) }` exclusively (thread-safe compare-and-set).

### 10. Localization Strategy

`strings/twine.txt` is the single source of truth. Format: `[[Section]]` headers, then `[key]` entries with `en`, `fr`, `de`, `lb` values. Running `make strings` invokes the `twine` gem to generate `androidApp/src/main/res/values/strings.xml` and variants (`values-fr/`, `values-de/`, `values-lb/`).

iOS currently references strings as `NSLocalizedString("key", comment: "")` without a generated `.strings` file — the Makefile can be extended with `--format apple` to generate `Localizable.strings` for Xcode.

All user-visible string keys must have all four language values. Accessibility description keys use the `cd.*` prefix (e.g., `cd.back`, `cd_show_password`).

### 11. Design System

**Android token files:**
- `/core/ui/theme/Color.kt` — 40+ named `Color` vals (Primary, PrimaryDark, PrimaryLight, Background, Surface, TextPrimary, TextSecondary, TextHint, Danger, Success, Warning, GradientStart, GradientEnd, plus icon palette and status colors)
- `/core/ui/theme/Dimens.kt` — `object Dimens` with ~80 `Dp`/`TextUnit` tokens in semantic categories: spacing, radius, borders/elevations, component heights, icon sizes, avatar sizes, layout dimensions, orb sizes, font sizes
- `/core/ui/theme/Typography.kt` — `AppTypography: Typography` with all 13 Material3 text styles (display, headline, title, body, label scales)
- `/core/ui/theme/Gradients.kt` — `object Gradients` with `primaryBrush`, `softBrush`, `verticalBrush`
- `/core/ui/theme/AppTheme.kt` — `AppTheme { }` composable wrapping `MaterialTheme` with light/dark color schemes

**iOS token files:**
- `Core/UI/Theme/AppColors.swift` — `extension Color` with all color tokens mirroring Android, plus `AppGradient` struct with `primary`, `primaryVertical`, `surface` gradient definitions, plus `Color(hex:)` initializer
- `Core/UI/Theme/AppDimens.swift` — `enum Dimens` (~80 `static let CGFloat` tokens), plus alias namespaces `enum Spacing`, `enum Radius`, `enum Sizing` for ergonomic call sites
- `Core/UI/Theme/AppFonts.swift` — `extension Font` with `appSerif(_:weight:)`, `appSans(_:weight:)` factories and 17 named font tokens
- `Core/UI/Theme/ThemeManager.swift` — `ObservableObject` for theme state

### 12. Testing Strategy

**Shared `commonTest`** (11 files):
- `HomeViewModelTest.kt`: 6 tests, uses `FakePractitionerRepositoryForHome` inline class, `StandardTestDispatcher`, fixed `FakeClock` at `2026-05-24T00:00:00Z`
- `AuthRepositoryTest.kt`: 8 tests, inline `FakeAuthRepository` with controllable success/failure
- `PractitionerRepositoryTest.kt`: repository contract tests with fake `ApiService`
- `AppointmentRepositoryTest.kt`: similar pattern
- `UserRepositoryTest.kt`: profile operations
- `ViewModelTest.kt`: generic ViewModel state machine tests
- `UseCaseTest.kt`: use case delegation tests
- `LogoutUseCaseTest.kt`: side-effect verification
- `SerializationSmokeTest.kt`: `assertRoundTrip<Dto>` for every `@Serializable` DTO
- `util/ValidationUtilTest.kt`, `CnsFormatterTest.kt`, `GenderTest.kt`, `PhoneParserTest.kt`

**Pattern for all commonTest**: use `StandardTestDispatcher`, `Dispatchers.setMain` in `@BeforeTest`, `Dispatchers.resetMain` in `@AfterTest`, `testDispatcher.scheduler.advanceUntilIdle()` after triggering async actions. No MockK.

**iOS XCTest** (5 files in `eSklepiosTests/`):
- `AuthViewModelTests.swift`, `HomeViewModelTests.swift`, `BookAppointmentViewModelTests.swift`: instantiation + key behavior tests
- `KeychainStorageTests.swift`: write/read/delete with `tearDown()` cleanup
- `ThemeManagerTests.swift`: color token presence

**Android JVM tests**: would use `@get:Rule val mainDispatcherRule = MainDispatcherRule()` + Turbine for `StateFlow` assertions (mockk available here for Android-only classes).

### 13. Shared Utilities

All in `shared/src/commonMain/kotlin/lu/esklepios/app/util/`:

| File | Purpose |
|------|---------|
| `FlowExtensions.kt` | `FlowWatcher` class + `StateFlow<T>.watch(onChange)` extension — bridges Kotlin StateFlow to Swift observation |
| `ValidationUtil.kt` | `object ValidationUtil` — `isValidEmail`, `isPasswordMinLength`, `emailsMatch`, `passwordsMatch`, `passwordStrength`, `passwordCriteria` |
| `PasswordStrength.kt` | `enum PasswordStrength { NONE, WEAK, FAIR, GOOD, STRONG }`, `enum PasswordCriterion`, `data class PasswordCriteriaResult` |
| `DateFilter.kt` | `enum DateFilter { ALL, TODAY, WITHIN_3_DAYS }` with `apiKey` and `labelKey` fields — single source for practitioner search date filter chips |
| `AvailabilityFilter.kt` | `enum AvailabilityFilter` for availability chip variants — links to `DateFilter` |
| `Gender.kt` | `enum Gender { MALE, FEMALE, OTHER }` with `apiValue`, `labelKey`, and lenient `fromApiString()` parser |
| `Locales.kt` | `data class SupportedLanguage`, `val supportedLanguages: List<SupportedLanguage>` — 4 supported app languages (fr, en, de, lb) |
| `DialCodes.kt` | `data class DialCode`, `val supportedDialCodes: List<DialCode>` — 5 EU country codes + `PhoneParser` for splitting raw phone strings |
| `AppointmentStatusOptions.kt` | `object AppointmentStatusOptions` — status → Twine label key + semantic color scheme; Android extension functions on `AppointmentStatus` |
| `CnsFormatter.kt` | `object CnsFormatter` — masks CNS numbers after 9 visible digits |

### 14. Reusable Components

**Android** (`core/ui/components/` — 27 files):

| File | Components |
|------|-----------|
| `Typography.kt` | `AppTitleText`, `AppSubtitleText`, `AppBodyText`, `AppCaptionText`, `AppToolbarTitle`, `AppButtonText`, `AppLabelText`, `AppErrorText`, `AppSectionHeaderText`, `AppFieldValueText`, `AppSpannedText`, `FormFieldLabel` |
| `Buttons.kt` | `PrimaryButton` (gradient pill), `SecondaryButton` (outlined pill), `GhostButton` (text), `GlassButton` (frosted) |
| `Cards.kt` | `AppCard` (surface card with elevation), `InfoCard` (titled card with colored header) |
| `FormField.kt` | `FormField` (labeled OutlinedTextField with password toggle, error state), `TextAreaField` |
| `GradientHeader.kt` | `GradientHeader` (primitive — internal use only in AppGradientHeader) |
| `AppGradientHeader.kt` | `AppGradientHeader` (composite header with leading/center/trailing actions, text block, profile block, search block) |
| `AppToolbar.kt` | `AppToolbar` (back + title + trailing action) |
| `AppTextLink.kt` | `AppTextLink` + `DividerWithLabel` |
| `AvatarCircle.kt` | `AvatarCircle` (initials circle with gradient background) |
| `PractitionerCard.kt` | `PractitionerCard` (expandable card with weekly slot calendar grid) |
| `StatusBadge.kt` | `StatusBadge` (colored chip for appointment status) |
| `FilterChip.kt` | `FilterChip` (toggle chip for search filters) |
| `AvailabilityChip.kt` | `AvailabilityChip` |
| `InfoRow.kt` | `InfoRow` (icon + label + value row) |
| `MapPreviewCard.kt` | `MapPreviewCard` (static map placeholder with coordinates) |
| `SearchCard.kt` | `SearchCard` (specialty + location search combo) |
| `SearchInputField.kt` | `SearchInputField` (single search input with Light/Dark variant) |
| `LoadingIndicator.kt` | `LoadingIndicator` |
| `EmptyStateView.kt` | `EmptyStateView` |
| `ErrorView.kt` | `ErrorView` (message + retry button) |
| `AppIcon.kt` | `AppIcon` (icon with mandatory a11y contract) |
| `AppIconButton.kt` | `AppIconButton` (44dp tap target enforced) |
| `AppTabRow.kt` | `AppTabRow`, `AppTabItem` |
| `Spacers.kt` | `VSpace(Dp)`, `HSpace(Dp)` |
| `CheckRow.kt` | `CheckRow` (label-value pair with optional leading icon and divider) |
| `AppDrawer.kt` | `AppDrawerContent` (side-drawer navigation menu) |
| `AppFormScreen.kt` | `AppFormScreen` (scroll + toolbar + error snackbar shell) |
| `SocialSignInButton.kt` | `GoogleSignInButton`, `AppleSignInButton` |

**iOS** (`Core/UI/Components/` — 22 files, mirroring Android):

`AppCard`, `AppGradientHeaderView`, `AppIcon`, `AppIconButton`, `AppScreen`, `AppTextLink`, `AppToolbar`, `AppTypography` (all `App*Text` structs), `AppointmentCard`, `AvatarCircle`, `CheckRow`, `EmptyStateView`, `ErrorView`, `FilterChip`, `GhostButton`, `GlassButton`, `GradientHeader`, `InfoRow`, `LoadingView`, `MapPreviewCard`, `PractitionerCard`, `PrimaryButton`, `SearchCard`, `SearchInputField`, `SecondaryButton`, `SectionTitle`, `StatusBadge`, `ViewExtensions` (shared `placeholder()` and other modifiers)

### 15. Business Workflows

1. **Authentication**: Landing → Login (email/password) → token stored in SecureStorage/Keychain → navigate Home. Register (3-step form: personal info, contact, credentials) → same flow. Forgot Password → email sent confirmation.

2. **Practitioner Search**: Home screen auto-loads all practitioners on init. User types specialty/location → live client-side filtering. Date chips (All/Today/Within 3 Days) and "New Patients" toggle further filter the list. Tapping a practitioner card header → PractitionerDetail. Tapping a slot button → BookAppointment.

3. **Appointment Booking**: BookAppointment receives `practitionerId` + `slotId` via nav arg. Loads practitioner data if not already in memory. User enters consultation reason + message to doctor. Confirm → `CreateAppointmentUseCase` → API call → navigate AppointmentSuccess with returned `appointmentId`.

4. **Appointment Management**: MyAppointments loads upcoming and past appointments from SQLDelight (cache) + refreshes from API. Status displayed via `StatusBadge`. Cancel action calls `CancelAppointmentUseCase`. Modify flow reuses BookAppointment screen with `isChange=true`.

5. **Profile Management**: Profile screen shows user data from `GetProfileUseCase`. Edit Profile updates name, phone, gender, DOB, CNS, language via `UpdateProfileUseCase`. Change Email requires current password confirmation. Change Password shows real-time strength meter via `ValidationUtil.passwordStrength()`. Logout calls `LogoutUseCase` → clears token + SQLDelight → navigate Landing.

### Architecture Map

```
┌──────────────────────────────────────────────────────────────────────┐
│                         Platform Layer                                │
│                                                                       │
│  androidApp/view/                    iosApp/Features/                 │
│  ┌─────────────────────┐            ┌──────────────────────────────┐  │
│  │  FooScreen.kt       │            │  FooView.swift               │  │
│  │  koinViewModel()    │            │  @StateObject FooWrapper     │  │
│  │  collectAsState...  │            │  .task { viewModel.load() }  │  │
│  └────────┬────────────┘            └──────────┬───────────────────┘  │
│           │                                    │                      │
│           │ Koin inject                        │ KoinHelper.get()     │
│           ▼                                    ▼                      │
│  ┌──────────────────────────────────────────────────────────────────┐ │
│  │                   Shared Presentation Layer                       │ │
│  │  FooViewModel.kt : ViewModel()                                    │ │
│  │  _uiState: MutableStateFlow<FooUiState>  ←── .update { copy() } │ │
│  │  uiState: StateFlow<FooUiState>           ─── .asStateFlow()     │ │
│  │  viewModelScope.launch { useCase().onSuccess/onFailure }         │ │
│  └──────────┬───────────────────────────────────────────────────────┘ │
│             │ constructor inject                                       │
│             ▼                                                          │
│  ┌──────────────────────────────────────────────────────────────────┐ │
│  │                     Domain Layer                                  │ │
│  │  FooUseCase(fooRepository: FooRepository) {                      │ │
│  │      operator fun invoke(): Result<T> = repository.get()         │ │
│  │  }                                                               │ │
│  │  interface FooRepository { suspend fun get(): Result<T> }        │ │
│  └──────────┬───────────────────────────────────────────────────────┘ │
│             │ Koin binds interface → impl                             │
│             ▼                                                          │
│  ┌──────────────────────────────────────────────────────────────────┐ │
│  │                      Data Layer                                   │ │
│  │  FooRepositoryImpl(apiService: ApiService, db: ESklepiosDB)      │ │
│  │    → runCatching { db.queries.select() }        (local cache)    │ │
│  │    → apiService.getFoo().map { dto.toDomain() } (network)        │ │
│  │  KtorApiService.getFoo() → safeCall { client.get("/foo").body() }│ │
│  │  HttpClientFactory → Auth { bearer { tokenStorage.getToken() } } │ │
│  └──────────┬───────────────────────────────────────────────────────┘ │
│             │                                                          │
│     ┌───────┴──────────────────────────┐                              │
│     │                                  │                              │
│     ▼                                  ▼                              │
│  TokenStorage (interface)           SQLDelight ESklepiosDatabase      │
│  ┌──────────────────────┐           ┌────────────────────────────┐    │
│  │ Android: SecureStorage│          │ Android: AndroidSqliteDriver│    │
│  │ iOS: IosTokenStorage  │          │ iOS: NativeSqliteDriver     │    │
│  └──────────────────────┘          └────────────────────────────┘    │
└──────────────────────────────────────────────────────────────────────┘
```

---

## PHASE 2 — SCREEN INVENTORY

### 1. Splash Screen

- **Android**: `view/splash/SplashScreen.kt` — `@Composable fun SplashScreen(navController)`
- **iOS**: `Features/Splash/SplashView.swift` + `SplashViewModelWrapper.swift`
- **ViewModel**: `SplashViewModel` (shared) — `checkAuth()` reads `authRepository.isLoggedIn()`
- **Route**: `NavDestination.Splash` = `"splash"` (startDestination)
- **Entry**: app start
- **Exit**: `isAuthenticated == true` → Home; `isAuthenticated == false` → Landing
- **Components**: `GradientHeader` (iOS `GradientHeader`), `AppTitleText` for brand name, `LoadingIndicator`
- **APIs**: none — reads TokenStorage synchronously
- **Key strings**: `app.name`, `splash.loading`

### 2. Landing Screen

- **Android**: `view/landing/LandingScreen.kt`
- **iOS**: `Features/Landing/LandingView.swift`
- **ViewModel**: `HomeViewModel` (shared via activity scope on Android) for pre-loading search
- **Route**: `NavDestination.Landing` = `"landing"`
- **Entry**: Splash (unauthenticated) or Logout
- **Exit**: → Login, → Register, → Home (guest mode via `continueAsGuest()`)
- **Components**: `AppGradientHeader`/`AppGradientHeaderView`, `SearchCard`, `PrimaryButton`, `SecondaryButton`, `GlassButton`, `AppTitleText`, `AppBodyText`
- **APIs**: none (search triggered on Home)
- **Key strings**: `landing.title`, `landing.subtitle`, `landing.login`, `landing.register`, `landing.continue_as_guest`

### 3. Login Screen

- **Android**: `view/auth/login/LoginScreen.kt`
- **iOS**: `Features/Auth/Login/LoginView.swift` + `AuthViewModelWrapper.swift`
- **ViewModel**: `AuthViewModel` (shared) — `login()`, `updateField(EMAIL/PASSWORD)`, `forgotPassword()`
- **Route**: `NavDestination.Login` = `"login"`
- **Entry**: Landing, Register ("already have account")
- **Exit**: `isLoggedIn == true` → Home (clearing back stack); → ForgotPassword; → Register
- **Components**: `AppFormScreen`/`AppScreen`, `FormField` (email, password), `PrimaryButton`, `AppTextLink`, `GoogleSignInButton`, `AppleSignInButton`, `DividerWithLabel`
- **APIs**: `POST /auth/login`
- **Key strings**: `login.title`, `login.email`, `login.password`, `login.forgot_password`, `login.sign_in`, `login.no_account`, `login.register`

### 4. Register Screen

- **Android**: `view/auth/register/RegisterScreen.kt`
- **iOS**: `Features/Auth/Register/RegisterView.swift` (uses `AuthViewModelWrapper`)
- **ViewModel**: `AuthViewModel` — `register()`, multi-step via `uiState.step`
- **Route**: `NavDestination.Register` = `"register"`
- **Entry**: Landing, Login
- **Exit**: `isLoggedIn == true` → Home
- **Components**: `AppFormScreen`, `FormField` (firstName, lastName, email, password, confirmPassword, phone, gender picker, DOB, CNS), `PrimaryButton`, step indicator
- **APIs**: `POST /auth/register`
- **Key strings**: `register.*` section (~20 keys), `gender.*` keys, `cd.*` accessibility keys

### 5. Forgot Password Screen

- **Android**: `view/auth/forgotpassword/ForgotPasswordScreen.kt`
- **iOS**: `Features/Auth/ForgotPassword/ForgotPasswordView.swift`
- **ViewModel**: `AuthViewModel` — `forgotPassword()`, `uiState.forgotPasswordSent`
- **Route**: `NavDestination.ForgotPassword` = `"forgot_password"`
- **Entry**: Login
- **Exit**: Success state shown inline; back → Login
- **Components**: `AppFormScreen`, `FormField` (email), `PrimaryButton`, `EmptyStateView` (success)
- **APIs**: `POST /auth/forgot-password`
- **Key strings**: `forgot_password.*` section

### 6. Home Screen

- **Android**: `view/dashboard/home/HomeScreen.kt`
- **iOS**: `Features/Dashboard/Home/HomeView.swift` + `HomeViewModelWrapper.swift`
- **ViewModel**: `HomeViewModel` — `search()`, `setDateFilter()`, `toggleNewPatientsFilter()`, `toggleFavorite()`, computed `filteredPractitioners`
- **Route**: `NavDestination.Home` = `"home"` (bottom nav tab 0)
- **Entry**: Splash (authenticated), Login/Register success, bottom nav
- **Exit**: → PractitionerDetail (card header tap), → PractitionerList ("see all"), → BookAppointment (slot button tap)
- **Components**: `AppGradientHeader`, `SearchCard` (Dark variant), `FilterChip` (date chips), `PractitionerCard`, `LoadingIndicator`, `EmptyStateView`, `ErrorView`
- **APIs**: `GET /practitioners` (search)
- **Key strings**: `home.*` section (~15 keys), `home_filter_*` keys

### 7. Practitioner List Screen

- **Android**: `view/dashboard/home/practitioners/PractitionerListScreen.kt`
- **iOS**: `Features/Dashboard/Home/PractitionerList/PractitionerListView.swift` + `SearchResultsView.swift`
- **ViewModel**: `HomeViewModel` (shared activity-scoped instance on Android)
- **Route**: `NavDestination.PractitionerList` = `"practitioners"`
- **Entry**: Home ("see all")
- **Exit**: → PractitionerDetail, back → Home
- **Components**: `AppToolbar`, `SearchCard` (Light variant), `FilterChip`, `LazyColumn`/`List` of `PractitionerCard`, `EmptyStateView`, `LoadingIndicator`
- **APIs**: same `HomeViewModel` state, no additional API
- **Key strings**: `practitioner_list.*` section

### 8. Practitioner Detail Screen

- **Android**: `view/dashboard/home/practitioner_data/PractitionerDetailScreen.kt`
- **iOS**: `Features/Dashboard/Home/PractitionerDetail/PractitionerDetailView.swift` + `PractitionerDetailViewModelWrapper.swift`
- **ViewModel**: `PractitionerDetailViewModel` — `loadPractitioner(id)`, `selectSlot(slotId)`
- **Route**: `NavDestination.PractitionerDetail` = `"practitioner_detail/{practitionerId}"`
- **Entry**: PractitionerCard header tap (Home, PractitionerList)
- **Exit**: → BookAppointment (`practitionerId` + `slotId`), back navigation
- **Components**: `AppGradientHeader` (with avatar, name, specialty), `InfoCard` (contact, schedule, payment), `MapPreviewCard`, slot grid, `AppToolbar`
- **APIs**: `GET /practitioners/{id}`
- **Key strings**: `practitioner_detail.*` section

### 9. Book Appointment Screen

- **Android**: `view/dashboard/appointments/booking/BookingScreen.kt`
- **iOS**: `Features/Dashboard/Appointments/Booking/BookAppointmentView.swift` + `BookAppointmentViewModelWrapper.swift`
- **ViewModel**: `BookAppointmentViewModel` — `loadData(practitionerId, slotId)`, `confirm()`, `updateConsultationReason()`, `updateMessageToDoctor()`
- **Route**: `NavDestination.BookAppointment` = `"book_appointment/{practitionerId}/{slotId}"` (also `NavDestination.Booking` with `isChange` param)
- **Entry**: PractitionerDetail (slot tap), MyAppointments (modify action)
- **Exit**: `isConfirmed == true` → AppointmentSuccess; back
- **Components**: `AppFormScreen`, `CheckRow` (appointment summary), `TextAreaField` (reason, message), `PrimaryButton`, `LoadingIndicator`
- **APIs**: `POST /appointments`
- **Key strings**: `booking.*` section

### 10. Appointment Success Screen

- **Android**: `view/dashboard/appointments/booking/AppointmentSuccessScreen.kt`
- **iOS**: `Features/Dashboard/Appointments/Booking/AppointmentSuccessView.swift` + `AppointmentSuccessViewModelWrapper.swift`
- **ViewModel**: `AppointmentSuccessViewModel` — loads confirmation details
- **Route**: `NavDestination.AppointmentSuccess` = `"appointment_success/{appointmentId}"`
- **Entry**: BookAppointment on success
- **Exit**: → Home (clearing booking back stack), → MyAppointments
- **Components**: success icon, `AppTitleText`, `AppBodyText`, `CheckRow` (appointment summary), `PrimaryButton` (go to appointments), `SecondaryButton` (go home)
- **APIs**: none (uses data already in state)
- **Key strings**: `appointment_success.*` section

### 11. My Appointments Screen

- **Android**: `view/dashboard/appointments/MyAppointmentsScreen.kt`
- **iOS**: `Features/Dashboard/Appointments/MyAppointmentsView.swift` + `MyAppointmentsViewModelWrapper.swift`
- **ViewModel**: `MyAppointmentsViewModel` — `loadAppointments()`, `cancelAppointment(id)`, tab switching (upcoming/past)
- **Route**: `NavDestination.MyAppointments` = `"my_appointments"` (bottom nav tab 1)
- **Entry**: bottom nav, AppointmentSuccess
- **Exit**: → BookAppointment (modify), back navigation
- **Components**: `AppGradientHeader`, `AppTabRow` (Upcoming/Past), `AppointmentCard`/`StatusBadge`, `EmptyStateView`, `LoadingIndicator`, `ErrorView`
- **APIs**: `GET /appointments?user_id=...`, `DELETE /appointments/{id}`
- **Key strings**: `appointments.*` section, `status_*` keys

### 12. Profile Screen

- **Android**: `view/dashboard/profile/ProfileScreen.kt`
- **iOS**: `Features/Dashboard/Profile/ProfileView.swift` + `ProfileViewModelWrapper.swift`
- **ViewModel**: `ProfileViewModel` — `loadProfile()`, `logout()`, `uiState.isLoggedOut`
- **Route**: `NavDestination.Profile` = `"profile"` (bottom nav tab 2)
- **Entry**: bottom nav
- **Exit**: `isLoggedOut == true` → Landing; → EditProfile, → ChangeEmail, → ChangePassword
- **Components**: `AppGradientHeader` (centered profile layout), `AvatarCircle`, `InfoCard`, `InfoRow` (profile fields), `AppTextLink` (action rows), `SecondaryButton` (logout)
- **APIs**: `GET /profile`
- **Key strings**: `profile.*` section

### 13. Edit Profile Screen

- **Android**: `view/dashboard/profile/profile_edit/EditProfileScreen.kt`
- **iOS**: `Features/Dashboard/Profile/ProfileEdit/EditProfileView.swift` + `EditProfileViewModelWrapper.swift`
- **ViewModel**: `EditProfileViewModel` — `loadProfile()`, `updateProfile()`, field update methods
- **Route**: `NavDestination.EditProfile` = `"edit_profile"`
- **Entry**: Profile
- **Exit**: success → back to Profile; back
- **Components**: `AppFormScreen`, `FormField` (firstName, lastName, phone, gender dropdown, DOB picker, CNS, language picker), `PrimaryButton`
- **APIs**: `GET /profile`, `PUT /profile`
- **Key strings**: `edit_profile.*` section

### 14. Change Email Screen

- **Android**: `view/dashboard/profile/profile_edit/ChangeEmailScreen.kt`
- **iOS**: `Features/Dashboard/Profile/ProfileEdit/ChangeEmailView.swift` + `ChangeEmailViewModelWrapper.swift`
- **ViewModel**: `ChangeEmailViewModel` — `changeEmail()`, validates with `ValidationUtil.isValidEmail()`, `emailsMatch()`
- **Route**: `NavDestination.ChangeEmail` = `"change_email"`
- **Entry**: Profile
- **Exit**: success → back; back
- **Components**: `AppFormScreen`, `FormField` (newEmail, confirmEmail, password), `PrimaryButton`, `ValidationCaption`/`AppErrorText`
- **APIs**: `PUT /profile/email`
- **Key strings**: `change_email.*` section

### 15. Change Password Screen

- **Android**: `view/dashboard/profile/profile_edit/ChangePasswordScreen.kt`
- **iOS**: `Features/Dashboard/Profile/ProfileEdit/ChangePasswordView.swift` + `ChangePasswordViewModelWrapper.swift`
- **ViewModel**: `ChangePasswordViewModel` — `changePassword()`, `ValidationUtil.passwordStrength()`, `passwordCriteria()`
- **Route**: `NavDestination.ChangePassword` = `"change_password"`
- **Entry**: Profile
- **Exit**: success → back; back
- **Components**: `AppFormScreen`, `FormField` (oldPassword, newPassword, confirmPassword), password strength progress bar, criteria checklist (`ValidationCaption`), `PrimaryButton`
- **APIs**: `PUT /profile/password`
- **Key strings**: `change_password.*` section, `change_password_strength_*` keys

---

## PHASE 3 — USER FLOWS

```
1. SPLASH → AUTH FLOW
   App Launch
       │
       ▼
   SplashScreen (SplashViewModel.checkAuth())
       │
       ├── isAuthenticated == true ──────────────────────────────► HOME
       │
       └── isAuthenticated == false
               │
               ▼
           LandingScreen
               │
               ├── "Login" ──────────► LoginScreen
               │                           │
               │                           ├── success → HOME (popUpTo 0)
               │                           ├── "Forgot Password" → ForgotPasswordScreen
               │                           │                           │ success (inline)
               │                           │                           └── back → Login
               │                           └── "Register" → RegisterScreen
               │                                               │ success → HOME (popUpTo 0)
               │
               ├── "Register" ────────► RegisterScreen (same as above)
               │
               └── "Continue as Guest" ─► HOME (guest mode, isLoggedIn=true)

2. HOME FLOW
   HomeScreen (bottom nav tab 0)
       │
       ├── Search (text + location) → filteredPractitioners updated (client-side)
       ├── Date filter chip → setDateFilter()
       ├── "New Patients" toggle → toggleNewPatientsFilter()
       ├── Favorite heart → toggleFavorite()
       ├── "See All" → PractitionerListScreen
       │                   │
       │                   └── (same exit points as HomeScreen card taps)
       │
       └── Card header tap ──► PractitionerDetailScreen
               │
               └── Slot button tap ──► BookAppointmentScreen

3. BOOKING FLOW
   BookAppointmentScreen
       │
       ├── Fill reason + message
       └── "Confirm" ──► AppointmentSuccessScreen
               │
               ├── "View Appointments" → MyAppointmentsScreen
               └── "Back to Home" → HomeScreen (popUpTo Home)

4. MY APPOINTMENTS FLOW
   MyAppointmentsScreen (bottom nav tab 1)
       │
       ├── Tab "Upcoming" ─── upcoming list
       ├── Tab "Past" ──────── past list
       ├── "Cancel" → confirm dialog → cancelAppointment() → refresh
       └── "Modify" → BookAppointmentScreen (isChange=true)
               │
               └── success → AppointmentSuccessScreen → back to MyAppointments

5. PROFILE FLOW
   ProfileScreen (bottom nav tab 2)
       │
       ├── "Edit Profile" → EditProfileScreen → success → back
       ├── "Change Email" → ChangeEmailScreen → success → back
       ├── "Change Password" → ChangePasswordScreen → success → back
       └── "Logout" → ProfileViewModel.logout()
               │
               └── isLoggedOut == true → LandingScreen (popUpTo 0 inclusive)

6. BACK NAVIGATION RULES
   - Auth screens: standard back stack pop
   - Booking success: back button goes to Home, NOT BookAppointment (popUpTo)
   - Logout: clears entire back stack (popUpTo(0) { inclusive = true })
   - Bottom nav switches: popUpTo(Home) { saveState = true } + restoreState = true
   - iOS: NavigationStack automatically handles back; RootView state transitions use animation

7. DRAWER NAVIGATION (Android only)
   - Hamburger icon on Home, MyAppointments, Profile screens
   - Drawer shows user info + quick-logout
   - Logout from drawer → LandingScreen (popUpTo 0)
```

---

## PHASE 4 — DESIGN SYSTEM EXTRACTION

### Typography — all App*Text components

| Android Composable | iOS Struct | Material Style | Font | Size | Weight |
|---|---|---|---|---|---|
| `AppTitleText` | `AppTitleText` | `headlineMedium` | DMSans | 20sp/22pt | SemiBold |
| `AppSubtitleText` | `AppSubtitleText` | `titleMedium` | DMSans | 15sp/15pt | Medium |
| `AppBodyText` | `AppBodyText` | `bodyMedium` | DMSans | 14sp/14pt | Regular |
| `AppCaptionText` | `AppCaptionText` | `bodySmall` | DMSans | 12sp/12pt | Regular |
| `AppToolbarTitle` | `AppToolbarTitle` | `titleLarge` | DMSans | 16sp/16pt | SemiBold |
| `AppButtonText` | `AppButtonText` | `labelLarge` | DMSans | 14sp/14pt | SemiBold |
| `AppLabelText` | `AppLabelText` | `labelLarge` | DMSans | 14sp/14pt | SemiBold |
| `AppErrorText` | `AppErrorText` | `bodySmall` | DMSans | 12sp/12pt | Regular (Danger color) |
| `FormFieldLabel` | `FormFieldLabel` | `labelLarge` | DMSans | 14sp | SemiBold |
| `AppSectionHeaderText` | `AppSectionHeaderText` | custom | DMSans | 10sp/nano | Bold, 0.8sp letter-spacing |
| `AppFieldValueText` | `AppFieldValueText` | custom | DMSans | 12sp | SemiBold |
| — | `ValidationCaption` | — | DMSans | 13pt/label | Medium |

Additional iOS-only: `SubheadingText`, `OverlineText`, `GradientText`, `CardSectionTitle`, `CardTitleText`, `LabelText`, `BodyText`, `BodyMediumText`, `CaptionText`, `BannerText`

### Colors — all tokens with hex values

| Token (Android) | Token (iOS extension) | Hex | Usage |
|---|---|---|---|
| `Primary` | `.appPrimary` | `#3B4FE8` | CTAs, active tabs, accent |
| `PrimaryDark` | `.appPrimaryDark` | `#1A2580` | Gradient end, dark variant |
| `PrimaryLight` | `.appPrimaryLight` | `#EEF0FD` | Subtle backgrounds, filter chips |
| `PrimaryMid` | `.appPrimaryMid` | `#6B7BED` | Icon tint, secondary |
| `Background` | `.appBackground` | `#F4F6FB` | Screen background |
| `Surface` | `.appSurface` | `#FFFFFF` | Cards, modals |
| `TextPrimary` | `.appTextPrimary` | `#1A1D2E` | Main text |
| `TextSecondary` | `.appTextSecondary` | `#6B7280` | Secondary labels |
| `TextHint` | `.appTextHint` | `#9CA3AF` | Placeholders |
| `BorderColor` | (11% black) | `#0000001A` | Card borders |
| `BorderLight` | (6% black) | `#0000000F` | Dividers |
| `Success` | `.appSuccess` | `#3B6D11` | Confirmed status |
| `SuccessBg` | `.appSuccessBg` | `#EAF3DE` | Confirmed badge background |
| `Danger` | `.appDanger` | `#D83B3B` | Errors, cancelled status |
| `DangerBg` | `.appDangerBg` | `#FFF3F3` | Error background |
| `Warning` | `.appWarning` | `#F59E0B` | Pending status |
| `WarningBg` | `.appWarningBg` | `#FAEEDA` | Warning background |
| `GradientStart` | `AppGradient.primary start` | `#2C3AEF` | Header gradient start |
| `GradientEnd` | `AppGradient.primary end` | `#1A2580` | Header gradient end |
| `TealAccent` | `.appTealAccent` | `#4DD0E1` | Decorative accent |
| `FavoriteRed` | `.appFavoriteRed` | `#FF6B6B` | Favorite heart |
| `GoogleBlue` | `.appGoogleBlue` | `#4285F4` | Google sign-in button |
| `StrengthGood` | `.appStrengthGood` | `#65A30D` | Password strength good |

### Spacing — all Dimens tokens

| Token | Android (dp) | iOS (CGFloat) |
|---|---|---|
| `paddingNone` / `Spacing.none` | 0 | 0 |
| `paddingXXS` / `Spacing.xxs` | 2 | 2 |
| `paddingXS` / `Spacing.xs` | 4 | 4 |
| `paddingTiny` / `Spacing.tiny` | 6 | 6 |
| `paddingS` / `Spacing.s` | 8 | 8 |
| `paddingM` / `Spacing.m` | 12 | 12 |
| `paddingPlus` / `Spacing.plus` | 14 | 14 |
| `paddingL` / `Spacing.l` | 16 | 16 |
| `paddingXL` / `Spacing.xl` | 20 | 20 |
| `paddingXXL` / `Spacing.xxl` | 24 | 24 |
| `paddingXXXL` / `Spacing.xxxl` | 32 | 32 |

### Radius tokens

| Token | Android (dp) | iOS (CGFloat) |
|---|---|---|
| `cornerNone` / `Radius.none` | 0 | 0 |
| `radiusSm` / `Radius.sm` | 8 | 8 |
| — / `Radius.input` | — | 10 |
| `radiusMd` / `Radius.md` | 12 | 12 |
| `radiusAction` / `Radius.action` | 14 | 14 |
| `radiusLg` / `Radius.lg` | 16 | 16 |
| `radiusCard` / `Radius.card` | 18 | 18 |
| `radiusXl` / `Radius.xl` | 20 | 20 |
| `radiusPill` / `Radius.pill` | 50 | 50 |

### Elevation tokens

| Token | Android (dp) |
|---|---|
| `elevationNone` | 0 |
| `cardElevation` | 2 |

### Icon sizes

| Token | Android (dp) | iOS (CGFloat) |
|---|---|---|
| `iconSizeXxs` / `Sizing.iconXxs` | 13 | 10 |
| `iconSizeChevron` / `Sizing.iconChevron` | 13 | 13 |
| `iconSizeMicro` / `Sizing.iconMicro` | 14 | 14 |
| `iconSizeSm` / `Sizing.iconSm` | 16 | 16 |
| `iconSizeCompact` / `Sizing.iconCompact` | 18 | 18 |
| `iconSizeMd` / `Sizing.iconMd` | 20 | 20 |
| `iconSizeLg` / `Sizing.iconLg` | 24 | 24 |
| `iconButtonSize` / `Sizing.iconButton` | 40 | 40 |

### Avatar sizes

| Token | Android (dp) | iOS (CGFloat) |
|---|---|---|
| `avatarSizeMd` / `Sizing.avatarMd` | 48 | 48 |
| `avatarSizeLg` / `Sizing.avatarLg` | 64 | 64 |
| `avatarSizeXl` / `Sizing.avatarXl` | 84 | 84 |
| `detailAvatarSize` | 56 | 56 |

### Component Heights

| Component | Android (dp) | iOS (CGFloat) |
|---|---|---|
| `buttonHeight` / `Sizing.buttonHeight` | 52 | 52 |
| `inputHeight` / `Sizing.inputHeight` | — | 52 |
| `appBarHeight` / `toolbarHeight` | 56 | 56 |
| `statusBadgeHeight` | 24 | — |
| `filterChipHeight` | 32 | — |
| `mapPreviewHeight` | 140 | 140 |
| `timeSlotGridHeight` | 120 | 120 |

### Component Inventory

**Buttons**: `PrimaryButton` (gradient pill, loading state), `SecondaryButton` (outlined pill), `GhostButton` (text-only), `GlassButton` (frosted glass for gradient overlays), `GoogleSignInButton`, `AppleSignInButton`

**Cards**: `AppCard` (surface + elevation + optional click), `InfoCard` (colored header + body), `PractitionerCard` (5-day slot grid, expandable), `AppointmentCard` (status badge + datetime)

**Inputs**: `FormField` (labeled + password toggle + error), `TextAreaField`, `SearchInputField` (Light/Dark variant), `SearchCard` (specialty + location combo)

**Headers**: `GradientHeader` (primitive), `AppGradientHeader`/`AppGradientHeaderView` (composite with interchangeable content zones)

**Navigation**: `AppToolbar` (back + title + trailing), `AppBottomNavBar` (Android), `AppTabView` (iOS), `AppDrawer` (Android slide-out)

**Status**: `LoadingIndicator`/`LoadingView`, `ErrorView`, `EmptyStateView`, `StatusBadge`

**Filter/Chips**: `FilterChip`, `AvailabilityChip`

**Info**: `InfoRow`, `CheckRow`, `MapPreviewCard`

**Text/Links**: `AppTextLink`, `DividerWithLabel`

**Avatars**: `AvatarCircle`

**Layout helpers**: `VSpace(Dp)`, `HSpace(Dp)`, `AppFormScreen`/`AppScreen` (scroll shell)

**Icons**: `AppIcon`, `AppIconButton`

---

## PHASE 5 — DOMAIN MODEL INVENTORY

### Domain Models

**`User`** (`domain/model/User.kt`)
Fields: `id: String`, `firstName: String`, `lastName: String`, `email: String`, `phone: String`, `gender: String`, `dateOfBirth: String`, `cnsNumber: String`, `profileType: ProfileType`, `language: String`
Computed: `fullName: String`, `initials: String`
Relations: has `ProfileType` enum (`PATIENT`, `PRACTITIONER`)

**`Practitioner`** (`domain/model/Practitioner.kt`)
Fields: `id`, `firstName`, `lastName`, `specialty`, `clinicName`, `address`, `city`, `postalCode`, `phone`, `email`, `latitude: Double`, `longitude: Double`, `acceptingNewPatients: Boolean`, `availableSlots: List<AppointmentSlot>`, `schedule: List<ScheduleEntry>`, `paymentMethods: List<String>`, `diplomas: List<String>`, `presentation: String`, `isFavorite: Boolean`
Computed: `fullName: String`, `initials: String`
Note: `ScheduleEntry` is `@Serializable` (stored as JSON blob in SQLDelight)

**`Appointment`** (`domain/model/Appointment.kt`)
Fields: `id`, `practitionerId`, `practitionerName`, `clinicName`, `specialty`, `dateTime: String` (ISO-8601), `status: AppointmentStatus`, `messageToDoctor: String`, `consultationReason: String`
Relations: has `AppointmentStatus` enum (`PENDING`, `CONFIRMED`, `CANCELLED`, `COMPLETED`, `NO_SHOW`)

**`AppointmentSlot`** (`domain/model/AppointmentSlot.kt`)
Fields: `id`, `practitionerId`, `dateTime: String`, `available: Boolean`, `durationMinutes: Int`
Note: `@Serializable` because stored as JSON blob in SQLDelight

**`ScheduleEntry`** (`domain/model/Practitioner.kt`)
Fields: `day: String`, `hours: String`
Note: `@Serializable` (JSON blob storage)

**`ScheduleDay`** (`domain/model/ScheduleDay.kt`)
Fields: `dayOfWeek: String`, `openTime: String`, `closeTime: String`, `isOpen: Boolean`

### DTOs (`data/network/DTOs.kt` — 14 @Serializable classes)

`LoginRequest`, `LoginResponse` (token + refreshToken + user), `RegisterRequest` (all user fields + password), `RegisterResponse`, `ForgotPasswordRequest/Response`, `RefreshTokenRequest/Response`, `AppointmentSlotDto`, `ScheduleDayDto`, `PractitionerDto`, `AppointmentDto`, `UserDto`, `CreateAppointmentRequest`, `ModifyAppointmentRequest`, `UpdateProfileRequest`, `ChangeEmailRequest`, `ChangePasswordRequest`

All fields use `@SerialName("snake_case")`. All DTOs use `@Serializable`.

### UiState Classes (one per ViewModel)

| UiState | Key Fields beyond isLoading/error |
|---|---|
| `SplashUiState` | `isAuthenticated: Boolean` |
| `AuthUiState` | `isLoggedIn`, `email`, `password`, `confirmPassword`, `firstName`, `lastName`, `phone`, `gender`, `dateOfBirth`, `cnsNumber`, `profileType`, `step: Int`, `forgotPasswordSent` |
| `HomeUiState` | `hasSearched`, `allPractitioners: List<Practitioner>`, `practitioners: List<Practitioner>`, `specialtyQuery`, `locationQuery`, `selectedDateFilter`, `openToNewPatients` |
| `PractitionerDetailUiState` | `practitioner: Practitioner?`, `selectedSlot: AppointmentSlot?` |
| `BookAppointmentUiState` | `practitioner: Practitioner?`, `selectedSlot: AppointmentSlot?`, `previousAppointment: Appointment?`, `consultationReason`, `messageToDoctor`, `isAuthenticated`, `isConfirmed`, `confirmedAppointmentId` |
| `AppointmentSuccessUiState` | `appointment: Appointment?` |
| `MyAppointmentsUiState` | `upcomingAppointments: List<Appointment>`, `pastAppointments: List<Appointment>`, `selectedTab: Int` |
| `ProfileUiState` | `user: User?`, `isLoggedOut` |
| `EditProfileUiState` | all profile fields editable, `isSuccess` |
| `ChangeEmailUiState` | `newEmail`, `confirmEmail`, `password`, `isSuccess`, validation errors |
| `ChangePasswordUiState` | `oldPassword`, `newPassword`, `confirmPassword`, `passwordStrength`, `criteria`, `isSuccess` |

### Repository Interfaces

| Interface | Location | Methods |
|---|---|---|
| `AuthRepository` | `domain/repository/AuthRepository.kt` | `login`, `register`, `forgotPassword`, `refreshToken`, `logout`, `isLoggedIn()`, `getCurrentUser()` |
| `PractitionerRepository` | `domain/repository/PractitionerRepository.kt` | `searchPractitioners`, `getPractitionerById`, `toggleFavorite` |
| `AppointmentRepository` | `domain/repository/AppointmentRepository.kt` | `createAppointment`, `getAppointments(): Flow<>`, `getUpcomingAppointments`, `getPastAppointments`, `modifyAppointment`, `cancelAppointment` |
| `UserRepository` | `domain/repository/UserRepository.kt` | `getProfile`, `updateProfile`, `changeEmail`, `changePassword` |

### Use Cases (15 total in `domain/usecase/`)

All are single-`invoke`-operator classes injecting a repository:
`LoginUseCase`, `RegisterUseCase`, `ForgotPasswordUseCase`, `LogoutUseCase`, `SearchPractitionersUseCase`, `GetPractitionerDetailUseCase`, `ToggleFavoriteUseCase`, `CreateAppointmentUseCase`, `GetUpcomingAppointmentsUseCase`, `GetPastAppointmentsUseCase`, `CancelAppointmentUseCase`, `ModifyAppointmentUseCase`, `GetProfileUseCase`, `UpdateProfileUseCase`, `ChangeEmailUseCase`, `ChangePasswordUseCase`

---

## PHASE 6 — REUSABLE COMPONENT INVENTORY

### Android Components (`core/ui/components/`)

| Component | Purpose | Key Parameters | iOS Counterpart |
|---|---|---|---|
| `PrimaryButton` | Gradient pill CTA | `text, onClick, modifier, enabled, isLoading` | `PrimaryButton` |
| `SecondaryButton` | Outlined pill | `text, onClick, modifier, enabled` | `SecondaryButton` |
| `GhostButton` | Text-only action | `text, onClick, modifier, textColor` | `GhostButton` |
| `GlassButton` | Frosted glass (on gradients) | `text, onClick, modifier` | `GlassButton` |
| `GoogleSignInButton` | Google SSO | `onClick, modifier` | — |
| `AppleSignInButton` | Apple SSO | `onClick, modifier` | — |
| `AppCard` | Base card | `modifier, onClick?, content` | `AppCard` |
| `InfoCard` | Titled card section | `modifier, title, icon, content` | (AppCard + header) |
| `FormField` | Labeled text input | `label, value, onValueChange, isPassword, leadingIcon?, errorMessage?` | (custom in AppScreen) |
| `TextAreaField` | Multi-line input | `label, value, onValueChange, minLines, errorMessage?` | — |
| `SearchInputField` | Search input | `value, onValueChange, placeholder, variant(Light/Dark)` | `SearchInputField` |
| `SearchCard` | Specialty+location combo | `searchQuery, locationQuery, onSearchClick, variant` | `SearchCard` |
| `GradientHeader` | Gradient box primitive | `modifier, roundedBottom, topPadding, bottomPadding, content` | `GradientHeader` |
| `AppGradientHeader` | Full composite header | `leadingAction, centerAction, trailingAction, textBlock?, profile?, search?` | `AppGradientHeaderView` |
| `AppToolbar` | Back + title + trailing | `title, onNavigateBack?, trailingContent?` | `AppToolbar` |
| `AppDrawerContent` | Side drawer | `onLogout, onCloseDrawer` | — |
| `AppFormScreen` | Scroll+toolbar shell | `title, onNavigateBack?, error?, onErrorDismissed, content` | `AppScreen` |
| `AvatarCircle` | Initials circle | `initials, size, fontSize` | `AvatarCircle` |
| `PractitionerCard` | Full appointment-booking card | `practitioner: PractitionerUiModel, onBook, onSeeProfile` | `PractitionerCard` |
| `StatusBadge` | Colored status chip | `status: AppointmentStatus` | `StatusBadge` |
| `FilterChip` | Toggle filter chip | `label, selected, onClick` | `FilterChip` |
| `AvailabilityChip` | Availability toggle | `filter, selected, onClick` | — |
| `InfoRow` | Icon + label + value | `icon, label, value, modifier` | `InfoRow` |
| `MapPreviewCard` | Map placeholder | `latitude, longitude, address` | `MapPreviewCard` |
| `AppIcon` | Icon with a11y contract | `imageVector, contentDescription, tint, size` | `AppIcon` |
| `AppIconButton` | 44dp tap-target icon btn | `icon, contentDescription, onClick, tint, iconSize, enabled` | `AppIconButton` |
| `AppTabRow` | Material tab bar | `selectedIndex, tabs: List<AppTabItem>` | — |
| `VSpace` | Vertical spacer token | `height: Dp` | (VStack spacing) |
| `HSpace` | Horizontal spacer token | `width: Dp` | (HStack spacing) |
| `CheckRow` | Label-value pair row | `label, value, isLast, leadingIcon?` | `CheckRow` |
| `AppTextLink` | Text navigation link | `text, onClick, modifier` | `AppTextLink` |
| `DividerWithLabel` | "or" divider | `label, modifier` | `DividerWithLabel` |
| `LoadingIndicator` | Centered spinner | `modifier, message?` | `LoadingView` |
| `ErrorView` | Error + retry button | `modifier, message, onRetry?` | `ErrorView` |
| `EmptyStateView` | Empty state with action | `modifier, icon, title, subtitle, actionLabel?, onAction?` | `EmptyStateView` |
| `AppTitleText` | `headlineMedium` text | `text, modifier, color, textAlign, maxLines` | `AppTitleText` |
| `AppSubtitleText` | `titleMedium` text | same | `AppSubtitleText` |
| `AppBodyText` | `bodyMedium` text | same | `AppBodyText` |
| `AppCaptionText` | `bodySmall` text | same | `AppCaptionText` |
| `AppToolbarTitle` | `titleLarge` 1-line text | `text, modifier, color` | `AppToolbarTitle` |
| `AppButtonText` | `labelLarge` text | `text, modifier, color, textAlign` | `AppButtonText` |
| `AppLabelText` | `labelLarge` form label | same | `AppLabelText` |
| `AppErrorText` | `bodySmall` danger text | `text, modifier, textAlign` | `AppErrorText` |
| `FormFieldLabel` | Label + optional asterisk | `text, required, modifier` | `FormFieldLabel` |

---

## PHASE 7 — APP CLONING BLUEPRINT

### Step 1: Rename Kotlin Package

Files that must change — every `.kt` file with `package lu.esklepios.app`:

1. `shared/build.gradle.kts` — change `namespace = "lu.esklepios.app.shared"`, `packageName.set("lu.esklepios.app.db")`, `packageName = "lu.esklepios.app"` → `"lu.newapp.app"` in all three
2. Every `.kt` file in `shared/src/` — change the `package lu.esklepios.app.*` declaration
3. Every `import lu.esklepios.app.*` in all `.kt` files
4. `settings.gradle.kts` — change `rootProject.name = "esklepios"` → `"newapp"`
5. `shared/src/iosMain/kotlin/lu/esklepios/app/di/IosKoinInit.kt` — package + class `IOSViewModelFactory`

Use `find . -name "*.kt" | xargs sed -i '' 's/lu\.esklepios\.app/lu.newapp.app/g'` (adjust for actual new ID)

### Step 2: Rename Android applicationId and build.gradle.kts

Files: `androidApp/build.gradle.kts`
- `namespace = "lu.esklepios.app"` → `"lu.newapp.app"`
- `applicationId = "lu.esklepios.app"` → `"lu.newapp.app"`
- `applicationIdSuffix = ".debug"` (keep)
- All database/package references

### Step 3: Rename iOS Bundle Identifier and product name

Files:
- `iosApp/eSklepios.xcodeproj/project.pbxproj` — change `PRODUCT_BUNDLE_IDENTIFIER = lu.esklepios.app` → `lu.newapp.app`; `PRODUCT_NAME = "eSklepios"` → `"NewApp"`
- `iosApp/eSklepios/eSklepiosApp.swift` — rename `struct eSklepiosApp` → `struct NewApp`, rename file
- `iosApp/eSklepios/Storage/KeychainStorage.swift` — change `let service = "lu.esklepios.app"` → `"lu.newapp.app"`

### Step 4: Replace Branding

**Colors:**
- `androidApp/.../core/ui/theme/Color.kt` — replace `val Primary = Color(0xFF3B4FE8)` and all other hex values
- `iosApp/.../Core/UI/Theme/AppColors.swift` — replace all `Color(hex: "...")` values in the extension

**Gradients:**
- `androidApp/.../core/ui/theme/Gradients.kt` — replace gradient color references
- `iosApp/.../Core/UI/Theme/AppColors.swift` — replace `AppGradient.primary` colors

**App name:**
- `strings/twine.txt` — change `app.name` values
- Run `make strings` to regenerate `strings.xml`
- iOS: update app name in `Info.plist` (`CFBundleDisplayName`)

### Step 5: Replace Assets

- `androidApp/src/main/res/mipmap-*/` — replace all `ic_launcher*` files with new icon
- `androidApp/src/main/res/drawable/` — replace splash assets
- `iosApp/eSklepios/Assets.xcassets/` — replace `AppIcon.appiconset/` with new icon images
- Update `LaunchScreen.storyboard` or replace with new splash design

### Step 6: Replace Localization Strings

- `strings/twine.txt` — the entire file. Keep the structure (sections, format) but replace all string values. The key names can stay identical — only the content changes.
- Run `make strings` after any change
- Key sections to review: `[[General]]`, `[[Navigation]]`, `[[Auth]]`, `[[Home]]`, `[[Practitioner]]`, `[[Appointments]]`, `[[Profile]]`, `[[Status]]`, `[[cd]]` (accessibility)

### Step 7: Replace API Endpoints

- `dev.properties` — change `BASE_API_URL=https://dev-api.esklepios.lu/v1` → your dev URL
- `prod.properties` — change `BASE_API_URL=https://api.esklepios.lu` → your prod URL (already committed; contains non-sensitive values only)
- `shared/build.gradle.kts` — the `buildkonfig { }` block reads from these files automatically
- `shared/src/commonMain/.../data/network/KtorApiService.kt` — update endpoint paths (`/auth/login`, `/practitioners`, etc.) to match your backend
- Run `./gradlew :shared:generateBuildKonfig` after property changes

### Step 8: Replace Domain Models

**If your domain is different from healthcare:**
1. `shared/src/commonMain/.../domain/model/` — replace `Practitioner.kt`, `Appointment.kt`, `AppointmentSlot.kt`, `ScheduleDay.kt`. Keep `User.kt` structure (adapt fields).
2. `shared/src/commonMain/.../data/network/DTOs.kt` — replace all DTOs to match your API contract
3. `shared/src/commonMain/.../data/network/Mappers.kt` — rewrite all `toDomain()` / `toDto()` extensions
4. `shared/src/commonMain/.../domain/repository/` — rewrite interfaces for new entities
5. `shared/src/commonMain/.../domain/usecase/` — replace use cases for new business operations
6. `shared/src/commonMain/.../data/repository/` — rewrite implementations

**Util files to adapt:**
- `AppointmentStatusOptions.kt` → rename/replace with your entity status options
- `DateFilter.kt` → adapt filter options to your domain
- Keep `ValidationUtil.kt`, `PasswordStrength.kt`, `CnsFormatter.kt` (or replace CNS with your country-specific ID format), `Gender.kt`, `Locales.kt`, `DialCodes.kt`, `FlowExtensions.kt` (keep as-is)

### Step 9: Adapt Navigation

**Android:**
- `NavDestination.kt` — rename objects to match your screens (keep the sealed class pattern)
- `AppNavGraph.kt` — update `composable()` registrations and route strings; update bottom nav `items` list if your tabs differ

**iOS:**
- `RootView.swift` — update if auth flow structure changes
- `AppTabView.swift` — update tab items (label, systemImage, view type)
- Feature views — update `NavigationStack` + `.navigationDestination(for:)` cases

### Step 10: Validate Android Build

```bash
./gradlew clean
./gradlew :shared:generateBuildKonfig
./gradlew :shared:commonTest
./gradlew :androidApp:assembleDebug
./gradlew :androidApp:lint
./gradlew :shared:detekt
make strings   # validate localization
```

Check for: package resolution errors, missing Koin bindings (will fail at startup with `NoBeanDefFoundException`), missing string resources.

### Step 11: Validate iOS Build

```bash
./gradlew :shared:assembleXCFramework
xcodebuild -project iosApp/eSklepios.xcodeproj \
  -scheme eSklepios \
  -destination 'platform=iOS Simulator,name=iPhone 16' \
  build
xcodebuild test \
  -project iosApp/eSklepios.xcodeproj \
  -scheme eSklepios \
  -destination 'platform=iOS Simulator,name=iPhone 16'
```

Check for: Swift compiler errors from renamed KMM classes, missing KoinHelper getters for new ViewModels, `FlowWatcher` type casting errors if UiState class names changed.

---

## PHASE 8 — SCREEN GENERATION GUIDE

For each screen, this checklist applies universally. I'll document the most instructive examples:

### Universal Checklist (all 15 screens)

**1. Create shared ViewModel:**
- File: `shared/src/commonMain/kotlin/lu/esklepios/app/presentation/viewmodel/<Name>ViewModel.kt`
- Extends `ViewModel()`, has `private val _uiState`, `val uiState: StateFlow`, all actions in `viewModelScope.launch { }`, `clearError()`, `override fun onCleared() { super.onCleared() }`
- Register: `factoryOf(::<Name>ViewModel)` in `SharedModule.kt`
- Add getter in `IOSViewModelFactory` and `KoinHelper.swift`

**2. UiState fields needed:**
- Always: `isLoading: Boolean = false`, `error: String? = null`
- List screens: `items: List<T> = emptyList()`
- Detail screens: `item: T? = null`
- Form screens: form fields + `isSuccess: Boolean = false`
- Auth-gated: `isAuthenticated: Boolean`
- Navigation triggers: `isLoggedOut: Boolean`, `isConfirmed: Boolean`

**3. Navigation wiring:**
- Android: add `object <Name> : NavDestination("<name>")` in `NavDestination.kt`; add `composable(NavDestination.<Name>.route) { <Name>Screen(navController) }` in `AppNavGraph.kt`
- iOS: add `.<Name>View()` as `.navigationDestination(for: <Name>DestinationType.self)` in relevant `NavigationStack`

**4. Components to assemble:**
- Form screens → `AppFormScreen` / `AppScreen`
- Dashboard/detail screens → `AppGradientHeader` / `AppGradientHeaderView`
- Every async screen must handle: loading → `LoadingIndicator`, error → `ErrorView`, empty → `EmptyStateView`
- Never raw `Text`, `Button`, `Icon` in `view/` or `Features/` files

**5. Tests:**
- `shared/src/commonTest/kotlin/lu/esklepios/app/<Name>ViewModelTest.kt`
- Inline `Fake<Dependency>Repository` implementing the interface
- `StandardTestDispatcher`, `@BeforeTest`/`@AfterTest`
- Tests: initial state, success path, failure path, clearError, navigation triggers

**6. Android implementation steps:**
1. Add UiState + ViewModel in shared module
2. Register in SharedModule.kt
3. Add `NavDestination` object
4. Add `composable()` to AppNavGraph
5. Create `FooScreen.kt` in correct `view/<area>/` package
6. Use `AppFormScreen` or gradient header shell
7. Collect state with `collectAsStateWithLifecycle()`
8. Trigger load in `LaunchedEffect(Unit) { viewModel.load() }`
9. Wire navigation side effects in `LaunchedEffect(uiState.isSuccess)` etc.

**7. iOS implementation steps:**
1. Create `FooViewModelWrapper.swift` co-located with the view
2. Annotate `@MainActor`, implement `FlowWatcher` pattern, close in `deinit`
3. Create `FooView.swift` using `AppScreen` or `AppGradientHeaderView`
4. Use `@StateObject private var viewModel = FooViewModelWrapper()`
5. Use `.task { viewModel.viewModel.load() }` for async loading
6. Wire navigation in the parent `NavigationStack` with `.navigationDestination(for:)`
7. Handle error with `.alert` or pass to `AppScreen(error:onErrorDismissed:)`

**8. Localization:**
- Add `[[<Screen>]]` section to `strings/twine.txt` with all 4 languages
- Run `make strings`
- Reference on Android: `stringResource(R.string.<screen>_<key>)`
- Reference on iOS: `NSLocalizedString("<screen>_<key>", value: "...", comment: "")`

---

## PHASE 9 — CLAUDE SKILLS & RULES RECOMMENDATIONS

### Rules That Should Exist But Don't

**1. `mock-strategy-rules.md`** (NEW)
```markdown
# Mock / Fake Strategy Rules

## Rule MOCK-1: Fakes Over Mocks in All Shared Tests
All tests in `shared/src/commonTest/` must use hand-written fake implementations of interfaces.
MockK is JVM-only and cannot be used in commonTest.

## Rule MOCK-2: Fakes in androidTest May Use MockK
JVM-only tests in `androidApp/src/test/` may use `mockk<T>()` for Android-specific classes.

## Rule MOCK-3: Fake Naming Convention
Fakes are named `Fake<InterfaceName>` and defined inline in the test file unless shared across 3+ test files, in which case they move to `shared/src/commonTest/kotlin/lu/esklepios/app/fakes/`.

## Rule MOCK-4: Fakes Must Be Complete
A fake must implement every method in the interface. Non-exercised methods return sensible no-op defaults (e.g., `Result.success(Unit)`, `Result.failure(NotImplementedError())`).
```

**2. `navigation-rules.md`** (NEW)
```markdown
# Navigation Rules

## Rule NAV-1: NavDestination Is the Single Source of Route Strings
Route strings must never be inlined in screen files. All routes live in `NavDestination.kt` (Android) or as typed enums/structs (iOS). No `navController.navigate("hardcoded_string")`.

## Rule NAV-2: Parameterized Routes Use createRoute() Helpers
Routes with path params always have a companion `createRoute()` function.
`navController.navigate(NavDestination.PractitionerDetail.createRoute(id))`

## Rule NAV-3: Back-Stack Clearing Navigation Uses popUpTo
Any navigation that should not allow the user to return uses:
`navController.navigate(dest) { popUpTo(0) { inclusive = true } }`
Examples: login success, logout.

## Rule NAV-4: iOS Navigation Is State-Driven, Not Imperative
iOS navigation state lives in `@State` vars in the parent view (e.g., `@State private var path = NavigationPath()`). Views never call navigation APIs directly.

## Rule NAV-5: Bottom Nav Items Use saveState / restoreState
Tab switches preserve state per-tab: `launchSingleTop = true; restoreState = true; popUpTo(Home) { saveState = true }`.
```

**3. `component-ownership-rules.md`** (NEW)
```markdown
# Component Ownership Rules

## Rule CO-1: Components Live in core/ui/components/ Only
A reusable composable may only be defined in `androidApp/.../core/ui/components/` (Android) or `iosApp/.../Core/UI/Components/` (iOS). No reusable component definitions in `view/` or `Features/` directories.

## Rule CO-2: Three-Strike Rule for New Components
A raw primitive pattern (e.g., custom-styled Text, inline avatar) that appears in 3+ places in screen files must be extracted to a named component before the 3rd call site is merged.

## Rule CO-3: Every Component Has a Cross-Platform Counterpart
When a new component is created for Android, an equivalent must exist or be created for iOS within the same PR. Documented exceptions: `AppDrawer` (Android-only, iOS uses `NavigationStack`), `VSpace`/`HSpace` (iOS uses `VStack(spacing:)`).

## Rule CO-4: Component Files Are One Component Per File
Each file in `core/ui/components/` contains exactly one primary public composable (plus closely-related private sub-composables). Exception: `Buttons.kt` groups the four button variants.
```

### Existing Rules That Should Be Improved

**`architecture-rules.md` improvements:**

Add:
```markdown
## Rule A-17: ViewModelWrapper Is Not a ViewModel
iOS `*ViewModelWrapper` classes are adapters — they translate `StateFlow` to `@Published`. They must not contain business logic. All logic lives in the shared ViewModel.

## Rule A-18: AppNavGraph Is the Single Navigation Authority (Android)
No screen file may hold a `NavController` reference beyond what is passed as a parameter. Screens navigate by calling `navController.navigate()` with `NavDestination.*` routes — never by hardcoding strings.

## Rule A-19: One Mapper File per Data Layer Boundary
`Mappers.kt` is the only file that contains `DTO.toDomain()` and `Domain.toDto()` extension functions. Repository implementations must not contain inline mapping lambdas.
```

**`create-screen.md` improvements:**

Current skill is comprehensive. Add:
- Explicit note that `KoinHelper.swift` must be updated with a new getter when a new ViewModel is introduced
- `IOSViewModelFactory` in `IosKoinInit.kt` must have the new `by inject()` property added
- Template for the `SharedTransition` wrapper (Android has `LocalNavAnimatedVisibilityScope` pattern for shared-element transitions)

**`create-viewmodel.md` improvements:**

Add the full iOS registration path:
1. Add `factoryOf(::FooViewModel)` to `SharedModule.kt`
2. Add `val fooViewModel: FooViewModel by inject()` to `IOSViewModelFactory` in `IosKoinInit.kt`
3. Add `func fooViewModel() -> FooViewModel { factory!.fooViewModel }` to `KoinHelper.swift`

### New Skills That Should Exist

**`create-component.md`** (NEW skill):
```markdown
# Skill: Create Component

Creates a paired Android Composable + iOS SwiftUI component following project conventions.

Usage: /create-component <Name> [description]

Steps:
1. Create `androidApp/.../core/ui/components/<Name>.kt` with:
   - `@Composable fun <Name>(params, modifier: Modifier = Modifier) { }`
   - All dimensions from Dimens.kt, all colors from Color.kt
   - `@Preview` annotation with at least 2 states
2. Create `iosApp/.../Core/UI/Components/<Name>.swift` with:
   - `struct <Name>: View { var body: some View { } }`
   - All dimensions from AppDimens/Spacing/Radius, all colors from AppColors extension
   - `#Preview` block
3. Add entry to create-screen.md component table
4. Refactor existing call sites in same PR
```

**`audit-screen.md`** (NEW skill for compliance checking):
Runs the grep enforcement commands from `ui-rules.md` against a specific screen file and reports violations.

---

## PHASE 10 — MASTER IMPLEMENTATION PLAN

### Phase 1: Project Setup (Days 1–3)

**Prerequisites**: Kotlin 2.0.21, Android Studio Hedgehog+, Xcode 15+, JDK 17, Ruby (for twine)

**Deliverables:**
1. Clone/fork repo structure (or scaffold via `kotlin-multiplatform-mobile` wizard)
2. Rename all package identifiers (Steps 1–3 of Phase 7)
3. Set up `dev.properties` and `prod.properties` with new API URLs
4. Verify `./gradlew :shared:generateBuildKonfig` succeeds
5. Verify `./gradlew :androidApp:assembleDebug` produces APK
6. Verify `./gradlew :shared:assembleXCFramework` produces `.xcframework`
7. Open Xcode project, resolve framework references, verify build compiles

**Effort**: 3 days
**Risks**: Xcode framework path resolution after rename; gradle version conflicts

### Phase 2: Design System (Days 4–6)

**Prerequisites**: Brand color palette, typography decision, icon set

**Deliverables:**
1. `Color.kt` — replace all hex values with brand palette
2. `AppColors.swift` — mirror changes
3. `Dimens.kt` / `AppDimens.swift` — review tokens, add any domain-specific ones
4. `Typography.kt` — adapt if using a custom font (add `.ttf` to `res/font/`, update `FontFamily`)
5. `AppFonts.swift` — add custom font if needed (add to Xcode project, update `Font` extension)
6. `Gradients.kt` / `AppColors.swift` `AppGradient` — update gradient colors
7. Replace app icons and splash assets in both platforms
8. Run `make strings` → verify no localization errors

**Effort**: 3 days
**Risks**: Custom font embedding (Android: `res/font/` + `FontFamily`; iOS: Xcode target membership + `Info.plist` font list)

### Phase 3: Navigation (Days 7–9)

**Prerequisites**: Screen list finalized, user flow diagram approved

**Deliverables:**
1. Update `NavDestination.kt` with new screen routes
2. Rewrite `AppNavGraph.kt` with correct `composable()` entries and bottom nav items
3. Update `RootView.swift` if auth flow differs
4. Update `AppTabView.swift` with new tab definitions
5. Stub all screen files (empty `@Composable fun Foo...()` and `struct FooView: View {}`) so navigation compiles
6. Verify navigation between stubs works end-to-end on both platforms

**Effort**: 3 days
**Risks**: iOS navigation is implicit via `NavigationStack` state; missing `navigationDestination(for:)` registrations cause silent navigation failures

### Phase 4: Authentication (Days 10–16)

**Prerequisites**: API auth endpoint contract (request/response shapes)

**Deliverables:**
1. Update `LoginRequest`, `RegisterRequest`, `LoginResponse` DTOs
2. Update `KtorApiService` auth endpoint paths
3. Update `AuthRepositoryImpl` if token field names differ
4. Adapt `AuthUiState` fields for your registration form
5. Implement `LandingScreen`/`LandingView` with new branding
6. Implement `LoginScreen`/`LoginView`
7. Implement `RegisterScreen`/`RegisterView`
8. Implement `ForgotPasswordScreen`/`ForgotPasswordView`
9. Implement `SplashScreen`/`SplashView`
10. End-to-end auth flow test on both platforms

**Effort**: 7 days
**Risks**: Multi-step register form complexity; social sign-in (Google/Apple) requires additional SDK setup not included in base project

### Phase 5: Home & Search (Days 17–24)

**Prerequisites**: Practitioner/entity API contract, filter requirements

**Deliverables:**
1. Rename/replace `Practitioner` domain model with your entity
2. Rewrite `PractitionerDto` → new DTO
3. Update `Mappers.kt`
4. Update `PractitionerRepository` interface and `PractitionerRepositoryImpl`
5. Adapt `HomeViewModel` (rename `SearchPractitionersUseCase` → your entity)
6. Adapt `DateFilter` / filter options for your domain
7. Implement `HomeScreen`/`HomeView`
8. Implement `PractitionerListScreen`/`PractitionerListView`
9. Implement `PractitionerDetailScreen`/`PractitionerDetailView`
10. Adapt `PractitionerCard` composable for your entity shape

**Effort**: 8 days
**Risks**: `PractitionerCard` with its 5-day slot calendar is the most complex component; if your domain has a different booking UI, this will require significant redesign

### Phase 6: Appointments/Core Transactions (Days 25–32)

**Prerequisites**: Booking API contract, appointment status values

**Deliverables:**
1. Replace `Appointment` domain model
2. Rewrite `AppointmentDto`, `CreateAppointmentRequest`, `AppointmentStatus`
3. Update `AppointmentRepository` and `AppointmentRepositoryImpl`
4. Update `AppointmentStatusOptions` shared util
5. Implement `BookingScreen`/`BookAppointmentView`
6. Implement `AppointmentSuccessScreen`/`AppointmentSuccessView`
7. Implement `MyAppointmentsScreen`/`MyAppointmentsView`
8. Update SQLDelight schema for new appointment fields
9. Run `./gradlew :shared:generateSqlDelightInterface` after schema changes

**Effort**: 8 days
**Risks**: SQLDelight schema migration is manual (no automatic migrations in this setup); boolean columns stored as `Long` must be handled explicitly

### Phase 7: Profile (Days 33–38)

**Prerequisites**: User data model finalized, identity verification requirements

**Deliverables:**
1. Adapt `User` domain model (retain base fields, add domain-specific ones)
2. Update `UserDto`, `UpdateProfileRequest`
3. Implement `ProfileScreen`/`ProfileView`
4. Implement `EditProfileScreen`/`EditProfileView` (adapt form fields)
5. Implement `ChangeEmailScreen`/`ChangeEmailView`
6. Implement `ChangePasswordScreen`/`ChangePasswordView`
7. Validate `CnsFormatter` → replace with your country-specific ID masking if needed
8. Update `DialCodes` with countries relevant to your market
9. Update `supportedLanguages` for your supported locales

**Effort**: 6 days
**Risks**: Country-specific ID validation (CNS is Luxembourg-specific); date-of-birth picker platform differences

### Phase 8: Testing (Days 39–45)

**Prerequisites**: All screens implemented, API stable enough for fake-response fixtures

**Deliverables:**
1. `shared/src/commonTest/` — write ViewModel tests for all 11 (or your equivalent) ViewModels
2. `shared/src/commonTest/` — write Repository tests for all 4 repositories
3. `shared/src/commonTest/` — `SerializationSmokeTest` for all new DTOs
4. `shared/src/commonTest/` — `ValidationUtilTest` updates for new validation rules
5. iOS: update 5 XCTest files for renamed classes
6. Run `./gradlew :shared:commonTest` to full green
7. Run `xcodebuild test ...` to full green
8. `./gradlew :androidApp:lint` — address all warnings

**Effort**: 7 days
**Risks**: `SerializationSmokeTest` failures indicate ProGuard stripping serializer factories — add explicit keep rules in `proguard-rules.pro`

### Phase 9: Release (Days 46–52)

**Prerequisites**: App store accounts, signing certificates, final QA sign-off

**Deliverables:**
1. Configure GitHub Actions (or Bitrise) pipeline with the 6 CI stages from CLAUDE.md
2. Android: generate keystore, set `KEYSTORE_PASSWORD` in CI secrets, update `release` signingConfig
3. iOS: configure provisioning profiles, `APPLE_TEAM_ID` in CI secrets, update archive export options
4. Set `prod.properties` with production `BASE_API_URL` and `ENABLE_LOGGING=false`
5. `./gradlew :androidApp:assembleRelease` → upload to Play Console internal track
6. `xcodebuild archive ...` → upload to App Store Connect TestFlight
7. Update `strings/twine.txt` store listing strings → submit for review

**Effort**: 7 days
**Risks**: iOS notarization / entitlements; Play Store 64-bit requirement (already met with Kotlin/JVM); ProGuard R8 stripping — validate with `minifyEnabled = true` build early

---

### Summary: Total Effort Estimate

| Phase | Effort | Key Risk |
|---|---|---|
| 1 — Project Setup | 3 days | Xcode framework resolution |
| 2 — Design System | 3 days | Custom font embedding |
| 3 — Navigation | 3 days | iOS NavigationStack wiring |
| 4 — Authentication | 7 days | Multi-step form + social SSO |
| 5 — Home & Search | 8 days | PractitionerCard complexity |
| 6 — Appointments | 8 days | SQLDelight schema changes |
| 7 — Profile | 6 days | Country-specific validation |
| 8 — Testing | 7 days | Serialization ProGuard |
| 9 — Release | 7 days | Signing + store review |
| **Total** | **52 days** | |

One engineer can complete this in approximately 10–11 weeks working solo. With a two-person team (one Android-focused, one iOS-focused) sharing the shared KMM work, Phases 4–7 can run in parallel, compressing to approximately 6–7 weeks.

---

**Key files for a clone builder to read first, in order:**
1. `/shared/src/commonMain/kotlin/lu/esklepios/app/di/SharedModule.kt` — the dependency wiring map
2. `/androidApp/src/main/kotlin/lu/esklepios/app/core/navigation/NavDestination.kt` — the screen inventory
3. `/androidApp/src/main/kotlin/lu/esklepios/app/core/ui/theme/Dimens.kt` — the spacing contract
4. `/shared/src/commonMain/kotlin/lu/esklepios/app/data/network/ApiService.kt` — the API surface
5. `/shared/src/commonMain/kotlin/lu/esklepios/app/presentation/viewmodel/HomeViewModel.kt` — the canonical ViewModel pattern
6. `/iosApp/eSklepios/Features/Auth/Login/AuthViewModelWrapper.swift` — the canonical iOS wrapper pattern
7. `/shared/src/commonTest/kotlin/lu/esklepios/app/HomeViewModelTest.kt` — the canonical test pattern
