# eSklepios — Architecture

## Overview

eSklepios is a **Kotlin Multiplatform Mobile (KMM)** application using **Clean Architecture** with an **MVVM** presentation layer. Business logic and data access are shared between Android and iOS via the `shared` Gradle module. Platform UIs are built natively — Jetpack Compose for Android and SwiftUI for iOS.

---

## Project Structure

```
esklepios/
├── androidApp/                     Android application module
│   └── src/main/
│       ├── AndroidManifest.xml
│       └── kotlin/lu/esklepios/app/
│           ├── ESklepiosApp.kt     Application class (Koin init)
│           ├── MainActivity.kt     Single-activity host
│           ├── di/
│           │   └── AndroidModule.kt  Android-specific DI bindings
│           ├── storage/
│           │   └── SecureStorage.kt  TokenStorage impl (EncryptedSharedPreferences)
│           └── ui/
│               ├── components/     Reusable Compose components
│               ├── navigation/
│               │   ├── NavDestination.kt
│               │   └── AppNavGraph.kt
│               ├── screens/        15 Compose screens
│               └── theme/          Color, Theme, Typography, Dimens
│
├── iosApp/
│   └── eSklepios/
│       ├── eSklepiosApp.swift      App entry point (Koin init)
│       ├── Components/             Reusable SwiftUI components
│       ├── Navigation/
│       │   ├── RootView.swift      Auth gate
│       │   └── AppTabView.swift    Main tab controller
│       ├── Theme/                  Color extensions, gradients, Dimens
│       ├── ViewModels/             ViewModelWrappers + KoinHelper
│       └── Views/                  15 SwiftUI screens
│
└── shared/
    └── src/
        ├── commonMain/             Shared Kotlin code (KMM)
        │   └── kotlin/lu/esklepios/app/
        │       ├── data/
        │       │   ├── db/         SQLDelight schema + generated code
        │       │   ├── network/    ApiService, ApiServiceImpl, TokenStorage
        │       │   └── repository/ Repository implementations
        │       ├── di/
        │       │   └── SharedModule.kt   All Koin bindings
        │       ├── domain/
        │       │   ├── model/      Domain data classes
        │       │   ├── repository/ Repository interfaces
        │       │   └── usecase/    Use case classes
        │       └── presentation/
        │           └── viewmodel/  11 ViewModels + UiState classes
        ├── androidMain/            Android-specific shared code
        ├── iosMain/                iOS-specific shared code
        └── commonTest/             Platform-agnostic tests
```

---

## Architecture Layers

### Layer 1: Domain (innermost, most stable)

Contains the core business logic, independent of any framework or platform.

- **Models** — pure Kotlin data classes: `User`, `Appointment`, `Practitioner`, `Slot`
- **Repository Interfaces** — define data contracts: `AuthRepository`, `PractitionerRepository`, `AppointmentRepository`
- **Use Cases** — single-operation classes: `LoginUseCase`, `BookAppointmentUseCase`, `CancelAppointmentUseCase`

### Layer 2: Data

Implements the domain interfaces using external systems.

- **ApiService** — Ktor HTTP client abstraction
- **ApiServiceImpl** — Ktor implementation with Bearer auth
- **Repository Implementations** — fetch from API / local DB, map DTOs to domain models
- **SQLDelight Database** — local caching layer
- **TokenStorage** — interface for secure token persistence

### Layer 3: Presentation (ViewModels)

Mediates between domain and UI. Lives in `shared/commonMain`.

- Extends `androidx.lifecycle.ViewModel` (works in KMM via androidTarget config)
- Holds `MutableStateFlow<UiState>`, exposes `StateFlow<UiState>`
- Calls use cases from `viewModelScope.launch { }`
- Mutates state via `_uiState.update { it.copy(...) }`

### Layer 4: UI (Platform-specific)

Android Jetpack Compose + iOS SwiftUI. Observes ViewModel state and emits events.

---

## Dependency Flow

```
UI (Compose / SwiftUI)
    ↓ calls
ViewModel (shared)
    ↓ calls
UseCase (shared domain)
    ↓ calls
Repository Interface (shared domain)
    ↓ implemented by
Repository Impl (shared data)
    ↓ calls
ApiService / Database (shared data)
    ↓ platform-specific
HTTP Engine / SQLite Driver / TokenStorage
```

---

## Dependency Injection (Koin)

