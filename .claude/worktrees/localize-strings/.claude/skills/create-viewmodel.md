# Skill: Create ViewModel

Creates a shared KMM ViewModel with its UiState, Koin registration, and a companion iOS ViewModelWrapper.

## Usage
```
/create-viewmodel <Name> [description]
```
Example: `/create-viewmodel MedicalRecords List and filter patient medical records`

## Steps

### 1. Create the ViewModel
File: `shared/src/commonMain/kotlin/lu/esklepios/app/presentation/viewmodel/<Name>ViewModel.kt`

```kotlin
package lu.esklepios.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import lu.esklepios.app.domain.repository.<Dependency>Repository

data class <Name>UiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    // Add domain data fields here
)

class <Name>ViewModel(
    private val repository: <Dependency>Repository
) : ViewModel() {

    private val _uiState = MutableStateFlow(<Name>UiState())
    val uiState: StateFlow<<Name>UiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            repository.getData()
                .onSuccess { data ->
                    _uiState.update { it.copy(isLoading = false, data = data) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message ?: "Unknown error") }
                }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
```

### 2. Register in SharedModule.kt
In `shared/src/commonMain/kotlin/lu/esklepios/app/di/SharedModule.kt`:
```kotlin
factoryOf(::<Name>ViewModel)
```

### 3. Create iOS ViewModelWrapper
File: `iosApp/eSklepios/ViewModels/<Name>ViewModelWrapper.swift`

```swift
import Foundation
import shared

class <Name>ViewModelWrapper: ObservableObject {
    let viewModel: <Name>ViewModel
    @Published var uiState: <Name>UiState

    init(viewModel: <Name>ViewModel = KoinHelper.get()) {
        self.viewModel = viewModel
        self.uiState = viewModel.uiState.value as! <Name>UiState
        viewModel.uiState.watch { [weak self] state in
            guard let state else { return }
            DispatchQueue.main.async {
                self?.uiState = state
            }
        }
    }

    func load() {
        viewModel.load()
    }

    func clearError() {
        viewModel.clearError()
    }
}
```

## ViewModel Checklist
- [ ] Extends `ViewModel()` from `androidx.lifecycle`
- [ ] `_uiState` is private `MutableStateFlow<UiState>`
- [ ] `uiState` is public `StateFlow<UiState>` via `.asStateFlow()`
- [ ] All mutations use `_uiState.update { it.copy(...) }`
- [ ] Long operations in `viewModelScope.launch { }`
- [ ] UiState has `isLoading: Boolean = false` and `error: String? = null`
- [ ] Error is cleared before starting new load
- [ ] Registered with `factoryOf` in SharedModule.kt
- [ ] iOS ViewModelWrapper created with `@Published var uiState` and `.watch { }` observer

## Common UiState Patterns

### List screen
```kotlin
data class <Name>UiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val items: List<Item> = emptyList(),
    val isEmpty: Boolean = false
)
```

### Detail screen
```kotlin
data class <Name>UiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val item: Item? = null
)
```

### Form screen
```kotlin
data class <Name>UiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val fieldValue: String = "",
    val validationError: String? = null,
    val isSuccess: Boolean = false
)
```
