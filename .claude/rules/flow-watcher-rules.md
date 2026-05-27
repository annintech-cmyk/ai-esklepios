# FlowWatcher Rules

## Rule FW-1: All iOS ViewModelWrappers Must Use FlowWatcher and @MainActor
Every `*ViewModelWrapper` class in iOS must:
1. Be annotated with `@MainActor`
2. Hold a `private var stateObserver: FlowWatcher?`
3. Start observation in `init` via `FlowExtensionsKt.watch(...)`
4. Call `stateObserver?.close()` in `deinit`

```swift
@MainActor
class FooViewModelWrapper: ObservableObject {
    let viewModel: FooViewModel
    @Published var uiState: FooUiState
    private var stateObserver: FlowWatcher?

    init(viewModel: FooViewModel = KoinHelper.get()) {
        self.viewModel = viewModel
        uiState = viewModel.uiState.value as! FooUiState
        stateObserver = FlowExtensionsKt.watch(viewModel.uiState) { [weak self] state in
            guard let state = state as? FooUiState else { return }
            Task { @MainActor [weak self] in
                self?.uiState = state
            }
        }
    }

    deinit {
        stateObserver?.close()
    }
}
```

## Rule FW-2: Never Use DispatchQueue.main.async in ViewModelWrappers
`DispatchQueue.main.async { }` is replaced by `Task { @MainActor [weak self] in ... }` inside the observation callback. The `@MainActor` annotation guarantees all `@Published` mutations happen on the main actor.

## Rule FW-3: FlowWatcher Is Defined Once in FlowExtensions.kt
`FlowWatcher` and the `watch()` extension are defined in `shared/src/commonMain/.../util/FlowExtensions.kt`. Do not define equivalent cancellable-observation helpers elsewhere.

```kotlin
class FlowWatcher(private val scope: CoroutineScope) {
    fun close() = scope.cancel()
}

fun <T> StateFlow<T>.watch(onChange: (T) -> Unit): FlowWatcher {
    val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    scope.launch { collect { onChange(it) } }
    return FlowWatcher(scope)
}
```

## Rule FW-4: SwiftUI Views Use @StateObject at the Ownership Level
Only the view that owns the wrapper lifecycle uses `@StateObject`. Pass to child views as `@ObservedObject` (Rule SM-11 still applies).
