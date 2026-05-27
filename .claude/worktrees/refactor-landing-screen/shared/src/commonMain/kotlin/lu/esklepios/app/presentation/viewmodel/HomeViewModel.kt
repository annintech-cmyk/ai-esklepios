package lu.esklepios.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import lu.esklepios.app.domain.model.Practitioner
import lu.esklepios.app.domain.usecase.SearchPractitionersUseCase
import lu.esklepios.app.domain.usecase.ToggleFavoriteUseCase

data class HomeUiState(
    val isLoading: Boolean = false,
    val practitioners: List<Practitioner> = emptyList(),
    val searchQuery: String = "",
    val locationQuery: String = "",
    val selectedFilters: Set<String> = emptySet(),
    val error: String? = null
)

class HomeViewModel(
    private val searchPractitionersUseCase: SearchPractitionersUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        search()
    }

    fun search() {
        val state = _uiState.value
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            searchPractitionersUseCase(state.locationQuery, state.searchQuery)
                .onSuccess { practitioners ->
                    _uiState.update { it.copy(isLoading = false, practitioners = practitioners) }
                }
                .onFailure { throwable ->
                    _uiState.update { it.copy(isLoading = false, error = throwable.message ?: "Search failed") }
                }
        }
    }

    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun updateLocationQuery(location: String) {
        _uiState.update { it.copy(locationQuery = location) }
    }

    fun toggleFilter(filter: String) {
        _uiState.update { state ->
            val newFilters = if (state.selectedFilters.contains(filter)) {
                state.selectedFilters - filter
            } else {
                state.selectedFilters + filter
            }
            state.copy(selectedFilters = newFilters)
        }
    }

    fun toggleFavorite(id: String) {
        viewModelScope.launch {
            toggleFavoriteUseCase(id)
                .onSuccess {
                    _uiState.update { state ->
                        state.copy(
                            practitioners = state.practitioners.map { practitioner ->
                                if (practitioner.id == id) {
                                    practitioner.copy(isFavorite = !practitioner.isFavorite)
                                } else practitioner
                            }
                        )
                    }
                }
                .onFailure { throwable ->
                    _uiState.update { it.copy(error = throwable.message ?: "Failed to update favorite") }
                }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun search(query: String, location: String) {
        updateSearchQuery(query)
        updateLocationQuery(location)
        search()
    }

    fun applyFilter(filter: String) = toggleFilter(filter)

    fun refresh() = search()

    fun loadMore() { /* pagination placeholder */ }

    override fun onCleared() {
        super.onCleared()
    }
}
