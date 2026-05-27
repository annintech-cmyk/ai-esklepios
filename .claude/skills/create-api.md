# Skill: Create API Endpoint

Adds a new endpoint to `ApiService` with its request/response DTOs and Ktor implementation.

## Usage
```
/create-api <method> <endpoint> <description>
```
Example: `/create-api POST /appointments/book Book a new appointment`

## Steps

### 1. Create Request DTO (if needed)
Add to the single DTOs file: `shared/src/commonMain/kotlin/lu/esklepios/app/data/network/DTOs.kt`

```kotlin
// in DTOs.kt
@Serializable
data class <Name>Request(
    val field1: String,
    val field2: String?
)
```

### 2. Create Response DTO (if needed)
Also in `data/network/DTOs.kt`:

```kotlin
// in DTOs.kt
@Serializable
data class <Name>Response(
    val id: String,
    val field1: String
)
```

> If the response maps to a domain model, add a `<Name>Response.toDomain(): <DomainModel>` extension in `data/network/Mappers.kt`.

### 3. Add to ApiService Interface
In `shared/src/commonMain/kotlin/lu/esklepios/app/data/network/ApiService.kt`:

```kotlin
// GET example
suspend fun get<Name>s(filter: String? = null): Result<List<<Name>Response>>

// POST example
suspend fun create<Name>(request: <Name>Request): Result<<Name>Response>

// PUT example
suspend fun update<Name>(id: String, request: <Name>Request): Result<<Name>Response>

// DELETE example
suspend fun delete<Name>(id: String): Result<Unit>
```

### 4. Implement in KtorApiService
In `shared/src/commonMain/kotlin/lu/esklepios/app/data/network/KtorApiService.kt`:

```kotlin
// GET
override suspend fun get<Name>s(filter: String?): Result<List<<Name>Response>> =
    runCatching {
        client.get("$BASE_URL/endpoint") {
            filter?.let { parameter("filter", it) }
        }.body()
    }

// POST
override suspend fun create<Name>(request: <Name>Request): Result<<Name>Response> =
    runCatching {
        client.post("$BASE_URL/endpoint") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

// PUT
override suspend fun update<Name>(id: String, request: <Name>Request): Result<<Name>Response> =
    runCatching {
        client.put("$BASE_URL/endpoint/$id") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

// DELETE
override suspend fun delete<Name>(id: String): Result<Unit> =
    runCatching {
        client.delete("$BASE_URL/endpoint/$id")
        Unit
    }
```

### 5. Update Fake in Tests
If there is a `FakeApiService` in any test file, add the new method implementation with a sensible default.

## Endpoint URL Conventions
- List: `GET /resources` — plural noun
- Single: `GET /resources/{id}`
- Create: `POST /resources`
- Update: `PUT /resources/{id}`
- Partial update: `PATCH /resources/{id}`
- Delete: `DELETE /resources/{id}`
- Action: `POST /resources/{id}/action`

## Authentication
All endpoints are authenticated by default (the Ktor `Auth { bearer { } }` provider configured in `HttpClientFactory.kt` injects the header). For public endpoints (e.g., `/auth/login`), the `Auth` plugin's `realm` / path-skip mechanism should be used to bypass token injection.

## Error Handling
`runCatching { }` already wraps all calls in `Result<T>`. Repositories further map Throwable to user-readable strings before surfacing to ViewModels.
