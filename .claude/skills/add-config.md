# Skill: Add Build Config Value

Adds a new compile-time configuration value accessible via `BuildKonfig` in shared Kotlin code.

## Usage
```
/add-config <KEY_NAME> <description>
```
Example: `/add-config ANALYTICS_ENABLED Flag to gate analytics SDK initialization`

## Steps

### 1. Add to dev.properties
```
ANALYTICS_ENABLED=true
```

### 2. Add to prod.properties
```
ANALYTICS_ENABLED=false
```

### 3. Declare in shared/build.gradle.kts
Inside the `buildkonfig { defaultConfigs { } }` block:
```kotlin
booleanField("ANALYTICS_ENABLED", devProperties.getProperty("ANALYTICS_ENABLED", "false").toBoolean())
```

For a string field:
```kotlin
stringField("FEATURE_FLAG_URL", devProperties.getProperty("FEATURE_FLAG_URL", ""))
```

### 4. Regenerate
```bash
./gradlew :shared:generateBuildKonfig
```

### 5. Use in Kotlin
```kotlin
import lu.esklepios.app.BuildKonfig

if (BuildKonfig.ANALYTICS_ENABLED) {
    // initialize analytics
}
```

## Supported Field Types
| Type | Builder method |
|------|---------------|
| String | `stringField("KEY", value)` |
| Boolean | `booleanField("KEY", value)` |
| Int | `intField("KEY", value)` |
| Long | `longField("KEY", value)` |

## Notes
- `dev.properties` is NOT committed (in `.gitignore`).
- Never hardcode secrets — they must come from BuildKonfig or runtime API calls.
- On iOS, `BuildKonfig` is generated as a Kotlin class accessible via the shared framework — no equivalent Swift file is needed.
