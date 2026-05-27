import Foundation
import Combine
import shared

class SplashViewModelWrapper: ObservableObject {
    private let viewModel: SplashViewModel
    @Published var isLoading: Bool = true
    @Published var isAuthenticated: Bool = false

    init() {
        self.viewModel = KoinHelper.shared.splashViewModel()
        let initial = viewModel.uiState.value
        self.isLoading = initial?.isLoading ?? true
        self.isAuthenticated = initial?.isAuthenticated ?? false
        startCollecting()
    }

    private func startCollecting() {
        viewModel.uiState.watch { [weak self] state in
            guard let state = state else { return }
            DispatchQueue.main.async {
                self?.isLoading = state.isLoading
                self?.isAuthenticated = state.isAuthenticated
            }
        }
    }

    struct UiState {
        var isLoading: Bool
        var isAuthenticated: Bool
    }
    var uiState: UiState { UiState(isLoading: isLoading, isAuthenticated: isAuthenticated) }

    func checkAuth() {
        viewModel.checkAuth()
    }

    deinit {
        viewModel.onCleared()
    }
}
