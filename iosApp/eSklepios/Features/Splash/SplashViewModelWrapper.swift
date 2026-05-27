import Foundation
import Combine
import shared

@MainActor
class SplashViewModelWrapper: ObservableObject {
    private let viewModel: SplashViewModel
    private var stateObserver: FlowWatcher?
    @Published var isLoading: Bool = true
    @Published var isAuthenticated: Bool = false

    init() {
        self.viewModel = KoinHelper.shared.splashViewModel()
        let initial = viewModel.uiState.value as? SplashUiState
        self.isLoading = initial?.isLoading ?? true
        self.isAuthenticated = initial?.isAuthenticated ?? false
        startCollecting()
    }

    private func startCollecting() {
        stateObserver = FlowExtensionsKt.watch(viewModel.uiState) { [weak self] anyState in
            guard let state = anyState as? SplashUiState else { return }
            Task { @MainActor [weak self] in
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
        stateObserver?.close()
        viewModel.onCleared()
    }
}
