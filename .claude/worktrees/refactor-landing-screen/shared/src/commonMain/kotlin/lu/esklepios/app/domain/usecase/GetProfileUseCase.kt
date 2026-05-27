package lu.esklepios.app.domain.usecase

import lu.esklepios.app.domain.model.User
import lu.esklepios.app.domain.repository.UserRepository

class GetProfileUseCase(private val repository: UserRepository) {
    suspend operator fun invoke(): Result<User> {
        return repository.getProfile()
    }
}
