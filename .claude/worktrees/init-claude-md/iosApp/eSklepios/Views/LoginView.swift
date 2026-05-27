import SwiftUI

struct LoginView: View {
    @StateObject private var viewModel = AuthViewModelWrapper()
    @Environment(\.dismiss) var dismiss
    @State private var email = ""
    @State private var password = ""
    @State private var navigateToHome = false
    @State private var navigateToRegister = false
    @State private var navigateToForgot = false
    @State private var showError = false

    var body: some View {
        ScrollView {
            VStack(spacing: 0) {
                AppToolbar(title: "Sign In", onBack: { dismiss() })

                VStack(spacing: Dimens.paddingL) {
                    // Security banner
                    HStack(spacing: 10) {
                        Image(systemName: "shield.fill")
                            .foregroundColor(.appWarning)
                        Text("This is the official eSklepios app. Never share your password.")
                            .font(.system(size: 12, weight: .medium))
                            .foregroundColor(.appWarning)
                    }
                    .padding(12)
                    .frame(maxWidth: .infinity, alignment: .leading)
                    .background(Color.appWarningBg)
                    .cornerRadius(Dimens.radiusMd)

                    // Email field
                    VStack(alignment: .leading, spacing: 6) {
                        Text("Email address")
                            .font(.system(size: 13, weight: .medium))
                            .foregroundColor(.appTextPrimary)
                        HStack {
                            Image(systemName: "envelope").foregroundColor(.appPrimaryMid)
                            TextField("your@email.com", text: $email)
                                .keyboardType(.emailAddress)
                                .autocapitalization(.none)
                        }
                        .padding(12)
                        .background(Color.appSurface)
                        .cornerRadius(Dimens.radiusMd)
                        .overlay(RoundedRectangle(cornerRadius: Dimens.radiusMd).stroke(Color.appTextHint.opacity(0.4), lineWidth: 1))
                    }

                    // Password field
                    VStack(alignment: .leading, spacing: 6) {
                        Text("Password")
                            .font(.system(size: 13, weight: .medium))
                            .foregroundColor(.appTextPrimary)
                        HStack {
                            Image(systemName: "lock").foregroundColor(.appPrimaryMid)
                            SecureField("Enter your password", text: $password)
                        }
                        .padding(12)
                        .background(Color.appSurface)
                        .cornerRadius(Dimens.radiusMd)
                        .overlay(RoundedRectangle(cornerRadius: Dimens.radiusMd).stroke(Color.appTextHint.opacity(0.4), lineWidth: 1))
                    }

                    HStack {
                        Spacer()
                        Button("Forgot password?") { navigateToForgot = true }
                            .font(.system(size: 13))
                            .foregroundColor(.appPrimary)
                    }

                    PrimaryButton(title: "Sign In", isLoading: viewModel.uiState.isLoading) {
                        viewModel.login(email: email, password: password)
                    }
                    .frame(maxWidth: .infinity)

                    // Or divider
                    HStack {
                        Rectangle().frame(height: 1).foregroundColor(.appTextHint.opacity(0.3))
                        Text("or").font(.system(size: 13)).foregroundColor(.appTextSecondary).padding(.horizontal, 8)
                        Rectangle().frame(height: 1).foregroundColor(.appTextHint.opacity(0.3))
                    }

                    // Social buttons
                    VStack(spacing: 10) {
                        socialButton(icon: "g.circle.fill", label: "Sign in with Google", color: Color(hex: "4285F4"))
                        socialButton(icon: "apple.logo", label: "Sign in with Apple", color: Color.black)
                    }

                    HStack {
                        Text("No account?").foregroundColor(.appTextSecondary).font(.system(size: 14))
                        Button("Register") { navigateToRegister = true }
                            .font(.system(size: 14, weight: .semibold))
                            .foregroundColor(.appPrimary)
                    }
                }
                .padding(.horizontal, Dimens.paddingXXL)
                .padding(.top, Dimens.paddingXXL)
            }
        }
        .background(Color.appBackground)
        .navigationBarHidden(true)
        .alert("Error", isPresented: $showError) {
            Button("OK") { viewModel.clearError() }
        } message: {
            Text(viewModel.uiState.error ?? "")
        }
        .onChange(of: viewModel.uiState.isLoggedIn) { loggedIn in
            if loggedIn { navigateToHome = true }
        }
        .onChange(of: viewModel.uiState.error) { error in
            showError = error != nil
        }
        .navigationDestination(isPresented: $navigateToHome) { HomeView() }
        .navigationDestination(isPresented: $navigateToRegister) { RegisterView() }
        .navigationDestination(isPresented: $navigateToForgot) { ForgotPasswordView() }
    }

    private func socialButton(icon: String, label: String, color: Color) -> some View {
        Button(action: {}) {
            HStack(spacing: 10) {
                Image(systemName: icon).font(.system(size: 18)).foregroundColor(color)
                Text(label).font(.system(size: 14, weight: .medium)).foregroundColor(.appTextPrimary)
            }
            .frame(maxWidth: .infinity)
            .frame(height: Dimens.buttonHeight)
            .background(Color.appSurface)
            .cornerRadius(Dimens.radiusMd)
            .overlay(RoundedRectangle(cornerRadius: Dimens.radiusMd).stroke(Color.appTextHint.opacity(0.3), lineWidth: 1))
        }
    }
}
