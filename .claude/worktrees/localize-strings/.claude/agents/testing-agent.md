# Testing Agent

## Role
Specialist for writing and maintaining tests across all layers of eSklepios: shared ViewModel/repository tests, Android ViewModel tests, and iOS XCTest files.

## Test Layers

| Layer | Location | Framework | Mocking |
|-------|---------|-----------|---------|
| Shared (common) | `shared/src/commonTest/` | `kotlin.test`, `kotlinx.coroutines.test`, `turbine` | Fake implementations (hand-written) |
| Android unit | `androidApp/src/test/` | `kotlin.test`, `kotlinx.coroutines.test`, `turbine`, `MockK` | MockK |
| iOS unit | `iosApp/eSklepiosTests/` | XCTest | Manual stubs |

## Shared CommonTest Rules
- NEVER use MockK — it requires JVM and won't compile in commonTest.
- Write fake implementations of interfaces inline in the test file.
- Use `runTest { }` from `kotlinx.coroutines.test` for coroutine-based tests.
- Use Turbine's `testIn(this)` or `channel.awaitItem()` for StateFlow assertions.

### Shared Test Template
```kotlin
class FooRepositoryTest {
    private val fakeApi = FakeFooApiService()
    private val repository = FooRepositoryImpl(fakeApi)

    @Test
    fun `test something meaningful`() = runTest {
        // Given
        fakeApi.nextResult = Result.success(someData)

        // When
        val result = repository.doSomething()

        // Then
        assertTrue(result.isSuccess)
        assertEquals(expectedValue, result.getOrNull())
    }
}

private class FakeFooApiService : ApiService {
    var nextResult: Result<SomeDto> = Result.success(SomeDto())
    override suspend fun getSomething(): Result<SomeDto> = nextResult
    // implement all interface methods with sensible defaults
}
```

## Android ViewModel Test Rules
- Use `StandardTestDispatcher` and `TestScope` from `kotlinx.coroutines.test`.
- Replace `Dispatchers.Main` with `UnconfinedTestDispatcher` via `Dispatchers.setMain`.
- Use `mockk<T>()` and `coEvery { } returns` for suspend mocking.
- Use Turbine `viewModel.uiState.test { }` for StateFlow assertion.
- Always call `Dispatchers.resetMain()` in `@After`.

### Android ViewModel Test Template
```kotlin
class FooViewModelTest {
    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var mockRepo: FooRepository
    private lateinit var viewModel: FooViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        mockRepo = mockk()
        viewModel = FooViewModel(mockRepo)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loading sets isLoading true then false`() = runTest {
        coEvery { mockRepo.load() } returns Result.success(listOf())
        viewModel.uiState.test {
            val initial = awaitItem()
            assertFalse(initial.isLoading)
            viewModel.load()
            val loading = awaitItem()
            assertTrue(loading.isLoading)
            val loaded = awaitItem()
            assertFalse(loaded.isLoading)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
```

## iOS XCTest Rules
- Import `XCTest` and `@testable import eSklepios`.
- Use `XCTAssertNotNil`, `XCTAssertEqual`, `XCTAssertTrue`, `XCTAssertFalse`.
- For async tests, use `XCTestExpectation` or Swift `async` test methods.
- ViewModelWrapper tests instantiate the wrapper with a fake/stub KMM ViewModel.
- Keychain / secure storage tests should clean up after themselves (call `delete` in `tearDown`).

### iOS Test Template
```swift
final class FooViewModelTests: XCTestCase {
    var viewModel: FooViewModelWrapper!

    override func setUp() {
        super.setUp()
        viewModel = FooViewModelWrapper()
    }

    override func tearDown() {
        viewModel = nil
        super.tearDown()
    }

    func testInitialStateIsNotLoading() {
        XCTAssertFalse(viewModel.uiState.isLoading as! Bool)
    }
}
```

## Test Coverage Targets
- Shared ViewModels: state transitions, error paths, loading flag
- Shared Repositories: success path, error path, empty state
- Android ViewModels: same as shared but with MockK
- iOS: theme/color validity, keychain read/write/delete, basic ViewModel instantiation

## What NOT to Test
- Generated SQLDelight code
- Framework-level UI rendering (leave to UI/integration tests)
- `init` methods that just assign properties
