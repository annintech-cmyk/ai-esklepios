package lu.esklepios.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.daysUntil
import kotlinx.datetime.toLocalDateTime
import lu.esklepios.app.domain.model.Practitioner
import lu.esklepios.app.domain.usecase.SearchPractitionersUseCase
import lu.esklepios.app.domain.usecase.ToggleFavoriteUseCase
import lu.esklepios.app.util.DateFilter

data class HomeUiState(
    val isLoading: Boolean = false,
    val hasSearched: Boolean = false,
    /** Full unfiltered list returned by the search use case. */
    val allPractitioners: List<Practitioner> = emptyList(),
    /** Date + newPatients filtered list — keyword filtering applied on top via filteredPractitioners. */
    val practitioners: List<Practitioner> = emptyList(),
    val specialtyQuery: String = "",
    val locationQuery: String = "",
    val selectedDateFilter: String = DateFilter.ALL.apiKey,
    val openToNewPatients: Boolean = false,
    val error: String? = null
)

class HomeViewModel(
    private val searchPractitionersUseCase: SearchPractitionersUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val clock: Clock = Clock.System
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        search()
    }

    /** Keyword-filtered view: date+newPatients-filtered practitioners further narrowed by specialty/location text. */
    val filteredPractitioners: List<Practitioner>
        get() {
            val state = _uiState.value
            val sq = state.specialtyQuery.trim().lowercase()
            val lq = state.locationQuery.trim().lowercase()
            return state.practitioners.filter { p ->
                val matchSpec = sq.isEmpty() ||
                    p.specialty.lowercase().contains(sq) ||
                    p.firstName.lowercase().contains(sq) ||
                    p.lastName.lowercase().contains(sq) ||
                    p.clinicName.lowercase().contains(sq)
                val matchLoc = lq.isEmpty() ||
                    p.address.lowercase().contains(lq) ||
                    p.city.lowercase().contains(lq) ||
                    p.clinicName.lowercase().contains(lq)
                matchSpec && matchLoc
            }
        }

    fun search() {
        val state = _uiState.value
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            searchPractitionersUseCase(state.locationQuery, state.specialtyQuery)
                .onSuccess { practitioners ->
                    _uiState.update { current ->
                        val filtered = applyFilters(
                            practitioners,
                            current.selectedDateFilter,
                            current.openToNewPatients
                        )
                        current.copy(
                            isLoading = false,
                            allPractitioners = practitioners,
                            practitioners = filtered
                        )
                    }
                }
                .onFailure { throwable ->
                    _uiState.update { it.copy(isLoading = false, error = throwable.message ?: "Search failed") }
                }
        }
    }

    /**
     * Applies the active date filter and new-patients flag to [all] and returns
     * the filtered subset.
     */
    private fun applyFilters(
        all: List<Practitioner>,
        dateFilter: String,
        newPatientsOnly: Boolean
    ): List<Practitioner> {
        val today = clock.now()
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .date

        return all.filter { p ->
            val passesNewPatients = !newPatientsOnly || p.acceptingNewPatients
            val passesDate = when (dateFilter) {
                DateFilter.TODAY.apiKey -> p.availableSlots.any { slot ->
                    slot.available && slot.dateTime.take(10) == today.toString()
                }
                DateFilter.WITHIN_3_DAYS.apiKey -> {
                    p.availableSlots.any { slot ->
                        if (!slot.available) return@any false
                        val slotDate = runCatching { LocalDate.parse(slot.dateTime.take(10)) }.getOrNull()
                            ?: return@any false
                        val daysAhead = today.daysUntil(slotDate)
                        daysAhead in 0..3
                    }
                }
                else -> true // DateFilter.ALL
            }
            passesNewPatients && passesDate
        }
    }

    /**
     * Re-applies the current filters whenever the filter state changes.
     */
    fun applyFiltersToState() {
        _uiState.update { current ->
            current.copy(
                practitioners = applyFilters(
                    current.allPractitioners,
                    current.selectedDateFilter,
                    current.openToNewPatients
                )
            )
        }
    }

    fun onSpecialtyQueryChange(value: String) {
        _uiState.update { it.copy(specialtyQuery = value) }
    }

    fun onLocationQueryChange(value: String) {
        _uiState.update { it.copy(locationQuery = value) }
    }

    fun updateSearchQuery(query: String) = onSpecialtyQueryChange(query)
    fun updateLocationQuery(location: String) = onLocationQueryChange(location)

    fun setDateFilter(filter: String) {
        _uiState.update { current ->
            val filtered = applyFilters(current.allPractitioners, filter, current.openToNewPatients)
            current.copy(selectedDateFilter = filter, practitioners = filtered)
        }
    }

    fun toggleNewPatientsFilter() {
        _uiState.update { current ->
            val newFlag = !current.openToNewPatients
            val filtered = applyFilters(current.allPractitioners, current.selectedDateFilter, newFlag)
            current.copy(openToNewPatients = newFlag, practitioners = filtered)
        }
    }

    fun toggleFavorite(id: String) {
        viewModelScope.launch {
            toggleFavoriteUseCase(id)
                .onSuccess {
                    _uiState.update { state ->
                        val updatedAll = state.allPractitioners.map { p ->
                            if (p.id == id) p.copy(isFavorite = !p.isFavorite) else p
                        }
                        state.copy(
                            allPractitioners = updatedAll,
                            practitioners = applyFilters(
                                updatedAll,
                                state.selectedDateFilter,
                                state.openToNewPatients
                            )
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
        onSpecialtyQueryChange(query)
        onLocationQueryChange(location)
        search()
    }

    fun applyFilter(filter: String) = setDateFilter(filter)

    fun onSearch() {
        _uiState.update { it.copy(hasSearched = true) }
        search()
    }

    fun refresh() = search()

    fun loadMore() { /* pagination placeholder */ }

    override fun onCleared() {
        super.onCleared()
    }
}
