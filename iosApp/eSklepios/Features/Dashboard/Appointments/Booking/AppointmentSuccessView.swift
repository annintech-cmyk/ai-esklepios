import SwiftUI

struct AppointmentSuccessView: View {
    let appointmentId: String
    var onNavigateToAppointments: () -> Void = {}
    var onNavigateToHome: () -> Void = {}

    @StateObject private var viewModel: AppointmentSuccessViewModelWrapper
    @State private var checkmarkScale: CGFloat = 0.5

    init(
        appointmentId: String,
        onNavigateToAppointments: @escaping () -> Void = {},
        onNavigateToHome: @escaping () -> Void = {}
    ) {
        self.appointmentId = appointmentId
        self.onNavigateToAppointments = onNavigateToAppointments
        self.onNavigateToHome = onNavigateToHome
        _viewModel = StateObject(wrappedValue: AppointmentSuccessViewModelWrapper(appointmentId: appointmentId))
    }

    var body: some View {
        VStack(spacing: Spacing.none) {
            Spacer()

            // Animated success circle
            ZStack {
                Circle()
                    .fill(Color.appSuccessBg)
                    .frame(width: Dimens.appointmentSuccessIcon, height: Dimens.appointmentSuccessIcon)
                Image(systemName: "checkmark.circle.fill")
                    .font(.system(size: Dimens.appointmentSuccessIcon * 0.6))
                    .foregroundColor(.appSuccess)
                    .scaleEffect(checkmarkScale)
            }
            .onAppear {
                withAnimation(.spring(response: 0.5, dampingFraction: 0.6)) {
                    checkmarkScale = 1.0
                }
            }

            Spacer().frame(height: Dimens.cardOverlap)

            AppTitleText(
                text: NSLocalizedString("success_title", value: "Appointment Confirmed!", comment: ""),
                alignment: .center
            )

            Spacer().frame(height: Spacing.m)

            AppSubtitleText(
                text: NSLocalizedString("success_subtitle", value: "Your appointment has been successfully booked.", comment: ""),
                color: .appTextSecondary,
                alignment: .center
            )

            Spacer().frame(height: Spacing.xxxl)

            // Detail card
            AppCard {
                VStack(alignment: .leading, spacing: Spacing.m) {
                    if !viewModel.uiState.practitionerName.isEmpty {
                        SuccessDetailRow(
                            icon: "person.fill",
                            label: NSLocalizedString("label_practitioner", value: "Practitioner", comment: ""),
                            value: viewModel.uiState.practitionerName
                        )
                    }
                    if !viewModel.uiState.dateTime.isEmpty {
                        SuccessDetailRow(
                            icon: "calendar",
                            label: NSLocalizedString("label_date_time", value: "Date & Time", comment: ""),
                            value: viewModel.uiState.dateTime
                        )
                    }
                    if !viewModel.uiState.clinicName.isEmpty {
                        SuccessDetailRow(
                            icon: "building.2.fill",
                            label: NSLocalizedString("label_clinic", value: "Clinic", comment: ""),
                            value: viewModel.uiState.clinicName
                        )
                    }
                    HStack {
                        Spacer()
                        AppLabelText(
                            text: NSLocalizedString("success_confirmed_badge", value: "CONFIRMED", comment: ""),
                            color: .appSuccess
                        )
                        Spacer()
                    }
                    .padding(Spacing.m)
                    .background(Color.appSuccessBg)
                    .cornerRadius(Radius.sm)
                }
            }
            .padding(.horizontal, Spacing.xxl)

            Spacer().frame(height: Spacing.xxxl)

            PrimaryButton(
                title: NSLocalizedString("success_view_appointments", value: "View My Appointments", comment: ""),
                action: onNavigateToAppointments
            )
            .padding(.horizontal, Spacing.xxl)
            .frame(maxWidth: .infinity)

            Spacer().frame(height: Spacing.m)

            GhostButton(
                title: NSLocalizedString("success_back_home", value: "Back to Home", comment: ""),
                action: onNavigateToHome
            )
            .padding(.horizontal, Spacing.xxl)
            .frame(maxWidth: .infinity)

            Spacer().frame(height: Spacing.xxxl)
        }
        .background(Color.appBackground.ignoresSafeArea())
        .navigationBarHidden(true)
        .task { viewModel.loadAppointment() }
    }
}

private struct SuccessDetailRow: View {
    let icon: String
    let label: String
    let value: String

    var body: some View {
        HStack(spacing: Spacing.m) {
            Image(systemName: icon)
                .font(.system(size: Dimens.iconCompact))
                .foregroundColor(.appPrimary)
            VStack(alignment: .leading, spacing: Spacing.xxs) {
                AppCaptionText(text: label)
                AppBodyText(text: value, color: .appTextPrimary)
            }
        }
    }
}
