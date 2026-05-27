import Foundation
import Combine
import shared

class AppointmentSuccessViewModelWrapper: ObservableObject {
    private let viewModel: AppointmentSuccessViewModel
    @Published var uiState: AppointmentSuccessUiState

    private let appointmentId: String

    init(appointmentId: String) {
        self.appointmentId = appointmentId
        self.viewModel = KoinHelper.shared.appointmentSuccessViewModel()
        self.uiState = viewModel.uiState.value ?? AppointmentSuccessUiState()
        startCollecting()
    }

    private func startCollecting() {
        viewModel.uiState.watch { [weak self] state in
            DispatchQueue.main.async {
                self?.uiState = state ?? AppointmentSuccessUiState()
            }
        }
    }

    func loadAppointment() {
        viewModel.loadAppointment(appointmentId: appointmentId)
    }

    deinit {
        viewModel.onCleared()
    }
}
