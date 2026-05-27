import XCTest
@testable import eSklepios

final class HomeViewModelTests: XCTestCase {

    func testInitialStateHasEmptyPractitioners() {
        let vm = HomeViewModelWrapper()
        XCTAssertTrue(vm.uiState.practitioners.isEmpty, "Initial practitioners list should be empty")
    }

    func testLoadingStateChanges() {
        let vm = HomeViewModelWrapper()
        let expectation = expectation(description: "loading completes")
        DispatchQueue.main.asyncAfter(deadline: .now() + 0.5) {
            XCTAssertFalse(vm.uiState.isLoading, "Loading should be false after initial load")
            expectation.fulfill()
        }
        wait(for: [expectation], timeout: 2.0)
    }

    func testSearchUpdatesQueryState() {
        let vm = HomeViewModelWrapper()
        vm.search(query: "cardiologist", location: "Luxembourg")
        XCTAssertNotNil(vm.uiState, "State should not be nil after search")
    }

    func testRefreshDoesNotCrash() {
        let vm = HomeViewModelWrapper()
        XCTAssertNoThrow(vm.refresh(), "Refresh should not throw")
    }

    func testApplyFilterDoesNotCrash() {
        let vm = HomeViewModelWrapper()
        XCTAssertNoThrow(vm.applyFilter("Cardiology"), "Apply filter should not throw")
    }
}
