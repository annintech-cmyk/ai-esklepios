# eSklepios — Project Intelligence

## Project Overview

eSklepios is a Kotlin Multiplatform Mobile (KMM) application for Luxembourg's healthcare system, enabling patients to search for practitioners, book appointments, manage their health profile, and view appointment history. The app targets both Android (API 26+) and iOS (17+).

**Business Domain:** Healthcare appointment booking for the Luxembourg market.
**Supported Languages:** English (en), French (fr), German (de), Luxembourgish (lb).
**Package / Bundle ID:** `lu.esklepios.app`
**API Base URL (prod):** `https://api.esklepios.lu`
**API Base URL (dev):** configured via `dev.properties`

---

## Text-Based Architecture Diagram

```
┌─────────────────────────────────────────────────────────────────┐
│                          eSklepios                              │
│                                                                 │
│  ┌─────────────────────────┐   ┌────────────────────────────┐  │
│  │       androidApp        │   │          iosApp            │  │
│  │  (Jetpack Compose UI)   │   │       (SwiftUI UI)         │  │
│  │                         │   │                            │  │
│  │  Screens (15)           │   │  Views (15)                │  │
│  │  NavGraph + BottomNav   │   │  AppTabView + NavStack     │  │
│  │  Koin Android Module    │   │  ViewModelWrappers         │  │
│  │  SecureStorage          │   │  KeychainStorage           │  │
│  │  ESklepiosApp           │   │  eSklepiosApp              │  │
│  └──────────┬──────────────┘   └──────────┬─────────────────┘  │
│             │                             │                     │
│             └──────────────┬──────────────┘                     │
│                            │                                    │
│  ┌─────────────────────────▼──────────────────────────────────┐ │
│  │                     shared (KMM)                           │ │
│  │                                                            │ │
│  │  presentation/viewmodel/   ← 11 ViewModels                │ │
│  │    SplashViewModel                                         │ │
│  │    AuthViewModel                                           │ │
│  │    HomeViewModel                                           │ │
│  │    PractitionerDetailViewModel                             │ │
│  │    BookAppointmentViewModel                                │ │
│  │    AppointmentSuccessViewModel                             │ │
│  │    MyAppointmentsViewModel                                 │ │
│  │    ProfileViewModel                                        │ │
│  │    EditProfileViewModel                                    │ │
│  │    ChangeEmailViewModel                                    │ │
│  │    ChangePasswordViewModel                                 │ │
│  │                                                            │ │
│  │  domain/                                                   │ │
│  │    model/    ← User, Appointment, Practitioner, Slot       │ │
│  │    repository/ (interfaces)                                │ │
│  │    usecase/                                                │ │
│  │                                                            │ │
│  │  data/                                                     │ │
│  │    network/  ← Ktor ApiService + TokenStorage interface    │ │
│  │    repository/ (implementations)                           │ │
│  │    db/       ← SQLDelight schema                           │ │
│  │                                                            │ │
│  │  di/SharedModule.kt  ← Koin wiring                        │ │
│  └────────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────────┘
```

---

## Full Dependency Graph with Versions

### Core Tools
| Tool | Version |
|------|---------|
| Kotlin | 2.0.21 |
| Android Gradle Plugin | 8.x |
| Xcode | 15+ |

### Shared (commonMain)
| Library | Version | Purpose |
|---------|---------|---------|
| ktor-client-core | 3.0.3 | HTTP client |
| ktor-client-content-negotiation | 3.0.3 | JSON negotiation |
| ktor-client-auth | 3.0.3 | Token refresh |
| ktor-client-logging | 3.0.3 | Network logging |
| ktor-serialization-kotlinx-json | 3.0.3 | JSON serialization |
| kotlinx-coroutines-core | 1.9.0 | Coroutines |
| kotlinx-serialization-json | 1.7.3 | JSON parsing |
| kotlinx-datetime | 0.6.1 | Date/time |
| koin-core | 4.0.0 | Dependency injection |
| sqldelight-runtime | 2.0.2 | Local database |
| sqldelight-coroutines-extensions | 2.0.2 | Flow queries |
| multiplatform-settings | 1.2.0 | Key-value storage |
| androidx.lifecycle:lifecycle-viewmodel | 2.8.7 | KMM ViewModel base |