### Module Hierarchy
```
Application Start
  └── startKoin {
        modules(sharedModule + androidModule)  [Android]
        modules(sharedModule + iosModule)      [iOS via KoinHelper]
      }
```

### SharedModule Bindings
| Type | Scope | Implementation |
|------|-------|---------------|
| `HttpClient` | single | Configured Ktor client |
| `ApiService` | single | `ApiServiceImpl` |
| `AuthRepository` | single | `AuthRepositoryImpl` |
| `PractitionerRepository` | single | `PractitionerRepositoryImpl` |
| `AppointmentRepository` | single | `AppointmentRepositoryImpl` |
| `LoginUseCase`, etc. | factory | Direct class |
| `SplashViewModel`, etc. | factory | Direct class via `factoryOf` |

### Platform Modules
| Platform | Adds |
|---------|------|
| Android (`AndroidModule`) | `TokenStorage` → `SecureStorage`, Android `SqlDriver` |
| iOS (Koin init in `KoinHelper`) | `TokenStorage` → `KeychainStorage`, Native `SqlDriver` |

---

## Navigation Architecture

### Android
- **Single Activity** (`MainActivity`) hosts a `NavHost`
- `NavDestination` sealed class defines all routes as objects
- `AppNavGraph` wires the `NavHost` with composable routes
- Bottom navigation: Home, My Appointments, Profile
- Deep navigation uses `navController.navigate()` + `popUpTo`

### iOS
- `RootView` — auth gate: shows `SplashView`, then `LandingView` or `AppTabView`
- `AppTabView` — `TabView` with 3 tabs (Home, Appointments, Profile)
- Each tab uses a `NavigationStack` with `navigationDestination(for:)` for type-safe push navigation
- `AppDestination` enum drives navigation

---

## State Management

### ViewModel State Pattern
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
                .onSuccess { _uiState.update { s -> s.copy(isLoading = false, data = it) } }
                .onFailure { _uiState.update { s -> s.copy(isLoading = false, error = it.message) } }
        }
    }
}
```

### iOS Observation Pattern
iOS ViewModelWrappers use `.watch { }` from the KMM Kotlin/Swift interop to observe `StateFlow`:
```swift
viewModel.uiState.watch { [weak self] state in
    guard let state else { return }
    DispatchQueue.main.async { self?.uiState = state }
}
```

---

## Networking

- **Engine:** OkHttp (Android), Darwin (iOS) via Ktor
- **Auth:** Ktor `BearerTokenPlugin` — injects token automatically, refreshes on 401
- **Serialization:** `kotlinx.serialization` with `ignoreUnknownKeys = true`
- **Timeout:** 30 seconds connect + request + socket
- **Error handling:** All calls wrapped in `runCatching { }` → `Result<T>`

---

## Local Database (SQLDelight)

- Schema defined as `.sq` files in `shared/src/commonMain/sqldelight/`
- Generates type-safe Kotlin queries at compile time
- `ESklepiosDatabase` is a Koin singleton
- Driver: `AndroidSqliteDriver` (Android), `NativeSqliteDriver` (iOS)
- Used for caching: recent searches, favorites

---

## Security

| Concern | Android | iOS |
|---------|---------|-----|
| Access token storage | `EncryptedSharedPreferences` | Keychain (Security.framework) |
| Refresh token storage | Same | Same |
| Network | HTTPS only | HTTPS only |
| Sensitive data in logs | Disabled in prod (BuildKonfig) | Disabled in prod |

---

## Testing Strategy

| Level | Location | Tooling |
|-------|----------|---------|
| Shared unit (repos) | `shared/src/commonTest/` | `kotlin.test`, `kotlinx.coroutines.test`, Turbine |
| Android ViewModel | `androidApp/src/test/` | MockK, Turbine, `kotlin.test` |
| iOS unit | `iosApp/eSklepiosTests/` | XCTest |
| Integration | Not yet configured | Planned |
| UI/E2E | Not yet configured | Planned |

---

## Build Configuration

| Tool | Purpose |
|------|---------|
| Gradle (KMM) | Build, dependencies, Kotlin compilation |
| BuildKonfig | Inject `BASE_URL`, `ENABLE_LOGGING` at compile time |
| Xcode | iOS build, signing, distribution |
| Twine | Multi-language string management |
| Make | `make strings` shortcut for Twine generation |
