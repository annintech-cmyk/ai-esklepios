# Clock Rules

## Rule A-11: Inject Clock, Never Call Clock.System Directly in ViewModels
`kotlinx.datetime.Clock.System` must **never** be called inline in shared ViewModels or use cases. Inject `Clock` as a constructor parameter with `Clock.System` as the default.

```kotlin
// CORRECT
class HomeViewModel(
    private val searchUseCase: SearchPractitionersUseCase,
    private val clock: Clock = Clock.System
) : ViewModel() {
    private fun today() = clock.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
}

// FORBIDDEN
val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
```

**Why:** Inline `Clock.System` makes date-dependent behavior impossible to unit test deterministically. A `FakeClock` returning a fixed `Instant` is the only way to verify filter logic without depending on real wall time.

**Koin registration:** `Clock.System` is registered once in `SharedModule.kt`:
```kotlin
single<Clock> { Clock.System }
```
`factoryOf(::HomeViewModel)` resolves `Clock` from the container automatically.

**Tests:** Always inject a fixed `FakeClock`:
```kotlin
private val fixedClock = object : Clock {
    override fun now(): Instant = Instant.parse("2026-05-24T00:00:00Z")
}
```

## Rule A-12: kotlinx.datetime in Shared, java.time in Android Only
Shared ViewModels use `kotlinx.datetime.LocalDate`, `kotlinx.datetime.Clock`, and `kotlinx.datetime.Instant`. `java.time.*` is confined to `androidApp` (via `DateUtil.kt`). Never import `java.time` in `commonMain`.
