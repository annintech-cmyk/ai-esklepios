package lu.esklepios.app

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import lu.esklepios.app.domain.model.AppointmentSlot
import lu.esklepios.app.domain.model.Practitioner
import lu.esklepios.app.domain.model.ProfileType
import lu.esklepios.app.domain.model.User
import lu.esklepios.app.domain.repository.AuthRepository
import lu.esklepios.app.domain.repository.PractitionerRepository
import lu.esklepios.app.domain.repository.UserRepository
import lu.esklepios.app.domain.usecase.ChangeEmailUseCase
import lu.esklepios.app.domain.usecase.ChangePasswordUseCase
import lu.esklepios.app.domain.usecase.GetPractitionerDetailUseCase
import lu.esklepios.app.domain.usecase.GetProfileUseCase
import lu.esklepios.app.domain.usecase.UpdateProfileUseCase
import lu.esklepios.app.presentation.viewmodel.AppointmentSuccessViewModel
import lu.esklepios.app.presentation.viewmodel.ChangeEmailViewModel
import lu.esklepios.app.presentation.viewmodel.ChangePasswordViewModel
import lu.esklepios.app.presentation.viewmodel.EditProfileViewModel
import lu.esklepios.app.presentation.viewmodel.PractitionerDetailViewModel
import lu.esklepios.app.presentation.viewmodel.SplashViewModel
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class SplashViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private lateinit var repo: FakeAuthRepoV
    private lateinit var viewModel: SplashViewModel

    @BeforeTest fun setUp() {
        Dispatchers.setMain(dispatcher)
        repo = FakeAuthRepoV()
        viewModel = SplashViewModel(repo)
    }

    @AfterTest fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state has isLoading true`() {
        assertTrue(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `checkAuth sets authenticated when token present`() =
        runTest {
            repo.loggedIn = true
            viewModel.checkAuth()
            dispatcher.scheduler.advanceUntilIdle()
            val state = viewModel.uiState.value
            assertFalse(state.isLoading)
            assertTrue(state.isAuthenticated)
        }

    @Test
    fun `checkAuth sets not authenticated when no token`() =
        runTest {
            repo.loggedIn = false
            viewModel.checkAuth()
            dispatcher.scheduler.advanceUntilIdle()
            val state = viewModel.uiState.value
            assertFalse(state.isLoading)
            assertFalse(state.isAuthenticated)
        }
}

@OptIn(ExperimentalCoroutinesApi::class)
class PractitionerDetailViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private lateinit var practRepo: FakePractRepoV
    private lateinit var viewModel: PractitionerDetailViewModel

    @BeforeTest fun setUp() {
        Dispatchers.setMain(dispatcher)
        practRepo = FakePractRepoV()
        viewModel =
            PractitionerDetailViewModel(
                GetPractitionerDetailUseCase(practRepo),
            )
    }

    @AfterTest fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadPractitioner success populates state`() =
        runTest {
            viewModel.loadPractitioner("p1")
            dispatcher.scheduler.advanceUntilIdle()
            val state = viewModel.uiState.value
            assertFalse(state.isLoading)
            assertNotNull(state.practitioner)
            assertEquals("p1", state.practitioner?.id)
            assertNull(state.error)
        }

    @Test
    fun `loadPractitioner failure sets error`() =
        runTest {
            practRepo.getByIdResult = Result.failure(Exception("not found"))
            viewModel.loadPractitioner("p99")
            dispatcher.scheduler.advanceUntilIdle()
            val state = viewModel.uiState.value
            assertFalse(state.isLoading)
            assertNull(state.practitioner)
            assertNotNull(state.error)
        }

    @Test
    fun `selectSlot updates selectedSlot`() {
        val slot = AppointmentSlot(id = "s1", practitionerId = "p1", dateTime = "2026-06-01T09:00:00", available = true)
        viewModel.selectSlot(slot)
        assertEquals(slot, viewModel.uiState.value.selectedSlot)
    }

    @Test
    fun `clearError clears the error state`() =
        runTest {
            practRepo.getByIdResult = Result.failure(Exception("test error"))
            viewModel.loadPractitioner("p1")
            dispatcher.scheduler.advanceUntilIdle()
            assertNotNull(viewModel.uiState.value.error)

            viewModel.clearError()
            assertNull(viewModel.uiState.value.error)
        }

    @Test
    fun `clearError resets error field`() =
        runTest {
            practRepo.getByIdResult = Result.failure(Exception("err"))
            viewModel.loadPractitioner("p99")
            dispatcher.scheduler.advanceUntilIdle()
            assertNotNull(viewModel.uiState.value.error)

            viewModel.clearError()
            assertNull(viewModel.uiState.value.error)
        }
}

