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
            VStack(alignment: .leading, spacing: Dimens.paddingM) {
                // Header row
                HStack(alignment: .top) {
                    VStack(alignment: .leading, spacing: 3) {
                        Text(practitionerName)
                            .font(.system(size: 15, weight: .bold))
                            .foregroundColor(.appTextPrimary)
                        Text(specialty)
                            .font(.system(size: 13))
                            .foregroundColor(.appPrimary)
                        Text(clinicName)
                            .font(.system(size: 12))
                            .foregroundColor(.appTextSecondary)
                    }
                    Spacer()
                    StatusBadge(status: status)
                }

                // Date row
                HStack(spacing: 6) {
                    Image(systemName: "calendar")
                        .font(.system(size: 13))
                        .foregroundColor(.appTextSecondary)
                    Text(dateTime)
                        .font(.system(size: 13))
                        .foregroundColor(.appTextSecondary)
                }

                // Action buttons for upcoming
                if isUpcoming && status != .cancelled {
                    Divider()
                        .background(Color.appTextHint.opacity(0.3))

                    HStack(spacing: Dimens.paddingM) {
                        if let modify = onModify {
                            SecondaryButton(title: "Modify", action: modify)
                        }
                        if let cancel = onCancel {
                            Button(action: cancel) {
                                Text("Cancel")
                                    .font(.system(size: 14, weight: .semibold))
                                    .frame(maxWidth: .infinity)
                                    .frame(height: Dimens.buttonHeight)
                                    .background(Color.appDangerBg)
                                    .foregroundColor(.appDanger)
                                    .cornerRadius(Dimens.radiusPill)
                            }
                        }
                    }
                }
            }
        }
    }
}

#Preview {
    VStack(spacing: 12) {
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
