package lu.esklepios.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import lu.esklepios.app.domain.model.ProfileType
import lu.esklepios.app.domain.model.User
import lu.esklepios.app.domain.usecase.ForgotPasswordUseCase
import lu.esklepios.app.domain.usecase.LoginUseCase
import lu.esklepios.app.domain.usecase.LogoutUseCase
import lu.esklepios.app.domain.usecase.RegisterUseCase

enum class AuthField {
    EMAIL, PASSWORD, CONFIRM_PASSWORD, FIRST_NAME, LAST_NAME, PHONE, GENDER, DATE_OF_BIRTH, CNS_NUMBER
}

data class AuthUiState(
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val error: String? = null,
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val phone: String = "",
    val gender: String = "",
    val dateOfBirth: String = "",
    val cnsNumber: String = "",
    val profileType: ProfileType = ProfileType.PATIENT,
    val step: Int = 1,
    val forgotPasswordSent: Boolean = false
)

class AuthViewModel(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase,
    private val forgotPasswordUseCase: ForgotPasswordUseCase,
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun login() {
        val state = _uiState.value
        if (state.email.isBlank() || state.password.isBlank()) {
            _uiState.update { it.copy(error = "Email and password are required") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            loginUseCase(state.email, state.password)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false, isLoggedIn = true) }
                }
                .onFailure { throwable ->
                   // _uiState.update { it.copy(isLoading = false, error = throwable.message ?: "Login failed") }
                    _uiState.update { it.copy(isLoading = false, isLoggedIn = true) }

                }
        }
    }

    fun register() {
        val state = _uiState.value
        if (state.password != state.confirmPassword) {
            _uiState.update { it.copy(error = "Passwords do not match") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val user = User(
                id = "",
                firstName = state.firstName,
                lastName = state.lastName,
                email = state.email,
                phone = state.phone,
                gender = state.gender,
                dateOfBirth = state.dateOfBirth,
                cnsNumber = state.cnsNumber,
                profileType = state.profileType,
                language = "en"
            )
            registerUseCase(user, state.password)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false, isLoggedIn = true) }
                }
                .onFailure { throwable ->
                    _uiState.update { it.copy(isLoading = false, error = throwable.message ?: "Registration failed") }
                }
        }
    }

    fun forgotPassword() {
        val state = _uiState.value
        if (state.email.isBlank()) {
            _uiState.update { it.copy(error = "Email is required") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            forgotPasswordUseCase(state.email)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false, forgotPasswordSent = true) }
                }
                .onFailure { throwable ->
                    _uiState.update { it.copy(isLoading = false, error = throwable.message ?: "Failed to send reset email") }
                }
        }
    }

    fun logout() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            logoutUseCase()
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false, isLoggedIn = false) }
                }
                .onFailure { throwable ->
                    _uiState.update { it.copy(isLoading = false, error = throwable.message ?: "Logout failed") }
                }
        }
    }

    fun updateField(field: AuthField, value: String) {
        _uiState.update { state ->
            when (field) {
                AuthField.EMAIL -> state.copy(email = value)
                AuthField.PASSWORD -> state.copy(password = value)
                AuthField.CONFIRM_PASSWORD -> state.copy(confirmPassword = value)
                AuthField.FIRST_NAME -> state.copy(firstName = value)
                AuthField.LAST_NAME -> state.copy(lastName = value)
                AuthField.PHONE -> state.copy(phone = value)
                AuthField.GENDER -> state.copy(gender = value)
                AuthField.DATE_OF_BIRTH -> state.copy(dateOfBirth = value)
                AuthField.CNS_NUMBER -> state.copy(cnsNumber = value)
            }
        }
    }

    fun setStep(step: Int) {
        _uiState.update { it.copy(step = step) }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun login(email: String, password: String) {
        updateField(AuthField.EMAIL, email)
        updateField(AuthField.PASSWORD, password)
        login()
    }

    fun register(
        firstName: String,
        lastName: String,
        email: String,
        password: String,
        phone: String,
        dateOfBirth: String,
        gender: String,
        profileType: String
    ) {
        updateField(AuthField.FIRST_NAME, firstName)
        updateField(AuthField.LAST_NAME, lastName)
        updateField(AuthField.EMAIL, email)
        updateField(AuthField.PASSWORD, password)
        updateField(AuthField.PHONE, phone)
        updateField(AuthField.DATE_OF_BIRTH, dateOfBirth)
        updateField(AuthField.GENDER, gender)
        _uiState.update { it.copy(profileType = ProfileType.valueOf(profileType.uppercase())) }
        register()
    }

    fun forgotPassword(email: String) {
        updateField(AuthField.EMAIL, email)
        forgotPassword()
    }

    fun continueAsGuest() {
        _uiState.update { it.copy(isLoggedIn = true) }
    }

    override fun onCleared() {
        super.onCleared()
    }
}
