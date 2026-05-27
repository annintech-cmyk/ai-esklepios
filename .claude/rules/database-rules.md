# Database Rules

## Rule DB-1: SQLDelight Queries Are the Only DB Access Path
Never write raw SQL strings in Kotlin/Swift. All queries are defined in `.sq` files in `shared/src/commonMain/sqldelight/` and accessed through generated `*Queries` objects on `ESklepiosDatabase`.

## Rule DB-2: Multi-Table Writes Use database.transaction { }
Any operation that writes to more than one table must be wrapped in `database.transaction { }` to ensure atomicity:

```kotlin
database.transaction {
    database.usersQueries.deleteAll()
    database.practitionersQueries.deleteAll()
    database.appointmentsQueries.deleteAll()
}
```

Do not perform multi-table writes sequentially outside a transaction — partial failures leave the DB in an inconsistent state.

## Rule DB-3: Cache-First for Reads, API-Refresh for Network
The standard repository pattern is:
1. Return cached data from SQLDelight immediately (fast path)
2. Trigger an API fetch in the background to refresh the cache
3. The UI observes `Flow<List<T>>` from SQLDelight and updates automatically

```kotlin
override fun getAppointments(userId: String): Flow<List<Appointment>> =
    database.appointmentsQueries.selectAll().asFlow().mapToList(Dispatchers.Default).map { ... }
```

## Rule DB-4: JSON Blob Columns Use runCatching for Decoding
Columns that store JSON (e.g., `availableSlotsJson`, `scheduleJson`) must decode with a fallback:

```kotlin
val slots = runCatching { json.decodeFromString<List<AppointmentSlot>>(availableSlotsJson) }.getOrDefault(emptyList())
```

## Rule DB-5: Boolean Columns Are Stored as Long (0/1)
SQLite has no native boolean type. SQLDelight maps booleans to `Long`. Use `if (practitioner.acceptingNewPatients) 1L else 0L` when inserting, and `acceptingNewPatients != 0L` when reading.

## Rule DB-6: ESklepiosDatabase Driver Is Created in the DI Module
The platform-specific driver (`AndroidSqliteDriver` / `NativeSqliteDriver`) is created inside the Koin module — never outside DI. The shared module accesses it via `get<DatabaseDriverFactory>().createDriver()`.
