import SwiftUI

struct ForgotPasswordView: View {
    @StateObject private var viewModel = AuthViewModelWrapper()
    @Environment(\.dismiss) var dismiss
    @State private var email = ""
    @State private var showError = false

    var body: some View {
        ScrollView {
            VStack(spacing: 0) {
                AppToolbar(title: "Forgot Password", onBack: { dismiss() })

                VStack(spacing: Dimens.paddingXXL) {
                    Spacer().frame(height: Dimens.paddingXXL)

                    // Icon
                    ZStack {
                        Circle()
                            .fill(Color.appPrimaryLight)
                            .frame(width: 96, height: 96)
                        Image(systemName: viewModel.uiState.forgotPasswordSent ? "checkmark.circle.fill" : "lock.fill")
                            .font(.system(size: 44))
                            .foregroundColor(viewModel.uiState.forgotPasswordSent ? .appSuccess : .appPrimary)
                    }

                    if viewModel.uiState.forgotPasswordSent {
                        VStack(spacing: 10) {
                            Text("Check your email")
                                .font(.system(size: 22, weight: .bold))
                                .foregroundColor(.appTextPrimary)
                            Text("We've sent a reset link to \(email)")
                                .font(.system(size: 14))
                                .foregroundColor(.appTextSecondary)
                                .multilineTextAlignment(.center)
                        }

                        PrimaryButton(title: "Back to Sign In") { dismiss() }
                            .frame(maxWidth: .infinity)
                    } else {
                        VStack(spacing: 10) {
                            Text("Reset your password")
                                .font(.system(size: 22, weight: .bold))
                                .foregroundColor(.appTextPrimary)
                            Text("Enter your email and we'll send you a reset link.")
                                .font(.system(size: 14))
                                .foregroundColor(.appTextSecondary)
                                .multilineTextAlignment(.center)
                        }

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

                        PrimaryButton(title: "Send Reset Link", isLoading: viewModel.uiState.isLoading) {
                            viewModel.forgotPassword(email: email)
                        }
                        .frame(maxWidth: .infinity)
                    }
                }
                .padding(.horizontal, Dimens.paddingXXL)
                .padding(.bottom, Dimens.paddingXXXL)
            }
        }
        .background(Color.appBackground)
        .navigationBarHidden(true)
        .alert("Error", isPresented: $showError) {
            Button("OK") { viewModel.clearError() }
        } message: {
            Text(viewModel.uiState.error ?? "")
        }
        .onChange(of: viewModel.uiState.error) { error in
            showError = error != nil
        }
    }
}
