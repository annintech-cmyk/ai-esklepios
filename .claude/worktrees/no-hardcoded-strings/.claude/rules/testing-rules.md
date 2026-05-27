# Testing Rules

## Rule T-1: No MockK in commonTest
MockK requires JVM — it cannot be used in `shared/src/commonTest/`. Use hand-written fake implementations of interfaces.

```kotlin
// CORRECT for commonTest
private class FakeAuthRepository : AuthRepository {
    var loginResult: Result<User> = Result.success(testUser)
    override suspend fun login(email: String, password: String) = loginResult
    // ... implement all methods
}

// FORBIDDEN in commonTest
val mockRepo = mockk<AuthRepository>()
```

## Rule T-2: Test One Behavior per Test Function
Each test function tests exactly one behavior. Function names describe the scenario:
```kotlin
fun `login success updates uiState with user`() = runTest { ... }
fun `login failure sets error message`() = runTest { ... }
fun `login clears previous error on retry`() = runTest { ... }
```

## Rule T-3: Always Restore Dispatchers in Android Tests
```kotlin
@After
fun tearDown() {
    Dispatchers.resetMain()
}
```

## Rule T-4: Use Turbine for StateFlow Assertions
```kotlin
viewModel.uiState.test {
    val initial = awaitItem()
    assertFalse(initial.isLoading)
    viewModel.load()
    val loading = awaitItem()
    assertTrue(loading.isLoading)
    cancelAndIgnoreRemainingEvents()
}
```

## Rule T-5: Tests Must Be Deterministic
- No `Thread.sleep()` in tests — use coroutine testing utilities instead.
- Fake implementations return controlled values, not random data.
- Date/time values are hardcoded strings, not generated from `Clock.System.now()`.

## Rule T-6: Fakes Must Implement All Interface Methods
Even if a test only exercises one method, the fake must compile — implement all interface methods with sensible no-op or fixed defaults.

## Rule T-7: iOS Tests Clean Up After Themselves
Keychain tests (and any test that writes to persistent storage) must delete what they created in `tearDown()`:
```swift
override func tearDown() {
    KeychainStorage.shared.delete(key: "test_key")
    super.tearDown()
}
```

## Rule T-8: Test File Naming
| Type | Convention | Example |
|------|-----------|---------|
| Android ViewModel test | `<ViewModel>Test.kt` | `HomeViewModelTest.kt` |
| Shared repository test | `<Repository>Test.kt` | `PractitionerRepositoryTest.kt` |
| iOS ViewModel test | `<ViewModel>Tests.swift` | `HomeViewModelTests.swift` |

## Rule T-9: Do Not Test Generated Code
Do not write tests for:
- SQLDelight generated queries (test the repository, not the query)
- BuildKonfig generated fields
- Koin module wiring (trust that Koin verifies at startup)

## Rule T-10: Minimum Coverage Expectations
| Layer | Minimum |
|-------|---------|
| Shared ViewModels | All state transitions |
| Shared Repositories | Success + error paths |
| Android ViewModels | Same as shared |
| iOS (XCTest) | Instantiation + key behaviors |

No specific % target is set — focus on behavior coverage, not line coverage.
