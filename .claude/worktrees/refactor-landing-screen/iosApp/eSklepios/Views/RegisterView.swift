import SwiftUI

struct RegisterView: View {
    @StateObject private var viewModel = AuthViewModelWrapper()
    @Environment(\.dismiss) var dismiss
    @State private var currentStep = 1
    @State private var firstName = ""
    @State private var lastName = ""
    @State private var dateOfBirth = ""
    @State private var gender = "Other"
    @State private var cnsNumber = ""
    @State private var email = ""
    @State private var phone = ""
    @State private var password = ""
    @State private var profileType = "PATIENT"
    @State private var navigateToHome = false
    @State private var showError = false

    var body: some View {
        ScrollView {
            VStack(spacing: 0) {
                AppToolbar(title: "Create Account", onBack: {
                    if currentStep > 1 { currentStep -= 1 } else { dismiss() }
                })

                VStack(spacing: Dimens.paddingL) {
                    // Progress indicator
                    VStack(alignment: .leading, spacing: 6) {
                        HStack(spacing: 6) {
                            ForEach(1...3, id: \.self) { step in
                                RoundedRectangle(cornerRadius: 2)
                                    .fill(step <= currentStep ? Color.appPrimary : Color.appTextHint.opacity(0.3))
                                    .frame(maxWidth: .infinity)
                                    .frame(height: 4)
                            }
                        }
                        Text("Step \(currentStep) of 3")
                            .font(.system(size: 12))
                            .foregroundColor(.appTextSecondary)
                    }

                    // Step content
                    switch currentStep {
                    case 1:
                        RegisterStep1View(
                            firstName: $firstName, lastName: $lastName,
                            dateOfBirth: $dateOfBirth, gender: $gender, cnsNumber: $cnsNumber
                        )
                    case 2:
                        RegisterStep2View(email: $email, phone: $phone)
                    default:
                        RegisterStep3View(password: $password, profileType: $profileType)
                    }

                    // Navigation buttons
                    VStack(spacing: 12) {
                        if currentStep < 3 {
                            PrimaryButton(title: "Next") {
                                withAnimation { currentStep += 1 }
                            }
                            .frame(maxWidth: .infinity)
                        } else {
                            PrimaryButton(title: "Create Account", isLoading: viewModel.uiState.isLoading) {
                                viewModel.register(
                                    firstName: firstName, lastName: lastName,
                                    email: email, password: password,
                                    phone: phone, dateOfBirth: dateOfBirth,
                                    gender: gender, profileType: profileType
                                )
                            }
                            .frame(maxWidth: .infinity)
                        }

                        HStack {
                            Text("Already have an account?").foregroundColor(.appTextSecondary).font(.system(size: 14))
                            Button("Sign In") { dismiss() }
                                .font(.system(size: 14, weight: .semibold))
                                .foregroundColor(.appPrimary)
                        }
                    }
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
        .onChange(of: viewModel.uiState.isLoggedIn) { loggedIn in
            if loggedIn { navigateToHome = true }
        }
        .onChange(of: viewModel.uiState.error) { error in
            showError = error != nil
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
            Text("Personal Information").font(.system(size: 17, weight: .bold)).foregroundColor(.appTextPrimary)
            inputField(label: "First name", placeholder: "John", text: $firstName)
            inputField(label: "Last name", placeholder: "Doe", text: $lastName)
            inputField(label: "Date of birth", placeholder: "DD/MM/YYYY", text: $dateOfBirth)

            VStack(alignment: .leading, spacing: 8) {
                Text("Gender").font(.system(size: 13, weight: .medium)).foregroundColor(.appTextPrimary)
                HStack(spacing: 8) {
                    ForEach(["Male", "Female", "Other"], id: \.self) { g in
                        FilterChip(label: g, isSelected: gender == g) { gender = g }
                    }
                }
            }

            inputField(label: "CNS number", placeholder: "0000000000", text: $cnsNumber, keyboardType: .numberPad)
        }
    }
}

private struct RegisterStep2View: View {
    @Binding var email: String
    @Binding var phone: String

    var body: some View {
        VStack(alignment: .leading, spacing: Dimens.paddingL) {
            Text("Contact Information").font(.system(size: 17, weight: .bold)).foregroundColor(.appTextPrimary)
            inputField(label: "Email address", placeholder: "your@email.com", text: $email, keyboardType: .emailAddress)
            inputField(label: "Phone number", placeholder: "+352 000 000 000", text: $phone, keyboardType: .phonePad)
        }
    }
}

private struct RegisterStep3View: View {
    @Binding var password: String
    @Binding var profileType: String

    var body: some View {
        VStack(alignment: .leading, spacing: Dimens.paddingL) {
            Text("Account Setup").font(.system(size: 17, weight: .bold)).foregroundColor(.appTextPrimary)

            VStack(alignment: .leading, spacing: 6) {
                Text("Password").font(.system(size: 13, weight: .medium)).foregroundColor(.appTextPrimary)
                SecureField("At least 8 characters", text: $password)
                    .padding(12)
                    .background(Color.appSurface)
                    .cornerRadius(Dimens.radiusMd)
                    .overlay(RoundedRectangle(cornerRadius: Dimens.radiusMd).stroke(Color.appTextHint.opacity(0.4), lineWidth: 1))
            }

            VStack(alignment: .leading, spacing: 8) {
                Text("Profile type").font(.system(size: 13, weight: .medium)).foregroundColor(.appTextPrimary)
                HStack(spacing: 8) {
                    FilterChip(label: "Patient", isSelected: profileType == "PATIENT") { profileType = "PATIENT" }
                    FilterChip(label: "Practitioner", isSelected: profileType == "PRACTITIONER") { profileType = "PRACTITIONER" }
                }
            }
        }
    }
}

private func inputField(label: String, placeholder: String, text: Binding<String>, keyboardType: UIKeyboardType = .default) -> some View {
    VStack(alignment: .leading, spacing: 6) {
        Text(label).font(.system(size: 13, weight: .medium)).foregroundColor(.appTextPrimary)
        TextField(placeholder, text: text)
            .keyboardType(keyboardType)
            .autocapitalization(.none)
            .padding(12)
            .background(Color.appSurface)
            .cornerRadius(Dimens.radiusMd)
            .overlay(RoundedRectangle(cornerRadius: Dimens.radiusMd).stroke(Color.appTextHint.opacity(0.4), lineWidth: 1))
    }
}
