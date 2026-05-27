import Foundation
import Combine
import shared

@MainActor
class ProfileViewModelWrapper: ObservableObject {
    private let viewModel: ProfileViewModel
    private var stateObserver: FlowWatcher?
    @Published var uiState: ProfileUiState

    init() {
        self.viewModel = KoinHelper.shared.profileViewModel()
        self.uiState = viewModel.uiState.value as? ProfileUiState
            ?? ProfileUiState(isLoading: false, user: nil, error: nil, isLoggedOut: false)
        startCollecting()
    }

    private func startCollecting() {
        stateObserver = FlowExtensionsKt.watch(viewModel.uiState) { [weak self] anyState in
            Task { @MainActor [weak self] in
                self?.uiState = anyState as? ProfileUiState
                    ?? ProfileUiState(isLoading: false, user: nil, error: nil, isLoggedOut: false)
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
        stateObserver?.close()
        viewModel.onCleared()
    }
}
