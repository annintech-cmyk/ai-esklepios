package lu.esklepios.app

import kotlinx.coroutines.test.runTest
import lu.esklepios.app.domain.model.*
import lu.esklepios.app.domain.repository.PractitionerRepository
import kotlin.test.*

/**
 * Tests for PractitionerRepository contract using a fake implementation.
 * Per Rule T-1, no MockK or real SQLDelight driver in commonTest.
 */
class PractitionerRepositoryTest {

    private class FakePractitionerRepository : PractitionerRepository {
        var searchResult: Result<List<Practitioner>> = Result.success(emptyList())
        var getByIdResult: Result<Practitioner> = Result.success(fakePractitioner("1"))
        var toggleResult: Result<Unit> = Result.success(Unit)
        var toggleCallCount = 0

        override suspend fun searchPractitioners(location: String, specialty: String, page: Int, limit: Int) = searchResult
        override suspend fun getPractitionerById(id: String) = getByIdResult
        override suspend fun toggleFavorite(practitionerId: String): Result<Unit> {
            toggleCallCount++
            return toggleResult
        }
    }

    companion object {
        fun fakePractitioner(id: String) = Practitioner(
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
            isFavorite = false
        )
    }

    @Test
    fun `searchPractitioners returns list on success`() = runTest {
        val practitioners = listOf(fakePractitioner("1"), fakePractitioner("2"))
        val repo = FakePractitionerRepository().also { it.searchResult = Result.success(practitioners) }

        val result = repo.searchPractitioners("Luxembourg", "GP", 1, 20)

        assertTrue(result.isSuccess)
        assertEquals(2, result.getOrNull()?.size)
        assertEquals("1", result.getOrNull()?.get(0)?.id)
    }

    @Test
    fun `searchPractitioners propagates error`() = runTest {
        val repo = FakePractitionerRepository().also {
            it.searchResult = Result.failure(Exception("Network failure"))
        }

        val result = repo.searchPractitioners("Luxembourg", "GP", 1, 20)

        assertTrue(result.isFailure)
        assertEquals("Network failure", result.exceptionOrNull()?.message)
    }

    @Test
    fun `getPractitionerById returns practitioner on success`() = runTest {
        val repo = FakePractitionerRepository().also {
            it.getByIdResult = Result.success(fakePractitioner("prac42"))
        }

        val result = repo.getPractitionerById("prac42")

        assertTrue(result.isSuccess)
        assertEquals("prac42", result.getOrNull()?.id)
        assertEquals("Jane", result.getOrNull()?.firstName)
    }

    @Test
    fun `getPractitionerById propagates not found error`() = runTest {
        val repo = FakePractitionerRepository().also {
            it.getByIdResult = Result.failure(Exception("Not found"))
        }

        val result = repo.getPractitionerById("unknown")

        assertTrue(result.isFailure)
    }

    @Test
    fun `toggleFavorite returns success`() = runTest {
        val repo = FakePractitionerRepository()

        val result = repo.toggleFavorite("prac1")

        assertTrue(result.isSuccess)
        assertEquals(1, repo.toggleCallCount)
    }

    @Test
    fun `toggleFavorite propagates failure`() = runTest {
        val repo = FakePractitionerRepository().also {
            it.toggleResult = Result.failure(Exception("DB error"))
        }

        val result = repo.toggleFavorite("prac1")

        assertTrue(result.isFailure)
    }
}