class AppointmentSuccessViewModelTest {
    private val viewModel = AppointmentSuccessViewModel()

    @Test
    fun `initial state has empty fields`() {
        val state = viewModel.uiState.value
        assertEquals("", state.appointmentId)
        assertEquals("", state.practitionerName)
    }

    @Test
    fun `setAppointmentData populates all fields`() {
        viewModel.setAppointmentData("appt1", "Dr Smith", "2026-06-01T10:00:00", "Clinic A")
        val state = viewModel.uiState.value
        assertEquals("appt1", state.appointmentId)
        assertEquals("Dr Smith", state.practitionerName)
        assertEquals("2026-06-01T10:00:00", state.dateTime)
        assertEquals("Clinic A", state.clinicName)
    }

    @Test
    fun `loadAppointment with non-blank id sets appointmentId`() {
        viewModel.loadAppointment("appt42")
        assertEquals("appt42", viewModel.uiState.value.appointmentId)
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class EditProfileViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private lateinit var userRepo: FakeUserRepoV
    private lateinit var viewModel: EditProfileViewModel

    @BeforeTest fun setUp() {
        Dispatchers.setMain(dispatcher)
        userRepo = FakeUserRepoV()
        viewModel = EditProfileViewModel(GetProfileUseCase(userRepo), UpdateProfileUseCase(userRepo))
    }

    @AfterTest fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init loads profile and populates fields`() =
        runTest {
            dispatcher.scheduler.advanceUntilIdle()
            val state = viewModel.uiState.value
            assertFalse(state.isLoading)
            assertEquals("Anna", state.firstName)
            assertEquals("Test", state.lastName)
        }

    @Test
    fun `init load failure sets error`() =
        runTest {
            userRepo.profileResult = Result.failure(Exception("unauthenticated"))
            viewModel = EditProfileViewModel(GetProfileUseCase(userRepo), UpdateProfileUseCase(userRepo))
            dispatcher.scheduler.advanceUntilIdle()
            assertNotNull(viewModel.uiState.value.error)
        }

    @Test
    fun `save success sets isSaved`() =
        runTest {
            dispatcher.scheduler.advanceUntilIdle()
            viewModel.save()
            dispatcher.scheduler.advanceUntilIdle()
            assertTrue(viewModel.uiState.value.isSaved)
            assertNull(viewModel.uiState.value.error)
        }

    @Test
    fun `save failure sets error`() =
        runTest {
            dispatcher.scheduler.advanceUntilIdle()
            userRepo.updateResult = Result.failure(Exception("server error"))
            viewModel.save()
            dispatcher.scheduler.advanceUntilIdle()
            assertNotNull(viewModel.uiState.value.error)
            assertFalse(viewModel.uiState.value.isSaved)
        }
}

@OptIn(ExperimentalCoroutinesApi::class)
class ChangeEmailViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private lateinit var userRepo: FakeUserRepoV
    private lateinit var viewModel: ChangeEmailViewModel

    @BeforeTest fun setUp() {
        Dispatchers.setMain(dispatcher)
        userRepo = FakeUserRepoV()
        viewModel = ChangeEmailViewModel(ChangeEmailUseCase(userRepo))
    }

    @AfterTest fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `changeEmail with empty new email sets error`() {
        viewModel.changeEmail()
        assertNotNull(viewModel.uiState.value.error)
    }

    @Test
    fun `changeEmail with invalid email sets error`() {
        viewModel.updateNewEmail("notanemail")
        viewModel.updateConfirmEmail("notanemail")
        viewModel.changeEmail()
        assertNotNull(viewModel.uiState.value.error)
    }

    @Test
    fun `changeEmail with mismatched emails sets error`() {
        viewModel.updateNewEmail("a@b.com")
        viewModel.updateConfirmEmail("c@d.com")
        viewModel.changeEmail()
        assertNotNull(viewModel.uiState.value.error)
    }

    @Test
    fun `changeEmail success sets isSuccess`() =
        runTest {
            viewModel.updateNewEmail("new@test.lu")
            viewModel.updateConfirmEmail("new@test.lu")
            viewModel.changeEmail()
            dispatcher.scheduler.advanceUntilIdle()
            assertTrue(viewModel.uiState.value.isSuccess)
            assertNull(viewModel.uiState.value.error)
        }

    @Test
    fun `changeEmail failure sets error`() =
        runTest {
            userRepo.changeEmailResult = Result.failure(Exception("wrong password"))
            viewModel.updateNewEmail("new@test.lu")
            viewModel.updateConfirmEmail("new@test.lu")
            viewModel.changeEmail()
            dispatcher.scheduler.advanceUntilIdle()
            assertFalse(viewModel.uiState.value.isSuccess)
            assertNotNull(viewModel.uiState.value.error)
        }
}

@OptIn(ExperimentalCoroutinesApi::class)
class ChangePasswordViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private lateinit var userRepo: FakeUserRepoV
    private lateinit var viewModel: ChangePasswordViewModel

    @BeforeTest fun setUp() {
        Dispatchers.setMain(dispatcher)
        userRepo = FakeUserRepoV()
        viewModel = ChangePasswordViewModel(ChangePasswordUseCase(userRepo))
    }

    @AfterTest fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `changePassword with blank old password sets error`() {
        viewModel.changePassword()
        assertNotNull(viewModel.uiState.value.error)
    }

    @Test
    fun `changePassword with mismatched passwords sets error`() {
        viewModel.updateOldPassword("old")
        viewModel.updateNewPassword("new1234!")
        viewModel.updateConfirmPassword("different!")
        viewModel.changePassword()
        assertNotNull(viewModel.uiState.value.error)
    }

    @Test
    fun `changePassword with too short password sets error`() {
        viewModel.updateOldPassword("old")
        viewModel.updateNewPassword("short")
        viewModel.updateConfirmPassword("short")
        viewModel.changePassword()
        assertNotNull(viewModel.uiState.value.error)
    }

    @Test
    fun `changePassword success sets isSuccess`() =
        runTest {
            viewModel.updateOldPassword("oldPass")
            viewModel.updateNewPassword("newPass123!!")
            viewModel.updateConfirmPassword("newPass123!!")
            viewModel.changePassword()
            dispatcher.scheduler.advanceUntilIdle()
            assertTrue(viewModel.uiState.value.isSuccess)
            assertNull(viewModel.uiState.value.error)
        }

    @Test
    fun `changePassword failure sets error`() =
        runTest {
            userRepo.changePasswordResult = Result.failure(Exception("wrong old password"))
            viewModel.updateOldPassword("wrong")
            viewModel.updateNewPassword("newPass123!!")
            viewModel.updateConfirmPassword("newPass123!!")
            viewModel.changePassword()
            dispatcher.scheduler.advanceUntilIdle()
            assertFalse(viewModel.uiState.value.isSuccess)
            assertNotNull(viewModel.uiState.value.error)
        }
}

// ── Shared test data ─────────────────────────────────────────────────────────

private val vmTestUser =
    User(
        id = "u1", firstName = "Anna", lastName = "Test", email = "anna@test.lu",
        phone = "+352600000000", gender = "female", dateOfBirth = "1990-01-01",
        cnsNumber = "1234567890", profileType = ProfileType.PATIENT, language = "fr",
    )

private val vmTestPractitioner =
    Practitioner(
        id = "p1", firstName = "Dr", lastName = "Smith", specialty = "General",
        clinicName = "Clinic A", address = "1 Main St", city = "Luxembourg",
        phone = "+352000000", email = "doc@clinic.lu", latitude = 49.6, longitude = 6.1,
        acceptingNewPatients = true, availableSlots = emptyList(), schedule = emptyList(),
        paymentMethods = emptyList(), diplomas = emptyList(), isFavorite = false,
    )

// ── Fakes ─────────────────────────────────────────────────────────────────────

private class FakeAuthRepoV : AuthRepository {
    var loggedIn = true

    override suspend fun login(
        email: String,
        password: String,
    ) = Result.success(vmTestUser)

    override suspend fun register(
        user: User,
        password: String,
    ) = Result.success(vmTestUser)

    override suspend fun forgotPassword(email: String) = Result.success(Unit)

    override suspend fun refreshToken() = Result.success("token")

    override suspend fun logout() = Result.success(Unit)

    override fun isLoggedIn() = loggedIn

    override fun getCurrentUser() = vmTestUser
}

private class FakePractRepoV : PractitionerRepository {
    var getByIdResult: Result<Practitioner> = Result.success(vmTestPractitioner)
    var toggleResult: Result<Unit> = Result.success(Unit)

    override suspend fun searchPractitioners(
        location: String,
        specialty: String,
        page: Int,
        limit: Int,
    ) = Result.success(listOf(vmTestPractitioner))

    override suspend fun getPractitionerById(id: String) = getByIdResult

    override suspend fun toggleFavorite(practitionerId: String) = toggleResult
}

private class FakeUserRepoV : UserRepository {
    var profileResult: Result<User> = Result.success(vmTestUser)
    var updateResult: Result<User> = Result.success(vmTestUser)
    var changeEmailResult: Result<Unit> = Result.success(Unit)
    var changePasswordResult: Result<Unit> = Result.success(Unit)

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
