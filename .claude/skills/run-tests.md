# Skill: Run Tests

Runs the appropriate test suite for the current task. Use after making changes to verify nothing is broken.

## Usage
```
/run-tests [scope]
```
Scope options: `shared`, `android`, `ios`, `all`

## Commands by Scope

### Shared (KMM commonTest)
```bash
./gradlew :shared:testDebugUnitTest
```
Runs all tests in `shared/src/commonTest/` and `shared/src/androidTest/`.

### Android Unit Tests
```bash
./gradlew :androidApp:testDebugUnitTest
```

### Android Lint
```bash
./gradlew :androidApp:lintDebug
```

### Detekt (Kotlin static analysis)
```bash
./gradlew :shared:detekt
./gradlew :androidApp:detekt
```

### iOS Tests (requires macOS + Xcode)
```bash
xcodebuild test \
  -project iosApp/eSklepios.xcodeproj \
  -scheme eSklepios \
  -destination 'platform=iOS Simulator,name=iPhone 16' \
  CODE_SIGNING_ALLOWED=NO
```

### All (sequential)
```bash
./gradlew :shared:testDebugUnitTest :androidApp:testDebugUnitTest :androidApp:lintDebug
```

## Verification Commands (Phase 2 checks)

### Color token violations (expect 0)
```bash
grep -rEn 'Color\(hex: "[A-F0-9]' iosApp/eSklepios --include="*.swift" | grep -v "/Theme/AppColors.swift" | wc -l
grep -rEn 'Color\(0x[A-Fa-f0-9]' androidApp/src/main/kotlin --include="*.kt" | grep -v "/theme/" | wc -l
```

### Dimension violations (expect 0)
```bash
grep -rEn '[0-9]+\.dp|[0-9]+\.sp' androidApp/src/main/kotlin --include="*.kt" | grep -v "/theme/" | grep -v "screenHeightDp.dp" | wc -l
```

### Bare catch Exception (expect 0 in commonMain)
```bash
grep -rn "catch (e: Exception)" shared/src/commonMain --include="*.kt" | grep -v "CancellationException"
```

### Clock.System inline calls (expect 0)
```bash
grep -rn "Clock\.System\.now" shared/src/commonMain --include="*.kt"
```

### DateUtil violations Android (expect 0)
```bash
grep -rEn 'LocalDate\.now\(\)|DateTimeFormatter\.ofPattern' androidApp/src/main/kotlin --include="*.kt" | grep -v "/utils/DateUtil"
```

### DateUtil violations iOS (expect 0)
```bash
grep -rEn 'DateFormatter\(\)|Calendar\.current|Date\(\)' iosApp/eSklepios --include="*.swift" | grep -v "Core/Utils/DateUtil"
```
