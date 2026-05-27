package lu.esklepios.app

import app.cash.turbine.test
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import lu.esklepios.app.domain.model.*
import lu.esklepios.app.domain.usecase.*
import lu.esklepios.app.presentation.viewmodel.AuthField
import lu.esklepios.app.presentation.viewmodel.AuthViewModel
import org.junit.*
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {
    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var loginUseCase: LoginUseCase
    private lateinit var registerUseCase: RegisterUseCase
    private lateinit var forgotPasswordUseCase: ForgotPasswordUseCase
    private lateinit var logoutUseCase: LogoutUseCase
    private lateinit var viewModel: AuthViewModel

    private val testUser = User(
        id = "user1",
        firstName = "Anna",
        lastName = "Test",
        email = "anna@test.lu",
        phone = "+352 123 456",
        gender = "Female",
        dateOfBirth = "01/01/1990",
        cnsNumber = "1234567890",
        profileType = ProfileType.PATIENT,
        language = "en"
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        loginUseCase = mockk()
        registerUseCase = mockk()
        forgotPasswordUseCase = mockk()
        logoutUseCase = mockk()
        viewModel = AuthViewModel(loginUseCase, registerUseCase, forgotPasswordUseCase, logoutUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `login success sets isLoggedIn to true`() = runTest {
        coEvery { loginUseCase(any(), any()) } returns Result.success(testUser)

        viewModel.updateField(AuthField.EMAIL, "anna@test.lu")
        viewModel.updateField(AuthField.PASSWORD, "password123")
        viewModel.login()

        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state.isLoggedIn)
            assertNull(state.error)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `login failure sets error state`() = runTest {
        coEvery { loginUseCase(any(), any()) } returns Result.failure(Exception("Invalid credentials"))

        viewModel.updateField(AuthField.EMAIL, "anna@test.lu")
        viewModel.updateField(AuthField.PASSWORD, "wrongpass")
        viewModel.login()

        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.isLoggedIn)
            assertNotNull(state.error)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `login with empty email sets validation error`() = runTest {
        viewModel.updateField(AuthField.EMAIL, "")
        viewModel.updateField(AuthField.PASSWORD, "password123")
        viewModel.login()

        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.isLoggedIn)
            assertNotNull(state.error)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `register calls registerUseCase with correct user data`() = runTest {
        coEvery { registerUseCase(any(), any()) } returns Result.success(testUser)

        viewModel.updateField(AuthField.FIRST_NAME, "Anna")
        viewModel.updateField(AuthField.LAST_NAME, "Test")
        viewModel.updateField(AuthField.EMAIL, "anna@test.lu")
        viewModel.updateField(AuthField.PASSWORD, "password123")
        viewModel.register()

        coVerify { registerUseCase(any(), "password123") }

        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state.isLoggedIn)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `register failure sets error`() = runTest {
        coEvery { registerUseCase(any(), any()) } returns Result.failure(Exception("Email already taken"))

        viewModel.updateField(AuthField.EMAIL, "taken@test.lu")
        viewModel.updateField(AuthField.PASSWORD, "password123")
        viewModel.register()

        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.isLoggedIn)
            assertNotNull(state.error)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `forgotPassword success sets forgotPasswordSent to true`() = runTest {
        coEvery { forgotPasswordUseCase(any()) } returns Result.success(Unit)

        viewModel.updateField(AuthField.EMAIL, "anna@test.lu")
        viewModel.forgotPassword()

        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state.forgotPasswordSent)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `clearError resets error to null`() = runTest {
        coEvery { loginUseCase(any(), any()) } returns Result.failure(Exception("Error"))

        viewModel.updateField(AuthField.EMAIL, "anna@test.lu")
        viewModel.updateField(AuthField.PASSWORD, "pass")
        viewModel.login()
        viewModel.clearError()

        viewModel.uiState.test {
            val state = awaitItem()
            assertNull(state.error)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `updateField correctly updates email`() = runTest {
        viewModel.updateField(AuthField.EMAIL, "test@example.com")

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals("test@example.com", state.email)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `setStep updates step in state`() = runTest {
        viewModel.setStep(2)

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(2, state.step)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
