import Foundation
import Combine
import shared

class PractitionerDetailViewModelWrapper: ObservableObject {
    private let viewModel: PractitionerDetailViewModel
    @Published var isLoading: Bool = false
    @Published var practitioner: Practitioner? = nil
    @Published var error: String? = nil
    @Published var isFavorite: Bool = false
    @Published var selectedSlot: AppointmentSlot? = nil

    private let practitionerId: String

    init(practitionerId: String) {
        self.practitionerId = practitionerId
        self.viewModel = KoinHelper.shared.practitionerDetailViewModel()
        let initial = viewModel.uiState.value
        self.isLoading = initial?.isLoading ?? false
        self.practitioner = initial?.practitioner
        self.error = initial?.error
        self.isFavorite = initial?.isFavorite ?? false
        self.selectedSlot = initial?.selectedSlot
        startCollecting()
    }

    private func startCollecting() {
        viewModel.uiState.watch { [weak self] state in
            guard let state = state else { return }
            DispatchQueue.main.async {
                self?.isLoading = state.isLoading
                self?.practitioner = state.practitioner
                self?.error = state.error
                self?.isFavorite = state.isFavorite
                self?.selectedSlot = state.selectedSlot
            }
        }
    }

    struct UiState {
        var isLoading: Bool
        var practitioner: Practitioner?
        var error: String?
        var selectedSlot: AppointmentSlot?
        var isFavorite: Bool
    }
    var uiState: UiState {
        UiState(isLoading: isLoading, practitioner: practitioner, error: error,
                selectedSlot: selectedSlot, isFavorite: isFavorite)
    }

    func loadDetail() {
        viewModel.loadDetail(id: practitionerId)
    }

    func selectSlot(_ slotId: String) {
        viewModel.selectSlot(slotId: slotId)
    }

    func toggleFavorite() {
        viewModel.toggleFavorite()
    }

    deinit {
        viewModel.onCleared()
    }
}
