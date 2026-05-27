import Foundation
import Combine
import shared

class ProfileViewModelWrapper: ObservableObject {
    private let viewModel: ProfileViewModel
    @Published var uiState: ProfileUiState

    init() {
        self.viewModel = KoinHelper.shared.profileViewModel()
        self.uiState = viewModel.uiState.value ?? ProfileUiState()
        startCollecting()
    }

    private func startCollecting() {
        viewModel.uiState.watch { [weak self] state in
            DispatchQueue.main.async {
                self?.uiState = state ?? ProfileUiState()
            }
        }
    }

    func loadProfile() {
        viewModel.loadProfile()
    }

    func logout() {
        viewModel.logout()
    }

    deinit {
        viewModel.onCleared()
    }
}
