package lu.esklepios.app

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import lu.esklepios.app.domain.model.AppointmentSlot
import lu.esklepios.app.domain.model.Practitioner
import lu.esklepios.app.domain.model.ScheduleEntry
import lu.esklepios.app.domain.repository.PractitionerRepository
import lu.esklepios.app.domain.usecase.SearchPractitionersUseCase
import lu.esklepios.app.domain.usecase.ToggleFavoriteUseCase
import lu.esklepios.app.presentation.viewmodel.HomeViewModel
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    // Fixed clock: 2026-05-24T00:00:00Z
    private val fixedClock = object : Clock {
        override fun now(): Instant = Instant.parse("2026-05-24T00:00:00Z")
    }

    private lateinit var fakePractitionerRepository: FakePractitionerRepositoryForHome
    private lateinit var viewModel: HomeViewModel

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakePractitionerRepository = FakePractitionerRepositoryForHome()
        val searchUseCase = SearchPractitionersUseCase(fakePractitionerRepository)
        val toggleUseCase = ToggleFavoriteUseCase(fakePractitionerRepository)
        viewModel = HomeViewModel(searchUseCase, toggleUseCase, fixedClock)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state has no search results and is not loading`() {
        // Before advancing dispatcher, the init block is queued but not run
        val state = viewModel.uiState.value
        assertFalse(state.hasSearched)
        assertNull(state.error)
    }

    @Test
    fun `search success populates practitioners`() = runTest {
        fakePractitionerRepository.searchResult = Result.success(listOf(samplePractitioner("p1")))
        viewModel.search()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(1, state.allPractitioners.size)
        assertNull(state.error)
    }

    @Test
    fun `search failure sets error message`() = runTest {
        fakePractitionerRepository.searchResult = Result.failure(Exception("network error"))
        viewModel.search()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNotNull(state.error)
        assertTrue(state.error!!.contains("network error"))
    }

    @Test
    fun `setDateFilter Today uses injected clock`() = runTest {
        // Slot on fixed clock's date (2026-05-24)
        val todaySlot = AppointmentSlot(id = "s1", practitionerId = "p1", dateTime = "2026-05-24T09:00:00", available = true)
        val tomorrowSlot = AppointmentSlot(id = "s2", practitionerId = "p2", dateTime = "2026-05-25T09:00:00", available = true)
        fakePractitionerRepository.searchResult = Result.success(listOf(
            samplePractitioner("p1", slots = listOf(todaySlot)),
            samplePractitioner("p2", slots = listOf(tomorrowSlot))
        ))
        viewModel.search()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.setDateFilter("Today")
        val filtered = viewModel.uiState.value.practitioners
        assertEquals(1, filtered.size)
        assertEquals("p1", filtered.first().id)
    }

    @Test
    fun `toggleNewPatientsFilter excludes closed practitioners`() = runTest {
        val open = samplePractitioner("p1", acceptingNew = true)
        val closed = samplePractitioner("p2", acceptingNew = false)
        fakePractitionerRepository.searchResult = Result.success(listOf(open, closed))
        viewModel.search()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.toggleNewPatientsFilter()
        val state = viewModel.uiState.value
        assertTrue(state.openToNewPatients)
        assertEquals(1, state.practitioners.size)
        assertEquals("p1", state.practitioners.first().id)
    }

    @Test
    fun `clearError resets error field`() = runTest {
        fakePractitionerRepository.searchResult = Result.failure(Exception("err"))
        viewModel.search()
        testDispatcher.scheduler.advanceUntilIdle()
        assertNotNull(viewModel.uiState.value.error)

        viewModel.clearError()
        assertNull(viewModel.uiState.value.error)
    }

    @Test
    fun `toggleFavorite flips isFavorite on matching practitioner`() = runTest {
        val p = samplePractitioner("p1", isFavorite = false)
        fakePractitionerRepository.searchResult = Result.success(listOf(p))
        fakePractitionerRepository.toggleResult = Result.success(Unit)
        viewModel.search()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.toggleFavorite("p1")
        testDispatcher.scheduler.advanceUntilIdle()

        val updated = viewModel.uiState.value.allPractitioners.first { it.id == "p1" }
        assertTrue(updated.isFavorite)
    }

    // --- Helpers ---

    private fun samplePractitioner(
        id: String,
        slots: List<AppointmentSlot> = emptyList(),
        acceptingNew: Boolean = true,
        isFavorite: Boolean = false
    ) = Practitioner(
        id = id,
        firstName = "Dr",
        lastName = "Smith",
        specialty = "General",
        clinicName = "Clinic",
        address = "1 Main St",
        city = "Luxembourg",
        phone = "+352000000",
        email = "doc@example.com",
        latitude = 49.6,
        longitude = 6.1,
        acceptingNewPatients = acceptingNew,
        availableSlots = slots,
        schedule = emptyList(),
        paymentMethods = emptyList(),
        diplomas = emptyList(),
        isFavorite = isFavorite
    )
}

private class FakePractitionerRepositoryForHome : PractitionerRepository {
    var searchResult: Result<List<Practitioner>> = Result.success(emptyList())
    var toggleResult: Result<Unit> = Result.success(Unit)

    override suspend fun searchPractitioners(
        location: String,
        specialty: String,
        page: Int,
        limit: Int
    ): Result<List<Practitioner>> = searchResult

    override suspend fun getPractitionerById(id: String): Result<Practitioner> =
        Result.failure(UnsupportedOperationException())

    override suspend fun toggleFavorite(practitionerId: String): Result<Unit> = toggleResult
}
