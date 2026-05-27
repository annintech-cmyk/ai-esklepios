import SwiftUI
import shared

struct EditProfileView: View {
    @StateObject private var viewModel = EditProfileViewModelWrapper()
    @Environment(\.dismiss) var dismiss

    @State private var selectedGender = "other"
    @State private var dateOfBirth = ""
    @State private var firstName = ""
    @State private var lastName = ""
    @State private var selectedPrefix = ""
    @State private var phoneNumber = ""
    @State private var cnsNumber = ""
    @State private var showDatePicker = false
    @State private var showPrefixPicker = false
    @State private var showError = false

    private var dobDisplay: String {
        guard !dateOfBirth.isEmpty else { return "" }
        let parts = dateOfBirth.split(separator: "-")
        guard parts.count == 3 else { return dateOfBirth }
        return "\(parts[2]) / \(parts[1]) / \(parts[0])"
    }

    var body: some View {
        ScrollView {
            VStack(spacing: Spacing.none) {
                AppToolbar(
                    title: NSLocalizedString("screen_edit_profile", value: "Edit Personal Information", comment: ""),
                    onBack: { dismiss() },
                    useGradient: true
                )

                VStack(spacing: Spacing.xl) {
                    // Gender segmented control
                    formSection(
                        label: NSLocalizedString("label_gender", value: "Gender", comment: ""),
                        required: true
                    ) {
                        HStack(spacing: Spacing.none) {
                            ForEach(Array(Gender.entries.enumerated()), id: \.offset) { index, gender in
                                let isSelected = selectedGender == gender.apiValue
                                let isFirst = index == 0
                                let isLast = index == Gender.entries.count - 1
                                let label = NSLocalizedString(gender.labelKey, value: gender.apiValue, comment: "")
                                Button {
                                    selectedGender = gender.apiValue
                                } label: {
                                    AppLabelText(text: label, color: isSelected ? .white : .appTextPrimary)
                                        .frame(maxWidth: .infinity)
                                        .frame(height: Dimens.inputHeightSmall)
                                        .background(isSelected ? Color.appPrimary : Color.appSurface)
                                        .overlay(
                                            RoundedRectangle(cornerRadius: Radius.input)
                                                .stroke(isSelected ? Color.appPrimary : Color.appTextHint.opacity(0.4), lineWidth: Dimens.strokeThin)
                                                .clipShape(
                                                    .rect(
                                                        topLeadingRadius: isFirst ? Radius.input : Radius.none,
                                                        bottomLeadingRadius: isFirst ? Radius.input : Radius.none,
                                                        bottomTrailingRadius: isLast ? Radius.input : Radius.none,
                                                        topTrailingRadius: isLast ? Radius.input : Radius.none
                                                    )
                                                )
                                        )
                                        .clipShape(
                                            .rect(
                                                topLeadingRadius: isFirst ? Radius.input : Radius.none,
                                                bottomLeadingRadius: isFirst ? Radius.input : Radius.none,
                                                bottomTrailingRadius: isLast ? Radius.input : Radius.none,
                                                topTrailingRadius: isLast ? Radius.input : Radius.none
                                            )
                                        )
                                }
                                .buttonStyle(.plain)
                            }
                        }
                    }

                    // Date of Birth
                    formSection(
                        label: NSLocalizedString("label_date_of_birth", value: "Date of birth", comment: ""),
                        required: true
                    ) {
                        Button { showDatePicker = true } label: {
                            HStack {
                                AppIcon(systemName: "calendar", tint: .appTextSecondary, size: Dimens.iconSm) // a11y: decorative — labelled by adjacent Text
                                AppSubtitleText(
                                    text: dobDisplay.isEmpty
                                        ? NSLocalizedString("edit_placeholder_dob", value: "DD / MM / YYYY", comment: "")
                                        : dobDisplay,
                                    color: dobDisplay.isEmpty ? .appTextHint : .appTextPrimary
                                )
                                Spacer()
                            }
                            .padding(.horizontal, Spacing.plus)
                            .frame(height: Sizing.inputHeight)
                            .background(Color.appSurface)
                            .cornerRadius(Radius.input)
                            .overlay(RoundedRectangle(cornerRadius: Radius.input).stroke(Color.appTextHint.opacity(0.4), lineWidth: Dimens.strokeThin))
                        }
                        .buttonStyle(.plain)
                    }
                    .sheet(isPresented: $showDatePicker) {
                        DatePickerSheet(selectedDate: Binding(
                            get: { DateUtil.isoToDate(dateOfBirth) ?? DateUtil.today() },
                            set: { dateOfBirth = DateUtil.dateToIso($0) }
                        ), onDone: { showDatePicker = false })
                    }

                    // First Name
                    formSection(
                        label: NSLocalizedString("label_first_name", value: "First name", comment: ""),
                        required: true
                    ) {
                        inputField(
                            placeholder: NSLocalizedString("edit_placeholder_first_name", value: "Sophie", comment: ""),
                            text: $firstName
                        )
                    }

                    // Last Name
                    formSection(
                        label: NSLocalizedString("label_last_name", value: "Last name", comment: ""),
                        required: true
                    ) {
                        inputField(
                            placeholder: NSLocalizedString("edit_placeholder_last_name", value: "Müller", comment: ""),
                            text: $lastName
                        )
                    }

                    // Phone Number
                    formSection(
                        label: NSLocalizedString("label_phone_number", value: "Phone number", comment: ""),
                        required: true
                    ) {
                        HStack(spacing: Spacing.s) {
                            // Country prefix menu
                            let dialCodes = DialCodesKt.supportedDialCodes
                            Menu {
                                ForEach(dialCodes, id: \.code) { dial in
                                    Button("\(dial.flagEmoji) \(dial.code)") { selectedPrefix = dial.code }
                                }
                            } label: {
                                let selected = dialCodes.first(where: { $0.code == selectedPrefix }) ?? dialCodes.first
                                HStack(spacing: Spacing.xs) {
                                    AppBodyText(
                                        text: "\(selected?.flagEmoji ?? "") \(selected?.code ?? selectedPrefix)",
                                        color: .appTextPrimary
                                    )
                                    AppIcon(systemName: "chevron.up.chevron.down", tint: .appTextSecondary, size: Dimens.iconChevron, weight: .semibold) // a11y: decorative — labelled by adjacent Text
                                }
                                .padding(.horizontal, Spacing.compact)
                                .frame(width: Dimens.countryCodeWidth, height: Sizing.inputHeight)
                                .background(Color.appSurface)
                                .cornerRadius(Radius.input)
                                .overlay(RoundedRectangle(cornerRadius: Radius.input).stroke(Color.appTextHint.opacity(0.4), lineWidth: Dimens.strokeThin))
                            }

                            TextField(
                                NSLocalizedString("edit_placeholder_phone", value: "621 123 456", comment: ""),
                                text: $phoneNumber
                            )
                            .keyboardType(.phonePad)
                            .padding(.horizontal, Spacing.plus)
                            .frame(height: Sizing.inputHeight)
                            .background(Color.appSurface)
                            .cornerRadius(Radius.input)
                            .overlay(RoundedRectangle(cornerRadius: Radius.input).stroke(Color.appTextHint.opacity(0.4), lineWidth: Dimens.strokeThin))
                        }
                    }

                    // CNS Number
                    formSection(
                        label: NSLocalizedString("label_cns_number", value: "CNS number", comment: ""),
                        required: false
                    ) {
                        HStack {
                            AppIcon(systemName: "creditcard.fill", tint: .appTextSecondary, size: Dimens.iconSm) // a11y: decorative — labelled by adjacent Text
                            TextField(
                                NSLocalizedString("edit_placeholder_cns", value: "1992 0314 5678 9012", comment: ""),
                                text: $cnsNumber
                            )
                            .keyboardType(.numberPad)
                        }
                        .padding(.horizontal, Spacing.plus)
                        .frame(height: Sizing.inputHeight)
                        .background(Color.appSurface)
                        .cornerRadius(Radius.input)
                        .overlay(RoundedRectangle(cornerRadius: Radius.input).stroke(Color.appTextHint.opacity(0.4), lineWidth: Dimens.strokeThin))
                    }

                    Spacer().frame(height: Spacing.s)

                    // Save button
                    Button { save() } label: {
                        HStack(spacing: Spacing.tiny) {
                            if viewModel.uiState.isLoading {
                                ProgressView().tint(.white)
                            } else {
                                AppButtonText(text: NSLocalizedString("action_save", value: "Save", comment: ""))
                                AppIcon(systemName: "checkmark", tint: .white, size: Dimens.iconMicro, weight: .semibold) // a11y: decorative — labelled by adjacent AppButtonText
                            }
                        }
                        .frame(maxWidth: .infinity)
                        .frame(height: Sizing.buttonHeight)
                        .background(Color.appPrimary)
                        .cornerRadius(Radius.action)
                    }
                    .disabled(viewModel.uiState.isLoading)

                    // Cancel button
                    Button { dismiss() } label: {
                        AppButtonText(
                            text: NSLocalizedString("action_cancel", value: "Cancel", comment: ""),
                            color: .appPrimary
                        )
                        .frame(maxWidth: .infinity)
                        .frame(height: Sizing.buttonHeight)
                        .overlay(RoundedRectangle(cornerRadius: Radius.action).stroke(Color.appPrimary, lineWidth: Dimens.strokeMedium))
                    }

                    Spacer().frame(height: Spacing.xxxl)
                }
                .padding(.horizontal, Spacing.xl)
                .padding(.top, Spacing.xxl)
            }
        }
        .background(Color.appBackground)
        .navigationBarHidden(true)
        .onAppear { prefill() }
        .alert("Error", isPresented: $showError) {
            Button("OK") { viewModel.clearError() }
        } message: {
            AppBodyText(text: viewModel.uiState.error ?? "")
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

        selectedGender = Gender.companion.fromApiString(value: state.gender).apiValue

        let dialCodes = DialCodesKt.supportedDialCodes
        let dial = dialCodes.first(where: { state.phone.hasPrefix($0.code) })
        selectedPrefix = dial?.code ?? dialCodes.first!.code
        phoneNumber = dial != nil
            ? String(state.phone.dropFirst(selectedPrefix.count)).trimmingCharacters(in: .whitespaces)
            : state.phone
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
        VStack(alignment: .leading, spacing: Spacing.s) {
            FormFieldLabel(label: label, required: required)
            content()
        }
    }

    private func inputField(placeholder: String, text: Binding<String>, keyboard: UIKeyboardType = .default) -> some View {
        TextField(placeholder, text: text)
            .keyboardType(keyboard)
            .padding(.horizontal, Spacing.plus)
            .frame(height: Sizing.inputHeight)
            .background(Color.appSurface)
            .cornerRadius(Radius.input)
            .overlay(RoundedRectangle(cornerRadius: Radius.input).stroke(Color.appTextHint.opacity(0.4), lineWidth: Dimens.strokeThin))
    }
}

// MARK: - Date Picker Sheet

private struct DatePickerSheet: View {
    @Binding var selectedDate: Date
    let onDone: () -> Void

    var body: some View {
        VStack(spacing: Spacing.none) {
            HStack {
                Button(NSLocalizedString("action_cancel", value: "Cancel", comment: "")) { onDone() }
                    .foregroundColor(.appTextSecondary)
                Spacer()
                AppSubtitleText(text: NSLocalizedString("label_date_of_birth", value: "Date of Birth", comment: ""))
                Spacer()
                Button(NSLocalizedString("action_done", value: "Done", comment: "")) { onDone() }
                    .foregroundColor(.appPrimary)
                    .fontWeight(.semibold)
            }
            .padding()
            DatePicker("", selection: $selectedDate, displayedComponents: .date)
                .datePickerStyle(.wheel)
                .labelsHidden()
        }
        .presentationDetents([.height(320)])
    }
}
