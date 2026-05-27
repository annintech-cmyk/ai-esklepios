package lu.esklepios.app.domain.repository

import lu.esklepios.app.domain.model.User

interface AuthRepository {
    suspend fun login(
        email: String,
        password: String,
    ): Result<User>

    suspend fun register(
        user: User,
        password: String,
    ): Result<User>

    suspend fun forgotPassword(email: String): Result<Unit>

    suspend fun refreshToken(): Result<String>

    suspend fun logout(): Result<Unit>

    fun isLoggedIn(): Boolean

    fun getCurrentUser(): User?
}
