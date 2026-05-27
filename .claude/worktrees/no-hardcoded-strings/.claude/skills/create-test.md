# Skill: Create Test

Creates test files for a given ViewModel or repository across all three test layers.

## Usage
```
/create-test <TargetName> [android|shared|ios|all]
```
Example: `/create-test HomeViewModel all`

## Shared CommonTest (Repository Tests)

File: `shared/src/commonTest/kotlin/lu/esklepios/app/<Name>RepositoryTest.kt`

```kotlin
package lu.esklepios.app

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest
import lu.esklepios.app.data.repository.<Name>RepositoryImpl

class <Name>RepositoryTest {

    private val fakeApi = Fake<Name>ApiService()
    private val repository = <Name>RepositoryImpl(fakeApi)

    @Test
    fun `getAll returns domain list on success`() = runTest {
        fakeApi.listResult = Result.success(listOf(fake<Name>Dto()))
        val result = repository.getAll()
        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()?.size)
    }

    @Test
    fun `getAll propagates failure`() = runTest {
        fakeApi.listResult = Result.failure(Exception("Network error"))
        val result = repository.getAll()
        assertTrue(result.isFailure)
    }

    @Test
    fun `getById returns correct item`() = runTest {
        val dto = fake<Name>Dto()
        fakeApi.singleResult = Result.success(dto)
        val result = repository.getById("test-id")
        assertTrue(result.isSuccess)
        assertEquals(dto.id, result.getOrNull()?.id)
    }
}

private class Fake<Name>ApiService : ApiService {
    var listResult: Result<List<<Name>Dto>> = Result.success(emptyList())
    var singleResult: Result<<Name>Dto> = Result.success(fake<Name>Dto())
    override suspend fun get<Name>s() = listResult
    override suspend fun get<Name>(id: String) = singleResult
    // Implement remaining ApiService methods with defaults
}

private fun fake<Name>Dto() = <Name>Dto(id = "test-id", /* other fields */)
```

## Android ViewModel Test

File: `androidApp/src/test/kotlin/lu/esklepios/app/<Name>ViewModelTest.kt`

```kotlin
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
import lu.esklepios.app.domain.model.<Name>
import lu.esklepios.app.domain.repository.<Name>Repository
import lu.esklepios.app.presentation.viewmodel.<Name>ViewModel
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class <Name>ViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var mock<Name>Repository: <Name>Repository
    private lateinit var viewModel: <Name>ViewModel

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        mock<Name>Repository = mockk()
        viewModel = <Name>ViewModel(mock<Name>Repository)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state has isLoading false`() = runTest {
        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.isLoading)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `load success updates items`() = runTest {
        coEvery { mock<Name>Repository.getAll() } returns Result.success(listOf(fake<Name>()))
        viewModel.uiState.test {
            skipItems(1) // initial
            viewModel.load()
            val loading = awaitItem()
            assertTrue(loading.isLoading)
            val loaded = awaitItem()
            assertFalse(loaded.isLoading)
            assertNotNull(loaded.items)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `load failure sets error message`() = runTest {
        coEvery { mock<Name>Repository.getAll() } returns Result.failure(Exception("Network error"))
        viewModel.uiState.test {
            skipItems(1)
            viewModel.load()
            skipItems(1) // loading
            val error = awaitItem()
            assertNotNull(error.error)
            assertFalse(error.isLoading)
            cancelAndIgnoreRemainingEvents()
        }
    }
}

private fun fake<Name>() = <Name>(id = "test-id")
```

## iOS XCTest

File: `iosApp/eSklepiosTests/<Name>ViewModelTests.swift`

```swift
import XCTest
import SwiftUI
@testable import eSklepios

final class <Name>ViewModelTests: XCTestCase {

    var wrapper: <Name>ViewModelWrapper!

    override func setUp() {
        super.setUp()
        wrapper = <Name>ViewModelWrapper()
    }

    override func tearDown() {
        wrapper = nil
        super.tearDown()
    }

    func testInitialStateIsNotLoading() {
        // Initial state should not be loading
        XCTAssertNotNil(wrapper.uiState, "UiState should not be nil after initialization")
    }

    func testViewModelIsNotNil() {
        XCTAssertNotNil(wrapper.viewModel, "ViewModel should not be nil")
    }
}
```

## Notes
- For the shared test: read the actual `ApiService` interface to ensure all methods are implemented in the fake.
- For the Android test: always reset main dispatcher in `@AfterTest`.
- For iOS tests: if the test writes to Keychain or UserDefaults, clean up in `tearDown`.
