# Skill: Create Repository

Creates a fully wired repository: interface, implementation, Koin registration, and test stub.

## Usage
```
/create-repository <Name> [description]
```
Example: `/create-repository MedicalRecord Manage patient medical records`

## Steps

### 1. Define the Interface
File: `shared/src/commonMain/kotlin/lu/esklepios/app/domain/repository/<Name>Repository.kt`

```kotlin
package lu.esklepios.app.domain.repository

interface <Name>Repository {
    suspend fun getAll(): Result<List<<Name>>>
    suspend fun getById(id: String): Result<<Name>>
    // Add other operations as needed
}
```

### 2. Create the Implementation
File: `shared/src/commonMain/kotlin/lu/esklepios/app/data/repository/<Name>RepositoryImpl.kt`

```kotlin
package lu.esklepios.app.data.repository

import lu.esklepios.app.data.network.ApiService
import lu.esklepios.app.domain.model.<Name>
import lu.esklepios.app.domain.repository.<Name>Repository

class <Name>RepositoryImpl(
    private val apiService: ApiService
) : <Name>Repository {

    override suspend fun getAll(): Result<List<<Name>>> =
        apiService.get<Name>s().map { dtos -> dtos.map { it.toDomain() } }

    override suspend fun getById(id: String): Result<<Name>> =
        apiService.get<Name>(id).map { it.toDomain() }
}

// Mapping extension
private fun <Name>Dto.toDomain(): <Name> = <Name>(
    id = id,
    // ... map fields
)
```

### 3. Register in SharedModule.kt
```kotlin
single<<Name>Repository> { <Name>RepositoryImpl(get()) }
```

### 4. Write the Test
File: `shared/src/commonTest/kotlin/lu/esklepios/app/<Name>RepositoryTest.kt`

```kotlin
package lu.esklepios.app

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest

class <Name>RepositoryTest {
    private val fakeApi = FakeApiServiceFor<Name>()
    private val repository = <Name>RepositoryImpl(fakeApi)

    @Test
    fun `getAll returns mapped domain models on success`() = runTest {
        fakeApi.result = Result.success(listOf(sample<Name>Dto()))
        val result = repository.getAll()
        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()?.size)
    }

    @Test
    fun `getAll returns failure on api error`() = runTest {
        fakeApi.result = Result.failure(Exception("Network error"))
        val result = repository.getAll()
        assertTrue(result.isFailure)
    }
}

private class FakeApiServiceFor<Name> : ApiService {
    var result: Result<List<<Name>Dto>> = Result.success(emptyList())
    override suspend fun get<Name>s() = result
    // Implement all other ApiService methods with defaults
}
```

### 5. Add API Method (if needed)
If the repository requires a new API endpoint, refer to the `create-api` skill.

## Notes
- Repositories must NEVER throw — always return `Result<T>`.
- Use `runCatching { }` around local DB calls (never bare `try/catch(Exception)` — it swallows `CancellationException`).
- DTO-to-domain mapping must happen inside the repository, never in the ViewModel.
- For `toDomain()` that reads JSON blob columns, use `runCatching { json.decodeFromString<T>(blob) }.getOrDefault(emptyList())`.
- When `getAll()` fetches from both DB (immediate return) and API (background refresh), use `runCatching { }` around the combined block.
- Tests in `commonTest` must use fake implementations of interfaces — never MockK.
