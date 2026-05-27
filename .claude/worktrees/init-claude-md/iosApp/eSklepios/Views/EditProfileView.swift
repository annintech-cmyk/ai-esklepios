import SwiftUI

struct EditProfileView: View {
    @StateObject private var viewModel = EditProfileViewModelWrapper()
    @Environment(\.dismiss) var dismiss
    @State private var firstName = ""
    @State private var lastName = ""
    @State private var phone = ""
    @State private var gender = "Other"
    @State private var dateOfBirth = ""
    @State private var language = "en"
    @State private var showError = false
    @State private var isSaved = false

    var body: some View {
        ScrollView {
            VStack(spacing: 0) {
                AppToolbar(
                    title: "Edit Profile",
                    onBack: { dismiss() },
                    trailingAction: { save() },
                    trailingIcon: "checkmark"
                )

                VStack(spacing: Dimens.paddingL) {
                    editField(label: "First name", placeholder: "John", text: $firstName)
                    editField(label: "Last name", placeholder: "Doe", text: $lastName)
                    editField(label: "Phone number", placeholder: "+352 000 000 000", text: $phone, keyboard: .phonePad)
                    editField(label: "Date of birth", placeholder: "DD/MM/YYYY", text: $dateOfBirth)

                    // Gender chips
                    VStack(alignment: .leading, spacing: 8) {
                        Text("Gender").font(.system(size: 13, weight: .medium)).foregroundColor(.appTextPrimary)
                        ScrollView(.horizontal, showsIndicators: false) {
                            HStack(spacing: 8) {
                                ForEach(["Male", "Female", "Other"], id: \.self) { g in
                                    FilterChip(label: g, isSelected: gender == g) { gender = g }
                                }
                            }
                        }
                    }

                    // Language chips
                    VStack(alignment: .leading, spacing: 8) {
                        Text("Preferred language").font(.system(size: 13, weight: .medium)).foregroundColor(.appTextPrimary)
                        ScrollView(.horizontal, showsIndicators: false) {
                            HStack(spacing: 8) {
                                ForEach([("en", "English"), ("fr", "Français"), ("de", "Deutsch"), ("lb", "Lëtzebuergesch")], id: \.0) { code, label in
                                    FilterChip(label: label, isSelected: language == code) { language = code }
                                }
                            }
                        }
                    }

                    PrimaryButton(title: "Save Changes", isLoading: viewModel.uiState.isLoading) {
                        save()
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
        .onAppear {
            let state = viewModel.uiState
            firstName = state.firstName
            lastName = state.lastName
            phone = state.phone
            gender = state.gender.isEmpty ? "Other" : state.gender
            dateOfBirth = state.dateOfBirth
            language = state.language.isEmpty ? "en" : state.language
        }
        .alert("Error", isPresented: $showError) {
            Button("OK") { viewModel.clearError() }
        } message: {
            Text(viewModel.uiState.error ?? "")
        }
        .onChange(of: viewModel.uiState.isSaved) { saved in
            if saved { dismiss() }
        }
        .onChange(of: viewModel.uiState.error) { error in
            showError = error != nil
        }
    }

    private func save() {
        viewModel.saveProfile(
            firstName: firstName,
            lastName: lastName,
            phone: phone,
            dateOfBirth: dateOfBirth,
            gender: gender,
            address: ""
        )
    }

    private func editField(label: String, placeholder: String, text: Binding<String>, keyboard: UIKeyboardType = .default) -> some View {
        VStack(alignment: .leading, spacing: 6) {
            Text(label).font(.system(size: 13, weight: .medium)).foregroundColor(.appTextPrimary)
            TextField(placeholder, text: text)
                .keyboardType(keyboard)
                .padding(12)
                .background(Color.appSurface)
                .cornerRadius(Dimens.radiusMd)
                .overlay(RoundedRectangle(cornerRadius: Dimens.radiusMd).stroke(Color.appTextHint.opacity(0.4), lineWidth: 1))
        }
    }
}