### Android (androidMain / androidApp)
| Library | Version | Purpose |
|---------|---------|---------|
| androidx.compose BOM | 2024.12.01 | Compose UI |
| koin-android | 4.0.0 | Koin Android |
| koin-androidx-compose | 4.0.0 | `koinViewModel()` |
| ktor-client-okhttp | 3.0.3 | OkHttp engine |
| sqldelight-android-driver | 2.0.2 | SQLite driver |
| androidx.security:security-crypto | 1.1.0-alpha06 | EncryptedSharedPreferences |
| coil-compose | 2.7.0 | Image loading |

### iOS (iosMain / iosApp)
| Library | Version | Purpose |
|---------|---------|---------|
| ktor-client-darwin | 3.0.3 | Darwin HTTP engine |
| sqldelight-native-driver | 2.0.2 | SQLite driver |
| Swift | 5.9+ | Language |
| SwiftUI | - | UI framework |
| MapKit | - | Map display |
| Security.framework | - | Keychain access |

### Test Dependencies
| Library | Version | Purpose |
|---------|---------|---------|
| kotlinx-coroutines-test | 1.9.0 | TestScope |
| turbine | 1.2.0 | Flow testing |
| mockk | 1.13.12 | Android mocking |
| kotlin.test | 2.0.21 | Common test assertions |

---

## Build Commands

### Gradle (Android + Shared)
```bash
# Build entire project
./gradlew build

# Build Android app (debug)
./gradlew :androidApp:assembleDebug

# Build Android app (release)
./gradlew :androidApp:assembleRelease

# Build shared framework (for iOS)
./gradlew :shared:assembleXCFramework

# Run Android unit tests
./gradlew :androidApp:test
./gradlew :shared:testDebugUnitTest

# Run shared common tests
./gradlew :shared:commonTest

# Clean everything
./gradlew clean

# Generate localization strings
make strings

# Lint
./gradlew :androidApp:lint
./gradlew :shared:detekt

# Build KonfIG (for environment variables)
./gradlew :shared:generateBuildKonfig
```

### Xcode (iOS)
```bash
# Build via xcodebuild (simulator)
xcodebuild -project iosApp/eSklepios.xcodeproj \
  -scheme eSklepios \
  -destination 'platform=iOS Simulator,name=iPhone 16' \
  build

# Run iOS tests
xcodebuild test \
  -project iosApp/eSklepios.xcodeproj \
  -scheme eSklepios \
  -destination 'platform=iOS Simulator,name=iPhone 16'

# Archive for distribution
xcodebuild archive \
  -project iosApp/eSklepios.xcodeproj \
  -scheme eSklepios \
  -archivePath build/eSklepios.xcarchive
```

### Twine (Localization)
```bash
# Generate Android strings.xml from twine.txt
make strings

# Install twine if not present
gem install twine
```

---

## Coding Standards

### Kotlin (Shared + Android)

**Naming**
- Classes: `PascalCase` — `HomeViewModel`, `AppointmentRepository`
- Functions / properties: `camelCase` — `loadAppointments()`, `isLoading`
- Constants: `SCREAMING_SNAKE_CASE` in companion objects
- Packages: `lu.esklepios.app.<layer>.<sublayer>`

**State Management**
- All ViewModels extend `androidx.lifecycle.ViewModel` (works via androidTarget source set)
- Use `MutableStateFlow<UiState>` with a backing private field named `_uiState`
- Expose as `val uiState: StateFlow<UiState> = _uiState.asStateFlow()`
- Mutate via `_uiState.update { it.copy(...) }`
- Never expose `MutableStateFlow` directly

