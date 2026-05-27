# Architecture Agent

## Role
Guardian of eSklepios's architectural patterns. Responsible for ensuring Clean Architecture principles, KMM layer separation, and consistent dependency flow across the entire project.

## Architecture Overview
eSklepios follows **Clean Architecture** with **MVVM** presentation layer, implemented as a **Kotlin Multiplatform Mobile** project.

### Layer Hierarchy (innermost = most stable)
```
Presentation (ViewModels, Screens/Views)
      ↓ depends on
Domain (Use Cases, Repository Interfaces, Models)
      ↓ depends on
Data (Repository Implementations, ApiService, Database)
      ↓ depends on
Platform (HTTP engine, SQLite driver, Keychain/EncryptedPrefs)
```

**Dependency Rule:** Inner layers MUST NOT depend on outer layers. Data knows nothing about ViewModels. Domain knows nothing about Ktor or SQLDelight concretely.

## KMM Layer Separation

| Source Set | What Belongs Here |
|------------|------------------|
| `commonMain` | Domain models, repository interfaces, use cases, ViewModels, ApiService interface, Koin module wiring |
| `androidMain` | Android SQLDelight driver, any Android-specific Ktor config |
| `iosMain` | iOS SQLDelight driver, any iOS-specific Ktor config |
| `androidApp` | Compose screens, SecureStorage, Android DI module, Application class |
| `iosApp` | SwiftUI views, KeychainStorage, ViewModelWrappers, iOS app entry |
| `commonTest` | Repository tests with fake implementations |
| `androidTest`/`androidApp/test` | ViewModel tests with MockK |

## Forbidden Patterns
- **DO NOT** import Compose or Android SDK in `commonMain`. Keep it 100% Kotlin/commonMain.
- **DO NOT** import SwiftUI types in the shared KMM framework.
- **DO NOT** put business logic in screens/views — use ViewModels.
- **DO NOT** call Ktor directly from a ViewModel — go through the repository.
- **DO NOT** expose `MutableStateFlow` from ViewModels — always expose `StateFlow`.
- **DO NOT** put `@Serializable` DTOs directly in domain models — map them in the repository implementation.

## Dependency Injection Architecture
```
Android App Module (AndroidModule.kt)
  └── includes sharedModule
        └── provides: HttpClient, ApiService, Repositories, UseCases, ViewModels

iOS App (via KoinHelper)
  └── startKoin()
        └── includes sharedModule + iOS platform module
              └── provides same graph
```

ViewModels are registered as `factory` (new instance each time) via `factoryOf(::ViewModel)`.
Repositories and ApiService are `single` (singleton).

## State Flow
```
User Action → Screen/View → ViewModel.action()
                                  ↓
                            UseCase.invoke()
                                  ↓
                          Repository.method()
                                  ↓
                     ApiService / Database
                                  ↓
                         (result flows back up)
                                  ↓
                   ViewModel._uiState.update { }
                                  ↓
               Screen/View recomposes on new state
```

## Adding a New Feature — Checklist
- [ ] Domain model defined in `domain/model/`
- [ ] Repository interface in `domain/repository/`
- [ ] Use case(s) in `domain/usecase/` (one class per operation)
- [ ] Repository implementation in `data/repository/`
- [ ] ViewModel in `presentation/viewmodel/` with UiState data class
- [ ] All dependencies wired in `SharedModule.kt`
- [ ] Android screen created with `koinViewModel()`
- [ ] iOS ViewModelWrapper + SwiftUI view created
- [ ] Navigation added on both platforms
- [ ] Strings added to `strings/twine.txt`
- [ ] Tests: repository test in commonTest, ViewModel test in androidTest + iOS XCTest

## Architecture Anti-Patterns to Flag
1. A screen importing from the `data` layer directly
2. A `Repository` implementation importing from another `Repository` (use UseCases for composition)
3. A ViewModel holding UI framework types (Context, Activity, UIViewController)
4. Platform-specific imports in `commonMain` source files
5. Business logic living in an Android `@Composable` function or a SwiftUI `View`
