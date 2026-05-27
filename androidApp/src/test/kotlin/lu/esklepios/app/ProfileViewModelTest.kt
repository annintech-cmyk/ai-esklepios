package lu.esklepios.app

import app.cash.turbine.test
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import lu.esklepios.app.domain.model.*
import lu.esklepios.app.domain.usecase.*
import lu.esklepios.app.presentation.viewmodel.ProfileViewModel
import org.junit.*
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModelTest {
    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var getProfileUseCase: GetProfileUseCase
    private lateinit var logoutUseCase: LogoutUseCase
    private lateinit var viewModel: ProfileViewModel

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

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        getProfileUseCase = mockk()
        logoutUseCase = mockk()

        coEvery { getProfileUseCase() } returns Result.success(testUser)

        viewModel = ProfileViewModel(getProfileUseCase, logoutUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init calls loadProfile and populates user`() =
        runTest {
            coVerify { getProfileUseCase() }

            viewModel.uiState.test {
                val state = awaitItem()
                assertEquals(testUser, state.user)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `loadProfile failure sets error state`() =
        runTest {
            coEvery { getProfileUseCase() } returns Result.failure(Exception("Profile not found"))
            val vm = ProfileViewModel(getProfileUseCase, logoutUseCase)

            vm.uiState.test {
                val state = awaitItem()
                assertNotNull(state.error)
                assertNull(state.user)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `logout success sets isLoggedOut to true`() =
        runTest {
            coEvery { logoutUseCase() } returns Result.success(Unit)

            viewModel.logout()

            viewModel.uiState.test {
                val state = awaitItem()
                assertTrue(state.isLoggedOut)
                assertNull(state.user)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `logout failure sets error`() =
        runTest {
            coEvery { logoutUseCase() } returns Result.failure(Exception("Logout failed"))

            viewModel.logout()

            viewModel.uiState.test {
                val state = awaitItem()
                assertNotNull(state.error)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `clearError resets error to null`() =
        runTest {
            coEvery { getProfileUseCase() } returns Result.failure(Exception("error"))
            val vm = ProfileViewModel(getProfileUseCase, logoutUseCase)
            vm.clearError()

            vm.uiState.test {
                val state = awaitItem()
                assertNull(state.error)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `user initials derived correctly`() =
        runTest {
            viewModel.uiState.test {
                val state = awaitItem()
                assertEquals("AT", state.user?.initials)
                cancelAndIgnoreRemainingEvents()
            }
        }
}
