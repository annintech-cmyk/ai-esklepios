import Foundation
import Combine
import shared

@MainActor
class AppointmentSuccessViewModelWrapper: ObservableObject {
    private let viewModel: AppointmentSuccessViewModel
    private var stateObserver: FlowWatcher?
    @Published var uiState: AppointmentSuccessUiState

    private let appointmentId: String

    init(appointmentId: String) {
        self.appointmentId = appointmentId
        self.viewModel = KoinHelper.shared.appointmentSuccessViewModel()
        self.uiState = viewModel.uiState.value as? AppointmentSuccessUiState
            ?? AppointmentSuccessUiState(appointmentId: "", practitionerName: "", dateTime: "", clinicName: "")
        startCollecting()
    }

    private func startCollecting() {
        stateObserver = FlowExtensionsKt.watch(viewModel.uiState) { [weak self] anyState in
            Task { @MainActor [weak self] in
                self?.uiState = anyState as? AppointmentSuccessUiState
                    ?? AppointmentSuccessUiState(appointmentId: "", practitionerName: "", dateTime: "", clinicName: "")
            }
        }
    }

    func loadAppointment() {
        viewModel.loadAppointment(appointmentId: appointmentId)
    }

    deinit {
        stateObserver?.close()
        viewModel.onCleared()
    }
}
