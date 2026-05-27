package lu.esklepios.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import lu.esklepios.app.domain.repository.AuthRepository

data class SplashUiState(
    val isLoading: Boolean = true,
    val isAuthenticated: Boolean = false,
)

class SplashViewModel(
    private val authRepository: AuthRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(SplashUiState())
    val uiState: StateFlow<SplashUiState> = _uiState.asStateFlow()

    fun checkAuth() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = false,
                    isAuthenticated = authRepository.isLoggedIn(),
                )
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
    }
}
