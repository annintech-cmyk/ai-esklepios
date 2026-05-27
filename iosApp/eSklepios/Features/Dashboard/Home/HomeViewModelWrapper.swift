import Foundation
import Combine
import shared

struct HomeUiStateSwift {
    var isLoading: Bool = false
    var practitioners: [Practitioner] = []
    var selectedDateFilter: String = "All"
    var openToNewPatients: Bool = false
    var error: String? = nil
}

@MainActor
class HomeViewModelWrapper: ObservableObject {
    private let viewModel: HomeViewModel
    private var stateObserver: FlowWatcher?
    @Published var isLoading: Bool = false
    @Published var practitioners: [Practitioner] = []
    @Published var selectedDateFilter: String = "All"
    @Published var openToNewPatients: Bool = false
    @Published var error: String? = nil

    init() {
        self.viewModel = KoinHelper.shared.homeViewModel()
        let initial = viewModel.uiState.value as? HomeUiState
        self.isLoading = initial?.isLoading ?? false
        self.practitioners = initial?.practitioners as? [Practitioner] ?? []
        self.selectedDateFilter = initial?.selectedDateFilter ?? "All"
        self.openToNewPatients = initial?.openToNewPatients ?? false
        self.error = initial?.error
        startCollecting()
    }

    private func startCollecting() {
        stateObserver = FlowExtensionsKt.watch(viewModel.uiState) { [weak self] anyState in
            guard let state = anyState as? HomeUiState else { return }
            Task { @MainActor [weak self] in
                self?.isLoading = state.isLoading
                self?.practitioners = state.practitioners as? [Practitioner] ?? []
                self?.selectedDateFilter = state.selectedDateFilter
                self?.openToNewPatients = state.openToNewPatients
                self?.error = state.error
            }
        }
    }

    var uiState: HomeUiStateSwift {
        HomeUiStateSwift(
            isLoading: isLoading,
            practitioners: practitioners,
            selectedDateFilter: selectedDateFilter,
            openToNewPatients: openToNewPatients,
            error: error
        )
    }

    func search(query: String = "", location: String = "") {
        viewModel.search(query: query, location: location)
    }

    func setDateFilter(_ filter: String) {
        viewModel.setDateFilter(filter: filter)
    }

    func toggleNewPatientsFilter() {
        viewModel.toggleNewPatientsFilter()
    }

    func toggleFavorite(id: String) {
        viewModel.toggleFavorite(id: id)
    }

    func refresh() {
        viewModel.refresh()
    }

    func loadMore() {
        viewModel.loadMore()
    }

    deinit {
        stateObserver?.close()
        viewModel.onCleared()
    }
}
