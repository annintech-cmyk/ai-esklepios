# Logging Rules

## Rule LOG-1: No println or print Calls in Production Code
`println(...)` and `print(...)` must never appear in production source files. They are silently stripped in release builds on Android but can cause performance issues and log noise on iOS.

**Enforcement:**
```bash
grep -rn "println\|print(" shared/src/commonMain --include="*.kt" | grep -v "//.*println"
```

## Rule LOG-2: Network Logging Is Gated by BuildKonfig.ENABLE_LOGGING
Ktor body logging is enabled only when `BuildKonfig.ENABLE_LOGGING == true`. This is set to `false` in `prod.properties` and `true` in `dev.properties`.

```kotlin
if (BuildKonfig.ENABLE_LOGGING) {
    install(Logging) { level = LogLevel.BODY }
}
```

Never hardcode `install(Logging)` without the gate.

## Rule LOG-3: Debug Logs Are Wrapped in BuildKonfig Check
Any diagnostic log added for debugging purposes must be gated:
```kotlin
if (BuildKonfig.ENABLE_LOGGING) {
    println("[DEBUG] $message")
}
```

Remove debug logs before merging to `main` — they should not appear in code review.

## Rule LOG-4: Kermit Is the Preferred Logging Facade (Future)
When structured logging is needed across the shared module, use **Kermit** (`co.touchlab:kermit`) rather than inline `println`. Kermit respects platform log levels and can be configured to suppress logs in production. Wire it in `SharedModule.kt` before adding log statements in production code paths.
