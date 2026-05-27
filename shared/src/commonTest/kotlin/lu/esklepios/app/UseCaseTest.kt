package lu.esklepios.app

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import lu.esklepios.app.domain.model.Appointment
import lu.esklepios.app.domain.model.AppointmentStatus
import lu.esklepios.app.domain.model.Practitioner
import lu.esklepios.app.domain.model.ProfileType
import lu.esklepios.app.domain.model.User
import lu.esklepios.app.domain.repository.AppointmentRepository
import lu.esklepios.app.domain.repository.AuthRepository
import lu.esklepios.app.domain.repository.PractitionerRepository
import lu.esklepios.app.domain.repository.UserRepository
import lu.esklepios.app.domain.usecase.CancelAppointmentUseCase
import lu.esklepios.app.domain.usecase.ChangeEmailUseCase
import lu.esklepios.app.domain.usecase.ChangePasswordUseCase
import lu.esklepios.app.domain.usecase.CreateAppointmentUseCase
import lu.esklepios.app.domain.usecase.ForgotPasswordUseCase
import lu.esklepios.app.domain.usecase.GetPastAppointmentsUseCase
import lu.esklepios.app.domain.usecase.GetPractitionerDetailUseCase
import lu.esklepios.app.domain.usecase.GetProfileUseCase
import lu.esklepios.app.domain.usecase.GetUpcomingAppointmentsUseCase
import lu.esklepios.app.domain.usecase.LoginUseCase
import lu.esklepios.app.domain.usecase.LogoutUseCase
import lu.esklepios.app.domain.usecase.ModifyAppointmentUseCase
import lu.esklepios.app.domain.usecase.RegisterUseCase
import lu.esklepios.app.domain.usecase.SearchPractitionersUseCase
import lu.esklepios.app.domain.usecase.ToggleFavoriteUseCase
import lu.esklepios.app.domain.usecase.UpdateProfileUseCase
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

// ── Auth use cases ──────────────────────────────────────────────────────────

class LoginUseCaseTest {
    private val repo = FakeAuthRepo()
    private val useCase = LoginUseCase(repo)

    @Test
    fun `delegates to repository and returns success`() = runTest {
        val result = useCase("user@test.lu", "pass")
        assertTrue(result.isSuccess)
        assertEquals(testUser, result.getOrNull())
    }

    @Test
    fun `propagates repository failure`() = runTest {
        repo.loginResult = Result.failure(Exception("bad credentials"))
        val result = useCase("user@test.lu", "wrong")
        assertTrue(result.isFailure)
    }
}

class RegisterUseCaseTest {
    private val repo = FakeAuthRepo()
    private val useCase = RegisterUseCase(repo)

    @Test
    fun `delegates to repository and returns registered user`() = runTest {
        val result = useCase(testUser, "pass")
        assertTrue(result.isSuccess)
    }

    @Test
    fun `propagates repository failure`() = runTest {
        repo.registerResult = Result.failure(Exception("email taken"))
        val result = useCase(testUser, "pass")
        assertTrue(result.isFailure)
    }
}

class ForgotPasswordUseCaseTest {
    private val repo = FakeAuthRepo()
    private val useCase = ForgotPasswordUseCase(repo)

    @Test
    fun `delegates to repository and returns success`() = runTest {
        val result = useCase("user@test.lu")
        assertTrue(result.isSuccess)
    }

    @Test
    fun `propagates repository failure`() = runTest {
        repo.forgotPasswordResult = Result.failure(Exception("not found"))
        val result = useCase("user@test.lu")
        assertTrue(result.isFailure)
    }
}

class LogoutUseCaseTest2 {
    private val repo = FakeAuthRepo()
    private val useCase = LogoutUseCase(repo)

    @Test
    fun `delegates to repository and returns success`() = runTest {
        val result = useCase()
        assertTrue(result.isSuccess)
    }

    @Test
    fun `propagates repository failure`() = runTest {
        repo.logoutResult = Result.failure(Exception("storage error"))
        val result = useCase()
        assertTrue(result.isFailure)
    }
}

// ── Practitioner use cases ───────────────────────────────────────────────────

class SearchPractitionersUseCaseTest {
    private val repo = FakePractRepo()
    private val useCase = SearchPractitionersUseCase(repo)

    @Test
    fun `delegates to repository with correct parameters`() = runTest {
        val result = useCase("Luxembourg", "Cardiology", page = 2, limit = 10)
        assertTrue(result.isSuccess)
        assertEquals("Luxembourg", repo.lastLocation)
        assertEquals("Cardiology", repo.lastSpecialty)
    }

    @Test
    fun `propagates repository failure`() = runTest {
        repo.searchResult = Result.failure(Exception("network"))
        assertTrue(useCase("", "").isFailure)
    }
}

class GetPractitionerDetailUseCaseTest {
    private val repo = FakePractRepo()
    private val useCase = GetPractitionerDetailUseCase(repo)

