import SwiftUI

struct LoginView: View {
    @StateObject private var viewModel = AuthViewModelWrapper()
    @Environment(\.dismiss) var dismiss
    @State private var navigateToHome = false
    @State private var navigateToRegister = false
    @State private var navigateToForgot = false
    @State private var showSaveCredentialsDialog = false

    var body: some View {
        AppScreen(
            title: NSLocalizedString("screen_login", value: "Sign In", comment: ""),
            onBack: { dismiss() },
            error: viewModel.uiState.error,
            onErrorDismissed: { viewModel.clearError() }
        ) {
            HStack(spacing: Dimens.paddingM) {
                Image(systemName: "shield.fill")
                    .foregroundColor(.appWarning)
                AppCaptionText(text: NSLocalizedString("auth_security_banner", value: "This is the official eSklepios app. Never share your password.", comment: ""), color: .appWarning)
            }
            .padding(Dimens.paddingM)
            .frame(maxWidth: .infinity, alignment: .leading)
            .background(Color.appWarningBg)
            .cornerRadius(Dimens.radiusMd)

            VStack(alignment: .leading, spacing: Dimens.paddingS) {
                FormFieldLabel(label: NSLocalizedString("label_email_address", value: "Email address", comment: ""))
                HStack {
                    Image(systemName: "envelope").foregroundColor(.appPrimaryMid)
                    TextField("your@email.com", text: Binding(
                        get: { viewModel.uiState.email },
                        set: { viewModel.updateField(field: .email, value: $0) }
                    ))
                    .keyboardType(.emailAddress)
                    .textInputAutocapitalization(.never)
                }
                .padding(Dimens.paddingM)
                .background(Color.appSurface)
                .cornerRadius(Dimens.radiusMd)
                .overlay(RoundedRectangle(cornerRadius: Dimens.radiusMd).stroke(Color.appTextHint.opacity(0.4), lineWidth: 1))
            }

            VStack(alignment: .leading, spacing: Dimens.paddingS) {
                FormFieldLabel(label: NSLocalizedString("label_password", value: "Password", comment: ""))
                HStack {
                    Image(systemName: "lock").foregroundColor(.appPrimaryMid)
                    SecureField(NSLocalizedString("label_password", value: "Password", comment: ""), text: Binding(
                        get: { viewModel.uiState.password },
                        set: { viewModel.updateField(field: .password, value: $0) }
                    ))
                }
                .padding(Dimens.paddingM)
                .background(Color.appSurface)
                .cornerRadius(Dimens.radiusMd)
                .overlay(RoundedRectangle(cornerRadius: Dimens.radiusMd).stroke(Color.appTextHint.opacity(0.4), lineWidth: 1))
            }

            HStack {
                Spacer()
                AppTextLink(text: NSLocalizedString("login_forgot_password", value: "Forgot password?", comment: "")) {
                    navigateToForgot = true
                }
            }

            PrimaryButton(
                title: NSLocalizedString("action_sign_in", value: "Sign In", comment: ""),
                isLoading: viewModel.uiState.isLoading
            ) {
                viewModel.login(email: viewModel.uiState.email, password: viewModel.uiState.password)
            }
            .frame(maxWidth: .infinity)

            DividerWithLabel(label: NSLocalizedString("login_or", value: "or", comment: ""))

            VStack(spacing: Dimens.paddingM) {
                socialButton(
                    icon: "g.circle.fill",
                    label: NSLocalizedString("login_google", value: "Sign in with Google", comment: ""),
                    color: .appGoogleBlue
                )
                socialButton(
                    icon: "apple.logo",
                    label: NSLocalizedString("login_apple", value: "Sign in with Apple", comment: ""),
                    color: Color.primary
                )
            }

            HStack {
                AppBodyText(text: NSLocalizedString("login_no_account", value: "No account?", comment: ""))
                AppTextLink(text: NSLocalizedString("action_register", value: "Create Account", comment: "")) {
                    navigateToRegister = true
                }
            }
        }
        .onChange(of: viewModel.uiState.showSaveCredentialsDialog) { show in
            if show { showSaveCredentialsDialog = true }
        }
        .onChange(of: viewModel.uiState.isLoggedIn) { loggedIn in
            if loggedIn { navigateToHome = true }
        }
        .confirmationDialog(
            "Save Credentials?",
            isPresented: $showSaveCredentialsDialog,
            actions: {
                Button("Save") {
                    viewModel.saveCredentials()
                    showSaveCredentialsDialog = false
                }
                Button("Not Now") {
                    viewModel.skipSaveCredentials()
                    showSaveCredentialsDialog = false
                }
            },
            message: {
                Text("Would you like to save your email and password for faster login next time?")
            }
        )
        .navigationDestination(isPresented: $navigateToHome) { HomeView() }
        .navigationDestination(isPresented: $navigateToRegister) { RegisterView() }
        .navigationDestination(isPresented: $navigateToForgot) { ForgotPasswordView() }
    }

    private func socialButton(icon: String, label: String, color: Color) -> some View {
        Button(action: {}) {
            HStack(spacing: Dimens.paddingM) {
                Image(systemName: icon).font(.heading3).foregroundColor(color)
                AppBodyText(text: label, color: .appTextPrimary)
            }
            .frame(maxWidth: .infinity)
            .frame(height: Dimens.buttonHeight)
            .background(Color.appSurface)
            .cornerRadius(Dimens.radiusMd)
            .overlay(RoundedRectangle(cornerRadius: Dimens.radiusMd).stroke(Color.appTextHint.opacity(0.3), lineWidth: 1))
        }
    }
}