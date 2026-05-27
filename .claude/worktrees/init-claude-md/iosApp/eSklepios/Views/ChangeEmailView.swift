import SwiftUI

struct ChangeEmailView: View {
    @StateObject private var viewModel = ChangeEmailViewModelWrapper()
    @Environment(\.dismiss) var dismiss
    @State private var newEmail = ""
    @State private var password = ""
    @State private var showError = false

    var body: some View {
        ScrollView {
            VStack(spacing: 0) {
                AppToolbar(title: "Change Email", onBack: { dismiss() })

                VStack(spacing: Dimens.paddingL) {
                    Spacer().frame(height: Dimens.paddingS)

                    // Current email display
                    AppCard {
                        VStack(alignment: .leading, spacing: 4) {
                            Text("Current Email")
                                .font(.system(size: 12))
                                .foregroundColor(.appTextSecondary)
                            Text(viewModel.uiState.currentEmail.isEmpty ? "Not available" : viewModel.uiState.currentEmail)
                                .font(.system(size: 15, weight: .medium))
                                .foregroundColor(.appTextPrimary)
                        }
                    }

                    // New email field
                    VStack(alignment: .leading, spacing: 6) {
                        Text("New email address *")
                            .font(.system(size: 13, weight: .medium))
                            .foregroundColor(.appTextPrimary)
                        HStack {
                            Image(systemName: "envelope").foregroundColor(.appPrimaryMid)
                            TextField("new@email.com", text: $newEmail)
                                .keyboardType(.emailAddress)
                                .autocapitalization(.none)
                        }
                        .padding(12)
                        .background(Color.appSurface)
                        .cornerRadius(Dimens.radiusMd)
                        .overlay(RoundedRectangle(cornerRadius: Dimens.radiusMd).stroke(Color.appTextHint.opacity(0.4), lineWidth: 1))
                    }

                    // Password confirmation
                    VStack(alignment: .leading, spacing: 6) {
                        Text("Confirm with password *")
                            .font(.system(size: 13, weight: .medium))
                            .foregroundColor(.appTextPrimary)
                        HStack {
                            Image(systemName: "lock").foregroundColor(.appPrimaryMid)
                            SecureField("Enter your current password", text: $password)
                        }
                        .padding(12)
                        .background(Color.appSurface)
                        .cornerRadius(Dimens.radiusMd)
                        .overlay(RoundedRectangle(cornerRadius: Dimens.radiusMd).stroke(Color.appTextHint.opacity(0.4), lineWidth: 1))
                    }

                    PrimaryButton(title: "Update Email", isLoading: viewModel.uiState.isLoading) {
                        viewModel.changeEmail(currentPassword: password, newEmail: newEmail)
                    }
                    .frame(maxWidth: .infinity)
                }
                .padding(.horizontal, Dimens.paddingXXL)
                .padding(.top, Dimens.paddingXXL)
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
        .onChange(of: viewModel.uiState.isSuccess) { success in
            if success { dismiss() }
        }
        .onChange(of: viewModel.uiState.error) { error in
            showError = error != nil
        }
    }
}
