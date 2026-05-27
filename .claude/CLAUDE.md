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

## Git Hooks — Automated Code Quality Gates

### Setup (One-Time)
After cloning, install Git hooks to enforce coding standards automatically:
```bash
bash scripts/install-hooks.sh
```

This installs two hooks:
- **Pre-Commit** (`scripts/pre-commit-review.sh`) — validates code quality before `git commit`
- **Pre-Push** (`scripts/pre-push.sh`) — runs full build + tests before `git push`

### What Gets Checked

**Pre-Commit (Warnings allow commit; Errors block it):**
- Sensitive data: no `dev.properties`, API keys, private keys
- Kotlin standards: no debug `println()`, `CancellationException` rethrows, no MockK in `commonTest`, no `java.time` in shared, layer separation, no `GlobalScope`, no hardcoded dimensions/colors
- Swift standards: no debug `print()`, no `DispatchQueue.main.async` in ViewModelWrapper, `FlowWatcher` required, no force unwraps, no inline `DateFormatter`, no hardcoded dimensions
- Localization: all 4 languages required in `twine.txt`
- Platform parity: Android/iOS screen pairs must exist

**Pre-Push (All checks must pass):**
1. Detekt (Kotlin static analysis)
2. KtLint (Kotlin formatting)
3. Android Lint
4. Shared KMM unit tests
5. Android unit tests
6. Android debug build
7. SwiftLint (iOS)
8. iOS simulator build

### To Skip Hooks (Emergency Only)
```bash
git commit --no-verify    # skip pre-commit
git push --no-verify      # skip pre-push
```

### Full Hook Documentation
See `.claude/rules/git-hooks-rules.md` for:
- Detailed check reference for both hooks
- How hooks enforce project rules
- Common failures and fixes
- Troubleshooting guide

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

**Design tokens (hard rule):** See `.claude/rules/ui-rules.md` **Rule UI-1a** — Never Introduce Hardcoded Dimensions. All layout values (padding, width, height, corner radius, font size, etc.) must use design tokens.

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

Create `androidApp/src/main/kotlin/lu/esklepios/app/view/<area>/<screen>/FooScreen.kt`:
```kotlin
@Composable
fun FooScreen(viewModel: FooViewModel = koinViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    // UI
}
```

### Step 5: iOS — Add ViewModelWrapper

Create `iosApp/eSklepios/Features/<Area>/<Screen>/FooViewModelWrapper.swift` (co-located with the View).

### Step 6: iOS — Create the SwiftUI view

Create `iosApp/eSklepios/Features/<Area>/<Screen>/FooView.swift`.

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
3. **Implement** in `KtorApiService` using `client.get/post/put/delete { }`.
4. **Create or update** the repository to call the new service function.
5. **Create a use case** if the logic is complex or reused.
6. **Wire** new use cases / repositories in `SharedModule.kt`.
7. **Update tests** — add a fake ApiService method and write a repository test.

**Authentication:** The Ktor client in `HttpClientFactory` is configured with the `Auth` plugin's `bearer { ... }` provider. The `TokenStorage` interface (methods: `setToken`, `setRefreshToken`, `getToken`, `getRefreshToken`, `clear`) is implemented by `SecureStorage` (Android) and `KeychainStorage` (iOS).

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

### 11. Auth session persistence — single source of truth
**Rule:** `TokenStorage` (EncryptedSharedPreferences on Android via `SecureStorage`, Keychain on iOS via `KeychainStorage`) is the **sole** source of truth for login state. Never add a second flag (SharedPreferences boolean, UserDefaults, etc.) alongside it.

**Android:** `SecureStorage` keys `auth_token` / `auth_refresh_token` in EncryptedSharedPreferences.
**iOS:** `KeychainStorage` with service `lu.esklepios.app`.
**Check:** `authRepository.isLoggedIn()` = `tokenStorage.getToken() != null`.
**Splash:** `SplashViewModel.checkAuth()` reads `authRepository.isLoggedIn()` at startup — routes to `Home` if true, `Landing` if false.
**Login:** `authRepository.login()` calls `tokenStorage.setToken()` + `setRefreshToken()`. `AuthViewModel.uiState.isLoggedIn` flips to `true` on success; `LoginScreen` navigates to `Home` in `LaunchedEffect`.
**Logout:** `authRepository.logout()` calls `tokenStorage.clear()`. `ProfileViewModel.uiState.isLoggedOut` flips to `true`; `ProfileScreen` navigates to `Landing` in `LaunchedEffect`.

