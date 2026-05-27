import SwiftUI

struct BookAppointmentView: View {
    let practitionerId: String
    let slotId: String
    let isChange: Bool
    @StateObject private var viewModel: BookAppointmentViewModelWrapper
    @Environment(\.dismiss) var dismiss
    @State private var consultationReason = ""
    @State private var messageToDoctor = ""
    @State private var navigateToSuccess = false
    @State private var navigateToLogin = false
    @State private var showError = false

    init(practitionerId: String, slotId: String, isChange: Bool = false) {
        self.practitionerId = practitionerId
        self.slotId = slotId
        self.isChange = isChange
        _viewModel = StateObject(wrappedValue: BookAppointmentViewModelWrapper(practitionerId: practitionerId, slotId: slotId))
    }

    var body: some View {
        ScrollView {
            VStack(spacing: Spacing.none) {
                AppToolbar(
                    title: NSLocalizedString("booking_title", value: "Book Appointment", comment: ""),
                    onBack: { dismiss() }
                )

                VStack(spacing: Spacing.l) {
                    // Booking summary card
                    AppCard {
                        VStack(alignment: .leading, spacing: Spacing.m) {
                            AppLabelText(
                                text: NSLocalizedString("booking_summary", value: "Appointment Summary", comment: ""),
                                color: .appPrimary
                            )

                            let state = viewModel.uiState
                            if let p = state.practitioner {
                                InfoRow(
                                    icon: "person",
                                    label: NSLocalizedString("booking_info_practitioner", value: "Practitioner", comment: ""),
                                    value: p.fullName
                                )
                                InfoRow(
                                    icon: "stethoscope",
                                    label: NSLocalizedString("booking_info_specialty", value: "Specialty", comment: ""),
                                    value: p.specialty
                                )
                                InfoRow(
                                    icon: "building.2",
                                    label: NSLocalizedString("booking_info_clinic", value: "Clinic", comment: ""),
                                    value: p.clinicName
                                )
                            }
                            if let slot = state.selectedSlot {
                                InfoRow(
                                    icon: "calendar",
                                    label: NSLocalizedString("booking_info_datetime", value: "Date & Time", comment: ""),
                                    value: DateUtil.formatSlotSummary(slot.dateTime)
                                )
                            }
                        }
                    }

                    // Old appointment card (reschedule mode)
                    if isChange {
                        AppCard {
                            VStack(spacing: Spacing.none) {
                                HStack(spacing: Spacing.compact) {
                                    AppIcon(systemName: "clock.arrow.circlepath", tint: .appOldApptAmber, size: Dimens.iconSm)
                                    AppSectionHeaderText(
                                        text: NSLocalizedString("booking_header_old_selected", value: "OLD APPOINTMENT SELECTED", comment: ""),
                                        color: .appOldApptAmber
                                    )
                                }
                                .padding(.horizontal, Spacing.m)
                                .padding(.vertical, Spacing.s)
                                Divider()
                                let prev = viewModel.uiState.previousAppointment
                                CheckRow(
                                    label: NSLocalizedString("booking_row_reason", value: "Reason", comment: ""),
                                    value: prev?.consultationReason ?? "",
                                    accentColor: .appOldApptAmberIcon
                                )
                                CheckRow(
                                    label: NSLocalizedString("booking_row_datetime", value: "Date & time", comment: ""),
                                    value: prev.map { DateUtil.formatSlotSummary($0.dateTime) } ?? "",
                                    accentColor: .appOldApptAmberIcon
                                )
                                CheckRow(
                                    label: NSLocalizedString("booking_row_institute", value: "Institute", comment: ""),
                                    value: prev?.clinicName ?? "",
                                    accentColor: .appOldApptAmberIcon,
                                    isLast: true
                                )
                            }
                        }
                    }

                    // Cancellation policy
                    HStack(spacing: Spacing.compact) {
                        AppIcon(systemName: "info.circle", tint: .appWarning, size: Dimens.iconSm)
                        AppLabelText(
                            text: NSLocalizedString("booking_policy", value: "Cancellations must be made at least 24 hours in advance.", comment: ""),
                            color: .appWarning
                        )
                    }
                    .padding(Spacing.m)
                    .frame(maxWidth: .infinity, alignment: .leading)
                    .background(Color.appWarningBg)
                    .cornerRadius(Radius.md)

                    // Consultation reason
                    VStack(alignment: .leading, spacing: Spacing.tiny) {
                        FormFieldLabel(
                            label: NSLocalizedString("booking_reason", value: "Reason for consultation", comment: ""),
                            required: true
                        )
                        TextField(
                            NSLocalizedString("booking_reason_placeholder", value: "Briefly describe the reason for your visit", comment: ""),
                            text: $consultationReason
                        )
                        .padding(Spacing.m)
                        .background(Color.appSurface)
                        .cornerRadius(Radius.md)
                        .overlay(RoundedRectangle(cornerRadius: Radius.md).stroke(Color.appTextHint.opacity(0.4), lineWidth: Dimens.strokeThin))
                    }

                    // Message to doctor
                    VStack(alignment: .leading, spacing: Spacing.tiny) {
                        AppLabelText(
                            text: NSLocalizedString("booking_message_label", value: "Message to the doctor", comment: "")
                        )
                        TextEditor(text: $messageToDoctor)
                            .frame(height: Dimens.messageEditorHeight)
                            .padding(Spacing.s)
                            .background(Color.appSurface)
                            .cornerRadius(Radius.md)
                            .overlay(RoundedRectangle(cornerRadius: Radius.md).stroke(Color.appTextHint.opacity(0.4), lineWidth: Dimens.strokeThin))
                    }

                    // Auth required banner
                    if !viewModel.uiState.isAuthenticated {
                        HStack(spacing: Spacing.compact) {
                            AppIcon(systemName: "lock.fill", tint: .appPrimary, size: Dimens.iconSm)
                            VStack(alignment: .leading, spacing: Spacing.xxs) {
                                AppLabelText(
                                    text: NSLocalizedString("booking_auth_required", value: "Sign in required", comment: ""),
                                    color: .appPrimary
                                )
                                AppCaptionText(
                                    text: NSLocalizedString("booking_auth_subtitle", value: "You need an account to book appointments.", comment: "")
                                )
                            }
                        }
                        .padding(Spacing.plus)
                        .frame(maxWidth: .infinity, alignment: .leading)
                        .background(Color.appPrimaryLight)
                        .cornerRadius(Radius.md)
                    }

                    PrimaryButton(
                        title: viewModel.uiState.isAuthenticated
                            ? NSLocalizedString("booking_confirm", value: "Confirm Booking", comment: "")
                            : NSLocalizedString("booking_signin_to_book", value: "Sign In to Book", comment: ""),
                        isLoading: viewModel.uiState.isLoading
                    ) {
                        if !viewModel.uiState.isAuthenticated {
                            navigateToLogin = true
                        } else {
                            viewModel.confirmBooking(message: messageToDoctor, reason: consultationReason)
                        }
                    }
                    .frame(maxWidth: .infinity)
                }
                .padding(.horizontal, Spacing.l)
                .padding(.top, Spacing.l)
                .padding(.bottom, Spacing.xxxl)
            }
        }
        .background(Color.appBackground)
        .navigationBarHidden(true)
        .alert(NSLocalizedString("error_generic_title", value: "Error", comment: ""), isPresented: $showError) {
            Button(NSLocalizedString("action_ok", value: "OK", comment: "")) { viewModel.clearError() }
        } message: {
            AppBodyText(text: viewModel.uiState.error ?? "")
        }
        .onChange(of: viewModel.uiState.isConfirmed) { confirmed in
            if confirmed { navigateToSuccess = true }
        }
        .onChange(of: viewModel.uiState.error) { error in
            showError = error != nil
        }
        .navigationDestination(isPresented: $navigateToSuccess) {
            AppointmentSuccessView(
                appointmentId: viewModel.confirmedAppointmentId,
                onNavigateToAppointments: {
                    navigateToSuccess = false
                    dismiss()
                },
                onNavigateToHome: {
                    navigateToSuccess = false
                    dismiss()
                }
            )
        }
        .navigationDestination(isPresented: $navigateToLogin) {
            LoginView()
        }
    }
}