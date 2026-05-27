import Foundation
import Combine
import shared

@MainActor
class PractitionerDetailViewModelWrapper: ObservableObject {
    private let viewModel: PractitionerDetailViewModel
    private var stateObserver: FlowWatcher?
    @Published var isLoading: Bool = false
    @Published var practitioner: Practitioner? = nil
    @Published var error: String? = nil
    @Published var selectedSlot: AppointmentSlot? = nil

    private let practitionerId: String

    init(practitionerId: String) {
        self.practitionerId = practitionerId
        self.viewModel = KoinHelper.shared.practitionerDetailViewModel()
        let initial = viewModel.uiState.value as? PractitionerDetailUiState
        self.isLoading = initial?.isLoading ?? false
        self.practitioner = initial?.practitioner
        self.error = initial?.error
        self.selectedSlot = initial?.selectedSlot
        startCollecting()
    }

    private func startCollecting() {
        stateObserver = FlowExtensionsKt.watch(viewModel.uiState) { [weak self] anyState in
            guard let state = anyState as? PractitionerDetailUiState else { return }
            Task { @MainActor [weak self] in
                self?.isLoading = state.isLoading
                self?.practitioner = state.practitioner
                self?.error = state.error
                self?.selectedSlot = state.selectedSlot
            }
        }
    }

    struct UiState {
        var isLoading: Bool
        var practitioner: Practitioner?
        var error: String?
        var selectedSlot: AppointmentSlot?
    }
    var uiState: UiState {
        UiState(isLoading: isLoading, practitioner: practitioner, error: error,
                selectedSlot: selectedSlot)
    }

    func loadDetail() {
        viewModel.loadDetail(id: practitionerId)
    }

    func selectSlot(_ slotId: String) {
        viewModel.selectSlot(slotId: slotId)
    }

    deinit {
        stateObserver?.close()
        viewModel.onCleared()
    }
}
