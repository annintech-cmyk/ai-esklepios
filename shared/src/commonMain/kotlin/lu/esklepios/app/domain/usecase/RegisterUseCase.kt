package lu.esklepios.app.domain.usecase

import lu.esklepios.app.domain.model.User
import lu.esklepios.app.domain.repository.AuthRepository

class RegisterUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(
        user: User,
        password: String,
    ): Result<User> {
        return repository.register(user, password)
    }
}