    @Test
    fun `delegates to repository and returns practitioner`() = runTest {
        val result = useCase("p1")
        assertTrue(result.isSuccess)
        assertEquals("p1", repo.lastId)
    }

    @Test
    fun `propagates not-found failure`() = runTest {
        repo.getByIdResult = Result.failure(Exception("not found"))
        assertTrue(useCase("p99").isFailure)
    }
}

class ToggleFavoriteUseCaseTest {
    private val repo = FakePractRepo()
    private val useCase = ToggleFavoriteUseCase(repo)

    @Test
    fun `delegates to repository with practitioner id`() = runTest {
        val result = useCase("p1")
        assertTrue(result.isSuccess)
        assertEquals("p1", repo.lastToggleId)
    }

    @Test
    fun `propagates repository failure`() = runTest {
        repo.toggleResult = Result.failure(Exception("not cached"))
        assertTrue(useCase("p99").isFailure)
    }
}

// ── Appointment use cases ────────────────────────────────────────────────────

class CreateAppointmentUseCaseTest {
    private val repo = FakeApptRepo()
    private val useCase = CreateAppointmentUseCase(repo)

    @Test
    fun `delegates to repository and returns created appointment`() = runTest {
        val result = useCase(testAppointment)
        assertTrue(result.isSuccess)
    }

    @Test
    fun `propagates repository failure`() = runTest {
        repo.createResult = Result.failure(Exception("slot unavailable"))
        assertTrue(useCase(testAppointment).isFailure)
    }
}

class ModifyAppointmentUseCaseTest {
    private val repo = FakeApptRepo()
    private val useCase = ModifyAppointmentUseCase(repo)

    @Test
    fun `delegates to repository and returns modified appointment`() = runTest {
        val result = useCase(testAppointment)
        assertTrue(result.isSuccess)
    }

    @Test
    fun `propagates repository failure`() = runTest {
        repo.modifyResult = Result.failure(Exception("conflict"))
        assertTrue(useCase(testAppointment).isFailure)
    }
}

class CancelAppointmentUseCaseTest {
    private val repo = FakeApptRepo()
    private val useCase = CancelAppointmentUseCase(repo)

    @Test
    fun `delegates to repository with appointment id`() = runTest {
        val result = useCase("appt1")
        assertTrue(result.isSuccess)
        assertEquals("appt1", repo.lastCancelledId)
    }

    @Test
    fun `propagates repository failure`() = runTest {
        repo.cancelResult = Result.failure(Exception("already cancelled"))
        assertTrue(useCase("appt1").isFailure)
    }
}

class GetUpcomingAppointmentsUseCaseTest {
    private val repo = FakeApptRepo()
    private val useCase = GetUpcomingAppointmentsUseCase(repo)

    @Test
    fun `delegates to repository and returns upcoming list`() = runTest {
        val result = useCase("user1")
        assertTrue(result.isSuccess)
    }

    @Test
    fun `propagates repository failure`() = runTest {
        repo.upcomingResult = Result.failure(Exception("db error"))
        assertTrue(useCase("user1").isFailure)
    }
}

class GetPastAppointmentsUseCaseTest {
    private val repo = FakeApptRepo()
    private val useCase = GetPastAppointmentsUseCase(repo)

    @Test
    fun `delegates to repository and returns past list`() = runTest {
        val result = useCase("user1")
        assertTrue(result.isSuccess)
    }

    @Test
    fun `propagates repository failure`() = runTest {
        repo.pastResult = Result.failure(Exception("db error"))
        assertTrue(useCase("user1").isFailure)
    }
}

// ── User use cases ───────────────────────────────────────────────────────────

class GetProfileUseCaseTest {
    private val repo = FakeUserRepo()
    private val useCase = GetProfileUseCase(repo)

    @Test
    fun `delegates to repository and returns profile`() = runTest {
        val result = useCase()
        assertTrue(result.isSuccess)
        assertEquals(testUser, result.getOrNull())
    }

    @Test
    fun `propagates repository failure`() = runTest {
        repo.profileResult = Result.failure(Exception("unauthenticated"))
        assertTrue(useCase().isFailure)
    }
}

class UpdateProfileUseCaseTest {
    private val repo = FakeUserRepo()
    private val useCase = UpdateProfileUseCase(repo)

    @Test
    fun `delegates to repository and returns updated user`() = runTest {
        val result = useCase(testUser)
        assertTrue(result.isSuccess)
    }

    @Test
    fun `propagates repository failure`() = runTest {
        repo.updateResult = Result.failure(Exception("validation error"))
        assertTrue(useCase(testUser).isFailure)
    }
}

class ChangeEmailUseCaseTest {
    private val repo = FakeUserRepo()
    private val useCase = ChangeEmailUseCase(repo)

    @Test
    fun `delegates to repository and returns success`() = runTest {
        val result = useCase("new@test.lu", "pass")
        assertTrue(result.isSuccess)
    }

    @Test
    fun `propagates repository failure`() = runTest {
        repo.changeEmailResult = Result.failure(Exception("wrong password"))
        assertTrue(useCase("new@test.lu", "wrong").isFailure)
    }
}

