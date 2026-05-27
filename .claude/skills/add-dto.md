# Skill: Add Network DTO

Adds a new @Serializable DTO pair (request/response), wires it to the ApiService, and adds it to the serialization smoke test.

## Usage
```
/add-dto <Name> [description]
```
Example: `/add-dto Notification Add push notification preference DTO`

## Steps

### 1. Create Request/Response DTOs
File: `shared/src/commonMain/kotlin/lu/esklepios/app/data/network/models/<Name>Dto.kt`

```kotlin
package lu.esklepios.app.data.network

import kotlinx.serialization.Serializable

@Serializable
data class <Name>Request(
    val field: String
)

@Serializable
data class <Name>Response(
    val id: String,
    val field: String
)
```

**Rules:**
- Only DTOs (in `data/network/`) get `@Serializable`. Domain models do not.
- Use `@SerialName("json_key")` when the JSON field name differs from the Kotlin property name.

### 2. Add Domain Model (if new concept)
File: `shared/src/commonMain/kotlin/lu/esklepios/app/domain/model/<Name>.kt`
```kotlin
data class <Name>(val id: String, val field: String)
```

### 3. Add Mapping Extension
In the DTOs file:
```kotlin
fun <Name>Response.toDomain(): <Name> = <Name>(id = id, field = field)
fun <Name>.toRequest(): <Name>Request = <Name>Request(field = field)
```

### 4. Add to ApiService Interface
File: `shared/src/commonMain/kotlin/lu/esklepios/app/data/network/ApiService.kt`
```kotlin
suspend fun get<Name>(id: String): Result<<Name>Response>
suspend fun create<Name>(request: <Name>Request): Result<<Name>Response>
```

### 5. Implement in KtorApiService
File: `shared/src/commonMain/kotlin/lu/esklepios/app/data/network/KtorApiService.kt`
```kotlin
override suspend fun get<Name>(id: String): Result<<Name>Response> =
    safeCall { client.get("$baseUrl/v1/<names>/$id").body() }

override suspend fun create<Name>(request: <Name>Request): Result<<Name>Response> =
    safeCall { client.post("$baseUrl/v1/<names>") { setBody(request) }.body() }
```

### 6. Add to SerializationSmokeTest
File: `shared/src/commonTest/kotlin/lu/esklepios/app/SerializationSmokeTest.kt`
```kotlin
@Test fun `<Name>Request round-trips`() = assertRoundTrip(<Name>Request(field = "test"))
@Test fun `<Name>Response round-trips`() = assertRoundTrip(<Name>Response(id = "1", field = "test"))
```

### 7. Add ProGuard Keep Rule (if in new package)
In `androidApp/proguard-rules.pro` — verify the wildcard covers your new DTO:
```
-keep @kotlinx.serialization.Serializable class lu.esklepios.app.** { *; }
```

## Checklist
- [ ] `@Serializable` on both request and response DTOs
- [ ] Domain model is NOT annotated with `@Serializable` (unless used as JSON blob column)
- [ ] `toDomain()` mapping extension defined
- [ ] Added to `ApiService` interface (Rule API-3)
- [ ] Implemented in `KtorApiService` using `safeCall { }`
- [ ] Round-trip test added to `SerializationSmokeTest.kt` (Rule SR-4)
- [ ] `Json { ignoreUnknownKeys = true }` in Ktor client config (Rule SR-2)