**ViewModel Pattern**
```kotlin
class FooViewModel(
    private val fooRepository: FooRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FooUiState())
    val uiState: StateFlow<FooUiState> = _uiState.asStateFlow()

    fun doSomething() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            fooRepository.doSomething()
                .onSuccess { result ->
                    _uiState.update { it.copy(isLoading = false, data = result) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message) }
                }
        }
    }
}
```

**Coroutines**
- Launch from `viewModelScope.launch { }` inside ViewModels
- Use `flow { }` + `.catch { }` for repository streams
- Prefer `StateFlow` over `LiveData` across the board

**Compose (Android)**
- All screens are `@Composable fun FooScreen(viewModel: FooViewModel = koinViewModel())`
- Collect state with `val uiState by viewModel.uiState.collectAsStateWithLifecycle()`
- Use `LaunchedEffect(Unit)` for one-shot side effects on composition
- Use `rememberCoroutineScope()` for user-triggered async actions
- Extract large composables into private `@Composable` functions in the same file

**Error Handling**
- Use Kotlin `Result<T>` in repository return types where feasible
- UI state always has `error: String?` — set to null on new load attempts

### Swift (iOS)

**Naming**
- Types / protocols: `PascalCase` — `HomeView`, `AppointmentCard`
- Functions / properties: `camelCase` — `loadAppointments()`, `isLoading`
- Private vars: no underscore prefix (Swift convention)

**ViewModelWrapper Pattern**
```swift
class FooViewModelWrapper: ObservableObject {
    let viewModel: FooViewModel
    @Published var uiState: FooUiState

    init(viewModel: FooViewModel = KoinHelper.get()) {
        self.viewModel = viewModel
        uiState = viewModel.uiState.value as! FooUiState
        viewModel.uiState.watch { [weak self] state in
            guard let state else { return }
            DispatchQueue.main.async {
                self?.uiState = state
            }
        }
    }
}
```

**SwiftUI Views**
- Views receive a `@StateObject var viewModel: FooViewModelWrapper`
- Use `.task { }` modifier for async on-appear loading
- Use `NavigationStack` with `.navigationDestination(for:)` for type-safe navigation
- Pull-to-refresh via `.refreshable { }`
- Error / empty / loading states handled via `if/else` blocks based on `uiState`

**File Organization**
- `/Views/` — SwiftUI screen views
- `/Components/` — reusable UI components
- `/ViewModels/` — wrapper classes + KoinHelper
- `/Navigation/` — AppTabView, RootView
- `/Theme/` — colors, fonts, gradients, spacing

---

## How to Add a New Screen

### Step 1: Define the domain (if new data needed)

1. Add model to `shared/src/commonMain/.../domain/model/`
2. Add repository interface to `shared/src/commonMain/.../domain/repository/`
3. Add use cases to `shared/src/commonMain/.../domain/usecase/`
4. Add repository implementation to `shared/src/commonMain/.../data/repository/`
5. Wire in `shared/src/commonMain/.../di/SharedModule.kt`

### Step 2: Add the ViewModel (Shared)

Create `shared/src/commonMain/.../presentation/viewmodel/FooViewModel.kt`:
```kotlin
class FooViewModel(private val repo: FooRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(FooUiState())
    val uiState: StateFlow<FooUiState> = _uiState.asStateFlow()
    // ... actions
}
data class FooUiState(val isLoading: Boolean = false, val error: String? = null)
```

Register in `SharedModule.kt`:
```kotlin
factoryOf(::FooViewModel)
```

### Step 3: Android — Add navigation destination

In `NavDestination.kt`, add:
```kotlin
object Foo : NavDestination("foo")
```

In `AppNavGraph.kt`, add inside `NavHost`:
```kotlin
composable(NavDestination.Foo.route) { FooScreen() }
```

Add to bottom nav bar if top-level.

### Step 4: Android — Create the screen

Create `androidApp/src/.../ui/screens/FooScreen.kt`:
```kotlin
@Composable
fun FooScreen(viewModel: FooViewModel = koinViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    // UI
}
```

