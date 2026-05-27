package lu.esklepios.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import lu.esklepios.app.domain.model.User
import lu.esklepios.app.domain.usecase.GetProfileUseCase
import lu.esklepios.app.domain.usecase.UpdateProfileUseCase

enum class ProfileField {
    FIRST_NAME,
    LAST_NAME,
    PHONE,
    GENDER,
    DATE_OF_BIRTH,
    CNS_NUMBER,
    LANGUAGE,
}

data class EditProfileUiState(
    val isLoading: Boolean = false,
    val user: User? = null,
    val firstName: String = "",
    val lastName: String = "",
    val phone: String = "",
    val gender: String = "",
    val dateOfBirth: String = "",
    val cnsNumber: String = "",
    val language: String = "en",
    val isSaved: Boolean = false,
    val error: String? = null,
)

class EditProfileViewModel(
    private val getProfileUseCase: GetProfileUseCase,
    private val updateProfileUseCase: UpdateProfileUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(EditProfileUiState())
    val uiState: StateFlow<EditProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            getProfileUseCase()
                .onSuccess { user ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            user = user,
                            firstName = user.firstName,
                            lastName = user.lastName,
                            phone = user.phone,
                            gender = user.gender,
                            dateOfBirth = user.dateOfBirth,
                            cnsNumber = user.cnsNumber,
                            language = user.language,
                        )
                    }
                }
                .onFailure { throwable ->
                    _uiState.update { it.copy(isLoading = false, error = throwable.message ?: "Failed to load profile") }
                }
        }
    }

    fun save() {
        val state = _uiState.value
        val existingUser =
            state.user ?: run {
                _uiState.update { it.copy(error = "No user data available") }
                return
            }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val updatedUser =
                existingUser.copy(
                    firstName = state.firstName,
                    lastName = state.lastName,
                    phone = state.phone,
                    gender = state.gender,
                    dateOfBirth = state.dateOfBirth,
                    cnsNumber = state.cnsNumber,
                    language = state.language,
                )
            updateProfileUseCase(updatedUser)
                .onSuccess { user ->
                    _uiState.update { it.copy(isLoading = false, user = user, isSaved = true) }
                }
                .onFailure { throwable ->
                    _uiState.update { it.copy(isLoading = false, error = throwable.message ?: "Failed to save profile") }
                }
        }
    }

    fun updateField(
        field: ProfileField,
        value: String,
    ) {
        _uiState.update { state ->
            when (field) {
                ProfileField.FIRST_NAME -> state.copy(firstName = value)
                ProfileField.LAST_NAME -> state.copy(lastName = value)
                ProfileField.PHONE -> state.copy(phone = value)
                ProfileField.GENDER -> state.copy(gender = value)
                ProfileField.DATE_OF_BIRTH -> state.copy(dateOfBirth = value)
                ProfileField.CNS_NUMBER -> state.copy(cnsNumber = value)
                ProfileField.LANGUAGE -> state.copy(language = value)
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun saveProfile(
        firstName: String,
        lastName: String,
        phone: String,
        dateOfBirth: String,
        gender: String,
        address: String,
    ) {
        updateField(ProfileField.FIRST_NAME, firstName)
        updateField(ProfileField.LAST_NAME, lastName)
        updateField(ProfileField.PHONE, phone)
        updateField(ProfileField.DATE_OF_BIRTH, dateOfBirth)
        updateField(ProfileField.GENDER, gender)
        save()
    }

    override fun onCleared() {
        super.onCleared()
    }
}
