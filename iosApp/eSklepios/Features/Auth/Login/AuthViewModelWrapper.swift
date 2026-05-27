import Foundation
import Combine
import shared

@MainActor
class AuthViewModelWrapper: ObservableObject {
    private let viewModel: AuthViewModel
    private var stateObserver: FlowWatcher?
    @Published var isLoading: Bool = false
    @Published var isLoggedIn: Bool = false
    @Published var error: String? = nil
    @Published var forgotPasswordSent: Bool = false
    @Published var step: Int32 = 1
    @Published var email: String = ""
    @Published var password: String = ""
    @Published var confirmPassword: String = ""

    init() {
        self.viewModel = KoinHelper.shared.authViewModel()
        let initial = viewModel.uiState.value as? AuthUiState
        self.isLoading = initial?.isLoading ?? false
        self.isLoggedIn = initial?.isLoggedIn ?? false
        self.error = initial?.error
        self.forgotPasswordSent = initial?.forgotPasswordSent ?? false
        self.step = initial?.step ?? 1
        self.email = initial?.email ?? ""
        self.password = initial?.password ?? ""
        self.confirmPassword = initial?.confirmPassword ?? ""
        startCollecting()
    }

    private func startCollecting() {
        stateObserver = FlowExtensionsKt.watch(viewModel.uiState) { [weak self] anyState in
            guard let state = anyState as? AuthUiState else { return }
            Task { @MainActor [weak self] in
                self?.isLoading = state.isLoading
                self?.isLoggedIn = state.isLoggedIn
                self?.error = state.error
                self?.forgotPasswordSent = state.forgotPasswordSent
                self?.step = state.step
                self?.email = state.email
                self?.password = state.password
                self?.confirmPassword = state.confirmPassword
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

    func updateField(field: AuthField, value: String) {
        viewModel.updateField(field: field, value: value)
    }

    struct UiState {
        var isLoading: Bool
        var isLoggedIn: Bool
        var error: String?
        var forgotPasswordSent: Bool
        var step: Int32
        var email: String
        var password: String
        var confirmPassword: String
    }
    var uiState: UiState {
        UiState(isLoading: isLoading, isLoggedIn: isLoggedIn, error: error,
                forgotPasswordSent: forgotPasswordSent, step: step,
                email: email, password: password, confirmPassword: confirmPassword)
    }

    func clearError() {
        viewModel.clearError()
    }

    deinit {
        stateObserver?.close()
        viewModel.onCleared()
    }
}
