package lu.esklepios.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import lu.esklepios.app.domain.model.Appointment
import lu.esklepios.app.domain.model.AppointmentSlot
import lu.esklepios.app.domain.model.AppointmentStatus
import lu.esklepios.app.domain.model.Practitioner
import lu.esklepios.app.domain.repository.AuthRepository
import lu.esklepios.app.domain.usecase.CreateAppointmentUseCase
import lu.esklepios.app.domain.usecase.GetPractitionerDetailUseCase

data class BookAppointmentUiState(
    val isLoading: Boolean = false,
    val practitioner: Practitioner? = null,
    val selectedSlot: AppointmentSlot? = null,
    val previousAppointment: Appointment? = null,
    val consultationReason: String = "",
    val messageToDoctor: String = "",
    val isAuthenticated: Boolean = false,
    val isConfirmed: Boolean = false,
    val confirmedAppointmentId: String = "",
    val error: String? = null
)

class BookAppointmentViewModel(
    private val createAppointmentUseCase: CreateAppointmentUseCase,
    private val authRepository: AuthRepository,
    private val getPractitionerDetailUseCase: GetPractitionerDetailUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(BookAppointmentUiState())
    val uiState: StateFlow<BookAppointmentUiState> = _uiState.asStateFlow()

    init {
        checkAuth()
    }

    fun checkAuth() {
        _uiState.update { it.copy(isAuthenticated = authRepository.isLoggedIn()) }
    }

    fun loadData(practitionerId: String, slotId: String, practitioner: Practitioner?) {
        if (practitioner != null) {
            val slot = practitioner.availableSlots.find { it.id == slotId }
            _uiState.update { it.copy(practitioner = practitioner, selectedSlot = slot) }
        } else {
            loadData(practitionerId, slotId)
        }
    }

    fun loadData(practitionerId: String, slotId: String) {
        viewModelScope.launch {
            getPractitionerDetailUseCase(practitionerId).onSuccess { p ->
                val slot = p.availableSlots.find { it.id == slotId }
                _uiState.update { it.copy(practitioner = p, selectedSlot = slot) }
            }
        }
    }

    fun confirm() {
        val state = _uiState.value
        val practitioner = state.practitioner ?: run {
            _uiState.update { it.copy(error = "No practitioner selected") }
            return
        }
        val slot = state.selectedSlot ?: run {
            _uiState.update { it.copy(error = "No slot selected") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val appointment = Appointment(
                id = "",
                practitionerId = practitioner.id,
                practitionerName = practitioner.fullName,
                clinicName = practitioner.clinicName,
                specialty = practitioner.specialty,
                dateTime = slot.dateTime,
                status = AppointmentStatus.PENDING,
                messageToDoctor = state.messageToDoctor,
                consultationReason = state.consultationReason
            )
            createAppointmentUseCase(appointment)
                .onSuccess { created ->
                    _uiState.update { it.copy(isLoading = false, isConfirmed = true, confirmedAppointmentId = created.id) }
                }
                .onFailure { throwable ->
                    _uiState.update { it.copy(isLoading = false, error = throwable.message ?: "Failed to create appointment") }
                }
        }
    }

    fun updateConsultationReason(reason: String) {
        _uiState.update { it.copy(consultationReason = reason) }
    }

    fun updateMessageToDoctor(message: String) {
        _uiState.update { it.copy(messageToDoctor = message) }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun confirmBooking(message: String, reason: String) {
        updateMessageToDoctor(message)
        updateConsultationReason(reason)
        confirm()
    }

    override fun onCleared() {
        super.onCleared()
    }
}
