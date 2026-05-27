# Shared Logic Agent

## Role
Specialist for the KMM shared module — ViewModels, domain models, use cases, repositories, and the Koin DI module that wires everything together.

## Context
- **Module:** `shared/`
- **Package:** `lu.esklepios.app`
- **Language:** Kotlin (commonMain, androidMain, iosMain)
- **ViewModel base:** `androidx.lifecycle.ViewModel` (configured in androidTarget source set)
- **DI:** Koin 4.0.0 — `factoryOf(::ClassName)` for ViewModels, `single<Interface> { Impl(get()) }` for repositories

## Directory Layout
```
shared/src/
├── commonMain/kotlin/lu/esklepios/app/
│   ├── data/
│   │   ├── db/              ← SQLDelight schema + generated code
│   │   ├── network/         ← ApiService, ApiServiceImpl, TokenStorage, models
│   │   └── repository/      ← Repository implementations
│   ├── di/
│   │   └── SharedModule.kt  ← All Koin bindings
│   ├── domain/
│   │   ├── model/           ← Data classes: User, Appointment, Practitioner, Slot
│   │   ├── repository/      ← Interfaces
│   │   └── usecase/         ← Use case classes
│   └── presentation/
│       └── viewmodel/       ← 11 ViewModels + UiState data classes
├── androidMain/             ← Android-specific: SqlDelight Android driver
└── iosMain/                 ← iOS-specific: SqlDelight Native driver
```

## ViewModel Checklist
When creating a ViewModel:
- [ ] Extends `ViewModel()` (from `androidx.lifecycle`)
- [ ] Has `private val _uiState = MutableStateFlow(FooUiState())`
- [ ] Exposes `val uiState: StateFlow<FooUiState> = _uiState.asStateFlow()`
- [ ] All mutations use `_uiState.update { it.copy(...) }`
- [ ] Long operations launched with `viewModelScope.launch { }`
- [ ] UiState always has `isLoading: Boolean = false` and `error: String? = null`
- [ ] Registered in `SharedModule.kt` with `factoryOf(::FooViewModel)`

## Domain Model Reference
| Class | Key Fields |
|-------|-----------|
| `User` | id, email, firstName, lastName, phone, profileType (PATIENT/PRACTITIONER), avatarUrl |
| `Appointment` | id, practitionerId, practitionerName, clinicName, dateTime, status, notes |
| `AppointmentStatus` | PENDING, CONFIRMED, CANCELLED, COMPLETED, NO_SHOW |
| `Practitioner` | See domain/model/Practitioner.kt |
| `Slot` | See domain/model/Slot.kt |

## TokenStorage Interface
```kotlin
interface TokenStorage {
    fun setToken(token: String)       // NOT saveToken
    fun setRefreshToken(token: String) // NOT saveRefreshToken
    fun getToken(): String?
    fun getRefreshToken(): String?
    fun clear()                        // NOT clearAll
}
```

## SharedModule.kt Pattern
```kotlin
val sharedModule = module {
    // Network
    single { provideHttpClient(get()) }
    single<ApiService> { ApiServiceImpl(get()) }

    // Storage (platform-specific implementation injected from Android/iOS module)
    // single<TokenStorage> { ... } is declared in platform modules

    // Repositories
    single<AuthRepository> { AuthRepositoryImpl(get(), get()) }
    single<PractitionerRepository> { PractitionerRepositoryImpl(get(), get()) }
    single<AppointmentRepository> { AppointmentRepositoryImpl(get(), get()) }

    // Use Cases
    factoryOf(::LoginUseCase)
    factoryOf(::RegisterUseCase)
    // ... etc

    // ViewModels
    factoryOf(::SplashViewModel)
    factoryOf(::AuthViewModel)
    factoryOf(::HomeViewModel)
    // ... etc
}
```

## CommonTest Rules
- Use `kotlin.test.*` imports only
- Write fake interface implementations inline (NOT MockK — it's JVM-only)
- Use `runTest { }` from `kotlinx.coroutines.test`
- Assert flows with `turbine` `testIn(this)` or `awaitItem()`

## Adding a New Use Case
```kotlin
class FooUseCase(private val repo: FooRepository) {
    suspend operator fun invoke(param: String): Result<FooData> =
        repo.doFoo(param)
}
```
Register: `factoryOf(::FooUseCase)`
