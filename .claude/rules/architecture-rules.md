# Architecture Rules

These rules are enforced across all code in eSklepios. Violations should be flagged during code review and corrected before merging.

## Rule A-1: Strict Layer Separation
Dependencies must only point inward (toward the domain). Outer layers import inner layers, never the reverse.

**Allowed:**
- ViewModel imports from domain (model, repository interface, use case)
- Data layer imports from domain interfaces
- Android/iOS screens import from ViewModels

**Forbidden:**
- Domain importing from data (`KtorApiService`, SQLDelight queries)
- ViewModel importing Ktor, SQLDelight, Compose, SwiftUI
- Repository interface importing implementation details

## Rule A-2: No Platform Code in commonMain
`commonMain` source files must only use Kotlin stdlib, KotlinX libraries, Koin core, and the project's own domain/data interfaces. No:
- `android.*` imports
- `UIKit` / `SwiftUI` references
- `java.*` imports (use KotlinX DateTime instead of `java.util.Date`)

## Rule A-3: ViewModel Owns No UI State Objects
ViewModels hold only serializable, platform-agnostic UiState. They must not hold:
- `Context` or `Activity` references
- `Bitmap`, `Drawable`, `UIImage`
- Compose `State<T>` or SwiftUI `Binding<T>`

## Rule A-4: Single Responsibility per Use Case
Each use case class does exactly one thing. Its `invoke` operator is the only public method.

## Rule A-5: Repository Interface in Domain, Implementation in Data
- Interface: `shared/src/commonMain/.../domain/repository/FooRepository.kt`
- Implementation: `shared/src/commonMain/.../data/repository/FooRepositoryImpl.kt`

## Rule A-6: One ViewModel per Screen
Do not share a single ViewModel across multiple screens. If two screens share data, extract a shared repository or use case.

## Rule A-7: StateFlow, Never LiveData
All shared ViewModels use `StateFlow<UiState>`. `LiveData` is forbidden in commonMain.

## Rule A-8: Koin Registrations in Module Files Only
All DI registrations must be in:
- `shared/src/commonMain/.../di/SharedModule.kt`
- `androidApp/.../di/AndroidModule.kt`
- `shared/src/iosMain/.../di/IosModule.kt` (the iOS-specific Kotlin module),
  bootstrapped via `shared/src/iosMain/.../di/IosKoinInit.kt`'s `doInitKoin(baseUrl, enableLogging)` function, which is invoked from `iosApp/eSklepios/Core/DI/KoinHelper.swift` at app launch.

No `get()` calls outside of Koin module lambdas or `KoinHelper.get()` on iOS.

**See also:** Clock injection (Rule A-11 in `.claude/rules/clock-rules.md`) — `Clock.System` is registered here as a singleton.

## Rule A-9: Validation Logic Lives in Shared Utilities
Email validation, password validation, password-strength calculation, phone validation, CNS number validation, and other reusable business rules must live in shared util classes (e.g., `shared/src/commonMain/.../util/Validators.kt`).

Screens, SwiftUI Views, Composables, and ViewModels must **call** the utility — never re-implement the same logic inline.

**Forbidden:**
```kotlin
// In ChangeEmailViewModel.kt
private fun isValidEmail(email: String) = email.contains("@") && ...
```
**Required:**
```kotlin
// In shared util
fun isValidEmail(email: String): Boolean = ...
// In ViewModel
if (!isValidEmail(state.newEmail)) { ... }
```

## Rule A-10: Form Options Are Centralized
Static option lists must never be hardcoded inside screens. Options that appear on multiple screens or platforms must be defined once in a shared source of truth.

**Examples requiring centralization:**
- Language codes + display labels
- Country/dial-code pairs
- Gender options
- Date filter labels (`"All"`, `"Today"`, `"Within 3 Days"`)
- Appointment status display labels
- Payment method options

**Location:** `shared/src/commonMain/.../util/Options.kt` or a domain-specific equivalent (e.g., `AppointmentOptions`, `ProfileOptions`).

**Forbidden:** Defining `private val languages = listOf(...)` inside a SwiftUI `View` or Composable screen when those same options are needed in the other platform or in another screen.

## Rule A-11: Formatting Logic Is Centralized
Formatting and display helpers must live in dedicated utilities. Inline formatting inside screens is forbidden.

| Logic | Location |
|---|---|
| CNS number masking | `shared/.../util/Formatters.kt` or `DateUtil` |
| Phone number formatting | `shared/.../util/Formatters.kt` |
| Date display formatting | `DateUtil.kt` (Android) / `DateUtil.swift` (iOS) |
| Currency / price formatting | `shared/.../util/Formatters.kt` |
| Name formatting (initials, full name) | `User.fullName` / `User.initials` computed properties |
| Password strength calculation | `shared/.../util/Validators.kt` |

**Forbidden:**
```swift
// In ProfileView.swift
private var maskedCns: String {
    guard let cns = viewModel.uiState.user?.cnsNumber, !cns.isEmpty else { return "—" }
    return cns.count > 9 ? "\(cns.prefix(9)) ••••" : cns
}
```
**Required:** Move to `Formatters.kt` / `Formatters.swift` and call it from the View.

## Rule A-12: Shared First
If logic is required by both Android and iOS, it must be implemented in `shared/commonMain` first. Platform-specific implementations are allowed only for:
- Platform APIs (Keychain, EncryptedSharedPreferences)
- UI rendering (Composable, SwiftUI View)
- Platform display helpers that wrap shared logic

**Examples of logic that belongs in shared:**
- Input validation
- Business rule enforcement
- Date arithmetic
- String sanitization
- Option/enum lists

## Rule A-13: Promote Repeated Patterns
When a refactor introduces a reusable architectural pattern, update the relevant rule or skill file without waiting to be asked:
- **Permanent constraint** → update or add a rule in `.claude/rules/`
- **Repeatable workflow** → update or add a skill in `.claude/skills/`

**Guide:**
| Pattern | Destination |
|---|---|
| No hardcoded strings / colors / dimensions | Rule |
| Validation must be centralized | Rule |
| Shared-first KMP logic | Rule |
| Formatting must use utility | Rule |
| Rebuild screen process | Skill |
| Utility extraction workflow | Skill |
| Design system enforcement | Rule |
| How to migrate a screen | Skill |
