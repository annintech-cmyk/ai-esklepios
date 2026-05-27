import SwiftUI

struct EditProfileView: View {
    @StateObject private var viewModel = EditProfileViewModelWrapper()
    @Environment(\.dismiss) var dismiss

    @State private var selectedGender = "Other"
    @State private var dateOfBirth = ""
    @State private var firstName = ""
    @State private var lastName = ""
    @State private var selectedPrefix = "+352"
    @State private var phoneNumber = ""
    @State private var cnsNumber = ""
    @State private var showDatePicker = false
    @State private var showPrefixPicker = false
    @State private var showError = false

    // Gender values are language-neutral keys; display labels are localized
    private let genderOptions: [(value: String, labelKey: String)] = [
        ("Man", "edit_gender_man"),
        ("Woman", "edit_gender_woman"),
        ("Other", "edit_gender_other")
    ]
    private let prefixes: [(String, String)] = [
        ("+352", "🇱🇺 +352"), ("+33", "🇫🇷 +33"), ("+49", "🇩🇪 +49"), ("+32", "🇧🇪 +32"), ("+44", "🇬🇧 +44")
    ]

    private var dobDisplay: String {
        guard !dateOfBirth.isEmpty else { return "" }
        let parts = dateOfBirth.split(separator: "-")
        guard parts.count == 3 else { return dateOfBirth }
        return "\(parts[2]) / \(parts[1]) / \(parts[0])"
    }

