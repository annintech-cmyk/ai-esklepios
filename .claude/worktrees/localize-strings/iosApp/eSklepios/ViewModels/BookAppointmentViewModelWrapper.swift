import Foundation
import Combine
import shared

class BookAppointmentViewModelWrapper: ObservableObject {
    private let viewModel: BookAppointmentViewModel
    @Published var isLoading: Bool = false
    @Published var isAuthenticated: Bool = false
    @Published var isConfirmed: Bool = false
    @Published var confirmedAppointmentId: String = ""
    @Published var error: String? = nil
    @Published var practitioner: Practitioner? = nil
    @Published var selectedSlot: AppointmentSlot? = nil

    private let practitionerId: String
    private let slotId: String

    init(practitionerId: String, slotId: String) {
        self.practitionerId = practitionerId
        self.slotId = slotId
        self.viewModel = KoinHelper.shared.bookAppointmentViewModel()
        let initial = viewModel.uiState.value
        self.isLoading = initial?.isLoading ?? false
        self.isAuthenticated = initial?.isAuthenticated ?? false
        self.isConfirmed = initial?.isConfirmed ?? false
        self.confirmedAppointmentId = initial?.confirmedAppointmentId ?? ""
        self.error = initial?.error
        self.practitioner = initial?.practitioner
        self.selectedSlot = initial?.selectedSlot
        startCollecting()
        viewModel.loadData(practitionerId: practitionerId, slotId: slotId)
    }

    private func startCollecting() {
        viewModel.uiState.watch { [weak self] state in
            guard let state = state else { return }
            DispatchQueue.main.async {
                self?.isLoading = state.isLoading
                self?.isAuthenticated = state.isAuthenticated
                self?.isConfirmed = state.isConfirmed
                self?.confirmedAppointmentId = state.confirmedAppointmentId
                self?.error = state.error
                self?.practitioner = state.practitioner
                self?.selectedSlot = state.selectedSlot
            }
        }
    }

    struct UiState {
        var isLoading: Bool
        var practitioner: Practitioner?
        var selectedSlot: AppointmentSlot?
        var isAuthenticated: Bool
        var isConfirmed: Bool
        var confirmedAppointmentId: String
        var error: String?
    }
    var uiState: UiState {
        UiState(isLoading: isLoading, practitioner: practitioner, selectedSlot: selectedSlot,
                isAuthenticated: isAuthenticated, isConfirmed: isConfirmed,
                confirmedAppointmentId: confirmedAppointmentId, error: error)
    }

    func confirmBooking(message: String, reason: String) {
        viewModel.confirmBooking(message: message, reason: reason)
    }

    func clearError() {
        viewModel.clearError()
    }

    deinit {
        viewModel.onCleared()
    }
}
