import Foundation
import Combine
import shared

@MainActor
class ChangePasswordViewModelWrapper: ObservableObject {
    private let viewModel: ChangePasswordViewModel
    private var stateObserver: FlowWatcher?
    @Published var uiState: ChangePasswordUiState

    init() {
        self.viewModel = KoinHelper.shared.changePasswordViewModel()
        self.uiState = viewModel.uiState.value as? ChangePasswordUiState
            ?? ChangePasswordUiState(isLoading: false, oldPassword: "", newPassword: "", confirmPassword: "", isSuccess: false, error: nil)
        startCollecting()
    }

    private func startCollecting() {
        stateObserver = FlowExtensionsKt.watch(viewModel.uiState) { [weak self] anyState in
            Task { @MainActor [weak self] in
                self?.uiState = anyState as? ChangePasswordUiState
                    ?? ChangePasswordUiState(isLoading: false, oldPassword: "", newPassword: "", confirmPassword: "", isSuccess: false, error: nil)
            }
        }
    }

    func updateOldPassword(_ password: String) { viewModel.updateOldPassword(password: password) }
    func updateNewPassword(_ password: String) { viewModel.updateNewPassword(password: password) }
    func updateConfirmPassword(_ password: String) { viewModel.updateConfirmPassword(password: password) }

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
        stateObserver?.close()
        viewModel.onCleared()
    }
}
