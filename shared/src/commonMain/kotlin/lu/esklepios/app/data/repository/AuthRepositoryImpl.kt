package lu.esklepios.app.data.repository

import kotlin.random.Random
import lu.esklepios.app.BuildKonfig
import lu.esklepios.app.data.network.ApiService
import lu.esklepios.app.data.network.TokenStorage
import lu.esklepios.app.data.network.toDomain
import lu.esklepios.app.data.network.toRegisterRequest
import lu.esklepios.app.db.ESklepiosDatabase
import lu.esklepios.app.domain.model.ProfileType
import lu.esklepios.app.domain.model.User
import lu.esklepios.app.domain.repository.AuthRepository

class AuthRepositoryImpl(
    private val apiService: ApiService,
    private val tokenStorage: TokenStorage,
    private val database: ESklepiosDatabase,
) : AuthRepository {
    override suspend fun login(
        email: String,
        password: String,
    ): Result<User> {
        return if (BuildKonfig.FOR_DEMO) {
            runCatching {
                val mockToken = "demo_token_${Random.nextInt(100000, 999999)}"
                tokenStorage.setToken(mockToken)
                tokenStorage.setRefreshToken("demo_refresh_token")
                val user = User(
                    id = "demo_user_1",
                    firstName = email.substringBefore("@").replaceFirstChar { it.uppercase() },
                    lastName = "Demo User",
                    email = email,
                    phone = "+352 123 456 789",
                    gender = "Not specified",
                    dateOfBirth = "1990-01-01",
                    cnsNumber = "1990010100000",
                    profileType = ProfileType.PATIENT,
                    language = "en",
                )
                cacheUser(user)
                user
            }
        } else {
            apiService.login(email, password).map { response ->
                tokenStorage.setToken(response.token)
                tokenStorage.setRefreshToken(response.refreshToken)
                val user = response.user.toDomain()
                cacheUser(user)
                user
            }
        }
    }

    override suspend fun register(
        user: User,
        password: String,
    ): Result<User> {
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
        val currentRefreshToken =
            tokenStorage.getRefreshToken()
                ?: return Result.failure(Exception("No refresh token available"))

        return apiService.refreshToken(currentRefreshToken).map { response ->
            tokenStorage.setToken(response.token)
            tokenStorage.setRefreshToken(response.refreshToken)
            response.token
        }
    }

    override suspend fun logout(): Result<Unit> {
        return runCatching {
            tokenStorage.clear()
            database.transaction {
                database.usersQueries.deleteAll()
                database.practitionersQueries.deleteAll()
                database.appointmentsQueries.deleteAll()
            }
        }
    }

    override fun isLoggedIn(): Boolean {
        return tokenStorage.getToken() != null
    }

    override fun getCurrentUser(): User? {
        return runCatching {
            database.usersQueries.selectCurrent().executeAsOneOrNull()?.toDomain()
        }.getOrNull()
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
            language = user.language,
        )
    }

    private fun lu.esklepios.app.db.UserEntity.toDomain(): User =
        User(
            id = id,
            firstName = firstName,
            lastName = lastName,
            email = email,
            phone = phone,
            gender = gender,
            dateOfBirth = dateOfBirth,
            cnsNumber = cnsNumber,
            profileType = lu.esklepios.app.domain.model.ProfileType.fromString(profileType),
            language = language,
        )
}
