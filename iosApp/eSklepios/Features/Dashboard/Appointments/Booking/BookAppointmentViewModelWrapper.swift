import Foundation
import Combine
import shared

@MainActor
class BookAppointmentViewModelWrapper: ObservableObject {
    private let viewModel: BookAppointmentViewModel
    private var stateObserver: FlowWatcher?
    @Published var isLoading: Bool = false
    @Published var isAuthenticated: Bool = false
    @Published var isConfirmed: Bool = false
    @Published var confirmedAppointmentId: String = ""
    @Published var error: String? = nil
    @Published var practitioner: Practitioner? = nil
    @Published var selectedSlot: AppointmentSlot? = nil
    @Published var previousAppointment: Appointment? = nil

    private let practitionerId: String
    private let slotId: String

    init(practitionerId: String, slotId: String) {
        self.practitionerId = practitionerId
        self.slotId = slotId
        self.viewModel = KoinHelper.shared.bookAppointmentViewModel()
        let initial = viewModel.uiState.value as? BookAppointmentUiState
        self.isLoading = initial?.isLoading ?? false
        self.isAuthenticated = initial?.isAuthenticated ?? false
        self.isConfirmed = initial?.isConfirmed ?? false
        self.confirmedAppointmentId = initial?.confirmedAppointmentId ?? ""
        self.error = initial?.error
        self.practitioner = initial?.practitioner
        self.selectedSlot = initial?.selectedSlot
        self.previousAppointment = initial?.previousAppointment
        startCollecting()
        viewModel.loadData(practitionerId: practitionerId, slotId: slotId)
    }

    private func startCollecting() {
        stateObserver = FlowExtensionsKt.watch(viewModel.uiState) { [weak self] anyState in
            guard let state = anyState as? BookAppointmentUiState else { return }
            Task { @MainActor [weak self] in
                self?.isLoading = state.isLoading
                self?.isAuthenticated = state.isAuthenticated
                self?.isConfirmed = state.isConfirmed
                self?.confirmedAppointmentId = state.confirmedAppointmentId
                self?.error = state.error
                self?.practitioner = state.practitioner
                self?.selectedSlot = state.selectedSlot
                self?.previousAppointment = state.previousAppointment
            }
        }
    }

    struct UiState {
        var isLoading: Bool
        var practitioner: Practitioner?
        var selectedSlot: AppointmentSlot?
        var previousAppointment: Appointment?
        var isAuthenticated: Bool
        var isConfirmed: Bool
        var confirmedAppointmentId: String
        var error: String?
    }
    var uiState: UiState {
        UiState(isLoading: isLoading, practitioner: practitioner, selectedSlot: selectedSlot,
                previousAppointment: previousAppointment,
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
        stateObserver?.close()
        viewModel.onCleared()
    }
}
