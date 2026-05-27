import Foundation
import Combine
import shared

struct HomeUiStateSwift {
    var isLoading: Bool = false
    var practitioners: [Practitioner] = []
    var error: String? = nil
}

class HomeViewModelWrapper: ObservableObject {
    private let viewModel: HomeViewModel
    @Published var isLoading: Bool = false
    @Published var practitioners: [Practitioner] = []
    @Published var error: String? = nil

    init() {
        self.viewModel = KoinHelper.shared.homeViewModel()
        let initial = viewModel.uiState.value
        self.isLoading = initial?.isLoading ?? false
        self.practitioners = initial?.practitioners as? [Practitioner] ?? []
        self.error = initial?.error
        startCollecting()
    }

    private func startCollecting() {
        viewModel.uiState.watch { [weak self] state in
            guard let state = state else { return }
            DispatchQueue.main.async {
                self?.isLoading = state.isLoading
                self?.practitioners = state.practitioners as? [Practitioner] ?? []
                self?.error = state.error
            }
        }
    }

    var uiState: HomeUiStateSwift {
        HomeUiStateSwift(isLoading: isLoading, practitioners: practitioners, error: error)
    }

    func search(query: String = "", location: String = "") {
        viewModel.search(query: query, location: location)
    }

    func applyFilter(_ filter: String) {
        viewModel.applyFilter(filter: filter)
    }

    func refresh() {
        viewModel.refresh()
    }

    func loadMore() {
        viewModel.loadMore()
    }

    deinit {
        viewModel.onCleared()
    }
}
