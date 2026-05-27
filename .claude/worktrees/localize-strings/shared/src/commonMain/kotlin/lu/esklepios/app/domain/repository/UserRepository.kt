package lu.esklepios.app.domain.repository

import lu.esklepios.app.domain.model.User

interface UserRepository {
    suspend fun getProfile(): Result<User>

    suspend fun updateProfile(user: User): Result<User>

    suspend fun changeEmail(newEmail: String, password: String): Result<Unit>

    suspend fun changePassword(oldPassword: String, newPassword: String): Result<Unit>
}