### Step 5: iOS — Add ViewModelWrapper

Create `iosApp/eSklepios/ViewModels/FooViewModelWrapper.swift`.

### Step 6: iOS — Create the SwiftUI view

Create `iosApp/eSklepios/Views/FooView.swift`.

### Step 7: iOS — Wire navigation

In `AppTabView.swift` or the relevant `NavigationStack`, add a `navigationDestination` case.

### Step 8: Localization

Add keys to `strings/twine.txt` under a new `[foo.*]` section, then run `make strings`.

### Step 9: Tests

- Android: add `androidApp/src/test/.../FooViewModelTest.kt`
- Shared: add `shared/src/commonTest/.../FooRepositoryTest.kt`
- iOS: add `iosApp/eSklepiosTests/FooViewModelTests.swift`

---

## How to Add a New API Endpoint

1. **Define request/response models** in `shared/src/commonMain/.../data/network/` (annotated with `@Serializable`).
2. **Add the function** to the `ApiService` interface in `shared/src/commonMain/.../data/network/ApiService.kt`.
3. **Implement** in `ApiServiceImpl` using `client.get/post/put/delete { }`.
4. **Create or update** the repository to call the new service function.
5. **Create a use case** if the logic is complex or reused.
6. **Wire** new use cases / repositories in `SharedModule.kt`.
7. **Update tests** — add a fake ApiService method and write a repository test.

**Authentication:** The Ktor client in `ApiServiceImpl` is configured with `BearerTokenPlugin`. The `TokenStorage` interface (methods: `setToken`, `setRefreshToken`, `clear`) is implemented by `SecureStorage` (Android) and `KeychainStorage` (iOS).

---

## How to Add a New Repository

1. Define the interface in `shared/src/commonMain/.../domain/repository/FooRepository.kt`.
2. Implement in `shared/src/commonMain/.../data/repository/FooRepositoryImpl.kt`.
3. If it needs local caching, use the SQLDelight database via `ESklepiosDatabase`.
4. Register in `SharedModule.kt`:
   ```kotlin
   single<FooRepository> { FooRepositoryImpl(get(), get()) }
   ```
5. Write a `FooRepositoryTest` in `shared/src/commonTest/` using fake interface implementations (not MockK — MockK is JVM-only).

---

## Localization Workflow

The project uses **Twine** for multi-language string management.

### Source file
`strings/twine.txt` — master file with all string keys in en/fr/de/lb.

### Format example
```
[general.app_name]
en = eSklepios
fr = eSklepios
de = eSklepios
lb = eSklepios
```

### Generate Android strings
```bash
make strings
# Outputs to androidApp/src/main/res/values/strings.xml (and values-fr/, values-de/, values-lb/)
```

### iOS strings
Currently referenced manually in SwiftUI views with string literals. To integrate Twine output for iOS, add `--format apple` to the Makefile target and import the resulting `.strings` files into Xcode.

### Adding a new string
1. Add the key under the appropriate section in `strings/twine.txt` with all 4 language values.
2. Run `make strings` to regenerate Android resource files.
3. Reference on Android: `stringResource(R.string.key_name)`
4. Reference on iOS: `NSLocalizedString("key_name", comment: "")` or SwiftUI `Text("key_name")`

---

## Environment Configuration

The project uses **BuildKonfig** to inject environment variables at compile time.

### Property files
- `dev.properties` — local development values (not committed, in `.gitignore`)
- `prod.properties` — production values

### Accessing config in Kotlin
```kotlin
import lu.esklepios.app.BuildKonfig
val apiUrl = BuildKonfig.BASE_URL
```

### Adding a new config value
1. Add the key to both `dev.properties` and `prod.properties`.
2. In `shared/build.gradle.kts`, add inside `buildkonfig { defaultConfigs { } }`:
   ```kotlin
   stringField("MY_KEY", devProperties.getProperty("MY_KEY", ""))
   ```
3. Run `./gradlew :shared:generateBuildKonfig`.

---

## CI/CD Notes

