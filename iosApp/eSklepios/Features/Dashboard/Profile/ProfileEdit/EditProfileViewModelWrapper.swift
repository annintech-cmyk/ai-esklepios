import Foundation
import Combine
import shared

@MainActor
class EditProfileViewModelWrapper: ObservableObject {
    private let viewModel: EditProfileViewModel
    private var stateObserver: FlowWatcher?
    @Published var uiState: EditProfileUiState

    init() {
        self.viewModel = KoinHelper.shared.editProfileViewModel()
        self.uiState = viewModel.uiState.value as? EditProfileUiState
            ?? EditProfileUiState(isLoading: false, user: nil, firstName: "", lastName: "", phone: "", gender: "", dateOfBirth: "", cnsNumber: "", language: "", isSaved: false, error: nil)
        startCollecting()
    }

    private func startCollecting() {
        stateObserver = FlowExtensionsKt.watch(viewModel.uiState) { [weak self] anyState in
            Task { @MainActor [weak self] in
                self?.uiState = anyState as? EditProfileUiState
                    ?? EditProfileUiState(isLoading: false, user: nil, firstName: "", lastName: "", phone: "", gender: "", dateOfBirth: "", cnsNumber: "", language: "", isSaved: false, error: nil)
            }
        }
    }

    func saveProfile(firstName: String, lastName: String, phone: String,
                     dateOfBirth: String, gender: String, address: String) {
        viewModel.saveProfile(
            firstName: firstName, lastName: lastName, phone: phone,
            dateOfBirth: dateOfBirth, gender: gender, address: address
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
