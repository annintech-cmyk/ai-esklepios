package lu.esklepios.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import lu.esklepios.app.domain.usecase.ChangeEmailUseCase
import lu.esklepios.app.util.ValidationUtil

data class ChangeEmailUiState(
    val isLoading: Boolean = false,
    val currentEmail: String = "",
    val newEmail: String = "",
    val confirmEmail: String = "",
    val isSuccess: Boolean = false,
    val error: String? = null,
)

class ChangeEmailViewModel(
    private val changeEmailUseCase: ChangeEmailUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ChangeEmailUiState())
    val uiState: StateFlow<ChangeEmailUiState> = _uiState.asStateFlow()

    fun changeEmail() {
        val state = _uiState.value
        if (state.newEmail.isBlank()) {
            _uiState.update { it.copy(error = "New email is required") }
            return
        }
        if (!ValidationUtil.isValidEmail(state.newEmail)) {
            _uiState.update { it.copy(error = "Invalid email format") }
            return
        }
        if (!ValidationUtil.emailsMatch(state.newEmail, state.confirmEmail)) {
            _uiState.update { it.copy(error = "Emails do not match") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            changeEmailUseCase(state.newEmail, "")
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                }
                .onFailure { throwable ->
                    _uiState.update { it.copy(isLoading = false, error = throwable.message ?: "Failed to change email") }
                }
        }
    }

    fun updateNewEmail(email: String) {
        _uiState.update { it.copy(newEmail = email) }
    }

    fun updateConfirmEmail(email: String) {
        _uiState.update { it.copy(confirmEmail = email) }
    }

    fun setCurrentEmail(email: String) {
        _uiState.update { it.copy(currentEmail = email) }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    override fun onCleared() {
        super.onCleared()
    }
}
