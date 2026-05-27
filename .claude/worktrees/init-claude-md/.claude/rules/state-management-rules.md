# State Management Rules

## Kotlin / Shared ViewModels

### Rule SM-1: Single Source of Truth
Each screen has exactly one UiState data class. All state for that screen lives in it. No separate `MutableState` variables alongside `_uiState`.

### Rule SM-2: Private MutableStateFlow, Public StateFlow
```kotlin
// CORRECT
private val _uiState = MutableStateFlow(FooUiState())
val uiState: StateFlow<FooUiState> = _uiState.asStateFlow()

// FORBIDDEN
val uiState = MutableStateFlow(FooUiState())  // exposed mutable
```

### Rule SM-3: Always Use .update { }
```kotlin
// CORRECT
_uiState.update { it.copy(isLoading = true) }

// FORBIDDEN — not thread-safe
_uiState.value = _uiState.value.copy(isLoading = true)
```

### Rule SM-4: UiState is Immutable
UiState data classes use `val` properties only. Never `var` inside a UiState.

### Rule SM-5: Error Field is Nullable String
```kotlin
data class FooUiState(
    val isLoading: Boolean = false,
    val error: String? = null,         // null = no error
    val data: List<Item> = emptyList()
)
```

### Rule SM-6: Clear Error on New Action
```kotlin
fun load() {
    viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true, error = null) }  // clear error
        // ...
    }
}
```

### Rule SM-7: No Shared Mutable State Between ViewModels
ViewModels do not reference each other. Shared state is managed through a repository (single source of truth at the data layer).

## Android Compose

### Rule SM-8: Collect with Lifecycle Awareness
```kotlin
// CORRECT
val uiState by viewModel.uiState.collectAsStateWithLifecycle()

// AVOID in production (not lifecycle-aware)
val uiState by viewModel.uiState.collectAsState()
```

### Rule SM-9: Side Effects in LaunchedEffect
One-time loads and side effects go in `LaunchedEffect`:
```kotlin
LaunchedEffect(Unit) {
    viewModel.load()
}

// For navigation side effects
LaunchedEffect(uiState.isLoggedOut) {
    if (uiState.isLoggedOut) navController.navigate(NavDestination.Landing.route)
}
```

### Rule SM-10: User Actions in Event Callbacks
Buttons and user inputs call ViewModel methods directly:
```kotlin
Button(onClick = { viewModel.doSomething() }) { ... }
```
Do NOT call async operations directly from `onClick` lambdas. Let the ViewModel handle coroutines.

## iOS (SwiftUI + ViewModelWrapper)

### Rule SM-11: @StateObject for ViewModelWrappers
```swift
// CORRECT — view owns the lifecycle
@StateObject private var viewModel = HomeViewModelWrapper()

// FORBIDDEN at root view level
@ObservedObject var viewModel = HomeViewModelWrapper()  // will be recreated
```

Pass down as `@ObservedObject` to child views that do NOT own the ViewModel.

### Rule SM-12: Main Thread UI Updates
All `@Published` property updates from KMM StateFlow must be dispatched to main:
```swift
viewModel.uiState.watch { [weak self] state in
    guard let state else { return }
    DispatchQueue.main.async {
        self?.uiState = state
    }
}
```

### Rule SM-13: Use .task for Async Loading
```swift
.task {
    viewModel.viewModel.load()
}
```
Prefer `.task` over `.onAppear` for async operations — `.task` is automatically cancelled when the view disappears.
