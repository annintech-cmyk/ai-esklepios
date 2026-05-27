package lu.esklepios.app

import kotlinx.coroutines.test.runTest
import lu.esklepios.app.domain.model.ProfileType
import lu.esklepios.app.domain.model.User
import lu.esklepios.app.domain.repository.UserRepository
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class UserRepositoryTest {
    private val repo: UserRepository = FakeUserRepository()

    @Test
    fun `getProfile returns success with user`() =
        runTest {
            val result = repo.getProfile()
            assertTrue(result.isSuccess)
            assertEquals(storedUser.email, result.getOrNull()?.email)
        }

    @Test
    fun `getProfile failure propagates`() =
        runTest {
            val failing = FakeUserRepository(profileResult = Result.failure(Exception("unauthorized")))
            assertTrue(failing.getProfile().isFailure)
        }

    @Test
    fun `updateProfile returns updated user`() =
        runTest {
            val updated = storedUser.copy(firstName = "Updated")
            val repo2 = FakeUserRepository(updateResult = Result.success(updated))
            val result = repo2.updateProfile(updated)
            assertTrue(result.isSuccess)
            assertEquals("Updated", result.getOrNull()?.firstName)
        }

    @Test
    fun `updateProfile failure propagates`() =
        runTest {
            val failing = FakeUserRepository(updateResult = Result.failure(Exception("validation")))
            assertTrue(failing.updateProfile(storedUser).isFailure)
        }

    @Test
    fun `changeEmail success returns Unit`() =
        runTest {
            assertTrue(repo.changeEmail("new@test.lu", "pass").isSuccess)
        }

    @Test
    fun `changeEmail failure propagates`() =
        runTest {
            val failing = FakeUserRepository(changeEmailResult = Result.failure(Exception("wrong password")))
            assertTrue(failing.changeEmail("new@test.lu", "wrong").isFailure)
        }

    @Test
    fun `changePassword success returns Unit`() =
        runTest {
            assertTrue(repo.changePassword("old", "new").isSuccess)
        }

    @Test
    fun `changePassword failure propagates`() =
        runTest {
            val failing = FakeUserRepository(changePasswordResult = Result.failure(Exception("wrong old")))
            assertTrue(failing.changePassword("wrong", "new").isFailure)
        }
}

private val storedUser =
    User(
        id = "u1", firstName = "Anna", lastName = "Test", email = "anna@test.lu",
        phone = "+352600000000", gender = "female", dateOfBirth = "1990-01-01",
        cnsNumber = "1234567890", profileType = ProfileType.PATIENT, language = "fr",
    )

private class FakeUserRepository(
    private val profileResult: Result<User> = Result.success(storedUser),
    private val updateResult: Result<User> = Result.success(storedUser),
    private val changeEmailResult: Result<Unit> = Result.success(Unit),
    private val changePasswordResult: Result<Unit> = Result.success(Unit),
) : UserRepository {
    override suspend fun getProfile() = profileResult

    override suspend fun updateProfile(user: User) = updateResult

    override suspend fun changeEmail(
        newEmail: String,
        password: String,
    ) = changeEmailResult

    override suspend fun changePassword(
        oldPassword: String,
        newPassword: String,
    ) = changePasswordResult
}
