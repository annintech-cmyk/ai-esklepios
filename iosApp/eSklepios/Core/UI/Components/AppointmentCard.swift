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
                // Header: avatar + practitioner name/specialty + status badge
                HStack(alignment: .center, spacing: Spacing.m) {
                    AvatarCircle(initials: nameInitials(practitionerName), size: Sizing.avatarSm)
                    VStack(alignment: .leading, spacing: Spacing.xs) {
                        AppSubtitleText(text: practitionerName)
                        AppCaptionText(text: specialty, color: .appPrimary)
                    }
                    Spacer()
                    StatusBadge(status: status)
                }

                // Info box: clinic name on top of date, grouped with accent background
                VStack(alignment: .leading, spacing: Spacing.s) {
                    HStack(spacing: Spacing.s) {
                        Image(systemName: "building.2.fill")
                            .font(.system(size: Dimens.iconSm))
                            .foregroundColor(.appTextSecondary)
                            .accessibilityHidden(true)
                        AppCaptionText(text: clinicName, color: .appTextSecondary)
                    }
                    Rectangle()
                        .fill(Color.appBorderColor)
                        .frame(maxWidth: .infinity)
                        .frame(height: Dimens.strokeThin)
                    HStack(spacing: Spacing.s) {
                        Image(systemName: "calendar")
                            .font(.system(size: Dimens.iconSm))
                            .foregroundColor(.appTextSecondary)
                            .accessibilityHidden(true)
                        AppCaptionText(text: dateTime, color: .appTextSecondary)
                    }
                }
                .frame(maxWidth: .infinity, alignment: .leading)
                .padding(Spacing.m)
                .background(Color.appPrimaryLight)
                .cornerRadius(Radius.md)

                // Action buttons for upcoming appointments
                if isUpcoming && status != .cancelled {
                    Divider()
                        .background(Color.appBorderColor)

                    HStack(spacing: Spacing.m) {
                        if let modify = onModify {
                            SecondaryButton(
                                title: NSLocalizedString("appointments_modify", value: "Modify", comment: ""),
                                action: modify
                            )
                        }
                        if let cancel = onCancel {
                            Button(action: cancel) {
                                AppButtonText(
                                    text: NSLocalizedString("action_cancel", value: "Cancel", comment: ""),
                                    color: .appDanger
                                )
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

    private func nameInitials(_ name: String) -> String {
        name.split(separator: " ")
            .filter { !$0.isEmpty && !$0.contains(".") }
            .prefix(2)
            .compactMap { $0.first }
            .map { String($0) }
            .joined()
            .uppercased()
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
    .padding(.horizontal, Spacing.m)
    .background(Color.appBackground)
}
