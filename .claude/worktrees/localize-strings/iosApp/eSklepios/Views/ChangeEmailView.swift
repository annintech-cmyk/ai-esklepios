import SwiftUI

struct ChangeEmailView: View {
    @StateObject private var viewModel = ChangeEmailViewModelWrapper()
    @Environment(\.dismiss) var dismiss
    @State private var showError = false

    private var isNewEmailValid: Bool {
        let e = viewModel.uiState.newEmail
        return !e.isEmpty && e.contains("@") && e.contains(".") && e.firstIndex(of: "@")! < e.lastIndex(of: ".")!
    }

    private var emailsMatch: Bool {
        !viewModel.uiState.confirmEmail.isEmpty && viewModel.uiState.confirmEmail == viewModel.uiState.newEmail
    }

    private var canSave: Bool { isNewEmailValid && emailsMatch && !viewModel.uiState.isLoading }

    var body: some View {
        ScrollView {
            VStack(spacing: 0) {
                AppToolbar(title: String(localized: "screen_change_email"), onBack: { dismiss() }, useGradient: true)

                VStack(spacing: 20) {
                    Spacer().frame(height: 4)

                    // Info banner
                    HStack(alignment: .top, spacing: 12) {
                        Image(systemName: "envelope.badge.fill")
                            .font(.system(size: 24))
                            .foregroundColor(.appPrimary)
                            .padding(.top, 2)
                        Text(String(localized: "change_email_verification_info"))
                            .font(.system(size: 13))
                            .foregroundColor(.appPrimary)
                            .lineSpacing(3)
                    }
                    .padding(16)
                    .frame(maxWidth: .infinity, alignment: .leading)
                    .background(Color.appPrimaryLight)
                    .cornerRadius(12)

                    // Current Email (read-only)
                    formField(label: String(localized: "change_email_current_label"), required: false) {
                        HStack(spacing: 10) {
                            Image(systemName: "envelope").font(.system(size: 16)).foregroundColor(.appTextHint)
                            Text(viewModel.uiState.currentEmail.isEmpty ? String(localized: "change_email_not_available") : viewModel.uiState.currentEmail)
                                .font(.system(size: 15))
                                .foregroundColor(.appTextSecondary)
                            Spacer()
                        }
                        .padding(.horizontal, 14)
                        .frame(height: 52)
                        .background(Color(hex: "F9FAFB"))
                        .cornerRadius(10)
                        .overlay(RoundedRectangle(cornerRadius: 10).stroke(Color.appTextHint.opacity(0.3), lineWidth: 1))
                    }

                    // New Email
                    VStack(alignment: .leading, spacing: 8) {
                        formField(label: String(localized: "change_email_new_label"), required: true) {
                            HStack(spacing: 10) {
                                Image(systemName: "envelope").font(.system(size: 16)).foregroundColor(.appTextSecondary)
                                TextField(String(localized: "change_email_new_placeholder"), text: Binding(
                                    get: { viewModel.uiState.newEmail },
                                    set: { viewModel.updateNewEmail($0) }
                                ))
                                .keyboardType(.emailAddress)
                                .autocapitalization(.none)
                                .autocorrectionDisabled()
                            }
                            .padding(.horizontal, 14)
                            .frame(height: 52)
                            .background(Color.appSurface)
                            .cornerRadius(10)
                            .overlay(RoundedRectangle(cornerRadius: 10).stroke(Color.appTextHint.opacity(0.4), lineWidth: 1))
                        }
                        if isNewEmailValid {
                            HStack(spacing: 4) {
                                Image(systemName: "checkmark.circle.fill").font(.system(size: 13)).foregroundColor(.appSuccess)
                                Text(String(localized: "change_email_valid_format")).font(.system(size: 12)).foregroundColor(.appSuccess)
                            }
                        }
                    }

                    // Confirm New Email
                    VStack(alignment: .leading, spacing: 8) {
                        formField(label: String(localized: "change_email_confirm_label"), required: true) {
                            HStack(spacing: 10) {
                                Image(systemName: "envelope.badge").font(.system(size: 16)).foregroundColor(.appTextSecondary)
                                TextField(String(localized: "change_email_confirm_placeholder"), text: Binding(
                                    get: { viewModel.uiState.confirmEmail },
                                    set: { viewModel.updateConfirmEmail($0) }
                                ))
                                .keyboardType(.emailAddress)
                                .autocapitalization(.none)
                                .autocorrectionDisabled()
                            }
                            .padding(.horizontal, 14)
                            .frame(height: 52)
                            .background(Color.appSurface)
                            .cornerRadius(10)
                            .overlay(
                                RoundedRectangle(cornerRadius: 10).stroke(
                                    viewModel.uiState.confirmEmail.isEmpty ? Color.appTextHint.opacity(0.4) :
                                    emailsMatch ? Color.appSuccess.opacity(0.6) : Color.appDanger.opacity(0.6),
                                    lineWidth: 1
                                )
                            )
                        }
                        if emailsMatch {
                            HStack(spacing: 4) {
                                Image(systemName: "checkmark.circle.fill").font(.system(size: 13)).foregroundColor(.appSuccess)
                                Text(String(localized: "change_email_match")).font(.system(size: 12)).foregroundColor(.appSuccess)
                            }
                        } else if !viewModel.uiState.confirmEmail.isEmpty {
                            HStack(spacing: 4) {
                                Image(systemName: "xmark.circle.fill").font(.system(size: 13)).foregroundColor(.appDanger)
                                Text(String(localized: "change_email_no_match")).font(.system(size: 12)).foregroundColor(.appDanger)
                            }
                        }
                    }

                    Spacer().frame(height: 8)

                    // Save button
                    Button { viewModel.changeEmail() } label: {
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

    private func formField<Content: View>(label: String, required: Bool, @ViewBuilder content: () -> Content) -> some View {
        VStack(alignment: .leading, spacing: 8) {
            HStack(spacing: 2) {
                Text(label).font(.system(size: 14, weight: .medium)).foregroundColor(.appTextPrimary)
                if required { Text(" *").font(.system(size: 14, weight: .medium)).foregroundColor(.appDanger) }
            }
            content()
        }
    }
}
