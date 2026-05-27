package lu.esklepios.app

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import lu.esklepios.app.domain.model.*
import lu.esklepios.app.domain.repository.AppointmentRepository
import kotlin.test.*

/**
 * Tests for AppointmentRepository contract using a fake implementation.
 * These verify the expected behaviour of any implementation conforming to the interface.
 */
class AppointmentRepositoryTest {

    private class FakeAppointmentRepository(
        private val createResult: Result<Appointment> = Result.success(makeAppointment("created")),
        private val upcomingResult: Result<List<Appointment>> = Result.success(emptyList()),
        private val pastResult: Result<List<Appointment>> = Result.success(emptyList()),
        private val cancelResult: Result<Unit> = Result.success(Unit),
        private val modifyResult: Result<Appointment> = Result.success(makeAppointment("modified"))
    ) : AppointmentRepository {
        val cancelledIds = mutableListOf<String>()

        override suspend fun createAppointment(appointment: Appointment) = createResult
        override fun getAppointments(userId: String): Flow<List<Appointment>> = flowOf(emptyList())
        override suspend fun getUpcomingAppointments(userId: String) = upcomingResult
        override suspend fun getPastAppointments(userId: String) = pastResult
        override suspend fun modifyAppointment(appointment: Appointment) = modifyResult
        override suspend fun cancelAppointment(id: String): Result<Unit> {
            cancelledIds.add(id)
            return cancelResult
        }
    }

    companion object {
        fun makeAppointment(id: String, status: AppointmentStatus = AppointmentStatus.CONFIRMED) = Appointment(
            id = id,
            practitionerId = "prac1",
            practitionerName = "Dr. Test",
            clinicName = "Test Clinic",
            specialty = "GP",
            dateTime = "2025-06-01T10:00",
            status = status
        )
    }

    @Test
    fun `createAppointment returns created appointment`() = runTest {
        val expected = makeAppointment("new-id")
        val repo = FakeAppointmentRepository(createResult = Result.success(expected))

        val result = repo.createAppointment(makeAppointment(""))

        assertTrue(result.isSuccess)
        assertEquals("new-id", result.getOrNull()?.id)
    }

    @Test
    fun `createAppointment failure propagates error`() = runTest {
        val repo = FakeAppointmentRepository(createResult = Result.failure(Exception("Booking unavailable")))

        val result = repo.createAppointment(makeAppointment(""))

        assertTrue(result.isFailure)
        assertEquals("Booking unavailable", result.exceptionOrNull()?.message)
    }

    @Test
    fun `getUpcomingAppointments returns list`() = runTest {
        val appointments = listOf(makeAppointment("1"), makeAppointment("2"))
        val repo = FakeAppointmentRepository(upcomingResult = Result.success(appointments))

        val result = repo.getUpcomingAppointments("user1")

        assertTrue(result.isSuccess)
        assertEquals(2, result.getOrNull()?.size)
    }

    @Test
    fun `getPastAppointments returns completed appointments`() = runTest {
        val past = listOf(
            makeAppointment("3", AppointmentStatus.COMPLETED),
            makeAppointment("4", AppointmentStatus.CANCELLED)
        )
        val repo = FakeAppointmentRepository(pastResult = Result.success(past))

        val result = repo.getPastAppointments("user1")

        assertTrue(result.isSuccess)
        assertEquals(2, result.getOrNull()?.size)
        assertEquals(AppointmentStatus.COMPLETED, result.getOrNull()?.get(0)?.status)
    }

    @Test
    fun `cancelAppointment delegates to repository with correct id`() = runTest {
        val repo = FakeAppointmentRepository(cancelResult = Result.success(Unit))

        val result = repo.cancelAppointment("appointment-99")

        assertTrue(result.isSuccess)
        assertTrue(repo.cancelledIds.contains("appointment-99"))
    }

    @Test
    fun `cancelAppointment failure propagates`() = runTest {
        val repo = FakeAppointmentRepository(cancelResult = Result.failure(Exception("Cannot cancel")))

        val result = repo.cancelAppointment("appt1")

        assertTrue(result.isFailure)
        assertEquals("Cannot cancel", result.exceptionOrNull()?.message)
    }

    @Test
    fun `modifyAppointment returns modified appointment`() = runTest {
        val modified = makeAppointment("mod-id")
        val repo = FakeAppointmentRepository(modifyResult = Result.success(modified))

        val result = repo.modifyAppointment(makeAppointment("appt1"))

        assertTrue(result.isSuccess)
        assertEquals("mod-id", result.getOrNull()?.id)
    }
}
