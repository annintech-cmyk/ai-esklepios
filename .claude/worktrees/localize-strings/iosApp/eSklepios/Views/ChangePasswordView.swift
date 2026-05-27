import SwiftUI

struct ChangePasswordView: View {
    @StateObject private var viewModel = ChangePasswordViewModelWrapper()
    @Environment(\.dismiss) var dismiss
    @State private var showCurrentPw = false
    @State private var showNewPw = false
    @State private var showConfirmPw = false
    @State private var showError = false

    private var newPw: String { viewModel.uiState.newPassword }

    private var hasMinLength: Bool  { newPw.count >= 12 }
    private var hasMixedCase: Bool  { newPw.contains(where: { $0.isUppercase }) && newPw.contains(where: { $0.isLowercase }) }
    private var hasNumAndSymbol: Bool { newPw.contains(where: { $0.isNumber }) && newPw.contains(where: { !$0.isLetter && !$0.isNumber }) }

    private var strength: Int {
        guard !newPw.isEmpty else { return 0 }
        if hasMinLength && hasMixedCase && hasNumAndSymbol { return 4 }
        if newPw.count >= 10 && (newPw.contains(where: { $0.isNumber }) || newPw.contains(where: { $0.isUppercase })) { return 3 }
        if newPw.count >= 8 { return 2 }
        return 1
    }
    private var strengthPercent: Double { [0: 0, 1: 0.15, 2: 0.4, 3: 0.65, 4: 0.9][strength] ?? 0 }
    private var strengthLabelKey: String {
        switch strength {
        case 4: return "change_password_strength_strong"
        case 3: return "change_password_strength_good"
        case 2: return "change_password_strength_fair"
        default: return "change_password_strength_weak"
        }
    }
    private var strengthColor: Color   {
        switch strength {
        case 4: return .appSuccess
        case 3: return Color(hex: "65A30D")
        case 2: return .appWarning
        default: return .appDanger
        }
    }
    private var passwordsMatch: Bool {
        !viewModel.uiState.confirmPassword.isEmpty && viewModel.uiState.confirmPassword == newPw
    }
    private var canSave: Bool {
        !viewModel.uiState.oldPassword.isEmpty && !newPw.isEmpty && passwordsMatch && !viewModel.uiState.isLoading
    }

