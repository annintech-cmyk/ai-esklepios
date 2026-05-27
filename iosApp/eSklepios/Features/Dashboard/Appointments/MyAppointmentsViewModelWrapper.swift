import Foundation
import Combine
import shared

@MainActor
class MyAppointmentsViewModelWrapper: ObservableObject {
    private let viewModel: MyAppointmentsViewModel
    private var stateObserver: FlowWatcher?
    @Published var isLoading: Bool = false
    @Published var upcomingAppointments: [Appointment] = []
    @Published var pastAppointments: [Appointment] = []
    @Published var selectedTab: Int = 0
    @Published var error: String? = nil

    init() {
        self.viewModel = KoinHelper.shared.myAppointmentsViewModel()
        let initial = viewModel.uiState.value as? MyAppointmentsUiState
        self.isLoading = initial?.isLoading ?? false
        self.upcomingAppointments = initial?.upcomingAppointments as? [Appointment] ?? []
        self.pastAppointments = initial?.pastAppointments as? [Appointment] ?? []
        self.selectedTab = Int(initial?.selectedTab ?? 0)
        self.error = initial?.error
        startCollecting()
    }

    private func startCollecting() {
        stateObserver = FlowExtensionsKt.watch(viewModel.uiState) { [weak self] anyState in
            guard let state = anyState as? MyAppointmentsUiState else { return }
            Task { @MainActor [weak self] in
                self?.isLoading = state.isLoading
                self?.upcomingAppointments = state.upcomingAppointments as? [Appointment] ?? []
                self?.pastAppointments = state.pastAppointments as? [Appointment] ?? []
                self?.selectedTab = Int(state.selectedTab)
                self?.error = state.error
            }
        }
    }

    struct UiState {
        var isLoading: Bool
        var upcomingAppointments: [Appointment]
        var pastAppointments: [Appointment]
        var selectedTab: Int
        var error: String?
    }
    var uiState: UiState {
        UiState(isLoading: isLoading, upcomingAppointments: upcomingAppointments,
                pastAppointments: pastAppointments, selectedTab: selectedTab, error: error)
    }

    func loadAppointments() {
        viewModel.loadAppointments()
    }

    func selectTab(_ tab: Int) {
        viewModel.selectTab(tab: Int32(tab))
    }

    func cancelAppointment(appointmentId: String) {
        viewModel.cancelAppointment(appointmentId: appointmentId)
    }

    func refresh() {
        viewModel.refresh()
    }

    deinit {
        stateObserver?.close()
        viewModel.onCleared()
    }
}
