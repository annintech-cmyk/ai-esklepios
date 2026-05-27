import SwiftUI
import shared

struct RegisterView: View {
    @StateObject private var viewModel = AuthViewModelWrapper()
    @Environment(\.dismiss) var dismiss
    @State private var currentStep = 1
    @State private var firstName = ""
    @State private var lastName = ""
    @State private var dateOfBirth = ""
    @State private var gender = Gender.other.apiValue
    @State private var cnsNumber = ""
    @State private var email = ""
    @State private var phone = ""
    @State private var password = ""
    @State private var confirmPassword = ""
    @State private var navigateToHome = false

    var body: some View {
        AppScreen(
            title: NSLocalizedString("screen_register", value: "Create Account", comment: ""),
            onBack: {
                if currentStep > 1 { currentStep -= 1 } else { dismiss() }
            },
            error: viewModel.uiState.error,
            onErrorDismissed: { viewModel.clearError() }
        ) {
            VStack(alignment: .leading, spacing: Dimens.paddingS) {
                HStack(spacing: Dimens.paddingS) {
                    ForEach(1...3, id: \.self) { step in
                        RoundedRectangle(cornerRadius: 2)
                            .fill(step <= currentStep ? Color.appPrimary : Color.appTextHint.opacity(0.3))
                            .frame(maxWidth: .infinity)
                            .frame(height: Dimens.progressBarHeight)
                    }
                }
                AppCaptionText(text: String(format: NSLocalizedString("register_step_of", value: "Step %1$d of 3", comment: ""), currentStep, 3))
            }

            switch currentStep {
            case 1:
                RegisterStep1View(
                    firstName: $firstName, lastName: $lastName,
                    dateOfBirth: $dateOfBirth, gender: $gender, cnsNumber: $cnsNumber
                )
            case 2:
                RegisterStep2View(email: $email, phone: $phone)
            default:
                RegisterStep3View(password: $password, confirmPassword: $confirmPassword)
            }

            if currentStep < 3 {
                PrimaryButton(title: NSLocalizedString("action_next", value: "Next", comment: "")) {
                    withAnimation { currentStep += 1 }
                }
                .frame(maxWidth: .infinity)
            } else {
                PrimaryButton(
                    title: NSLocalizedString("action_register", value: "Create Account", comment: ""),
                    isLoading: viewModel.uiState.isLoading
                ) {
                    viewModel.register(
                        firstName: firstName, lastName: lastName,
                        email: email, password: password,
                        phone: phone, dateOfBirth: dateOfBirth,
                        gender: gender, profileType: "PATIENT"
                    )
                }
                .frame(maxWidth: .infinity)
            }

            HStack {
                AppBodyText(text: NSLocalizedString("register_have_account", value: "Already have an account?", comment: ""))
                AppTextLink(text: NSLocalizedString("action_sign_in", value: "Sign In", comment: "")) { dismiss() }
            }
        }
        .onChange(of: viewModel.uiState.isLoggedIn) { loggedIn in
            if loggedIn { navigateToHome = true }
        }
        .navigationDestination(isPresented: $navigateToHome) { HomeView() }
    }
}

private struct RegisterStep1View: View {
    @Binding var firstName: String
    @Binding var lastName: String
    @Binding var dateOfBirth: String
    @Binding var gender: String
    @Binding var cnsNumber: String

    var body: some View {
        VStack(alignment: .leading, spacing: Dimens.paddingL) {
            AppSubtitleText(text: NSLocalizedString("register_step_personal", value: "Personal Information", comment: ""))
            inputField(label: NSLocalizedString("label_first_name", value: "First name", comment: ""), placeholder: "John", text: $firstName)
            inputField(label: NSLocalizedString("label_last_name", value: "Last name", comment: ""), placeholder: "Doe", text: $lastName)
            inputField(label: NSLocalizedString("label_date_of_birth", value: "Date of birth", comment: ""), placeholder: "DD/MM/YYYY", text: $dateOfBirth)

            VStack(alignment: .leading, spacing: Dimens.paddingS) {
                FormFieldLabel(label: NSLocalizedString("label_gender", value: "Gender", comment: ""))
                HStack(spacing: Dimens.paddingS) {
                    ForEach(Array(Gender.entries.enumerated()), id: \.offset) { _, g in
                        FilterChip(
                            label: NSLocalizedString(g.labelKey, value: g.apiValue.capitalized, comment: ""),
                            isSelected: gender == g.apiValue
                        ) { gender = g.apiValue }
                    }
                }
            }

            inputField(
                label: NSLocalizedString("label_cns_number", value: "CNS number", comment: ""),
                placeholder: "0000000000",
                text: $cnsNumber,
                keyboardType: .numberPad
            )
        }
    }
}

private struct RegisterStep2View: View {
    @Binding var email: String
    @Binding var phone: String

    var body: some View {
        VStack(alignment: .leading, spacing: Dimens.paddingL) {
            AppSubtitleText(text: NSLocalizedString("register_step_contact", value: "Contact Information", comment: ""))
            inputField(
                label: NSLocalizedString("label_email_address", value: "Email address", comment: ""),
                placeholder: "your@email.com",
                text: $email,
                keyboardType: .emailAddress
            )
            inputField(
                label: NSLocalizedString("label_phone_number", value: "Phone number", comment: ""),
                placeholder: "+352 000 000 000",
                text: $phone,
                keyboardType: .phonePad
            )
        }
    }
}

private struct RegisterStep3View: View {
    @Binding var password: String
    @Binding var confirmPassword: String

    var body: some View {
        VStack(alignment: .leading, spacing: Dimens.paddingL) {
            AppSubtitleText(text: NSLocalizedString("register_step_account", value: "Account Setup", comment: ""))

            VStack(alignment: .leading, spacing: Dimens.paddingS) {
                FormFieldLabel(label: NSLocalizedString("label_password", value: "Password", comment: ""))
                SecureField(NSLocalizedString("error_password_too_short", value: "At least 8 characters", comment: ""), text: $password)
                    .padding(Dimens.paddingM)
                    .background(Color.appSurface)
                    .cornerRadius(Dimens.radiusMd)
                    .overlay(RoundedRectangle(cornerRadius: Dimens.radiusMd).stroke(Color.appTextHint.opacity(0.4), lineWidth: 1))
            }

            VStack(alignment: .leading, spacing: Dimens.paddingS) {
                FormFieldLabel(label: NSLocalizedString("label_confirm_password", value: "Confirm password", comment: ""))
                SecureField(NSLocalizedString("error_password_too_short", value: "At least 8 characters", comment: ""), text: $confirmPassword)
                    .padding(Dimens.paddingM)
                    .background(Color.appSurface)
                    .cornerRadius(Dimens.radiusMd)
                    .overlay(RoundedRectangle(cornerRadius: Dimens.radiusMd).stroke(Color.appTextHint.opacity(0.4), lineWidth: 1))
            }
        }
    }
}

private func inputField(label: String, placeholder: String, text: Binding<String>, keyboardType: UIKeyboardType = .default) -> some View {
    VStack(alignment: .leading, spacing: Dimens.paddingS) {
        FormFieldLabel(label: label)
        TextField(placeholder, text: text)
            .keyboardType(keyboardType)
            .textInputAutocapitalization(.never)
            .padding(Dimens.paddingM)
            .background(Color.appSurface)
            .cornerRadius(Dimens.radiusMd)
            .overlay(RoundedRectangle(cornerRadius: Dimens.radiusMd).stroke(Color.appTextHint.opacity(0.4), lineWidth: 1))
    }
}