    var body: some View {
        ScrollView {
            VStack(spacing: 0) {
                AppToolbar(title: String(localized: "edit_personal_info_title"), onBack: { dismiss() }, useGradient: true)

                VStack(spacing: 20) {
                    // Gender segmented control
                    formSection(label: String(localized: "edit_gender_label"), required: true) {
                        HStack(spacing: 0) {
                            ForEach(Array(genderOptions.enumerated()), id: \.0) { index, option in
                                let isSelected = selectedGender == option.value
                                let isFirst = index == 0
                                let isLast = index == genderOptions.count - 1
                                Button {
                                    selectedGender = option.value
                                } label: {
                                    Text(String(localized: String.LocalizationValue(option.labelKey)))
                                        .font(.system(size: 14, weight: isSelected ? .semibold : .regular))
                                        .foregroundColor(isSelected ? .white : .appTextPrimary)
                                        .frame(maxWidth: .infinity)
                                        .frame(height: 44)
                                        .background(isSelected ? Color.appPrimary : Color.appSurface)
                                        .overlay(
                                            RoundedRectangle(cornerRadius: 10)
                                                .stroke(isSelected ? Color.appPrimary : Color.appTextHint.opacity(0.4), lineWidth: 1)
                                                .clipShape(
                                                    .rect(
                                                        topLeadingRadius: isFirst ? 10 : 0,
                                                        bottomLeadingRadius: isFirst ? 10 : 0,
                                                        bottomTrailingRadius: isLast ? 10 : 0,
                                                        topTrailingRadius: isLast ? 10 : 0
                                                    )
                                                )
                                        )
                                        .clipShape(
                                            .rect(
                                                topLeadingRadius: isFirst ? 10 : 0,
                                                bottomLeadingRadius: isFirst ? 10 : 0,
                                                bottomTrailingRadius: isLast ? 10 : 0,
                                                topTrailingRadius: isLast ? 10 : 0
                                            )
                                        )
                                }
                                .buttonStyle(.plain)
                            }
                        }
                    }

                    // Date of Birth
                    formSection(label: String(localized: "edit_dob_label"), required: true) {
                        Button { showDatePicker = true } label: {
                            HStack {
                                Image(systemName: "calendar")
                                    .font(.system(size: 16))
                                    .foregroundColor(.appTextSecondary)
                                Text(dobDisplay.isEmpty ? String(localized: "edit_dob_placeholder") : dobDisplay)
                                    .font(.system(size: 16))
                                    .foregroundColor(dobDisplay.isEmpty ? .appTextHint : .appTextPrimary)
                                Spacer()
                            }
                            .padding(.horizontal, 14)
                            .frame(height: 52)
                            .background(Color.appSurface)
                            .cornerRadius(10)
                            .overlay(RoundedRectangle(cornerRadius: 10).stroke(Color.appTextHint.opacity(0.4), lineWidth: 1))
                        }
                        .buttonStyle(.plain)
                    }
                    .sheet(isPresented: $showDatePicker) {
                        DatePickerSheet(selectedDate: Binding(
                            get: {
                                let formatter = DateFormatter()
                                formatter.dateFormat = "yyyy-MM-dd"
                                return formatter.date(from: dateOfBirth) ?? Date()
                            },
                            set: { date in
                                let formatter = DateFormatter()
                                formatter.dateFormat = "yyyy-MM-dd"
                                dateOfBirth = formatter.string(from: date)
                            }
                        ), onDone: { showDatePicker = false })
                    }

                    // First Name
                    formSection(label: String(localized: "edit_first_name_label"), required: true) {
                        inputField(placeholder: "Sophie", text: $firstName)
                    }

                    // Last Name
                    formSection(label: String(localized: "edit_last_name_label"), required: true) {
                        inputField(placeholder: "Müller", text: $lastName)
                    }

                    // Phone Number
                    formSection(label: String(localized: "edit_phone_label"), required: true) {
                        HStack(spacing: 8) {
                            // Country prefix menu
                            Menu {
                                ForEach(prefixes, id: \.0) { code, label in
                                    Button(label) { selectedPrefix = code }
                                }
                            } label: {
                                HStack(spacing: 4) {
                                    Text(prefixes.first { $0.0 == selectedPrefix }?.1 ?? selectedPrefix)
                                        .font(.system(size: 14))
                                        .foregroundColor(.appTextPrimary)
                                    Image(systemName: "chevron.up.chevron.down")
                                        .font(.system(size: 11))
                                        .foregroundColor(.appTextSecondary)
                                }
                                .padding(.horizontal, 10)
                                .frame(width: 100, height: 52)
                                .background(Color.appSurface)
                                .cornerRadius(10)
                                .overlay(RoundedRectangle(cornerRadius: 10).stroke(Color.appTextHint.opacity(0.4), lineWidth: 1))
                            }

                            TextField("621 123 456", text: $phoneNumber)
                                .keyboardType(.phonePad)
                                .padding(.horizontal, 14)
                                .frame(height: 52)
                                .background(Color.appSurface)
                                .cornerRadius(10)
                                .overlay(RoundedRectangle(cornerRadius: 10).stroke(Color.appTextHint.opacity(0.4), lineWidth: 1))
                        }
                    }

                    // CNS Number
                    formSection(label: String(localized: "edit_cns_number"), required: false) {
                        HStack {
                            Image(systemName: "creditcard.fill")
                                .font(.system(size: 16))
                                .foregroundColor(.appTextSecondary)
                            TextField("1992 0314 5678 9012", text: $cnsNumber)
                                .keyboardType(.numberPad)
                        }
                        .padding(.horizontal, 14)
                        .frame(height: 52)
                        .background(Color.appSurface)
                        .cornerRadius(10)
                        .overlay(RoundedRectangle(cornerRadius: 10).stroke(Color.appTextHint.opacity(0.4), lineWidth: 1))
                    }

                    Spacer().frame(height: 8)

                    // Save button
                    Button { save() } label: {
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
                        .background(Color.appPrimary)
                        .cornerRadius(14)
                    }
                    .disabled(viewModel.uiState.isLoading)

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
                .padding(.top, 24)
            }
        }
        .background(Color.appBackground)
        .navigationBarHidden(true)
        .onAppear { prefill() }
        .alert(String(localized: "error_title"), isPresented: $showError) {
            Button(String(localized: "action_ok")) { viewModel.clearError() }
        } message: {
            Text(viewModel.uiState.error ?? "")
        }
        .onChange(of: viewModel.uiState.isSaved) { saved in if saved { dismiss() } }
        .onChange(of: viewModel.uiState.error) { error in showError = error != nil }
    }

    private func prefill() {
        let state = viewModel.uiState
        firstName = state.firstName
        lastName = state.lastName
        cnsNumber = state.cnsNumber
        dateOfBirth = state.dateOfBirth

        let genderMapped: String
        switch state.gender.lowercased() {
        case "male", "man": genderMapped = "Man"
        case "female", "woman": genderMapped = "Woman"
        default: genderMapped = "Other"
        }
        selectedGender = genderMapped.isEmpty ? "Other" : genderMapped

        let knownPrefixes = ["+352", "+33", "+49", "+32", "+44"]
        let prefix = knownPrefixes.first { state.phone.hasPrefix($0) } ?? "+352"
        selectedPrefix = prefix
        phoneNumber = state.phone.hasPrefix(prefix) ? String(state.phone.dropFirst(prefix.count)).trimmingCharacters(in: .whitespaces) : state.phone
    }

    private func save() {
        viewModel.saveProfile(
            firstName: firstName,
            lastName: lastName,
            phone: "\(selectedPrefix) \(phoneNumber)",
            dateOfBirth: dateOfBirth,
            gender: selectedGender,
            address: ""
        )
    }

    private func formSection<Content: View>(label: String, required: Bool, @ViewBuilder content: () -> Content) -> some View {
        VStack(alignment: .leading, spacing: 8) {
            HStack(spacing: 2) {
                Text(label).font(.system(size: 14, weight: .medium)).foregroundColor(.appTextPrimary)
                if required { Text(" *").font(.system(size: 14, weight: .medium)).foregroundColor(.appDanger) }
            }
            content()
        }
    }

    private func inputField(placeholder: String, text: Binding<String>, keyboard: UIKeyboardType = .default) -> some View {
        TextField(placeholder, text: text)
            .keyboardType(keyboard)
            .padding(.horizontal, 14)
            .frame(height: 52)
            .background(Color.appSurface)
            .cornerRadius(10)
            .overlay(RoundedRectangle(cornerRadius: 10).stroke(Color.appTextHint.opacity(0.4), lineWidth: 1))
    }
}

// MARK: - Date Picker Sheet

private struct DatePickerSheet: View {
    @Binding var selectedDate: Date
    let onDone: () -> Void

    var body: some View {
        VStack(spacing: 0) {
            HStack {
                Button(String(localized: "action_cancel")) { onDone() }.foregroundColor(.appTextSecondary)
                Spacer()
                Text(String(localized: "edit_dob_label")).font(.system(size: 16, weight: .semibold))
                Spacer()
                Button(String(localized: "action_done")) { onDone() }.foregroundColor(.appPrimary).fontWeight(.semibold)
            }
            .padding()
            DatePicker("", selection: $selectedDate, displayedComponents: .date)
                .datePickerStyle(.wheel)
                .labelsHidden()
        }
        .presentationDetents([.height(320)])
    }
}
