import SwiftUI

struct ChangePasswordView: View {
    @StateObject private var viewModel = ChangePasswordViewModelWrapper()
    @Environment(\.dismiss) var dismiss
    @State private var oldPassword = ""
    @State private var newPassword = ""
    @State private var confirmPassword = ""
    @State private var showError = false

    private var passwordStrength: Int {
        let p = newPassword
        if p.count >= 12 && p.contains(where: { $0.isNumber }) && p.contains(where: { $0.isUppercase }) && p.contains(where: { !$0.isLetter && !$0.isNumber }) { return 4 }
        if p.count >= 10 && (p.contains(where: { $0.isNumber }) || p.contains(where: { $0.isUppercase })) { return 3 }
        if p.count >= 8 { return 2 }
        return p.isEmpty ? 0 : 1
    }

    private var strengthLabel: String {
        switch passwordStrength {
        case 4: return "Strong"
        case 3: return "Good"
        case 2: return "Fair"
        case 1: return "Weak"
        default: return ""
        }
    }

    private var strengthColor: Color {
        switch passwordStrength {
        case 4: return .appSuccess
        case 3: return Color(hex: "65A30D")
        case 2: return .appWarning
        default: return .appDanger
        }
    }

    var body: some View {
        ScrollView {
            VStack(spacing: 0) {
                AppToolbar(title: "Change Password", onBack: { dismiss() })

                VStack(spacing: Dimens.paddingL) {
                    Spacer().frame(height: Dimens.paddingS)

                    // Old password
                    secureField(label: "Current password *", placeholder: "Enter your current password", text: $oldPassword)

                    // New password
                    secureField(label: "New password *", placeholder: "At least 8 characters", text: $newPassword)

                    // Strength indicator
                    if !newPassword.isEmpty {
                        VStack(alignment: .leading, spacing: 4) {
                            HStack(spacing: 4) {
                                ForEach(1...4, id: \.self) { level in
                                    RoundedRectangle(cornerRadius: 2)
                                        .fill(level <= passwordStrength ? strengthColor : Color.appTextHint.opacity(0.3))
                                        .frame(maxWidth: .infinity)
                                        .frame(height: 4)
                                }
                            }
                            Text(strengthLabel)
                                .font(.system(size: 12, weight: .medium))
                                .foregroundColor(strengthColor)
                        }
                    }

                    // Confirm password
                    secureField(
                        label: "Confirm new password *",
                        placeholder: "Re-enter your new password",
                        text: $confirmPassword,
                        errorMessage: !confirmPassword.isEmpty && confirmPassword != newPassword ? "Passwords do not match" : nil
                    )

                    PrimaryButton(title: "Update Password", isLoading: viewModel.uiState.isLoading) {
                        viewModel.changePassword(
                            currentPassword: oldPassword,
                            newPassword: newPassword,
                            confirmPassword: confirmPassword
                        )
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

    private func secureField(label: String, placeholder: String, text: Binding<String>, errorMessage: String? = nil) -> some View {
        VStack(alignment: .leading, spacing: 6) {
            Text(label)
                .font(.system(size: 13, weight: .medium))
                .foregroundColor(errorMessage != nil ? .appDanger : .appTextPrimary)
            SecureField(placeholder, text: text)
                .padding(12)
                .background(Color.appSurface)
                .cornerRadius(Dimens.radiusMd)
                .overlay(
                    RoundedRectangle(cornerRadius: Dimens.radiusMd)
                        .stroke(errorMessage != nil ? Color.appDanger : Color.appTextHint.opacity(0.4), lineWidth: 1)
                )
            if let error = errorMessage {
                Text(error).font(.system(size: 12)).foregroundColor(.appDanger)
            }
        }
    }
}
