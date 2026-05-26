# Coroutine Rules

## Rule CR-1: ViewModels Launch Only from viewModelScope
All coroutine launches in ViewModels must use `viewModelScope.launch { }`. Never create a `CoroutineScope` manually inside a ViewModel — it won't be cancelled when the ViewModel is cleared.

```kotlin
// CORRECT
fun load() {
    viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true) }
        ...
    }
}

// FORBIDDEN
val scope = CoroutineScope(Dispatchers.Main)
fun load() { scope.launch { ... } }
```

## Rule CR-2: CancellationException Must Always Be Rethrown
Any `catch` clause that catches `Exception` or `Throwable` must check for and rethrow `CancellationException`. **See Rule EH-2** for the detailed pattern and `safeCall` context.

Prefer `runCatching { }` (Rule EH-1) to avoid this pattern entirely.

## Rule CR-3: Use StateFlow, Never MutableStateFlow in Public API
See Rule SM-2. Public-facing state is always `StateFlow<UiState>`. Mutable backing field is always prefixed with `_`.

## Rule CR-4: Flow Collection in Tests Uses Turbine
In Android unit tests that test `StateFlow` emissions, use the Turbine library:
```kotlin
viewModel.uiState.test {
    awaitItem() // initial state
    viewModel.load()
    val loading = awaitItem()
    assertTrue(loading.isLoading)
    cancelAndIgnoreRemainingEvents()
}
```
In commonTest, use `StandardTestDispatcher` + `advanceUntilIdle()` and read `.value` directly.

## Rule CR-5: SupervisorJob in FlowWatcher Scope
`FlowWatcher` uses `CoroutineScope(Dispatchers.Main + SupervisorJob())` so that a failure in one collected Flow does not cancel the entire scope. Do not use `CoroutineScope(Dispatchers.Main)` without `SupervisorJob` for long-lived observation scopes.

## Rule CR-6: No GlobalScope
`GlobalScope` must never be used. It bypasses structured concurrency and leaks coroutines. Use `viewModelScope` in ViewModels, explicit `CoroutineScope` with lifecycle in `FlowWatcher`, and `TestScope` in tests.
