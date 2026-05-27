# Error Handling Rules

## Rule EH-1: Use runCatching, Never bare try/catch(Exception)
Bare `catch (e: Exception)` swallows `CancellationException`, breaking structured concurrency. Replace with `runCatching { }`.

```kotlin
// CORRECT
override suspend fun getUpcomingAppointments(userId: String): Result<List<Appointment>> =
    runCatching { database.appointmentsQueries.selectUpcoming().executeAsList().map { it.toDomain() } }

// FORBIDDEN — cancellations are swallowed
try {
    ...
} catch (e: Exception) {
    Result.failure(e)
}
```

## Rule EH-2: Multi-Exception Catch Chains Must Rethrow CancellationException
When a `catch (e: Exception)` must follow more specific exception handlers (e.g., in `safeCall`), always add a `CancellationException` rethrow before it:

```kotlin
} catch (e: kotlinx.coroutines.CancellationException) {
    throw e
} catch (e: Exception) {
    Result.failure(e)
}
```

## Rule EH-3: JSON Decoding Fallbacks Use runCatching
When decoding optional JSON fields from the database (e.g., in `toDomain()` extension functions), use `runCatching { ... }.getOrDefault(emptyList())`:

```kotlin
val slots = runCatching { json.decodeFromString<List<AppointmentSlot>>(slotsJson) }.getOrDefault(emptyList())
```

## Rule EH-4: Repositories Return Result<T>, ViewModels Never Throw
- All repository methods return `Result<T>`.
- ViewModels use `.onSuccess { }` / `.onFailure { }` — never `try/catch` at ViewModel level.
- `UiState.error: String?` is the sole error surface in the UI.

## Rule EH-5: Always Clear Error on New Action
```kotlin
_uiState.update { it.copy(isLoading = true, error = null) }  // null clears previous error
```
