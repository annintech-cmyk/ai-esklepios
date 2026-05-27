# Date & Time Rules

## Rule DT-1: All Date/Time Logic Goes Through DateUtil (Android)
`androidApp/src/main/kotlin/lu/esklepios/app/utils/DateUtil.kt` is the single source of truth for all date and time operations on Android. No date formatting, slot-ID parsing, or `LocalDate.now()` calls may appear outside of `DateUtil`.

| Forbidden in UI/component/debug code | Required replacement |
|---|---|
| `LocalDate.now()` | `DateUtil.today()` |
| `DateTimeFormatter.ofPattern("...", Locale.ENGLISH)` | `DateUtil.formatIsoDate(date, DateUtil.PATTERN_*)` |
| `slotId.split("_").last()` time parsing | `DateUtil.extractSlotTime(slotId)` |
| `"${compact.substring(0,4)}-${compact.substring(4,6)}-..."` | `DateUtil.compactToIso(compactDate)` |

**Enforcement (run before merging):**
```bash
# Should return 0
grep -rEn 'LocalDate\.now\(\)|DateTimeFormatter\.ofPattern' \
  androidApp/src/main/kotlin --include="*.kt" \
  | grep -v "/utils/DateUtil"
```

## Rule DT-2: DateUtil Is Android-Only
`DateUtil` uses `java.time.*` and lives in `androidApp`. It must **never** be imported into `shared/src/commonMain/`. Shared ViewModels use `kotlinx.datetime` (KMM-compatible) directly — `Clock.System.now()`, `kotlinx.datetime.LocalDate`, and `daysUntil()`.

## Rule DT-3: Add Constants for New Format Patterns
When a new date format string is needed, add a named constant to `DateUtil` — never inline a format string at a call site.

```kotlin
// FORBIDDEN
DateUtil.formatIsoDate(date, "dd/MM/yyyy")

// REQUIRED — add to DateUtil first
const val PATTERN_DISPLAY_SHORT = "dd/MM/yyyy"
// then call:
DateUtil.formatIsoDate(date, DateUtil.PATTERN_DISPLAY_SHORT)
```

## Rule DT-4: No Duplicate Slot-Parsing Functions
`extractSlotTime` is defined once in `DateUtil`. Do not add equivalent private functions to screens or components (e.g. `extractSlotTime`, `extractTimeLabel`, `getTimeFromSlot`). Delete any such function the moment it appears and redirect to `DateUtil`.

## Rule DT-5: All Date/Time Logic Goes Through DateUtil (iOS)
`iosApp/eSklepios/Core/Utils/DateUtil.swift` is the single source of truth for all date and time operations on iOS. No inline `DateFormatter`, `Calendar.current`, or `Date()` calls may appear outside of `DateUtil.swift`.

| Forbidden in UI/component code | Required replacement |
|---|---|
| `DateFormatter(); f.dateFormat = "yyyy-MM-dd"` | `DateUtil.dateToIso(_:)` / `DateUtil.isoToDate(_:)` |
| `DateFormatter(); f.dateFormat = "EEE"` | `DateUtil.formatIsoDate(_:pattern:)` with `DateUtil.PATTERN_DAY_ABBR` |
| `Date()` to get today | `DateUtil.today()` |
| `Calendar.current.date(byAdding:...)` in UI | `DateUtil.weekDays(from:count:)` |
| Inline `slotId.split("_").last` time parsing | `DateUtil.extractSlotTime(_:)` |
| Inline `"20260526"` → `"2026-05-26"` conversion | `DateUtil.compactToIso(_:)` |

**Enforcement (run before merging):**
```bash
# Should return 0
grep -rEn 'DateFormatter\(\)|Calendar\.current|Date\(\)' \
  iosApp/eSklepios --include="*.swift" \
  | grep -v "Core/Utils/DateUtil"
```

## Rule DT-6: DateUtil.swift Location
`DateUtil.swift` lives at `iosApp/eSklepios/Core/Utils/DateUtil.swift`. It is the iOS-only counterpart to `DateUtil.kt` — it may use `Foundation` (`DateFormatter`, `Calendar`, `Date`) freely. It must **never** be imported into the shared KMM module.

## Rule DT-7: iOS Format Pattern Constants
All format strings are defined as `static let` constants in `DateUtil`. Never inline a format string at a call site.

```swift
// FORBIDDEN
let f = DateFormatter()
f.dateFormat = "yyyy-MM-dd"

// REQUIRED — use named constant
DateUtil.isoToDate(isoString)   // or DateUtil.dateToIso(date)
```

When a new pattern is needed, add it to `DateUtil` first:
```swift
// Add to DateUtil:
static let PATTERN_DISPLAY_SHORT = "dd/MM/yyyy"

// Then call:
DateUtil.formatIsoDate(isoDate, pattern: DateUtil.PATTERN_DISPLAY_SHORT)
```

## Rule DT-8: No Duplicate Slot-Parsing in iOS
`DateUtil.extractSlotTime(_:)` is defined once. Do not add private `extractTime`, `getSlotTime`, or `timeFromSlotId` functions in any View or ViewModelWrapper. Delete any such function and redirect to `DateUtil`.

---

## Current DateUtil API

### Android — `androidApp/.../utils/DateUtil.kt`
```kotlin
object DateUtil {
    const val PATTERN_DISPLAY_FULL = "EEE, MMM d, yyyy"   // "Tue, May 26, 2026"
    const val PATTERN_DISPLAY_LONG = "EEEE, MMMM d"        // "Tuesday, May 26"

    fun extractSlotTime(slotId: String): String             // "slot_d1_20260526_0830" → "08:30"
    fun compactToIso(compactDate: String): String           // "20260526" → "2026-05-26"
    fun formatIsoDate(isoDate: String, pattern: String, locale: Locale = Locale.ENGLISH): String
    fun today(): LocalDate
}
```

### iOS — `iosApp/eSklepios/Core/Utils/DateUtil.swift`
```swift
enum DateUtil {
    static let PATTERN_ISO        = "yyyy-MM-dd"
    static let PATTERN_DAY_ABBR   = "EEE"            // "Tue"
    static let PATTERN_DISPLAY_FULL = "EEE, MMM d, yyyy"  // "Tue, May 26, 2026"
    static let PATTERN_DISPLAY_LONG = "EEEE, MMMM d"       // "Tuesday, May 26"

    static func today() -> Date
    static func todayKey() -> String                   // today as "yyyy-MM-dd"
    static func extractSlotTime(_ slotId: String) -> String  // "slot_d1_20260526_0830" → "08:30"
    static func compactToIso(_ compact: String) -> String    // "20260526" → "2026-05-26"
    static func isoToDate(_ isoDate: String) -> Date?
    static func dateToIso(_ date: Date) -> String
    static func formatIsoDate(_ isoDate: String, pattern: String) -> String
    static func weekDays(startingFrom date: Date, count: Int) -> [Date]
}
```
