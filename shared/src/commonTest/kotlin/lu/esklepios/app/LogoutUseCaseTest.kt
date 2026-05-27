package lu.esklepios.app

import kotlinx.coroutines.test.runTest
import lu.esklepios.app.domain.model.*
import lu.esklepios.app.domain.repository.AuthRepository
import lu.esklepios.app.domain.usecase.LogoutUseCase
import kotlin.test.*

class LogoutUseCaseTest {

    private class FakeAuthRepository : AuthRepository {
        var logoutCalled = false
        var logoutResult: Result<Unit> = Result.success(Unit)
        var tokenCleared = false
        var usersCleared = false
        var practitionersCleared = false
        var appointmentsCleared = false

        override suspend fun logout(): Result<Unit> {
            logoutCalled = true
            if (logoutResult.isSuccess) {
                tokenCleared = true
                usersCleared = true
                practitionersCleared = true
                appointmentsCleared = true
            }
            return logoutResult
        }

        override suspend fun login(email: String, password: String): Result<User> = Result.failure(Exception())
        override suspend fun register(user: User, password: String): Result<User> = Result.failure(Exception())
        override suspend fun forgotPassword(email: String): Result<Unit> = Result.success(Unit)
        override suspend fun refreshToken(): Result<String> = Result.success("token")
        override fun isLoggedIn(): Boolean = !tokenCleared
        override fun getCurrentUser(): User? = null
    }

    @Test
    fun `logout success clears token and all user-scoped tables`() = runTest {
        val repo = FakeAuthRepository()
        val useCase = LogoutUseCase(repo)

        val result = useCase()

        assertTrue(result.isSuccess)
        assertTrue(repo.tokenCleared, "Token should be cleared")
        assertTrue(repo.usersCleared, "Users table should be cleared")
        assertTrue(repo.practitionersCleared, "Practitioners table should be cleared")
        assertTrue(repo.appointmentsCleared, "Appointments table should be cleared")
    }

    @Test
    fun `logout failure propagates to caller`() = runTest {
        val repo = FakeAuthRepository().also {
            it.logoutResult = Result.failure(Exception("DB error"))
        }
        val useCase = LogoutUseCase(repo)

        val result = useCase()

        assertTrue(result.isFailure)
        assertEquals("DB error", result.exceptionOrNull()?.message)
    }

    @Test
    fun `logout delegates to repository`() = runTest {
        val repo = FakeAuthRepository()
        val useCase = LogoutUseCase(repo)

        useCase()

        assertTrue(repo.logoutCalled)
    }

    @Test
    fun `isLoggedIn is false after successful logout`() = runTest {
        val repo = FakeAuthRepository()
        val useCase = LogoutUseCase(repo)
        assertTrue(repo.isLoggedIn())

        useCase()

        assertFalse(repo.isLoggedIn())
    }
}
