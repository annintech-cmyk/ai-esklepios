package lu.esklepios.app

import app.cash.turbine.test
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import lu.esklepios.app.domain.model.Appointment
import lu.esklepios.app.domain.model.AppointmentStatus
import lu.esklepios.app.domain.usecase.CancelAppointmentUseCase
import lu.esklepios.app.domain.usecase.GetPastAppointmentsUseCase
import lu.esklepios.app.domain.usecase.GetUpcomingAppointmentsUseCase
import lu.esklepios.app.domain.usecase.ModifyAppointmentUseCase
import lu.esklepios.app.presentation.viewmodel.MyAppointmentsViewModel
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class MyAppointmentsViewModelTest {
    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var getUpcomingUseCase: GetUpcomingAppointmentsUseCase
    private lateinit var getPastUseCase: GetPastAppointmentsUseCase
    private lateinit var cancelUseCase: CancelAppointmentUseCase
    private lateinit var modifyUseCase: ModifyAppointmentUseCase
    private lateinit var viewModel: MyAppointmentsViewModel

    private fun makeAppointment(
        id: String,
        status: AppointmentStatus = AppointmentStatus.CONFIRMED,
    ) = Appointment(
        id = id,
        practitionerId = "prac1",
        practitionerName = "Dr. Smith",
        clinicName = "Clinic A",
        specialty = "GP",
        dateTime = "2025-06-01T10:00",
        status = status,
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        getUpcomingUseCase = mockk()
        getPastUseCase = mockk()
        cancelUseCase = mockk()
        modifyUseCase = mockk()

        coEvery { getUpcomingUseCase(any()) } returns Result.success(emptyList())
        coEvery { getPastUseCase(any()) } returns Result.success(emptyList())

        viewModel = MyAppointmentsViewModel(getUpcomingUseCase, getPastUseCase, cancelUseCase, modifyUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init loads both upcoming and past appointments`() =
        runTest {
            coVerify { getUpcomingUseCase(any()) }
            coVerify { getPastUseCase(any()) }
        }

    @Test
    fun `loadUpcoming populates upcomingAppointments`() =
        runTest {
            val upcoming = listOf(makeAppointment("1"), makeAppointment("2"))
            coEvery { getUpcomingUseCase(any()) } returns Result.success(upcoming)

            viewModel.loadUpcoming()

            viewModel.uiState.test {
                val state = awaitItem()
                assertEquals(2, state.upcomingAppointments.size)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `loadPast populates pastAppointments`() =
        runTest {
            val past = listOf(makeAppointment("3", AppointmentStatus.COMPLETED))
            coEvery { getPastUseCase(any()) } returns Result.success(past)

            viewModel.loadPast()

            viewModel.uiState.test {
                val state = awaitItem()
                assertEquals(1, state.pastAppointments.size)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `cancelAppointment removes appointment from upcoming list`() =
        runTest {
            val upcoming = listOf(makeAppointment("1"), makeAppointment("2"))
            coEvery { getUpcomingUseCase(any()) } returns Result.success(upcoming)
            coEvery { cancelUseCase("1") } returns Result.success(Unit)

            viewModel.loadUpcoming()
            viewModel.cancelAppointment("1")

            viewModel.uiState.test {
                val state = awaitItem()
                assertFalse(state.upcomingAppointments.any { it.id == "1" })
                assertTrue(state.upcomingAppointments.any { it.id == "2" })
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `cancelAppointment failure sets error`() =
        runTest {
            coEvery { cancelUseCase(any()) } returns Result.failure(Exception("Cancel failed"))

            viewModel.cancelAppointment("1")

            viewModel.uiState.test {
                val state = awaitItem()
                assertNotNull(state.error)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `selectTab updates selectedTab`() =
        runTest {
            viewModel.selectTab(1)

            viewModel.uiState.test {
                val state = awaitItem()
                assertEquals(1, state.selectedTab)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `clearError resets error to null`() =
        runTest {
            coEvery { cancelUseCase(any()) } returns Result.failure(Exception("error"))
            viewModel.cancelAppointment("1")
            viewModel.clearError()

            viewModel.uiState.test {
                val state = awaitItem()
                assertEquals(null, state.error)
                cancelAndIgnoreRemainingEvents()
            }
        }
}