- No CI configuration is committed yet (Phase 0 scaffolding).
- Recommended CI: **GitHub Actions** or **Bitrise**.
- Suggested pipeline stages:
  1. `./gradlew :shared:commonTest` — shared tests
  2. `./gradlew :androidApp:test` — Android unit tests
  3. `./gradlew :androidApp:lint` — Android lint
  4. `./gradlew :androidApp:assembleRelease` — Android release build
  5. `xcodebuild test ...` — iOS tests
  6. `xcodebuild archive ...` — iOS archive
- Secrets (`KEYSTORE_PASSWORD`, `APPLE_TEAM_ID`, etc.) should be stored in CI secrets vault.
- The `strings/twine.txt` must be committed; generated `strings.xml` files may be generated in CI.

---

## Branch Naming Conventions

```
feature/<short-description>      # New features
fix/<short-description>          # Bug fixes
hotfix/<short-description>       # Urgent production fixes
refactor/<short-description>     # Refactoring without behavior change
chore/<short-description>        # Dependency updates, config, tooling
docs/<short-description>         # Documentation only
test/<short-description>         # Test additions or fixes
```

Examples:
- `feature/practitioner-search-filters`
- `fix/appointment-cancellation-crash`
- `chore/update-ktor-3.0.3`

---

## Commit Message Standards

Follow **Conventional Commits**:

```
<type>(<scope>): <short description>

[optional body]

[optional footer]
```

**Types:** `feat`, `fix`, `refactor`, `test`, `docs`, `chore`, `style`, `perf`, `ci`

**Scopes:** `shared`, `android`, `ios`, `auth`, `home`, `appointments`, `profile`, `navigation`, `di`, `network`, `db`, `strings`, `ci`

**Examples:**
```
feat(shared): add HomeViewModel with search and filter support
fix(android): correct SecureStorage method names to match TokenStorage interface
refactor(ios): extract PractitionerCard into reusable component
test(shared): add PractitionerRepositoryTest with fake API service
chore(deps): update Ktor to 3.0.3
docs(strings): add Luxembourgish translations for all screen keys
```

---

## Common Pitfalls and Solutions

### 1. AppointmentStatus enum collision
**Problem:** `StatusBadge.kt` (Android component) defines its own `AppointmentStatus` enum with different values (CONFIRMED, RESERVED, CANCELLED) from the domain model (PENDING, CONFIRMED, CANCELLED, COMPLETED, NO_SHOW).
**Solution:** When using both in the same file, import the domain model explicitly and use a local composable to bridge. See `MyAppointmentsScreen.kt` for the pattern.

### 2. TokenStorage method names
**Problem:** The `TokenStorage` interface uses `setToken()`, `setRefreshToken()`, and `clear()` — NOT `saveToken()`, `saveRefreshToken()`, or `clearAll()`.
**Solution:** Always read the actual interface before implementing a platform-specific storage class.

### 3. MockK not available in commonTest
**Problem:** MockK is JVM-specific and cannot be used in `shared/src/commonTest/`.
**Solution:** Write fake interface implementations inline in the test file. See `PractitionerRepositoryTest.kt` for the pattern.

### 4. ProfileType enum
**Problem:** `ProfileType` only has `PATIENT` and `PRACTITIONER` — not FAMILY_MEMBER or CAREGIVER.
**Solution:** Always read existing domain model files before generating ViewModels or screens that reference them.

### 5. iOS placeholder() extension duplication
**Problem:** The `placeholder()` ViewModifier extension may be defined in multiple SwiftUI view files, causing a compile-time error.
**Solution:** Move the `placeholder()` extension to a shared file, e.g. `Components/ViewExtensions.swift`.

### 6. KoinHelper.startKoin parameters
**Problem:** `KoinHelper.startKoin` takes `(baseUrl: String, enableLogging: Bool)` — not a parameterless call.
**Solution:** Always pass both parameters; see `eSklepiosApp.swift` for the correct invocation.

