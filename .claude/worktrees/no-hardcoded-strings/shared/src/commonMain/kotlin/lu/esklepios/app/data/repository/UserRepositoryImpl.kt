package lu.esklepios.app.data.repository

import lu.esklepios.app.data.network.ApiService
import lu.esklepios.app.data.network.toDomain
import lu.esklepios.app.data.network.toUpdateRequest
import lu.esklepios.app.domain.model.User
import lu.esklepios.app.domain.repository.UserRepository

class UserRepositoryImpl(
    private val apiService: ApiService
) : UserRepository {

    override suspend fun getProfile(): Result<User> {
        return apiService.getProfile().map { it.toDomain() }
    }

    override suspend fun updateProfile(user: User): Result<User> {
        return apiService.updateProfile(user.toUpdateRequest()).map { it.toDomain() }
    }

    override suspend fun changeEmail(newEmail: String, password: String): Result<Unit> {
        return apiService.changeEmail(newEmail, password)
    }

    override suspend fun changePassword(oldPassword: String, newPassword: String): Result<Unit> {
        return apiService.changePassword(oldPassword, newPassword)
    }
}
