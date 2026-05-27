# Performance Rules

## Rule PERF-1: No Blocking Calls on the Main Dispatcher
Functions annotated with `suspend` that do I/O or CPU-heavy work must not run on `Dispatchers.Main`. Repository implementations that use SQLDelight's blocking `executeAsList()` or `executeAsOneOrNull()` should be called from `viewModelScope.launch { }` (which defaults to `Dispatchers.Main.immediate` but suspends coroutines) or explicitly switch context:

```kotlin
// OK — suspend bridges the main thread
override suspend fun getPastAppointments(userId: String): Result<List<Appointment>> =
    runCatching { database.appointmentsQueries.selectPast().executeAsList().map { it.toDomain() } }

// For CPU-heavy mapping, switch to Default
withContext(Dispatchers.Default) {
    entities.map { it.toDomain() }
}
```

## Rule PERF-2: Flow Queries Use Dispatchers.Default for Mapping
SQLDelight Flow queries use `mapToList(Dispatchers.Default)` to perform entity-to-domain mapping off the main thread:

```kotlin
database.appointmentsQueries.selectAll()
    .asFlow()
    .mapToList(Dispatchers.Default)
    .map { entities -> entities.map { it.toDomain() } }
```

## Rule PERF-3: Images Are Loaded Asynchronously
Never load images synchronously on the main thread. Use:
- Android: `AsyncImage` (Coil)
- iOS: `AsyncImage` (SwiftUI native)

Do not use `BitmapFactory.decodeFile()` or `UIImage(contentsOfFile:)` on the main thread.

## Rule PERF-4: Avoid Recomposition From Unstable Lambdas
Compose recomposition is triggered by unstable captures. Avoid:
```kotlin
// AVOID — creates a new lambda on every recomposition
Button(onClick = { viewModel.doSomething(item.id) }) { ... }
```
Prefer stable `onClick` references or `rememberUpdatedState` for long-lived callbacks in lists.

## Rule PERF-5: LazyColumn / LazyRow for Lists
Never use `Column { items.forEach { ... } }` for lists with unbounded items. Use `LazyColumn` / `LazyRow` on Android and `List` / `ScrollView` on iOS.