### 7. SocialSignInButton API
**Problem:** There is no generic `SocialSignInButton` composable. The components are `GoogleSignInButton(onClick:modifier:)` and `AppleSignInButton(onClick:modifier:)`.
**Solution:** Use the two separate composables directly in `LoginScreen.kt`.

### 8. GradientHeader iOS signature
**Problem:** `GradientHeader` on iOS requires `init(minHeight:onBack:trailingAction:trailingIcon:content:)`.
**Solution:** Always provide all named parameters; `onBack` and `trailingAction` can be `nil`.

### 9. iOS Map API (iOS 17+)
**Problem:** `Map(coordinateRegion:annotationItems:)` is deprecated in iOS 17.
**Solution:** The project targets iOS 17+ — consider migrating to the new `Map { }` closure-based API when refactoring `MapPreviewCard.swift`.

### 10. SQLDelight database in iOS
**Problem:** The `ESklepiosDatabase` driver must be created before Koin starts.
**Solution:** The iOS Koin module creates a `NativeSqliteDriver` inside the DI module itself; do not try to create it outside Koin.

---

## Project Domain Models Quick Reference

### User
```kotlin
data class User(
    val id: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val phone: String?,
    val profileType: ProfileType,
    val avatarUrl: String?
)
enum class ProfileType { PATIENT, PRACTITIONER }
```

### Appointment
```kotlin
data class Appointment(
    val id: String,
    val practitionerId: String,
    val practitionerName: String,
    val clinicName: String,
    val dateTime: String,
    val status: AppointmentStatus,
    val notes: String?
)
enum class AppointmentStatus { PENDING, CONFIRMED, CANCELLED, COMPLETED, NO_SHOW }
```

### Practitioner
Refer to `shared/src/commonMain/.../domain/model/` for full field list.

---

## File Structure Reference

```
esklepios/
├── androidApp/
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── kotlin/lu/esklepios/app/
│       │   ├── ESklepiosApp.kt
│       │   ├── MainActivity.kt
│       │   ├── di/AndroidModule.kt
│       │   ├── storage/SecureStorage.kt
│       │   └── ui/
│       │       ├── components/         ← shared Android UI components
│       │       ├── navigation/
│       │       │   ├── NavDestination.kt
│       │       │   └── AppNavGraph.kt
│       │       ├── screens/            ← 15 screen composables
│       │       └── theme/              ← Color, Theme, Typography, Dimens
│       └── res/
│           └── values/strings.xml
├── iosApp/
│   └── eSklepios/
│       ├── eSklepiosApp.swift
│       ├── Components/                 ← 11 reusable SwiftUI components
│       ├── Navigation/
│       │   ├── RootView.swift
│       │   └── AppTabView.swift
│       ├── Theme/                      ← Colors, Gradients, Dimens
│       ├── ViewModels/                 ← Wrappers + KoinHelper
│       └── Views/                      ← 15 SwiftUI screens
├── shared/
│   └── src/
│       ├── commonMain/kotlin/lu/esklepios/app/
│       │   ├── data/
│       │   │   ├── db/                 ← SQLDelight
│       │   │   ├── network/            ← ApiService, TokenStorage
│       │   │   └── repository/
│       │   ├── di/SharedModule.kt
│       │   ├── domain/
│       │   │   ├── model/
│       │   │   ├── repository/
│       │   │   └── usecase/
│       │   └── presentation/viewmodel/ ← 11 ViewModels
│       ├── androidMain/                ← Android-specific shared code
│       ├── iosMain/                    ← iOS-specific shared code
│       └── commonTest/                 ← Platform-agnostic tests
├── strings/twine.txt                   ← Master localization file
├── Makefile                            ← `make strings`
├── build.gradle.kts
├── settings.gradle.kts
├── dev.properties                      ← NOT committed
├── prod.properties
└── .claude/
    ├── CLAUDE.md                       ← This file
    ├── commands/
    ├── agents/
    ├── rules/
    ├── skills/
    ├── hooks/
    └── templates/
```
