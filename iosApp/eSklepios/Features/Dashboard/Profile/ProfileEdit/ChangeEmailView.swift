import SwiftUI
import shared

struct ChangeEmailView: View {
    @StateObject private var viewModel = ChangeEmailViewModelWrapper()
    @Environment(\.dismiss) var dismiss

    private var isNewEmailValid: Bool {
        ValidationUtil.shared.isValidEmail(email: viewModel.uiState.newEmail)
    }
    private var emailsMatch: Bool {
        ValidationUtil.shared.emailsMatch(first: viewModel.uiState.newEmail, confirm: viewModel.uiState.confirmEmail)
    }
    private var canSave: Bool { isNewEmailValid && emailsMatch && !viewModel.uiState.isLoading }

    var body: some View {
        AppScreen(
            title: NSLocalizedString("screen_change_email", value: "Change Email", comment: ""),
            onBack: { dismiss() },
            error: viewModel.uiState.error,
            onErrorDismissed: { viewModel.clearError() }
        ) {
            HStack(alignment: .top, spacing: Dimens.paddingM) {
                Image(systemName: "envelope.badge.fill")
                    .font(.system(size: Dimens.iconLg))
                    .foregroundColor(.appPrimary)
                    .padding(.top, Spacing.xxs)
                AppCaptionText(text: NSLocalizedString("change_email_banner", value: "We'll send a verification link to your new email. Your current email stays active until you confirm.", comment: ""), color: .appPrimary)
                    .lineSpacing(Dimens.strokeThick)
            }
            .padding(Dimens.paddingL)
            .frame(maxWidth: .infinity, alignment: .leading)
            .background(Color.appPrimaryLight)
            .cornerRadius(Dimens.radiusMd)

            formField(label: NSLocalizedString("label_current_email", value: "Current Email", comment: ""), required: false) {
                HStack(spacing: Dimens.paddingS) {
                    Image(systemName: "envelope").font(.system(size: Dimens.iconMd)).foregroundColor(.appTextHint)
                    AppBodyText(text: viewModel.uiState.currentEmail.isEmpty
                         ? NSLocalizedString("change_email_not_available", value: "Not available", comment: "")
                         : viewModel.uiState.currentEmail)
                    Spacer()
                }
                .padding(.horizontal, Dimens.paddingM)
                .frame(height: Dimens.inputHeight)
                .background(Color.appBackground)
                .cornerRadius(Dimens.radiusMd)
                .overlay(RoundedRectangle(cornerRadius: Dimens.radiusMd).stroke(Color.appTextHint.opacity(0.3), lineWidth: Dimens.strokeThin))
            }

            VStack(alignment: .leading, spacing: Dimens.paddingS) {
                formField(label: NSLocalizedString("label_new_email_field", value: "New Email", comment: ""), required: true) {
                    HStack(spacing: Dimens.paddingS) {
                        Image(systemName: "envelope").font(.system(size: Dimens.iconMd)).foregroundColor(.appTextSecondary)
                        TextField(NSLocalizedString("label_new_email", value: "Enter new email address", comment: ""), text: Binding(
                            get: { viewModel.uiState.newEmail },
                            set: { viewModel.updateNewEmail($0) }
                        ))
                        .keyboardType(.emailAddress)
                        .autocapitalization(.none)
                        .autocorrectionDisabled()
                    }
                    .padding(.horizontal, Dimens.paddingM)
                    .frame(height: Dimens.inputHeight)
                    .background(Color.appSurface)
                    .cornerRadius(Dimens.radiusMd)
                    .overlay(RoundedRectangle(cornerRadius: Dimens.radiusMd).stroke(Color.appTextHint.opacity(0.4), lineWidth: Dimens.strokeThin))
                }
                if isNewEmailValid {
                    ValidationCaption(text: NSLocalizedString("change_email_valid", value: "Valid email format", comment: ""), isValid: true)
                }
            }

            VStack(alignment: .leading, spacing: Dimens.paddingS) {
                formField(label: NSLocalizedString("label_confirm_email", value: "Confirm New Email", comment: ""), required: true) {
                    HStack(spacing: Dimens.paddingS) {
                        Image(systemName: "envelope.badge").font(.system(size: Dimens.iconMd)).foregroundColor(.appTextSecondary)
                        TextField(NSLocalizedString("label_confirm_email", value: "Confirm new email address", comment: ""), text: Binding(
                            get: { viewModel.uiState.confirmEmail },
                            set: { viewModel.updateConfirmEmail($0) }
                        ))
                        .keyboardType(.emailAddress)
                        .autocapitalization(.none)
                        .autocorrectionDisabled()
                    }
                    .padding(.horizontal, Dimens.paddingM)
                    .frame(height: Dimens.inputHeight)
                    .background(Color.appSurface)
                    .cornerRadius(Dimens.radiusMd)
                    .overlay(
                        RoundedRectangle(cornerRadius: Dimens.radiusMd).stroke(
                            viewModel.uiState.confirmEmail.isEmpty ? Color.appTextHint.opacity(0.4) :
                            emailsMatch ? Color.appSuccess.opacity(0.6) : Color.appDanger.opacity(0.6),
                            lineWidth: Dimens.strokeThin
                        )
                    )
                }
                if emailsMatch {
                    ValidationCaption(text: NSLocalizedString("change_email_match", value: "Emails match", comment: ""), isValid: true)
                } else if !viewModel.uiState.confirmEmail.isEmpty {
                    ValidationCaption(text: NSLocalizedString("change_email_no_match", value: "Emails do not match", comment: ""), isValid: false)
                }
            }

            PrimaryButton(
                title: NSLocalizedString("action_save", value: "Save", comment: ""),
                icon: "checkmark",
                isLoading: viewModel.uiState.isLoading,
                isEnabled: canSave
            ) {
                viewModel.changeEmail()
            }
            .frame(maxWidth: .infinity)

            SecondaryButton(title: NSLocalizedString("action_cancel", value: "Cancel", comment: "")) {
                dismiss()
            }
            .frame(maxWidth: .infinity)
        }
        .onChange(of: viewModel.uiState.isSuccess) { success in if success { dismiss() } }
    }

    private func formField<Content: View>(label: String, required: Bool, @ViewBuilder content: () -> Content) -> some View {
        VStack(alignment: .leading, spacing: Dimens.paddingS) {
            FormFieldLabel(label: label, required: required)
            content()
        }
    }
}