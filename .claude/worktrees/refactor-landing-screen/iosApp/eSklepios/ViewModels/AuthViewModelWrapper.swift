import Foundation
import Combine
import shared

class AuthViewModelWrapper: ObservableObject {
    private let viewModel: AuthViewModel
    @Published var isLoading: Bool = false
    @Published var isLoggedIn: Bool = false
    @Published var error: String? = nil
    @Published var forgotPasswordSent: Bool = false
    @Published var step: Int32 = 1

    init() {
        self.viewModel = KoinHelper.shared.authViewModel()
        let initial = viewModel.uiState.value
        self.isLoading = initial?.isLoading ?? false
        self.isLoggedIn = initial?.isLoggedIn ?? false
        self.error = initial?.error
        self.forgotPasswordSent = initial?.forgotPasswordSent ?? false
        self.step = initial?.step ?? 1
        startCollecting()
    }

    private func startCollecting() {
        viewModel.uiState.watch { [weak self] state in
            guard let state = state else { return }
            DispatchQueue.main.async {
                self?.isLoading = state.isLoading
                self?.isLoggedIn = state.isLoggedIn
                self?.error = state.error
                self?.forgotPasswordSent = state.forgotPasswordSent
                self?.step = state.step
            }
        }
    }

    func login(email: String, password: String) {
        viewModel.login(email: email, password: password)
    }

    func register(firstName: String, lastName: String, email: String, password: String,
                  phone: String, dateOfBirth: String, gender: String, profileType: String) {
        viewModel.register(
            firstName: firstName, lastName: lastName, email: email,
            password: password, phone: phone, dateOfBirth: dateOfBirth,
            gender: gender, profileType: profileType
        )
    }

    func forgotPassword(email: String) {
        viewModel.forgotPassword(email: email)
    }

    func continueAsGuest() {
        viewModel.continueAsGuest()
    }

    struct UiState {
        var isLoading: Bool
        var isLoggedIn: Bool
        var error: String?
        var forgotPasswordSent: Bool
        var step: Int32
    }
    var uiState: UiState {
        UiState(isLoading: isLoading, isLoggedIn: isLoggedIn, error: error,
                forgotPasswordSent: forgotPasswordSent, step: step)
    }

    func clearError() {
        viewModel.clearError()
    }

    deinit {
        viewModel.onCleared()
    }
}