**`SessionManager` is deleted.** Do not re-introduce any plain-SharedPreferences or UserDefaults login flag.

### 12. AvatarCircle fontSize parameter
**Problem:** `AvatarCircle(initials, size, fontSize=...)` takes an explicit font size parameter, but font size should scale proportionally with avatar size.
**Solution:** Remove the `fontSize` parameter from all AvatarCircle calls. The component now auto-calculates proportional text size: `fontSize = avatarSize * 0.33`. This keeps avatar typography consistent across all use cases. Always use named `size` parameter (e.g., `Dimens.avatarSizeLg`) to determine both dimensions.

### 13. Test import organization
**Problem:** Star imports (`import io.mockk.*`, `import kotlinx.coroutines.test.*`) hide dependencies and make code review harder; reviewers can't see which specific test utilities are used.
**Solution:** Use explicit imports in test files. Replace star imports with specific names:
- ❌ `import io.mockk.*` → ✅ `import io.mockk.coEvery`, `import io.mockk.mockk`, etc.
- ❌ `import kotlinx.coroutines.test.*` → ✅ `import kotlinx.coroutines.test.UnconfinedTestDispatcher`, etc.

Explicit imports make dependencies visible and help catch copy-paste errors across platforms.
This is especially important for Android test files which are reviewed frequently and may be ported to commonTest.

### 14. Git Hooks Enforce Coding Standards
**What:** Two automated Git hooks enforce project rules on every commit and push.
- **Pre-commit** (`scripts/pre-commit-review.sh`) — validates code quality, security, and architecture on staged changes. Warnings allow commit; errors block it.
- **Pre-push** (`scripts/pre-push.sh`) — runs full build + tests before push (detekt, ktlint, Android lint, unit tests, debug build, SwiftLint, iOS build).

**Setup:** One-time installation after cloning:
```bash
bash scripts/install-hooks.sh
```

**Reference:** See `.claude/rules/git-hooks-rules.md` for the full check reference, rules enforcement map, common failures & fixes, and troubleshooting guide.

**To skip (emergency only):** `git commit --no-verify` or `git push --no-verify`

### 15. KtLint Violations — Cannot Be Auto-Corrected
**Problem:** Two KtLint violation types **cannot be auto-corrected** by `./gradlew ktlintFormat`:
1. **Wildcard imports** (`standard:no-wildcard-imports`) — must expand manually to explicit imports
2. **Comment placement in arguments** (`standard:discouraged-comment-location`) — must move comments above arguments

**Solution:** See `.claude/rules/ktlint-rules.md` for comprehensive guidance:
- **Rule KL-1:** No Wildcard Imports — Always Explicit
- **Rule KL-2:** Comment Placement in Function Arguments
- **Rule KL-3:** Composable Function Naming (PascalCase) — false positive, do NOT fix
- **Rule KL-4:** Formatting Best Practices
- **Rule KL-5:** Import Organization in New Files

