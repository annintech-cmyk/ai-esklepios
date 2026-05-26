# Serialization Rules

## Rule SR-1: Only DTOs Are @Serializable
DTOs in `shared/src/commonMain/.../data/network/` are annotated with `@Serializable`. Domain models in `domain/model/` must NOT be annotated with `@Serializable`. See Rule API-2.

**Exception:** `AppointmentSlot` and `ScheduleEntry` are `@Serializable` because they are stored as JSON blobs in SQLDelight (not transmitted directly). Any domain model stored as a JSON column must be `@Serializable`.

## Rule SR-2: JSON Config Must Have ignoreUnknownKeys = true
All `Json { }` instances used for network or DB deserialization must set `ignoreUnknownKeys = true`. This prevents crashes when the API adds new fields.

**For network deserialization:** See **Rule API-7** in `.claude/rules/api-rules.md` for the Ktor client configuration.

## Rule SR-3: ProGuard Rules Are Required for Every @Serializable Class Namespace
`androidApp/proguard-rules.pro` must keep all serializable classes to prevent R8 from stripping serializer factories:

```
-keep @kotlinx.serialization.Serializable class lu.esklepios.app.** { *; }
-keepclassmembers class lu.esklepios.app.** {
    kotlinx.serialization.KSerializer serializer(...);
}
```

When adding a new `@Serializable` DTO, verify it is covered by the wildcard. If it lives in a different package, add an explicit rule.

## Rule SR-4: New DTOs Must Be Added to SerializationSmokeTest
`shared/src/commonTest/.../SerializationSmokeTest.kt` tests every `@Serializable` DTO with an encode-then-decode round trip. When adding a new DTO, add a `assertRoundTrip<NewDto>(sample)` call to the smoke test.

```kotlin
@Test
fun `NewDto round-trips`() = assertRoundTrip(NewDto(field = "value"))
```

**Why:** R8/ProGuard stripping serializer factories causes `SerializationException` at runtime. The smoke test will fail at build time if this happens during testing with minification enabled.

## Rule SR-5: Serializer Conflicts Are Resolved with @SerialName
If a JSON field name differs from the Kotlin property name, use `@SerialName("json_field_name")` on the DTO property. Never use `Json { isLenient = true }` as a workaround for name mismatches.
