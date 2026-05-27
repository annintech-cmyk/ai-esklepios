import XCTest
@testable import eSklepios

final class AuthViewModelTests: XCTestCase {

    func testInitialStateIsNotLoggedIn() {
        let vm = AuthViewModelWrapper()
        XCTAssertFalse(vm.uiState.isLoggedIn, "Initial state should not be logged in")
    }

    func testInitialErrorIsNil() {
        let vm = AuthViewModelWrapper()
        XCTAssertNil(vm.uiState.error, "Initial error should be nil")
    }

    func testClearErrorResetsErrorState() {
        let vm = AuthViewModelWrapper()
        vm.clearError()
        XCTAssertNil(vm.uiState.error, "Error should be nil after clearError()")
    }

    func testLoginWithEmptyCredentialsDoesNotCrash() {
        let vm = AuthViewModelWrapper()
        XCTAssertNoThrow(
            vm.login(email: "", password: ""),
            "Login with empty credentials should not crash"
        )
    }

    func testForgotPasswordDoesNotCrash() {
        let vm = AuthViewModelWrapper()
        XCTAssertNoThrow(
            vm.forgotPassword(email: "test@test.lu"),
            "Forgot password should not crash"
        )
    }

    func testForgotPasswordSentInitiallyFalse() {
        let vm = AuthViewModelWrapper()
        XCTAssertFalse(vm.uiState.forgotPasswordSent, "forgotPasswordSent should be initially false")
    }
}
