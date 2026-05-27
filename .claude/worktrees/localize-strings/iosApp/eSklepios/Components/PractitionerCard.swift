import SwiftUI

struct PractitionerCardData {
    let id: String
    let firstName: String
    let lastName: String
    let specialty: String
    let clinicName: String
    let address: String
    let city: String
    let acceptingNewPatients: Bool
    let isFavorite: Bool
    let nextSlots: [String]

    var fullName: String { "Dr. \(firstName) \(lastName)" }
    var initials: String {
        let f = firstName.first.map(String.init) ?? ""
        let l = lastName.first.map(String.init) ?? ""
        return "\(f)\(l)".uppercased()
    }
}

struct PractitionerCard: View {
    let data: PractitionerCardData
    let onSeeProfile: () -> Void
    let onBook: (String) -> Void
    let onFavorite: () -> Void

    var body: some View {
        AppCard {
            VStack(alignment: .leading, spacing: Dimens.paddingM) {
                // Header row
                HStack(alignment: .top, spacing: Dimens.paddingM) {
                    AvatarCircle(initials: data.initials, size: Dimens.avatarLg)

                    VStack(alignment: .leading, spacing: 3) {
                        Text(data.fullName)
                            .font(.system(size: 15, weight: .bold))
                            .foregroundColor(.appTextPrimary)
                        Text(data.specialty)
                            .font(.system(size: 13))
                            .foregroundColor(.appPrimary)
                        Text(data.clinicName)
                            .font(.system(size: 12))
                            .foregroundColor(.appTextSecondary)
                    }

                    Spacer()

                    VStack(alignment: .trailing, spacing: 6) {
                        Button(action: onFavorite) {
                            Image(systemName: data.isFavorite ? "heart.fill" : "heart")
                                .font(.system(size: 18))
                                .foregroundColor(data.isFavorite ? Color(hex: "FF6B6B") : Color.appTextSecondary)
                        }

                        if data.acceptingNewPatients {
                            Text("Accepting")
                                .font(.system(size: 10, weight: .semibold))
                                .padding(.horizontal, 7)
                                .padding(.vertical, 3)
                                .background(Color.appSuccessBg)
                                .foregroundColor(.appSuccess)
                                .cornerRadius(Dimens.radiusPill)
                        }
                    }
                }

                // Address row
                HStack(spacing: 4) {
                    Image(systemName: "mappin")
                        .font(.system(size: 12))
                        .foregroundColor(.appTextSecondary)
                    Text("\(data.address), \(data.city)")
                        .font(.system(size: 12))
                        .foregroundColor(.appTextSecondary)
                        .lineLimit(1)
                }

                Divider()
                    .background(Color.appTextHint.opacity(0.3))

                // Available slots
                if !data.nextSlots.isEmpty {
                    VStack(alignment: .leading, spacing: 6) {
                        Text("Available slots")
                            .font(.system(size: 11, weight: .semibold))
                            .foregroundColor(.appTextSecondary)

                        ScrollView(.horizontal, showsIndicators: false) {
                            HStack(spacing: 8) {
                                ForEach(data.nextSlots.prefix(4), id: \.self) { slot in
                                    Button(action: { onBook(slot) }) {
                                        Text(slot)
                                            .font(.system(size: 12, weight: .medium))
                                            .padding(.horizontal, 10)
                                            .padding(.vertical, 5)
                                            .background(Color.appPrimaryLight)
                                            .foregroundColor(.appPrimary)
                                            .cornerRadius(Dimens.radiusSm)
                                            .overlay(
                                                RoundedRectangle(cornerRadius: Dimens.radiusSm)
                                                    .stroke(Color.appPrimary.opacity(0.3), lineWidth: 1)
                                            )
                                    }
                                }
                            }
                        }
                    }
                }

                // Action buttons
                HStack(spacing: Dimens.paddingM) {
                    SecondaryButton(title: "See Profile", action: onSeeProfile)
                    PrimaryButton(title: "Book", action: {
                        if let slot = data.nextSlots.first { onBook(slot) }
                    })
                }
            }
        }
    }
}

#Preview {
    PractitionerCard(
        data: PractitionerCardData(
            id: "1",
            firstName: "Sarah",
            lastName: "Johnson",
            specialty: "Cardiologist",
            clinicName: "Heart Clinic",
            address: "12 Rue de la Santé",
            city: "Luxembourg",
            acceptingNewPatients: true,
            isFavorite: false,
            nextSlots: ["10:00", "11:30", "14:00"]
        ),
        onSeeProfile: {},
        onBook: { _ in },
        onFavorite: {}
    )
    .padding()
    .background(Color.appBackground)
}
