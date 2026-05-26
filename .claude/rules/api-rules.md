# API Rules

## Rule API-1: All Network Calls Return Result<T>
Every function in `ApiService` and repository implementations returns `Result<T>`. Callers use `.onSuccess { }` and `.onFailure { }`. Exceptions must never propagate to ViewModels uncaught.

```kotlin
// CORRECT
suspend fun login(request: LoginRequest): Result<AuthResponse>

// FORBIDDEN
suspend fun login(request: LoginRequest): AuthResponse  // throws on error
```

## Rule API-2: DTOs Are Serializable, Domain Models Are Not
Network DTOs in `shared/src/commonMain/.../data/network/DTOs.kt` are annotated with `@Serializable`. Domain models in `domain/model/` are plain Kotlin data classes with no Ktor or kotlinx.serialization annotations. Repositories perform the mapping (mappers live in `data/network/Mappers.kt`).

```kotlin
// DTO (in data layer)
@Serializable
data class PractitionerDto(val id: String, val name: String, val speciality: String)

// Domain model (in domain layer) — no @Serializable
data class Practitioner(val id: String, val name: String, val speciality: String)
```

## Rule API-3: Endpoints Are Defined in ApiService Interface
The `ApiService` interface is the single source of truth for available endpoints. Implementation details (URL construction, headers) live only in `KtorApiService` (the implementing class in `data/network/KtorApiService.kt`).

## Rule API-4: Authentication Headers Are Injected by the Ktor Plugin
Do not manually add `Authorization: Bearer <token>` headers in `KtorApiService` functions. The Ktor `Auth` plugin's `bearer { ... }` provider (configured in `HttpClientFactory.kt`) handles this. Only non-authenticated endpoints (like `/auth/login`) should bypass auth.

## Rule API-5: Refresh Logic Is in the Ktor Client, Not Repositories
Token refresh happens automatically in the Ktor client configuration. Repositories must not implement retry-on-401 logic.

## Rule API-6: Base URL Comes from BuildKonfig
```kotlin
val BASE_URL = BuildKonfig.BASE_URL  // from dev.properties or prod.properties
```
Never hardcode a URL in `KtorApiService` or `HttpClientFactory`.

## Rule API-7: JSON Deserialization Is Lenient
The Ktor client JSON configuration must include `ignoreUnknownKeys = true` to prevent crashes when the API adds new fields.

```kotlin
Json {
    ignoreUnknownKeys = true
    isLenient = true
}
```

## Rule API-8: Logging Is Controlled by BuildKonfig
Network logging in the Ktor client must be gated by `BuildKonfig.ENABLE_LOGGING`. See **Rule LOG-2** in `.claude/rules/logging-rules.md` for the full spec and examples.

## Rule API-9: Network Timeout Is Set
Default timeouts must be configured to avoid hanging requests:
- Connect timeout: 30 seconds
- Request timeout: 30 seconds
- Socket timeout: 30 seconds

## Rule API-10: Error Responses Are Parsed, Not Just Status Codes
When the server returns an error body (e.g. `{"error": "Invalid credentials"}`), parse and surface it. Do not just use the HTTP status code message.

## Rule API-11: CancellationException Is Always Rethrown in safeCall
The `safeCall` wrapper in `KtorApiService.kt` catches specific Ktor exceptions by type before a final `catch (e: Exception)`. The `CancellationException` rethrow must precede the general fallback:

```kotlin
} catch (e: kotlinx.coroutines.CancellationException) {
    throw e
} catch (e: Exception) {
    Result.failure(e)
}
```

Do not add a bare `catch (e: Exception)` without this guard anywhere in the network layer.
