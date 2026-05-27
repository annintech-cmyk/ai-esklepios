# Architecture Rules

These rules are enforced across all code in eSklepios. Violations should be flagged during code review and corrected before merging.

## Rule A-1: Strict Layer Separation
Dependencies must only point inward (toward the domain). Outer layers import inner layers, never the reverse.

**Allowed:**
- ViewModel imports from domain (model, repository interface, use case)
- Data layer imports from domain interfaces
- Android/iOS screens import from ViewModels

**Forbidden:**
- Domain importing from data (`ApiServiceImpl`, SQLDelight queries)
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
- iOS Koin module (initialized in `KoinHelper.swift`)

No `get()` calls outside of Koin module lambdas or `KoinHelper.get()` on iOS.
