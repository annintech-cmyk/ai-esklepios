package lu.esklepios.app.domain.usecase

import lu.esklepios.app.domain.repository.UserRepository

class ChangePasswordUseCase(private val repository: UserRepository) {
    suspend operator fun invoke(oldPassword: String, newPassword: String): Result<Unit> {
        return repository.changePassword(oldPassword, newPassword)
    }
}
