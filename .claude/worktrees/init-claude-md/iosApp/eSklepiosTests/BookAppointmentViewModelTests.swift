import XCTest
@testable import eSklepios

final class BookAppointmentViewModelTests: XCTestCase {

    func testInitialStateIsNotConfirmed() {
        let vm = BookAppointmentViewModelWrapper(practitionerId: "prac1", slotId: "slot1")
        XCTAssertFalse(vm.uiState.isConfirmed, "Initial state should not be confirmed")
    }

    func testInitialErrorIsNil() {
        let vm = BookAppointmentViewModelWrapper(practitionerId: "prac1", slotId: "slot1")
        XCTAssertNil(vm.uiState.error, "Initial error should be nil")
    }

    func testAuthGateForUnauthenticatedUser() {
        let vm = BookAppointmentViewModelWrapper(practitionerId: "prac1", slotId: "slot1")
        XCTAssertFalse(vm.uiState.isAuthenticated, "Unauthenticated user should see auth gate")
    }

    func testConfirmWithNoDataDoesNotCrash() {
        let vm = BookAppointmentViewModelWrapper(practitionerId: "prac1", slotId: "slot1")
        XCTAssertNoThrow(
            vm.confirmBooking(message: "test message", reason: "back pain"),
            "Confirm booking should not crash"
        )
    }

    func testClearErrorDoesNotCrash() {
        let vm = BookAppointmentViewModelWrapper(practitionerId: "prac1", slotId: "slot1")
        XCTAssertNoThrow(vm.clearError(), "Clear error should not crash")
    }
}
