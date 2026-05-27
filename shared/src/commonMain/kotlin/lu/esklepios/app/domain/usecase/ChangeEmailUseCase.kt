package lu.esklepios.app.domain.usecase

import lu.esklepios.app.domain.repository.UserRepository

class ChangeEmailUseCase(private val repository: UserRepository) {
    suspend operator fun invoke(
        newEmail: String,
        password: String,
    ): Result<Unit> {
        return repository.changeEmail(newEmail, password)
    }
}
