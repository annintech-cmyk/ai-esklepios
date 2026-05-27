package lu.esklepios.app.domain.usecase

import lu.esklepios.app.domain.model.User
import lu.esklepios.app.domain.repository.AuthRepository

class LoginUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(
        email: String,
        password: String,
    ): Result<User> {
        return repository.login(email, password)
    }
}
