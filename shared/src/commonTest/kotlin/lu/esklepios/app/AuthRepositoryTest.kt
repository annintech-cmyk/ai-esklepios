package lu.esklepios.app

import kotlinx.coroutines.test.runTest
import lu.esklepios.app.domain.model.ProfileType
import lu.esklepios.app.domain.model.User
import lu.esklepios.app.domain.repository.AuthRepository
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Tests for AuthRepository contract using a fake implementation.
 * Verifies login stores token, logout clears state, and isLoggedIn reflects auth state.
 */
class AuthRepositoryTest {
    private class FakeAuthRepository : AuthRepository {
        private var token: String? = null
        private var currentUser: User? = null

        private val loginSuccess: Boolean
        private val registerSuccess: Boolean

        constructor(loginSuccess: Boolean = true, registerSuccess: Boolean = true) {
            this.loginSuccess = loginSuccess
            this.registerSuccess = registerSuccess
        }

        val testUser =
            User(
                id = "u1",
                firstName = "Test",
                lastName = "User",
                email = "test@esklepios.lu",
                phone = "+352 000",
                gender = "Other",
                dateOfBirth = "1990-01-01",
                cnsNumber = "0000000000",
                profileType = ProfileType.PATIENT,
                language = "en",
            )

        override suspend fun login(
            email: String,
            password: String,
        ): Result<User> {
            return if (loginSuccess) {
                token = "fake-jwt-token"
                currentUser = testUser
                Result.success(testUser)
            } else {
                Result.failure(Exception("Invalid credentials"))
            }
        }

        override suspend fun register(
            user: User,
            password: String,
        ): Result<User> {
            return if (registerSuccess) {
                token = "fake-jwt-token-new"
                currentUser = user
                Result.success(user)
            } else {
                Result.failure(Exception("Registration failed"))
            }
        }

        override suspend fun forgotPassword(email: String): Result<Unit> = Result.success(Unit)

        override suspend fun refreshToken(): Result<String> = Result.success("new-token")

        override suspend fun logout(): Result<Unit> {
            token = null
            currentUser = null
            return Result.success(Unit)
        }

        override fun isLoggedIn(): Boolean = token != null

        override fun getCurrentUser(): User? = currentUser
    }

    @Test
    fun `login stores token and sets isLoggedIn to true`() =
        runTest {
            val repo = FakeAuthRepository(loginSuccess = true)
            assertFalse(repo.isLoggedIn())

            val result = repo.login("test@test.lu", "password")

            assertTrue(result.isSuccess)
            assertTrue(repo.isLoggedIn())
        }

    @Test
    fun `login returns user on success`() =
        runTest {
            val repo = FakeAuthRepository(loginSuccess = true)

            val result = repo.login("test@test.lu", "password")

            assertTrue(result.isSuccess)
            assertNotNull(result.getOrNull())
            assertEquals("u1", result.getOrNull()?.id)
        }

    @Test
    fun `login failure does not set isLoggedIn`() =
        runTest {
            val repo = FakeAuthRepository(loginSuccess = false)

            val result = repo.login("bad@test.lu", "wrongpass")

            assertTrue(result.isFailure)
            assertFalse(repo.isLoggedIn())
        }

    @Test
    fun `logout clears token and sets isLoggedIn to false`() =
        runTest {
            val repo = FakeAuthRepository(loginSuccess = true)
            repo.login("test@test.lu", "password")
            assertTrue(repo.isLoggedIn())

            val result = repo.logout()

            assertTrue(result.isSuccess)
            assertFalse(repo.isLoggedIn())
        }

    @Test
    fun `logout clears current user`() =
        runTest {
            val repo = FakeAuthRepository(loginSuccess = true)
            repo.login("test@test.lu", "password")
            assertNotNull(repo.getCurrentUser())

            repo.logout()

            assertNull(repo.getCurrentUser())
        }

    @Test
    fun `isLoggedIn returns false initially`() {
        val repo = FakeAuthRepository()
        assertFalse(repo.isLoggedIn())
    }

    @Test
    fun `getCurrentUser returns null before login`() {
        val repo = FakeAuthRepository()
        assertNull(repo.getCurrentUser())
    }

    @Test
    fun `register success sets isLoggedIn`() =
        runTest {
            val repo = FakeAuthRepository(registerSuccess = true)
            val user = User("", "New", "User", "new@test.lu", "", "", "", "", ProfileType.PATIENT, "en")

            val result = repo.register(user, "password123")

            assertTrue(result.isSuccess)
            assertTrue(repo.isLoggedIn())
        }

    @Test
    fun `register failure does not set isLoggedIn`() =
        runTest {
            val repo = FakeAuthRepository(registerSuccess = false)
            val user = User("", "New", "User", "new@test.lu", "", "", "", "", ProfileType.PATIENT, "en")

            val result = repo.register(user, "password123")

            assertTrue(result.isFailure)
            assertFalse(repo.isLoggedIn())
        }
}
