package lu.esklepios.app.domain.usecase

import lu.esklepios.app.domain.model.User
import lu.esklepios.app.domain.repository.UserRepository

class UpdateProfileUseCase(private val repository: UserRepository) {
    suspend operator fun invoke(user: User): Result<User> {
        return repository.updateProfile(user)
    }
}
