package lu.esklepios.app

import kotlinx.coroutines.test.runTest
import lu.esklepios.app.data.network.*
import lu.esklepios.app.data.repository.PractitionerRepositoryImpl
import lu.esklepios.app.domain.model.*
import kotlin.test.*

class PractitionerRepositoryTest {

    // Minimal fake ApiService for practitioner tests
    private class FakeApiService(
        private val searchResult: Result<List<PractitionerDto>> = Result.success(emptyList()),
        private val getByIdResult: Result<PractitionerDto> = Result.success(fakePractitionerDto("1"))
    ) : ApiService {
        var toggleFavoriteCallCount = 0

        override suspend fun searchPractitioners(location: String, specialty: String, page: Int, limit: Int) = searchResult
        override suspend fun getPractitioner(id: String) = getByIdResult
        override suspend fun login(email: String, password: String): Result<LoginResponse> = Result.failure(Exception("not implemented"))
        override suspend fun register(request: RegisterRequest): Result<RegisterResponse> = Result.failure(Exception("not implemented"))
        override suspend fun forgotPassword(email: String): Result<ForgotPasswordResponse> = Result.failure(Exception("not implemented"))
        override suspend fun refreshToken(refreshToken: String): Result<RefreshTokenResponse> = Result.failure(Exception("not implemented"))
        override suspend fun createAppointment(request: CreateAppointmentRequest): Result<AppointmentDto> = Result.failure(Exception("not implemented"))
        override suspend fun getAppointments(userId: String): Result<List<AppointmentDto>> = Result.success(emptyList())
        override suspend fun modifyAppointment(id: String, request: ModifyAppointmentRequest): Result<AppointmentDto> = Result.failure(Exception("not implemented"))
        override suspend fun cancelAppointment(id: String): Result<Unit> = Result.success(Unit)
        override suspend fun getProfile(): Result<UserDto> = Result.failure(Exception("not implemented"))
        override suspend fun updateProfile(request: UpdateProfileRequest): Result<UserDto> = Result.failure(Exception("not implemented"))
        override suspend fun changeEmail(newEmail: String, password: String): Result<Unit> = Result.failure(Exception("not implemented"))
        override suspend fun changePassword(oldPassword: String, newPassword: String): Result<Unit> = Result.failure(Exception("not implemented"))
    }

    private class FakeTokenStorage : TokenStorage {
        private var token: String? = null
        private var refreshToken: String? = null
        override fun getToken() = token
        override fun setToken(token: String) { this.token = token }
        override fun getRefreshToken() = refreshToken
        override fun setRefreshToken(token: String) { this.refreshToken = token }
        override fun clear() { token = null; refreshToken = null }
    }

    companion object {
        fun fakePractitionerDto(id: String) = PractitionerDto(
            id = id,
            firstName = "Jane",
            lastName = "Doctor",
            specialty = "GP",
            clinicName = "Test Clinic",
            address = "1 Test St",
            city = "Luxembourg",
            phone = "+352 000",
            email = "jane@test.lu",
            latitude = 49.6,
            longitude = 6.1,
            acceptingNewPatients = true,
            availableSlots = emptyList(),
            schedule = emptyList(),
            paymentMethods = emptyList(),
            diplomas = emptyList(),
            isFavorite = false
        )
    }

    @Test
    fun `searchPractitioners returns list from api`() = runTest {
        val dtoList = listOf(fakePractitionerDto("1"), fakePractitionerDto("2"))
        val repo = PractitionerRepositoryImpl(
            FakeApiService(searchResult = Result.success(dtoList)),
            FakeTokenStorage()
        )

        val result = repo.searchPractitioners("Luxembourg", "GP", 1, 20)

        assertTrue(result.isSuccess)
        assertEquals(2, result.getOrNull()?.size)
        assertEquals("1", result.getOrNull()?.get(0)?.id)
    }

    @Test
    fun `searchPractitioners propagates api error`() = runTest {
        val repo = PractitionerRepositoryImpl(
            FakeApiService(searchResult = Result.failure(Exception("Network failure"))),
            FakeTokenStorage()
        )

        val result = repo.searchPractitioners("Luxembourg", "GP", 1, 20)

        assertTrue(result.isFailure)
        assertEquals("Network failure", result.exceptionOrNull()?.message)
    }

    @Test
    fun `getPractitionerById returns practitioner`() = runTest {
        val dto = fakePractitionerDto("prac42")
        val repo = PractitionerRepositoryImpl(
            FakeApiService(getByIdResult = Result.success(dto)),
            FakeTokenStorage()
        )

        val result = repo.getPractitionerById("prac42")

        assertTrue(result.isSuccess)
        assertEquals("prac42", result.getOrNull()?.id)
        assertEquals("Jane", result.getOrNull()?.firstName)
    }

    @Test
    fun `getPractitionerById propagates not found error`() = runTest {
        val repo = PractitionerRepositoryImpl(
            FakeApiService(getByIdResult = Result.failure(Exception("Not found"))),
            FakeTokenStorage()
        )

        val result = repo.getPractitionerById("unknown")

        assertTrue(result.isFailure)
    }

    @Test
    fun `toggleFavorite returns success`() = runTest {
        val repo = PractitionerRepositoryImpl(
            FakeApiService(),
            FakeTokenStorage()
        )

        val result = repo.toggleFavorite("prac1")

        assertTrue(result.isSuccess)
    }
}
