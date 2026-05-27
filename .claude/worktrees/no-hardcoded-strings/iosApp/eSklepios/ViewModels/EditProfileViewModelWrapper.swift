import Foundation
import Combine
import shared

class EditProfileViewModelWrapper: ObservableObject {
    private let viewModel: EditProfileViewModel
    @Published var uiState: EditProfileUiState

    init() {
        self.viewModel = KoinHelper.shared.editProfileViewModel()
        self.uiState = viewModel.uiState.value ?? EditProfileUiState()
        startCollecting()
    }

    private func startCollecting() {
        viewModel.uiState.watch { [weak self] state in
            DispatchQueue.main.async {
                self?.uiState = state ?? EditProfileUiState()
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
        viewModel.onCleared()
    }
}