    var body: some View {
        ScrollView {
            VStack(spacing: 0) {
                AppToolbar(title: String(localized: "screen_change_password"), onBack: { dismiss() }, useGradient: true)

                VStack(spacing: 20) {
                    Spacer().frame(height: 4)

                    // Security banner
                    HStack(alignment: .top, spacing: 12) {
                        Image(systemName: "shield.fill")
                            .font(.system(size: 18))
                            .foregroundColor(.appDanger)
                            .padding(.top, 1)
                        VStack(alignment: .leading, spacing: 2) {
                            Text(String(localized: "change_password_security_title"))
                                .font(.system(size: 13, weight: .bold))
                                .foregroundColor(.appDanger)
                            Text(String(localized: "change_password_security_subtitle"))
                                .font(.system(size: 12))
                                .foregroundColor(.appDanger.opacity(0.8))
                        }
                        Spacer()
                    }
                    .padding(16)
                    .background(Color.appDangerBg)
                    .cornerRadius(12)

                    // Current Password
                    passwordFormField(
                        label: String(localized: "change_password_current_label"),
                        placeholder: String(localized: "change_password_current_placeholder"),
                        text: Binding(get: { viewModel.uiState.oldPassword }, set: { viewModel.updateOldPassword(it: $0) }),
                        showPassword: $showCurrentPw,
                        leadingIcon: "lock.fill"
                    )

                    // New Password + strength
                    VStack(alignment: .leading, spacing: 10) {
                        passwordFormField(
                            label: String(localized: "change_password_new_label"),
                            placeholder: String(localized: "change_password_new_placeholder"),
                            text: Binding(get: { viewModel.uiState.newPassword }, set: { viewModel.updateNewPassword(it: $0) }),
                            showPassword: $showNewPw,
                            leadingIcon: "lock.rotation"
                        )

                        if !newPw.isEmpty {
                            // Strength bar
                            ZStack(alignment: .leading) {
                                RoundedRectangle(cornerRadius: 3).fill(Color.appTextHint.opacity(0.25)).frame(height: 6)
                                RoundedRectangle(cornerRadius: 3).fill(strengthColor)
                                    .frame(width: max(0, CGFloat(strengthPercent) * (UIScreen.main.bounds.width - 40)), height: 6)
                            }
                            HStack {
                                Text(String(format: String(localized: "change_password_strength_format"), String(localized: strengthLabelKey)))
                                    .font(.system(size: 12, weight: .medium))
                                    .foregroundColor(strengthColor)
                                Spacer()
                                Text("\(Int(strengthPercent * 100))%")
                                    .font(.system(size: 12, weight: .medium))
                                    .foregroundColor(strengthColor)
                            }
                            // Criteria
                            VStack(alignment: .leading, spacing: 4) {
                                criterionRow(String(localized: "change_password_criteria_min_length"), met: hasMinLength)
                                criterionRow(String(localized: "change_password_criteria_mixed_case"), met: hasMixedCase)
                                criterionRow(String(localized: "change_password_criteria_number_symbol"), met: hasNumAndSymbol)
                            }
                        }
                    }

                    // Confirm New Password + match indicator
                    VStack(alignment: .leading, spacing: 8) {
                        passwordFormField(
                            label: String(localized: "change_password_confirm_label"),
                            placeholder: String(localized: "change_password_confirm_placeholder"),
                            text: Binding(get: { viewModel.uiState.confirmPassword }, set: { viewModel.updateConfirmPassword(it: $0) }),
                            showPassword: $showConfirmPw,
                            leadingIcon: "lock.open.fill",
                            isError: !viewModel.uiState.confirmPassword.isEmpty && !passwordsMatch
                        )
                        if passwordsMatch {
                            HStack(spacing: 4) {
                                Image(systemName: "checkmark.circle.fill").font(.system(size: 13)).foregroundColor(.appSuccess)
                                Text(String(localized: "change_password_match")).font(.system(size: 12)).foregroundColor(.appSuccess)
                            }
                        } else if !viewModel.uiState.confirmPassword.isEmpty {
                            HStack(spacing: 4) {
                                Image(systemName: "xmark.circle.fill").font(.system(size: 13)).foregroundColor(.appDanger)
                                Text(String(localized: "change_password_no_match")).font(.system(size: 12)).foregroundColor(.appDanger)
                            }
                        }
                    }

                    Spacer().frame(height: 8)

                    // Save button
                    Button {
                        viewModel.changePassword(
                            currentPassword: viewModel.uiState.oldPassword,
                            newPassword: viewModel.uiState.newPassword,
                            confirmPassword: viewModel.uiState.confirmPassword
                        )
                    } label: {
                        HStack(spacing: 6) {
                            if viewModel.uiState.isLoading {
                                ProgressView().tint(.white)
                            } else {
                                Text(String(localized: "action_save")).font(.system(size: 16, weight: .semibold))
                                Image(systemName: "checkmark").font(.system(size: 14, weight: .semibold))
                            }
                        }
                        .foregroundColor(.white)
                        .frame(maxWidth: .infinity)
                        .frame(height: 52)
                        .background(canSave ? Color.appPrimary : Color.appPrimary.opacity(0.4))
                        .cornerRadius(14)
                    }
                    .disabled(!canSave)

                    // Cancel button
                    Button { dismiss() } label: {
                        Text(String(localized: "action_cancel"))
                            .font(.system(size: 16, weight: .semibold))
                            .foregroundColor(.appPrimary)
                            .frame(maxWidth: .infinity)
                            .frame(height: 52)
                            .overlay(RoundedRectangle(cornerRadius: 14).stroke(Color.appPrimary, lineWidth: 1.5))
                    }

                    Spacer().frame(height: 32)
                }
                .padding(.horizontal, 20)
                .padding(.top, 20)
            }
        }
        .background(Color.appBackground)
        .navigationBarHidden(true)
        .alert(String(localized: "error_title"), isPresented: $showError) {
            Button(String(localized: "action_ok")) { viewModel.clearError() }
        } message: {
            Text(viewModel.uiState.error ?? "")
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
        VStack(alignment: .leading, spacing: 8) {
            HStack(spacing: 2) {
                Text(label).font(.system(size: 14, weight: .medium)).foregroundColor(.appTextPrimary)
                Text(" *").font(.system(size: 14, weight: .medium)).foregroundColor(.appDanger)
            }
            HStack(spacing: 10) {
                Image(systemName: leadingIcon).font(.system(size: 16)).foregroundColor(.appTextSecondary)
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
                Button {
                    showPassword.wrappedValue.toggle()
                } label: {
                    Image(systemName: showPassword.wrappedValue ? "eye.slash" : "eye")
                        .font(.system(size: 16))
                        .foregroundColor(.appTextHint)
                }
            }
            .padding(.horizontal, 14)
            .frame(height: 52)
            .background(Color.appSurface)
            .cornerRadius(10)
            .overlay(
                RoundedRectangle(cornerRadius: 10)
                    .stroke(isError ? Color.appDanger.opacity(0.6) : Color.appTextHint.opacity(0.4), lineWidth: 1)
            )
        }
    }

    private func criterionRow(_ label: String, met: Bool) -> some View {
        HStack(spacing: 6) {
            Image(systemName: met ? "checkmark.circle.fill" : "circle")
                .font(.system(size: 14))
                .foregroundColor(met ? .appSuccess : .appTextHint)
            Text(label)
                .font(.system(size: 13))
                .foregroundColor(met ? .appSuccess : .appTextHint)
        }
    }
}
