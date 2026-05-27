import Foundation
import Combine
import shared

class MyAppointmentsViewModelWrapper: ObservableObject {
    private let viewModel: MyAppointmentsViewModel
    @Published var isLoading: Bool = false
    @Published var upcomingAppointments: [Appointment] = []
    @Published var pastAppointments: [Appointment] = []
    @Published var error: String? = nil

    init() {
        self.viewModel = KoinHelper.shared.myAppointmentsViewModel()
        let initial = viewModel.uiState.value
        self.isLoading = initial?.isLoading ?? false
        self.upcomingAppointments = initial?.upcomingAppointments as? [Appointment] ?? []
        self.pastAppointments = initial?.pastAppointments as? [Appointment] ?? []
        self.error = initial?.error
        startCollecting()
    }

    private func startCollecting() {
        viewModel.uiState.watch { [weak self] state in
            guard let state = state else { return }
            DispatchQueue.main.async {
                self?.isLoading = state.isLoading
                self?.upcomingAppointments = state.upcomingAppointments as? [Appointment] ?? []
                self?.pastAppointments = state.pastAppointments as? [Appointment] ?? []
                self?.error = state.error
            }
        }
    }

    struct UiState {
        var isLoading: Bool
        var upcomingAppointments: [Appointment]
        var pastAppointments: [Appointment]
        var error: String?
    }
    var uiState: UiState {
        UiState(isLoading: isLoading, upcomingAppointments: upcomingAppointments,
                pastAppointments: pastAppointments, error: error)
    }

    func loadAppointments() {
        viewModel.loadAppointments()
    }

    func cancelAppointment(appointmentId: String) {
        viewModel.cancelAppointment(appointmentId: appointmentId)
    }

    func refresh() {
        viewModel.refresh()
    }

    deinit {
        viewModel.onCleared()
    }
}
