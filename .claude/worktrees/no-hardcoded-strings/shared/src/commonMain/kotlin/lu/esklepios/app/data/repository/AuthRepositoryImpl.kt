package lu.esklepios.app.data.repository

import lu.esklepios.app.data.network.ApiService
import lu.esklepios.app.data.network.TokenStorage
import lu.esklepios.app.data.network.toDomain
import lu.esklepios.app.data.network.toRegisterRequest
import lu.esklepios.app.db.ESklepiosDatabase
import lu.esklepios.app.domain.model.User
import lu.esklepios.app.domain.repository.AuthRepository

class AuthRepositoryImpl(
    private val apiService: ApiService,
    private val tokenStorage: TokenStorage,
    private val database: ESklepiosDatabase
) : AuthRepository {

    override suspend fun login(email: String, password: String): Result<User> {
        return apiService.login(email, password).map { response ->
            tokenStorage.setToken(response.token)
            tokenStorage.setRefreshToken(response.refreshToken)
            val user = response.user.toDomain()
            cacheUser(user)
            user
        }
    }

    override suspend fun register(user: User, password: String): Result<User> {
        return apiService.register(user.toRegisterRequest(password)).map { response ->
            val registeredUser = response.user.toDomain()
            cacheUser(registeredUser)
            registeredUser
        }
    }

    override suspend fun forgotPassword(email: String): Result<Unit> {
        return apiService.forgotPassword(email).map { }
    }

    override suspend fun refreshToken(): Result<String> {
        val currentRefreshToken = tokenStorage.getRefreshToken()
            ?: return Result.failure(Exception("No refresh token available"))

        return apiService.refreshToken(currentRefreshToken).map { response ->
            tokenStorage.setToken(response.token)
            tokenStorage.setRefreshToken(response.refreshToken)
            response.token
        }
    }

    override suspend fun logout(): Result<Unit> {
        return try {
            tokenStorage.clear()
            database.usersQueries.deleteAll()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun isLoggedIn(): Boolean {
        return tokenStorage.getToken() != null
    }

    override fun getCurrentUser(): User? {
        return try {
            database.usersQueries.selectCurrent().executeAsOneOrNull()?.toDomain()
        } catch (e: Exception) {
            null
        }
    }

    private fun cacheUser(user: User) {
        database.usersQueries.insertOrReplace(
            id = user.id,
            firstName = user.firstName,
            lastName = user.lastName,
            email = user.email,
            phone = user.phone,
            gender = user.gender,
            dateOfBirth = user.dateOfBirth,
            cnsNumber = user.cnsNumber,
            profileType = user.profileType.name,
            language = user.language
        )
    }

    private fun lu.esklepios.app.db.UserEntity.toDomain(): User = User(
        id = id,
        firstName = firstName,
        lastName = lastName,
        email = email,
        phone = phone,
        gender = gender,
        dateOfBirth = dateOfBirth,
        cnsNumber = cnsNumber,
        profileType = lu.esklepios.app.domain.model.ProfileType.fromString(profileType),
        language = language
    )
}
