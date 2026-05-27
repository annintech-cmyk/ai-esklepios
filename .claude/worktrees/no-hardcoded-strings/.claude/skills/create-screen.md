# Skill: Create Screen

Creates a fully wired screen on both Android and iOS — including the ViewModel, navigation, and test stubs.

## Usage
```
/create-screen <ScreenName> [description]
```
Example: `/create-screen MedicalRecords Show patient medical records list`

## Steps

### 1. Gather Requirements
- What data does the screen display?
- Does it need a new repository / API endpoint?
- What navigation triggers the screen (which screen navigates to it)?
- Is it a top-level tab or a detail/flow screen?

### 2. Shared: Create ViewModel
File: `shared/src/commonMain/kotlin/lu/esklepios/app/presentation/viewmodel/<Name>ViewModel.kt`

```kotlin
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class <Name>UiState(
    val isLoading: Boolean = false,
    val error: String? = null
)

class <Name>ViewModel(
    // inject dependencies
) : ViewModel() {
    private val _uiState = MutableStateFlow(<Name>UiState())
    val uiState: StateFlow<<Name>UiState> = _uiState.asStateFlow()

    fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            // ... repository call
            _uiState.update { it.copy(isLoading = false) }
        }
    }
}
```

Register in `SharedModule.kt`: `factoryOf(::<Name>ViewModel)`

### 3. Android: Add Navigation Destination
In `NavDestination.kt`:
```kotlin
object <Name> : NavDestination("<name_lowercase>")
```

In `AppNavGraph.kt`, inside `NavHost`:
```kotlin
composable(NavDestination.<Name>.route) { <Name>Screen() }
```

### 4. Android: Create Screen
File: `androidApp/src/main/kotlin/lu/esklepios/app/ui/screens/<Name>Screen.kt`

```kotlin
package lu.esklepios.app.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel

@Composable
fun <Name>Screen(
    viewModel: <Name>ViewModel = koinViewModel(),
    onNavigateBack: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    // UI implementation
}
```

### 5. iOS: Create ViewModelWrapper
File: `iosApp/eSklepios/ViewModels/<Name>ViewModelWrapper.swift`

```swift
import Foundation
import shared

class <Name>ViewModelWrapper: ObservableObject {
    let viewModel: <Name>ViewModel
    @Published var uiState: <Name>UiState

    init(viewModel: <Name>ViewModel = KoinHelper.get()) {
        self.viewModel = viewModel
        uiState = viewModel.uiState.value as! <Name>UiState
        viewModel.uiState.watch { [weak self] state in
            guard let state else { return }
            DispatchQueue.main.async { self?.uiState = state }
        }
    }
}
```

### 6. iOS: Create View
File: `iosApp/eSklepios/Views/<Name>View.swift`

```swift
import SwiftUI

struct <Name>View: View {
    @StateObject private var viewModel = <Name>ViewModelWrapper()

    var body: some View {
        // UI implementation
        .task { viewModel.viewModel.load() }
        .navigationTitle("<Screen Title>")
    }
}

#Preview {
    <Name>View()
}
```

### 7. Add Navigation in AppTabView or Parent View
Wire `navigationDestination(for:)` with the new view.

### 8. Localization
Add keys to `strings/twine.txt`, then run `make strings`.

### 9. Tests
- Android: `androidApp/src/test/kotlin/lu/esklepios/app/<Name>ViewModelTest.kt`
- iOS: `iosApp/eSklepiosTests/<Name>ViewModelTests.swift`
