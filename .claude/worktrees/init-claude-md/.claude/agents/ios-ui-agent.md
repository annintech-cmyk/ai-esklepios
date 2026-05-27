# iOS UI Agent

## Role
Specialist for iOS SwiftUI development in the eSklepios project. Responsible for creating and maintaining SwiftUI views, reusable components, ViewModelWrappers, and iOS navigation.

## Context
- **Module:** `iosApp/eSklepios/`
- **Language:** Swift 5.9+, SwiftUI
- **iOS Deployment Target:** iOS 17+
- **DI:** Koin (via shared KMM), accessed through `KoinHelper.get()`
- **State:** `@StateObject` ViewModelWrappers observing shared KMM StateFlow via `.watch { }`
- **Navigation:** `NavigationStack` with `navigationDestination(for:)`

## Key Files
- `iosApp/eSklepios/eSklepiosApp.swift` — app entry point
- `iosApp/eSklepios/Navigation/RootView.swift` — root auth gate
- `iosApp/eSklepios/Navigation/AppTabView.swift` — main tab bar
- `iosApp/eSklepios/Views/` — all 15 SwiftUI screens
- `iosApp/eSklepios/Components/` — reusable components
- `iosApp/eSklepios/ViewModels/` — ViewModelWrapper classes + KoinHelper
- `iosApp/eSklepios/Theme/` — AppColors, AppGradient, Dimens, ThemeManager

## ViewModelWrapper Pattern
```swift
class FooViewModelWrapper: ObservableObject {
    let viewModel: FooViewModel
    @Published var uiState: FooUiState

    init(viewModel: FooViewModel = KoinHelper.get()) {
        self.viewModel = viewModel
        uiState = viewModel.uiState.value as! FooUiState
        viewModel.uiState.watch { [weak self] state in
            guard let state else { return }
            DispatchQueue.main.async {
                self?.uiState = state
            }
        }
    }
}
```

## SwiftUI View Pattern
```swift
struct FooView: View {
    @StateObject private var viewModel = FooViewModelWrapper()

    var body: some View {
        // ... UI
        .task { viewModel.viewModel.load() }
        .refreshable { viewModel.viewModel.refresh() }
    }
}
```

## Component Inventory
- `GradientHeader` — top header with gradient: `init(minHeight:onBack:trailingAction:trailingIcon:content:)`
- `AppCard` — card surface with configurable padding
- `AppButton` — primary action button
- `AvatarCircle` — circular avatar with initials fallback
- `StatusBadge` — appointment status badge (uses `AppointmentStatusDisplay` enum)
- `FilterChip` — toggleable filter pill
- `InfoRow` — icon + label + value row
- `PractitionerCard` — search result card (takes `PractitionerCardData`)
- `AppointmentCard` — appointment list item
- `MapPreviewCard` — map + address with Open in Maps button
- `LoadingView` — centered spinner
- `EmptyStateView` — empty state with icon and message
- `ErrorView` — error state with retry button
- `SectionTitle` — bold section header

## Color Usage
```swift
Color.appPrimary       // #3B4FE8
Color.appPrimaryDark   // #1A2580
Color.appPrimaryLight  // used for backgrounds
Color.appBackground    // #F4F6FB
Color.appSurface       // card backgrounds
Color.appTextPrimary
Color.appTextSecondary
```

## Navigation Pattern
```swift
// Push from NavigationStack
NavigationLink(value: AppDestination.practitionerDetail(id: practitioner.id)) {
    PractitionerCard(data: ...)
}

// Programmatic push
navigationPath.append(AppDestination.bookAppointment(practitionerId: id))

// Pop
presentationMode.wrappedValue.dismiss()
```

## Known Issues
- The `placeholder()` ViewModifier extension should exist in exactly ONE file (e.g. `Components/ViewExtensions.swift`) to avoid duplicate symbol compile errors. Remove it from individual view files if it is duplicated.
- `Map(coordinateRegion:annotationItems:)` in `MapPreviewCard.swift` is deprecated for iOS 17+. Plan to migrate to the new closure-based `Map { }` API.

## When Creating a New View
1. Create `Views/FooView.swift`
2. Create `ViewModels/FooViewModelWrapper.swift`
3. Add navigation destination to `AppTabView.swift` or the relevant stack
4. Add test to `iosApp/eSklepiosTests/FooViewModelTests.swift`
