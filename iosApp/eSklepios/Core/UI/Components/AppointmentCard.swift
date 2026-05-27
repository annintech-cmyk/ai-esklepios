import SwiftUI

struct AppointmentCard: View {
    let practitionerName: String
    let specialty: String
    let dateTime: String
    let clinicName: String
    let status: AppointmentStatusDisplay
    let isUpcoming: Bool
    let onModify: (() -> Void)?
    let onCancel: (() -> Void)?

    var body: some View {
        AppCard {
            VStack(alignment: .leading, spacing: Spacing.m) {
                // Header row
                HStack(alignment: .top) {
                    VStack(alignment: .leading, spacing: Spacing.xxs) {
                        AppSubtitleText(text: practitionerName)
                        AppLabelText(text: specialty, color: .appPrimary)
                        AppCaptionText(text: clinicName)
                    }
                    Spacer()
                    StatusBadge(status: status)
                }

                // Date row
                HStack(spacing: Spacing.tiny) {
                    Image(systemName: "calendar")
                        .font(.label)
                        .foregroundColor(.appTextSecondary)
                    AppLabelText(text: dateTime, color: .appTextSecondary)
                }

                // Action buttons for upcoming
                if isUpcoming && status != .cancelled {
                    Divider()
                        .background(Color.appTextHint.opacity(0.3))

                    HStack(spacing: Spacing.m) {
                        if let modify = onModify {
                            SecondaryButton(title: "Modify", action: modify)
                        }
                        if let cancel = onCancel {
                            Button(action: cancel) {
                                AppButtonText(text: "Cancel", color: .appDanger)
                                    .frame(maxWidth: .infinity)
                                    .frame(height: Sizing.buttonHeight)
                                    .background(Color.appDangerBg)
                                    .cornerRadius(Radius.pill)
                            }
                        }
                    }
                }
            }
        }
    }
}

#Preview {
    VStack(spacing: Spacing.m) {
        AppointmentCard(
            practitionerName: "Dr. Sarah Johnson",
            specialty: "Cardiologist",
            dateTime: "Mon 2 Jun 2025, 10:00",
            clinicName: "Heart Clinic",
            status: .confirmed,
            isUpcoming: true,
            onModify: {},
            onCancel: {}
        )
        AppointmentCard(
            practitionerName: "Dr. Marc Müller",
            specialty: "Dermatologist",
            dateTime: "Thu 15 May 2025, 14:30",
            clinicName: "Skin Centre",
            status: .completed,
            isUpcoming: false,
            onModify: nil,
            onCancel: nil
        )
    }
    .padding()
    .background(Color.appBackground)
}
