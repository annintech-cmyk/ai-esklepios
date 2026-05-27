import Foundation
import Combine
import shared

class ChangeEmailViewModelWrapper: ObservableObject {
    private let viewModel: ChangeEmailViewModel
    @Published var uiState: ChangeEmailUiState

    init() {
        self.viewModel = KoinHelper.shared.changeEmailViewModel()
        self.uiState = viewModel.uiState.value ?? ChangeEmailUiState()
        startCollecting()
    }

    private func startCollecting() {
        viewModel.uiState.watch { [weak self] state in
            DispatchQueue.main.async {
                self?.uiState = state ?? ChangeEmailUiState()
            }
        }
    }

    func changeEmail(currentPassword: String, newEmail: String) {
        viewModel.changeEmail(currentPassword: currentPassword, newEmail: newEmail)
    }

    func clearError() {
        viewModel.clearError()
    }

    deinit {
        viewModel.onCleared()
    }
}
