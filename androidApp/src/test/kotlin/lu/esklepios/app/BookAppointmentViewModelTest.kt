package lu.esklepios.app

import app.cash.turbine.test
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import lu.esklepios.app.domain.model.Appointment
import lu.esklepios.app.domain.model.AppointmentSlot
import lu.esklepios.app.domain.model.Practitioner
import lu.esklepios.app.domain.model.ProfileType
import lu.esklepios.app.domain.model.User
import lu.esklepios.app.domain.repository.AuthRepository
import lu.esklepios.app.domain.usecase.CreateAppointmentUseCase
import lu.esklepios.app.presentation.viewmodel.BookAppointmentViewModel
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class BookAppointmentViewModelTest {
    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var createAppointmentUseCase: CreateAppointmentUseCase
    private lateinit var authRepository: AuthRepository
    private lateinit var viewModel: BookAppointmentViewModel

    private val testUser =
        User(
            id = "user1",
            firstName = "Anna",
            lastName = "Test",
            email = "anna@test.lu",
            phone = "+352 123 456",
            gender = "Female",
            dateOfBirth = "01/01/1990",
            cnsNumber = "1234567890",
            profileType = ProfileType.PATIENT,
            language = "en",
        )

    private val testPractitioner =
        Practitioner(
            id = "prac1",
            firstName = "Dr",
            lastName = "Smith",
            specialty = "GP",
            clinicName = "Clinic B",
            address = "2 Oak St",
            city = "Luxembourg",
            phone = "+352 999",
            email = "smith@clinic.lu",
            latitude = 49.6,
            longitude = 6.1,
            acceptingNewPatients = true,
            availableSlots = listOf(AppointmentSlot("slot1", "2025-06-01T10:00", true)),
            schedule = emptyList(),
            paymentMethods = listOf("Cash", "Card"),
            diplomas = emptyList(),
            isFavorite = false,
        )

    private val testAppointment =
        Appointment(
            id = "appt1",
            practitionerId = "prac1",
            practitionerName = "Dr. Dr Smith",
            clinicName = "Clinic B",
            specialty = "GP",
            dateTime = "2025-06-01T10:00",
            status = AppointmentStatus.PENDING,
        )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        createAppointmentUseCase = mockk()
        authRepository = mockk()
        every { authRepository.isLoggedIn() } returns false
        every { authRepository.getCurrentUser() } returns null
        viewModel = BookAppointmentViewModel(createAppointmentUseCase, authRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state isAuthenticated reflects auth repository`() =
        runTest {
            every { authRepository.isLoggedIn() } returns false
            val vm = BookAppointmentViewModel(createAppointmentUseCase, authRepository)

            vm.uiState.test {
                val state = awaitItem()
                assertFalse(state.isAuthenticated)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `checkAuth sets isAuthenticated true when logged in`() =
        runTest {
            every { authRepository.isLoggedIn() } returns true
            viewModel.checkAuth()

            viewModel.uiState.test {
                val state = awaitItem()
                assertTrue(state.isAuthenticated)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `loadData populates practitioner and slot`() =
        runTest {
            viewModel.loadData("prac1", "slot1", testPractitioner)

            viewModel.uiState.test {
                val state = awaitItem()
                assertEquals(testPractitioner, state.practitioner)
                assertEquals("slot1", state.selectedSlot?.id)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `confirm without auth sets error`() =
        runTest {
            every { authRepository.isLoggedIn() } returns false
            every { authRepository.getCurrentUser() } returns null

            viewModel.loadData("prac1", "slot1", testPractitioner)
            viewModel.confirm()

            viewModel.uiState.test {
                val state = awaitItem()
                assertNotNull(state.error)
                assertFalse(state.isConfirmed)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `confirm with auth and valid data sets isConfirmed`() =
        runTest {
            every { authRepository.isLoggedIn() } returns true
            every { authRepository.getCurrentUser() } returns testUser
            coEvery { createAppointmentUseCase(any()) } returns Result.success(testAppointment)

            viewModel.loadData("prac1", "slot1", testPractitioner)
            viewModel.confirm()

            viewModel.uiState.test {
                val state = awaitItem()
                assertTrue(state.isConfirmed)
                assertEquals("appt1", state.confirmedAppointmentId)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `confirm failure sets error state`() =
        runTest {
            every { authRepository.isLoggedIn() } returns true
            every { authRepository.getCurrentUser() } returns testUser
            coEvery { createAppointmentUseCase(any()) } returns Result.failure(Exception("Booking failed"))

            viewModel.loadData("prac1", "slot1", testPractitioner)
            viewModel.confirm()

            viewModel.uiState.test {
                val state = awaitItem()
                assertFalse(state.isConfirmed)
                assertNotNull(state.error)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `updateConsultationReason updates state`() =
        runTest {
            viewModel.updateConsultationReason("Back pain")

            viewModel.uiState.test {
                val state = awaitItem()
                assertEquals("Back pain", state.consultationReason)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `updateMessageToDoctor updates state`() =
        runTest {
            viewModel.updateMessageToDoctor("Please prepare X-ray")

            viewModel.uiState.test {
                val state = awaitItem()
                assertEquals("Please prepare X-ray", state.messageToDoctor)
                cancelAndIgnoreRemainingEvents()
            }
        }
}