class ChangePasswordUseCaseTest {
    private val repo = FakeUserRepo()
    private val useCase = ChangePasswordUseCase(repo)

    @Test
    fun `delegates to repository and returns success`() = runTest {
        val result = useCase("oldPass", "newPass123!")
        assertTrue(result.isSuccess)
    }

    @Test
    fun `propagates repository failure`() = runTest {
        repo.changePasswordResult = Result.failure(Exception("wrong old password"))
        assertTrue(useCase("wrong", "newPass").isFailure)
    }
}

// ── Shared test data ─────────────────────────────────────────────────────────

private val testUser = User(
    id = "u1",
    firstName = "Anna",
    lastName = "Test",
    email = "anna@test.lu",
    phone = "+352600000000",
    gender = "female",
    dateOfBirth = "1990-01-01",
    cnsNumber = "1234567890",
    profileType = ProfileType.PATIENT,
    language = "fr"
)

private val testAppointment = Appointment(
    id = "appt1",
    practitionerId = "p1",
    practitionerName = "Dr Smith",
    clinicName = "Clinic A",
    specialty = "General",
    dateTime = "2026-06-01T10:00:00",
    status = AppointmentStatus.PENDING
)

private val testPractitioner = Practitioner(
    id = "p1",
    firstName = "Dr",
    lastName = "Smith",
    specialty = "General",
    clinicName = "Clinic A",
    address = "1 Main St",
    city = "Luxembourg",
    phone = "+352000000",
    email = "doc@clinic.lu",
    latitude = 49.6,
    longitude = 6.1,
    acceptingNewPatients = true,
    availableSlots = emptyList(),
    schedule = emptyList(),
    paymentMethods = emptyList(),
    diplomas = emptyList(),
    isFavorite = false
)

// ── Fakes ────────────────────────────────────────────────────────────────────

private class FakeAuthRepo : AuthRepository {
    var loginResult: Result<User> = Result.success(testUser)
    var registerResult: Result<User> = Result.success(testUser)
    var forgotPasswordResult: Result<Unit> = Result.success(Unit)
    var logoutResult: Result<Unit> = Result.success(Unit)

    override suspend fun login(email: String, password: String) = loginResult
    override suspend fun register(user: User, password: String) = registerResult
    override suspend fun forgotPassword(email: String) = forgotPasswordResult
    override suspend fun refreshToken(): Result<String> = Result.success("token")
    override suspend fun logout() = logoutResult
    override fun isLoggedIn() = true
    override fun getCurrentUser() = testUser
}

private class FakePractRepo : PractitionerRepository {
    var searchResult: Result<List<Practitioner>> = Result.success(listOf(testPractitioner))
    var getByIdResult: Result<Practitioner> = Result.success(testPractitioner)
    var toggleResult: Result<Unit> = Result.success(Unit)
    var lastLocation = ""
    var lastSpecialty = ""
    var lastId = ""
    var lastToggleId = ""

    override suspend fun searchPractitioners(location: String, specialty: String, page: Int, limit: Int): Result<List<Practitioner>> {
        lastLocation = location
        lastSpecialty = specialty
        return searchResult
    }

    override suspend fun getPractitionerById(id: String): Result<Practitioner> {
        lastId = id
        return getByIdResult
    }

    override suspend fun toggleFavorite(practitionerId: String): Result<Unit> {
        lastToggleId = practitionerId
        return toggleResult
    }
}

private class FakeApptRepo : AppointmentRepository {
    var createResult: Result<Appointment> = Result.success(testAppointment)
    var modifyResult: Result<Appointment> = Result.success(testAppointment)
    var cancelResult: Result<Unit> = Result.success(Unit)
    var upcomingResult: Result<List<Appointment>> = Result.success(listOf(testAppointment))
    var pastResult: Result<List<Appointment>> = Result.success(listOf(testAppointment))
    var lastCancelledId = ""

    override suspend fun createAppointment(appointment: Appointment) = createResult
    override fun getAppointments(userId: String): Flow<List<Appointment>> = flowOf(listOf(testAppointment))
    override suspend fun getUpcomingAppointments(userId: String) = upcomingResult
    override suspend fun getPastAppointments(userId: String) = pastResult
    override suspend fun modifyAppointment(appointment: Appointment) = modifyResult
    override suspend fun cancelAppointment(id: String): Result<Unit> {
        lastCancelledId = id
        return cancelResult
    }
}

private class FakeUserRepo : UserRepository {
    var profileResult: Result<User> = Result.success(testUser)
    var updateResult: Result<User> = Result.success(testUser)
    var changeEmailResult: Result<Unit> = Result.success(Unit)
    var changePasswordResult: Result<Unit> = Result.success(Unit)

    override suspend fun getProfile() = profileResult
    override suspend fun updateProfile(user: User) = updateResult
    override suspend fun changeEmail(newEmail: String, password: String) = changeEmailResult
    override suspend fun changePassword(oldPassword: String, newPassword: String) = changePasswordResult
}
