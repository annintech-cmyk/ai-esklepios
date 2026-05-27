package lu.esklepios.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import lu.esklepios.app.domain.model.AppointmentSlot
import lu.esklepios.app.domain.model.Practitioner
import lu.esklepios.app.domain.usecase.GetPractitionerDetailUseCase

data class PractitionerDetailUiState(
    val isLoading: Boolean = false,
    val practitioner: Practitioner? = null,
    val error: String? = null,
    val selectedSlot: AppointmentSlot? = null
)

class PractitionerDetailViewModel(
    private val getPractitionerDetailUseCase: GetPractitionerDetailUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(PractitionerDetailUiState())
    val uiState: StateFlow<PractitionerDetailUiState> = _uiState.asStateFlow()

    fun loadPractitioner(id: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            getPractitionerDetailUseCase(id)
                .onSuccess { practitioner ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            practitioner = practitioner
                        )
                    }
                }
                .onFailure { throwable ->
                    _uiState.update { it.copy(isLoading = false, error = throwable.message ?: "Failed to load practitioner") }
                }
        }
    }

    fun selectSlot(slot: AppointmentSlot) {
        _uiState.update { it.copy(selectedSlot = slot) }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun loadDetail(id: String) = loadPractitioner(id)

    fun selectSlot(slotId: String) {
        val slot = _uiState.value.practitioner?.availableSlots?.find { it.id == slotId }
        if (slot != null) selectSlot(slot)
    }

    override fun onCleared() {
        super.onCleared()
    }
}
