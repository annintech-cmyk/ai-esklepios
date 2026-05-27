package lu.esklepios.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class AppointmentSuccessUiState(
    val appointmentId: String = "",
    val practitionerName: String = "",
    val dateTime: String = "",
    val clinicName: String = "",
)

class AppointmentSuccessViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(AppointmentSuccessUiState())
    val uiState: StateFlow<AppointmentSuccessUiState> = _uiState.asStateFlow()

    fun setAppointmentData(
        id: String,
        practitionerName: String,
        dateTime: String,
        clinicName: String,
    ) {
        _uiState.update {
            it.copy(
                appointmentId = id,
                practitionerName = practitionerName,
                dateTime = dateTime,
                clinicName = clinicName,
            )
        }
    }

    fun loadAppointment(appointmentId: String = "") {
        if (appointmentId.isNotBlank()) {
            _uiState.update { it.copy(appointmentId = appointmentId) }
        }
    }

    override fun onCleared() {
        super.onCleared()
    }
}
