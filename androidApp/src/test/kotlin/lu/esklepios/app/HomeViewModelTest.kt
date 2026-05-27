package lu.esklepios.app

import app.cash.turbine.test
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import lu.esklepios.app.domain.model.*
import lu.esklepios.app.domain.usecase.*
import lu.esklepios.app.presentation.viewmodel.HomeViewModel
import org.junit.*
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {
    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var searchUseCase: SearchPractitionersUseCase
    private lateinit var toggleFavoriteUseCase: ToggleFavoriteUseCase
    private lateinit var viewModel: HomeViewModel

    private fun makePractitioner(id: String = "1") = Practitioner(
        id = id,
        firstName = "John",
        lastName = "Doe",
        specialty = "GP",
        clinicName = "Clinic A",
        address = "1 Main St",
        city = "Luxembourg",
        phone = "+352 000 000",
        email = "john@clinic.lu",
        latitude = 49.6,
        longitude = 6.1,
        acceptingNewPatients = true,
        availableSlots = emptyList(),
        schedule = emptyList(),
        paymentMethods = emptyList(),
        diplomas = emptyList(),
        isFavorite = false
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        searchUseCase = mockk()
        toggleFavoriteUseCase = mockk()
        coEvery { searchUseCase(any(), any()) } returns Result.success(emptyList())
        viewModel = HomeViewModel(searchUseCase, toggleFavoriteUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state has empty practitioners`() = runTest {
        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(emptyList(), state.practitioners)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `search success updates practitioners list`() = runTest {
        val practitioners = listOf(makePractitioner("1"), makePractitioner("2"))
        coEvery { searchUseCase(any(), any()) } returns Result.success(practitioners)

        viewModel.search()

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(2, state.practitioners.size)
            assertEquals("1", state.practitioners[0].id)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `search error updates error state`() = runTest {
        coEvery { searchUseCase(any(), any()) } returns Result.failure(Exception("Network error"))

        viewModel.search()

        viewModel.uiState.test {
            val state = awaitItem()
            assertNotNull(state.error)
            assertTrue(state.error!!.contains("Network error") || state.error!!.isNotBlank())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `setDateFilter updates selectedDateFilter`() = runTest {
        viewModel.setDateFilter("Today")

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals("Today", state.selectedDateFilter)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `setDateFilter replaces previous selection`() = runTest {
        viewModel.setDateFilter("Today")
        viewModel.setDateFilter("Within 3 days")

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals("Within 3 days", state.selectedDateFilter)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `toggleNewPatientsFilter flips openToNewPatients`() = runTest {
        viewModel.toggleNewPatientsFilter()

        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state.openToNewPatients)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `toggleFavorite flips isFavorite on matching practitioner`() = runTest {
        val practitioner = makePractitioner("1")
        coEvery { searchUseCase(any(), any()) } returns Result.success(listOf(practitioner))
        coEvery { toggleFavoriteUseCase("1") } returns Result.success(Unit)

        viewModel.search()
        viewModel.toggleFavorite("1")

        viewModel.uiState.test {
            val state = awaitItem()
            val updated = state.practitioners.find { it.id == "1" }
            assertTrue(updated?.isFavorite == true)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `clearError sets error to null`() = runTest {
        coEvery { searchUseCase(any(), any()) } returns Result.failure(Exception("error"))
        viewModel.search()
        viewModel.clearError()

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(null, state.error)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `updateSearchQuery updates searchQuery in state`() = runTest {
        viewModel.updateSearchQuery("cardiologist")

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals("cardiologist", state.searchQuery)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
