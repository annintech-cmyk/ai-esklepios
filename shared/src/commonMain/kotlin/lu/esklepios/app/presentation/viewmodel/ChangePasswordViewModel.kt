package lu.esklepios.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import lu.esklepios.app.domain.usecase.ChangePasswordUseCase
import lu.esklepios.app.util.ValidationUtil

data class ChangePasswordUiState(
    val isLoading: Boolean = false,
    val oldPassword: String = "",
    val newPassword: String = "",
    val confirmPassword: String = "",
    val isSuccess: Boolean = false,
    val error: String? = null
)

class ChangePasswordViewModel(
    private val changePasswordUseCase: ChangePasswordUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChangePasswordUiState())
    val uiState: StateFlow<ChangePasswordUiState> = _uiState.asStateFlow()

    fun changePassword() {
        val state = _uiState.value
        if (state.oldPassword.isBlank()) {
            _uiState.update { it.copy(error = "Current password is required") }
            return
        }
        if (state.newPassword.isBlank()) {
            _uiState.update { it.copy(error = "New password is required") }
            return
        }
        if (!ValidationUtil.passwordsMatch(state.newPassword, state.confirmPassword)) {
            _uiState.update { it.copy(error = "Passwords do not match") }
            return
        }
        if (!ValidationUtil.isPasswordMinLength(state.newPassword)) {
            _uiState.update { it.copy(error = "Password must be at least 12 characters") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            changePasswordUseCase(state.oldPassword, state.newPassword)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                }
                .onFailure { throwable ->
                    _uiState.update { it.copy(isLoading = false, error = throwable.message ?: "Failed to change password") }
                }
        }
    }

    fun updateOldPassword(password: String) {
        _uiState.update { it.copy(oldPassword = password) }
    }

    fun updateNewPassword(password: String) {
        _uiState.update { it.copy(newPassword = password) }
    }

    fun updateConfirmPassword(password: String) {
        _uiState.update { it.copy(confirmPassword = password) }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun changePassword(currentPassword: String, newPassword: String, confirmPassword: String) {
        updateOldPassword(currentPassword)
        updateNewPassword(newPassword)
        updateConfirmPassword(confirmPassword)
        changePassword()
    }

    override fun onCleared() {
        super.onCleared()
    }
}
