# Networking Agent

## Role
Specialist for all networking concerns in eSklepios: Ktor client configuration, API service definition and implementation, authentication token management, and error handling.

## Context
- **HTTP Client:** Ktor 3.0.3
- **Engine (Android):** OkHttp (`ktor-client-okhttp`)
- **Engine (iOS):** Darwin (`ktor-client-darwin`)
- **Serialization:** `kotlinx.serialization` + `ktor-serialization-kotlinx-json`
- **Auth:** Bearer token with automatic refresh via `ktor-client-auth`
- **Files:** `shared/src/commonMain/.../data/network/`

## Key Files
- `data/network/ApiService.kt` — interface defining all endpoints
- `data/network/KtorApiService.kt` — Ktor implementation of `ApiService`
- `data/network/HttpClientFactory.kt` — Ktor `HttpClient` configuration (Auth, ContentNegotiation, Logging, Timeout)
- `data/network/TokenStorage.kt` — interface for token persistence
- `data/network/DTOs.kt` — `@Serializable` request/response DTOs (single file)
- `data/network/Mappers.kt` — DTO ↔ domain model mappers

## ApiService Interface Pattern
```kotlin
interface ApiService {
    suspend fun login(request: LoginRequest): Result<AuthResponse>
    suspend fun getPractitioners(query: String, location: String?): Result<List<PractitionerDto>>
    // ... etc
}
```

## KtorApiService Pattern
```kotlin
class KtorApiService(private val client: HttpClient) : ApiService {
    override suspend fun login(request: LoginRequest): Result<AuthResponse> =
        runCatching {
            client.post("$BASE_URL/auth/login") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }.body()
        }
}
```

## HTTP Client Configuration (`HttpClientFactory.kt`)
The client is configured with:
- `ContentNegotiation` (JSON with `ignoreUnknownKeys = true`, `isLenient = true`, `encodeDefaults = true`)
- `Auth` plugin with `bearer { ... }` provider:
  - `loadTokens` reads from `TokenStorage.getToken()` + `getRefreshToken()`
  - `refreshTokens` returns new `BearerTokens(...)` using stored values
- `Logging` (installed only when constructor's `enableLogging` is true — wired from `BuildKonfig.ENABLE_LOGGING`)
- Timeout: 30 s connect + request + socket
- `defaultRequest`: `Content-Type: application/json`, `Accept: application/json`, `X-App-Platform: mobile`

## TokenStorage Interface
```kotlin
interface TokenStorage {
    fun setToken(token: String)
    fun setRefreshToken(token: String)
    fun getToken(): String?
    fun getRefreshToken(): String?
    fun clear()
}
```
**CRITICAL:** Methods are `setToken`, `setRefreshToken`, `clear` — not `saveToken`, `saveRefreshToken`, `clearAll`.

## Platform Implementations
| Platform | Class | Storage Mechanism |
|---------|-------|--------------------|
| Android | `SecureStorage` | `EncryptedSharedPreferences` |
| iOS | `KeychainStorage` | iOS Keychain via Security.framework |

## Adding a New Endpoint
1. Add a `@Serializable` DTO to `data/network/DTOs.kt` for request and response.
2. Add a mapper in `data/network/Mappers.kt` if a domain model exists.
3. Add the function signature to `ApiService` interface.
4. Implement in `KtorApiService` using the appropriate HTTP method.
5. Update the relevant repository to call the new function.
6. Add a fake implementation in any existing `FakeApiService` in test files.

## Error Handling
- `runCatching { }` wraps each Ktor call to return `Result<T>`.
- `onSuccess` / `onFailure` in repositories to map to domain errors.
- Network timeouts: catch `HttpRequestTimeoutException`.
- Auth failures: the `Auth { bearer { } }` provider will attempt refresh before propagating 401 errors.
- Repositories should map `Throwable` to user-readable `String` messages for UI state.

## Environment Config
Base URL comes from `BuildKonfig.BASE_URL` which is set from `dev.properties` or `prod.properties` via `buildkonfig` Gradle plugin.
