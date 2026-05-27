import SwiftUI

struct ForgotPasswordView: View {
    @StateObject private var viewModel = AuthViewModelWrapper()
    @Environment(\.dismiss) var dismiss
    @State private var email = ""

    var body: some View {
        AppScreen(
            title: NSLocalizedString("screen_forgot_password", value: "Forgot Password", comment: ""),
            onBack: { dismiss() },
            error: viewModel.uiState.error,
            onErrorDismissed: { viewModel.clearError() }
        ) {
            Spacer().frame(height: Dimens.paddingXXL)

            ZStack {
                Circle()
                    .fill(Color.appPrimaryLight)
                    .frame(
                        width: Dimens.avatarLg + Dimens.paddingXXXL,
                        height: Dimens.avatarLg + Dimens.paddingXXXL
                    )
                Image(systemName: viewModel.uiState.forgotPasswordSent ? "checkmark.circle.fill" : "lock.fill")
                    .font(.system(size: Dimens.avatarMd))
                    .foregroundColor(viewModel.uiState.forgotPasswordSent ? .appSuccess : .appPrimary)
            }

            if viewModel.uiState.forgotPasswordSent {
                VStack(spacing: Dimens.paddingM) {
                    AppTitleText(text: NSLocalizedString("forgot_password_sent_title", value: "Check your email", comment: ""))
                    AppBodyText(text: String(format: NSLocalizedString("forgot_password_sent_subtitle", value: "We've sent a password reset link to %1$s", comment: ""), email), alignment: .center)
                }

                PrimaryButton(title: NSLocalizedString("forgot_password_back", value: "Back to Sign In", comment: "")) {
                    dismiss()
                }
                .frame(maxWidth: .infinity)
            } else {
                VStack(spacing: Dimens.paddingM) {
                    AppTitleText(text: NSLocalizedString("forgot_password_title", value: "Reset your password", comment: ""))
                    AppBodyText(text: NSLocalizedString("forgot_password_description", value: "Enter your email address and we'll send you a reset link.", comment: ""), alignment: .center)
                }

                VStack(alignment: .leading, spacing: Dimens.paddingS) {
                    FormFieldLabel(label: NSLocalizedString("label_email_address", value: "Email address", comment: ""))
                    HStack {
                        Image(systemName: "envelope").foregroundColor(.appPrimaryMid)
                        TextField("your@email.com", text: $email)
                            .keyboardType(.emailAddress)
                            .textInputAutocapitalization(.never)
                    }
                    .padding(Dimens.paddingM)
                    .background(Color.appSurface)
                    .cornerRadius(Dimens.radiusMd)
                    .overlay(RoundedRectangle(cornerRadius: Dimens.radiusMd).stroke(Color.appTextHint.opacity(0.4), lineWidth: 1))
                }

                PrimaryButton(
                    title: NSLocalizedString("forgot_password_send_link", value: "Send Reset Link", comment: ""),
                    isLoading: viewModel.uiState.isLoading
                ) {
                    viewModel.forgotPassword(email: email)
                }
                .frame(maxWidth: .infinity)
            }
        }
    }
}