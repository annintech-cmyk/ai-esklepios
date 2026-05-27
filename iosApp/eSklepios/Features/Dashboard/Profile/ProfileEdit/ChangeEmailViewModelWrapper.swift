import Foundation
import Combine
import shared

@MainActor
class ChangeEmailViewModelWrapper: ObservableObject {
    private let viewModel: ChangeEmailViewModel
    private var stateObserver: FlowWatcher?
    @Published var uiState: ChangeEmailUiState

    init() {
        self.viewModel = KoinHelper.shared.changeEmailViewModel()
        self.uiState = viewModel.uiState.value as? ChangeEmailUiState
            ?? ChangeEmailUiState(isLoading: false, currentEmail: "", newEmail: "", confirmEmail: "", isSuccess: false, error: nil)
        startCollecting()
    }

    private func startCollecting() {
        stateObserver = FlowExtensionsKt.watch(viewModel.uiState) { [weak self] anyState in
            Task { @MainActor [weak self] in
                self?.uiState = anyState as? ChangeEmailUiState
                    ?? ChangeEmailUiState(isLoading: false, currentEmail: "", newEmail: "", confirmEmail: "", isSuccess: false, error: nil)
            }
        }
    }

    func changeEmail() {
        viewModel.changeEmail()
    }

    func updateNewEmail(_ email: String) {
        viewModel.updateNewEmail(email: email)
    }

    func updateConfirmEmail(_ email: String) {
        viewModel.updateConfirmEmail(email: email)
    }

    func setCurrentEmail(_ email: String) {
        viewModel.setCurrentEmail(email: email)
    }

    func clearError() {
        viewModel.clearError()
    }

    deinit {
        stateObserver?.close()
        viewModel.onCleared()
    }
}
