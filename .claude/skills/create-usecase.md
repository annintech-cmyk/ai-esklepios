# Skill: Create Use Case

Creates a single-responsibility use case class in the domain layer.

## Usage
```
/create-usecase <Name> [description]
```
Example: `/create-usecase CancelAppointment Cancel an existing appointment by ID`

## Steps

### 1. Create the Use Case File
File: `shared/src/commonMain/kotlin/lu/esklepios/app/domain/usecase/<Name>UseCase.kt`

```kotlin
package lu.esklepios.app.domain.usecase

import lu.esklepios.app.domain.repository.<Relevant>Repository

class <Name>UseCase(
    private val repository: <Relevant>Repository
) {
    suspend operator fun invoke(<param>: <Type>): Result<<ReturnType>> =
        repository.<relevantMethod>(<param>)
}
```

### 2. Register in SharedModule.kt
```kotlin
factoryOf(::<Name>UseCase)
```

### 3. Inject in ViewModel
```kotlin
class FooViewModel(
    private val <name>UseCase: <Name>UseCase
) : ViewModel() {
    // use it:
    viewModelScope.launch {
        val result = <name>UseCase(param)
        result.onSuccess { ... }.onFailure { ... }
    }
}
```

## Use Case Design Rules
- One class = one operation.
- The `operator fun invoke(...)` is the only public method.
- Use cases may orchestrate multiple repository calls (e.g., cancel appointment AND refresh list).
- Use cases may add validation logic not appropriate for the repository.
- Use cases must NOT have any UI dependencies.
- Use cases must NOT access `ApiService`, SQLDelight queries, or `TokenStorage` directly — only repository methods.
- Use cases return `Result<T>` — never throw.

## Testing
Write a `<Name>UseCaseTest.kt` in `shared/src/commonTest/` for every use case:
```kotlin
class <Name>UseCaseTest {
    private val fakeRepo = Fake<Dependency>Repository()
    private val useCase = <Name>UseCase(fakeRepo)

    @Test
    fun `delegates to repository and returns success`() = runTest {
        assertTrue(useCase(<args>).isSuccess)
    }

    @Test
    fun `propagates repository failure`() = runTest {
        fakeRepo.result = Result.failure(Exception("err"))
        assertTrue(useCase(<args>).isFailure)
    }
}
```
Use fake interface implementations, never MockK (Rule T-1).

## Examples

### Simple passthrough
```kotlin
class GetPractitionerUseCase(
    private val repository: PractitionerRepository
) {
    suspend operator fun invoke(id: String): Result<Practitioner> =
        repository.getById(id)
}
```

### With validation
```kotlin
class ChangePasswordUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(
        currentPassword: String,
        newPassword: String
    ): Result<Unit> {
        if (newPassword.length < 8) {
            return Result.failure(IllegalArgumentException("Password must be at least 8 characters"))
        }
        return repository.changePassword(currentPassword, newPassword)
    }
}
```

### Orchestrating multiple operations
```kotlin
class LogoutUseCase(
    private val authRepository: AuthRepository,
    private val tokenStorage: TokenStorage
) {
    suspend operator fun invoke(): Result<Unit> {
        authRepository.logout()
        tokenStorage.clear()
        return Result.success(Unit)
    }
}
```
