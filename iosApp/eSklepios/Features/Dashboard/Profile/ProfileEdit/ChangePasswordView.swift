import SwiftUI
import shared

struct ChangePasswordView: View {
    @StateObject private var viewModel = ChangePasswordViewModelWrapper()
    @Environment(\.dismiss) var dismiss
    @State private var showCurrentPw = false
    @State private var showNewPw = false
    @State private var showConfirmPw = false
    @State private var showError = false

    private var newPw: String { viewModel.uiState.newPassword }

    private var criteriaResult: PasswordCriteriaResult { ValidationUtil.shared.passwordCriteriaResult(password: newPw) }
    private var hasMinLength: Bool    { criteriaResult.minLength }
    private var hasMixedCase: Bool    { criteriaResult.mixedCase }
    private var hasNumAndSymbol: Bool { criteriaResult.numAndSymbol }
    private var strength: PasswordStrength { ValidationUtil.shared.passwordStrength(password: newPw) }
    private var strengthPercent: Double { Double(strength.percent) }
    private var passwordsMatch: Bool {
        ValidationUtil.shared.passwordsMatch(newPassword: newPw, confirmPassword: viewModel.uiState.confirmPassword)
    }
    private var canSave: Bool {
        !viewModel.uiState.oldPassword.isEmpty && !newPw.isEmpty && passwordsMatch && !viewModel.uiState.isLoading
    }

    private var strengthBarWidth: CGFloat {
        max(0, CGFloat(strengthPercent) * (UIScreen.main.bounds.width - Sizing.toolbarSlot))
    }

    private var strengthLabel: String {
        let prefix = NSLocalizedString("change_password_strength", value: "Strength:", comment: "")
        return "\(prefix) \(strength.displayLabel)"
    }

    @ViewBuilder private var strengthSection: some View {
        if !newPw.isEmpty {
            ZStack(alignment: .leading) {
                RoundedRectangle(cornerRadius: Dimens.progressBarRadiusThick)
                    .fill(Color.appTextHint.opacity(0.25))
                    .frame(height: Dimens.progressBarHeightThick)
                RoundedRectangle(cornerRadius: Dimens.progressBarRadiusThick)
                    .fill(strength.displayColor)
                    .frame(width: strengthBarWidth, height: Dimens.progressBarHeightThick)
            }
            HStack {
                AppCaptionText(text: strengthLabel, color: strength.displayColor)
                Spacer()
                AppCaptionText(text: "\(Int(strengthPercent * 100))%", color: strength.displayColor)
            }
            VStack(alignment: .leading, spacing: Spacing.xs) {
                criterionRow("At least 12 characters", met: hasMinLength)
                criterionRow("One uppercase and lowercase", met: hasMixedCase)
                criterionRow("One number and symbol", met: hasNumAndSymbol)
            }
        }
    }

