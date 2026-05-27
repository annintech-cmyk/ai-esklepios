package lu.esklepios.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import lu.esklepios.app.domain.model.Appointment
import lu.esklepios.app.domain.usecase.CancelAppointmentUseCase
import lu.esklepios.app.domain.usecase.GetPastAppointmentsUseCase
import lu.esklepios.app.domain.usecase.GetUpcomingAppointmentsUseCase
import lu.esklepios.app.domain.usecase.ModifyAppointmentUseCase

data class MyAppointmentsUiState(
    val isLoading: Boolean = false,
    val upcomingAppointments: List<Appointment> = emptyList(),
    val pastAppointments: List<Appointment> = emptyList(),
    val selectedTab: Int = 0,
    val error: String? = null,
)

class MyAppointmentsViewModel(
    private val getUpcomingAppointmentsUseCase: GetUpcomingAppointmentsUseCase,
    private val getPastAppointmentsUseCase: GetPastAppointmentsUseCase,
    private val cancelAppointmentUseCase: CancelAppointmentUseCase,
    private val modifyAppointmentUseCase: ModifyAppointmentUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(MyAppointmentsUiState())
    val uiState: StateFlow<MyAppointmentsUiState> = _uiState.asStateFlow()

    init {
        loadUpcoming()
        loadPast()
    }

    fun loadUpcoming() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            getUpcomingAppointmentsUseCase("")
                .onSuccess { appointments ->
                    _uiState.update { it.copy(isLoading = false, upcomingAppointments = appointments) }
                }
                .onFailure { throwable ->
                    _uiState.update { it.copy(isLoading = false, error = throwable.message ?: "Failed to load upcoming appointments") }
                }
        }
    }

    fun loadPast() {
        viewModelScope.launch {
            getPastAppointmentsUseCase("")
                .onSuccess { appointments ->
                    _uiState.update { it.copy(pastAppointments = appointments) }
                }
                .onFailure { throwable ->
                    _uiState.update { it.copy(error = throwable.message ?: "Failed to load past appointments") }
                }
        }
    }

    fun cancelAppointment(appointmentId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            cancelAppointmentUseCase(appointmentId)
                .onSuccess {
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            upcomingAppointments = state.upcomingAppointments.filter { it.id != appointmentId },
                        )
                    }
                }
                .onFailure { throwable ->
                    _uiState.update { it.copy(isLoading = false, error = throwable.message ?: "Failed to cancel appointment") }
                }
        }
    }

    fun selectTab(tab: Int) {
        _uiState.update { it.copy(selectedTab = tab) }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun loadAppointments() {
        loadUpcoming()
        loadPast()
    }

    fun refresh() = loadAppointments()

    override fun onCleared() {
        super.onCleared()
    }
}
