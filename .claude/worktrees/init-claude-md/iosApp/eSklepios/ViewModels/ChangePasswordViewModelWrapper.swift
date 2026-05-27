import Foundation
import Combine
import shared

class ChangePasswordViewModelWrapper: ObservableObject {
    private let viewModel: ChangePasswordViewModel
    @Published var uiState: ChangePasswordUiState

    init() {
        self.viewModel = KoinHelper.shared.changePasswordViewModel()
        self.uiState = viewModel.uiState.value ?? ChangePasswordUiState()
        startCollecting()
    }

    private func startCollecting() {
        viewModel.uiState.watch { [weak self] state in
            DispatchQueue.main.async {
                self?.uiState = state ?? ChangePasswordUiState()
            }
        }
    }

    func changePassword(currentPassword: String, newPassword: String, confirmPassword: String) {
        viewModel.changePassword(
            currentPassword: currentPassword,
            newPassword: newPassword,
            confirmPassword: confirmPassword
        )
    }

    func clearError() {
        viewModel.clearError()
    }

    deinit {
        viewModel.onCleared()
    }
}