**Quick fixes:**
- Wildcard imports → expand to explicit imports
- Inline comments in args → move above the argument on a separate line
- Composable PascalCase warnings → ignore (it's correct per Compose conventions)

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

### Android — `lu.esklepios.app.view` package tree

```
androidApp/src/main/kotlin/lu/esklepios/app/
├── ESklepiosApp.kt
├── MainActivity.kt
├── core/
│   ├── navigation/
│   │   ├── NavDestination.kt
│   │   └── AppNavGraph.kt
│   ├── ui/
│   │   ├── components/         ← AppCard, AppToolbar, AvatarCircle, Buttons,
│   │   │                          FormField, InfoRow, PractitionerCard, …
│   │   └── theme/              ← Color.kt, Theme.kt, Typography.kt, Dimens.kt
├── di/AndroidModule.kt
├── debug/DummyPractitioners.kt
├── storage/SecureStorage.kt
└── view/
    ├── auth/
    │   ├── forgotpassword/ForgotPasswordScreen.kt
    │   ├── login/LoginScreen.kt
    │   └── register/RegisterScreen.kt
    ├── dashboard/
    │   ├── appointments/
    │   │   ├── MyAppointmentsScreen.kt
    │   │   └── booking/
    │   │       ├── AppointmentSuccessScreen.kt
    │   │       └── BookingScreen.kt
    │   ├── home/
    │   │   ├── HomeScreen.kt
    │   │   ├── HomePractitionerCardMapper.kt
    │   │   ├── practitioner_data/
    │   │   │   └── PractitionerDetailScreen.kt
    │   │   └── practitioners/
    │   │       └── PractitionerListScreen.kt
    │   └── profile/
    │       ├── ProfileScreen.kt
    │       └── profile_edit/
    │           ├── ChangeEmailScreen.kt
    │           ├── ChangePasswordScreen.kt
    │           └── EditProfileScreen.kt
    ├── landing/LandingScreen.kt
    └── splash/SplashScreen.kt
```

### iOS — `Features/` folder tree (mirrors Android packages)

```
iosApp/eSklepios/
├── eSklepiosApp.swift
├── Storage/KeychainStorage.swift
├── Core/
│   ├── DI/KoinHelper.swift
│   ├── Navigation/
│   │   ├── RootView.swift
│   │   └── AppTabView.swift
│   └── UI/
│       ├── Components/         ← AppCard, AppGradientHeaderView, AppToolbar,
│       │                          AppTypography, AvatarCircle, Buttons, InfoRow,
│       │                          PractitionerCard, StatusBadge, …
│       └── Theme/              ← AppColors.swift, AppDimens.swift, AppFonts.swift
└── Features/
    ├── Auth/
    │   ├── ForgotPassword/ForgotPasswordView.swift
    │   ├── Login/
    │   │   ├── LoginView.swift
    │   │   └── AuthViewModelWrapper.swift
    │   └── Register/RegisterView.swift
    ├── Dashboard/                          ← mirrors Android view/dashboard/
    │   ├── Appointments/                   ← mirrors view/dashboard/appointments/
    │   │   ├── MyAppointmentsView.swift
    │   │   ├── MyAppointmentsViewModelWrapper.swift
    │   │   └── Booking/                    ← mirrors view/dashboard/appointments/booking/
    │   │       ├── BookAppointmentView.swift
    │   │       ├── BookAppointmentViewModelWrapper.swift
    │   │       ├── AppointmentSuccessView.swift
    │   │       └── AppointmentSuccessViewModelWrapper.swift
    │   ├── Home/                           ← mirrors view/dashboard/home/
    │   │   ├── HomeView.swift
    │   │   ├── HomeViewModelWrapper.swift
    │   │   ├── PractitionerDetail/         ← mirrors view/dashboard/home/practitioner_data/
    │   │   │   ├── PractitionerDetailView.swift
    │   │   │   └── PractitionerDetailViewModelWrapper.swift
    │   │   └── PractitionerList/           ← mirrors view/dashboard/home/practitioners/
    │   │       ├── PractitionerListView.swift
    │   │       └── SearchResultsView.swift
    │   └── Profile/                        ← mirrors view/dashboard/profile/
    │       ├── ProfileView.swift
    │       ├── ProfileViewModelWrapper.swift
    │       └── ProfileEdit/                ← mirrors view/dashboard/profile/profile_edit/
    │           ├── ChangeEmailView.swift + ChangeEmailViewModelWrapper.swift
    │           ├── ChangePasswordView.swift + ChangePasswordViewModelWrapper.swift
    │           └── EditProfileView.swift + EditProfileViewModelWrapper.swift
    ├── Landing/LandingView.swift           ← mirrors view/landing/
    └── Splash/                             ← mirrors view/splash/
        ├── SplashView.swift
        └── SplashViewModelWrapper.swift
```

### Shared KMM — `commonMain`

```
shared/src/
├── commonMain/kotlin/lu/esklepios/app/
│   ├── data/
│   │   ├── db/                 ← SQLDelight schema + generated queries
│   │   ├── network/            ← ApiService, DTOs, Mappers, TokenStorage
│   │   └── repository/         ← *RepositoryImpl
│   ├── di/SharedModule.kt
│   ├── domain/
│   │   ├── model/              ← Practitioner, Appointment, User, AppointmentSlot, …
│   │   ├── repository/         ← interfaces
│   │   └── usecase/
│   └── presentation/viewmodel/ ← 11 ViewModels + UiState data classes
├── androidMain/                ← Android-specific actual implementations
├── iosMain/                    ← iOS-specific actual implementations
└── commonTest/                 ← shared unit tests (no MockK)
```

### Project root

```
esklepios/
├── androidApp/
├── iosApp/
├── shared/
├── strings/twine.txt           ← master localization (en/fr/de/lb)
├── Makefile                    ← `make strings` regenerates strings.xml
├── build.gradle.kts
├── settings.gradle.kts
├── dev.properties              ← NOT committed
├── prod.properties
└── .claude/
    ├── CLAUDE.md
    ├── rules/
    └── skills/
```
