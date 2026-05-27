# Dependency Injection Rules

## Rule DI-1: All Registrations Live in Module Files Only
Koin registrations must be in exactly two places:
- `shared/src/commonMain/.../di/SharedModule.kt` — shared + platform-agnostic singletons
- `androidApp/.../di/AndroidModule.kt` — Android-specific implementations

On iOS, the Koin module is initialized in `KoinHelper.swift`. No `get()` calls may appear outside module lambdas or `KoinHelper.get()`.

## Rule DI-2: Singletons vs Factories
| Component | Lifecycle | Registration |
|-----------|-----------|-------------|
| Repository | Application singleton | `single<FooRepository> { FooRepositoryImpl(get(), get()) }` |
| Use Case | New per injection | `factoryOf(::FooUseCase)` |
| ViewModel | New per screen | `factoryOf(::FooViewModel)` |
| Database | Application singleton | `single { ESklepiosDatabase(get()) }` |
| ApiService | Application singleton | `single<ApiService> { KtorApiService(get(), get()) }` |
| Clock | Application singleton | `single<Clock> { Clock.System }` |

## Rule DI-3: Interface Types for Repositories
Always bind repositories to their interface type, never the concrete class:
```kotlin
// CORRECT
single<AuthRepository> { AuthRepositoryImpl(get(), get(), get()) }

// FORBIDDEN
single { AuthRepositoryImpl(get(), get(), get()) }
```

## Rule DI-4: factoryOf Resolves Clock Automatically
`HomeViewModel` takes `clock: Clock` as a constructor parameter with a default. `factoryOf(::HomeViewModel)` resolves `Clock` from the container automatically — no manual `factory { HomeViewModel(get(), get(), get()) }` needed.

## Rule DI-5: No Service Locator Calls in ViewModels
ViewModels must never call `Koin.get()` or `KoinHelper.get()` internally. All dependencies are injected through the constructor by the DI container.

## Rule DI-6: Platform Modules Override Interface Bindings
Android-specific implementations (e.g., `SecureStorage`) are bound in `AndroidModule.kt`:
```kotlin
single<TokenStorage> { SecureStorage(androidContext()) }
```
This overrides any `shared` module binding for `TokenStorage`. iOS binds `KeychainStorage` in its Koin module.