    var body: some View {
        ScrollView {
            VStack(spacing: Spacing.none) {
                AppToolbar(title: "Change Password", onBack: { dismiss() }, useGradient: true)

                VStack(spacing: Spacing.xl) {
                    Spacer().frame(height: Spacing.xs)

                    // Security banner
                    HStack(alignment: .top, spacing: Spacing.m) {
                        AppIcon(systemName: "shield.fill", tint: .appDanger, size: Dimens.iconCompact)
                            .padding(.top, Spacing.xxs)
                        VStack(alignment: .leading, spacing: Spacing.xxs) {
                            AppLabelText(text: "Keep your account secure", color: .appDanger)
                            AppCaptionText(text: "Use a strong, unique password you don't reuse elsewhere.", color: .appDanger.opacity(0.8))
                        }
                        Spacer()
                    }
                    .padding(Spacing.l)
                    .background(Color.appDangerBg)
                    .cornerRadius(Radius.md)

                    // Current Password
                    passwordFormField(
                        label: "Current Password",
                        placeholder: "Enter current password",
                        text: Binding(get: { viewModel.uiState.oldPassword }, set: { viewModel.updateOldPassword($0) }),
                        showPassword: $showCurrentPw,
                        leadingIcon: "lock.fill"
                    )

                    // New Password + strength
                    VStack(alignment: .leading, spacing: Spacing.compact) {
                        passwordFormField(
                            label: "New Password",
                            placeholder: "Enter new password",
                            text: Binding(get: { viewModel.uiState.newPassword }, set: { viewModel.updateNewPassword($0) }),
                            showPassword: $showNewPw,
                            leadingIcon: "lock.rotation"
                        )

                        strengthSection
                    }

                    // Confirm New Password + match indicator
                    VStack(alignment: .leading, spacing: Spacing.s) {
                        passwordFormField(
                            label: "Confirm New Password",
                            placeholder: "Re-enter new password",
                            text: Binding(get: { viewModel.uiState.confirmPassword }, set: { viewModel.updateConfirmPassword($0) }),
                            showPassword: $showConfirmPw,
                            leadingIcon: "lock.open.fill",
                            isError: !viewModel.uiState.confirmPassword.isEmpty && !passwordsMatch
                        )
                        if passwordsMatch {
                            ValidationCaption(text: "Passwords match", isValid: true)
                        } else if !viewModel.uiState.confirmPassword.isEmpty {
                            ValidationCaption(text: "Passwords do not match", isValid: false)
                        }
                    }

                    Spacer().frame(height: Spacing.s)

                    PrimaryButton(
                        title: NSLocalizedString("action_save", value: "Save", comment: ""),
                        icon: "checkmark",
                        isLoading: viewModel.uiState.isLoading,
                        isEnabled: canSave
                    ) {
                        viewModel.changePassword(
                            currentPassword: viewModel.uiState.oldPassword,
                            newPassword: viewModel.uiState.newPassword,
                            confirmPassword: viewModel.uiState.confirmPassword
                        )
                    }
                    .frame(maxWidth: .infinity)

                    SecondaryButton(title: NSLocalizedString("action_cancel", value: "Cancel", comment: "")) {
                        dismiss()
                    }
                    .frame(maxWidth: .infinity)

                    Spacer().frame(height: Spacing.xxxl)
                }
                .padding(.horizontal, Spacing.xl)
                .padding(.top, Spacing.xl)
            }
        }
        .background(Color.appBackground)
        .navigationBarHidden(true)
        .alert("Error", isPresented: $showError) {
            Button("OK") { viewModel.clearError() }
        } message: {
            AppBodyText(text: viewModel.uiState.error ?? "")
        }
        .onChange(of: viewModel.uiState.isSuccess) { success in if success { dismiss() } }
        .onChange(of: viewModel.uiState.error) { error in showError = error != nil }
    }

    private func passwordFormField(
        label: String,
        placeholder: String,
        text: Binding<String>,
        showPassword: Binding<Bool>,
        leadingIcon: String,
        isError: Bool = false
    ) -> some View {
        VStack(alignment: .leading, spacing: Spacing.s) {
            FormFieldLabel(label: label, required: true)
            HStack(spacing: Spacing.compact) {
                AppIcon(systemName: leadingIcon, size: Dimens.iconSm)
                Group {
                    if showPassword.wrappedValue {
                        TextField(placeholder, text: text)
                    } else {
                        SecureField(placeholder, text: text)
                    }
                }
                .autocapitalization(.none)
                .autocorrectionDisabled()
                Spacer()
                AppIconButton(
                    systemName: showPassword.wrappedValue ? "eye.slash" : "eye",
                    accessibilityLabel: NSLocalizedString(
                        showPassword.wrappedValue ? "cd_hide_password" : "cd_show_password",
                        value: showPassword.wrappedValue ? "Hide password" : "Show password",
                        comment: ""
                    ),
                    action: { showPassword.wrappedValue.toggle() },
                    tint: .appTextHint,
                    iconSize: Dimens.iconSm
                )
            }
            .padding(.horizontal, Spacing.plus)
            .frame(height: Sizing.inputHeight)
            .background(Color.appSurface)
            .cornerRadius(Radius.input)
            .overlay(
                RoundedRectangle(cornerRadius: Radius.input)
                    .stroke(isError ? Color.appDanger.opacity(0.6) : Color.appTextHint.opacity(0.4), lineWidth: Dimens.strokeThin)
            )
        }
    }

    private func criterionRow(_ label: String, met: Bool) -> some View {
        HStack(spacing: Spacing.tiny) {
            AppIcon(
                systemName: met ? "checkmark.circle.fill" : "circle",
                tint: met ? .appSuccess : .appTextHint,
                size: Dimens.iconMicro
            )
            AppLabelText(text: label, color: met ? .appSuccess : .appTextHint)
        }
    }
}
