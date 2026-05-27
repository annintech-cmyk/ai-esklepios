import SwiftUI

// MARK: - Slot data models

struct SlotEntry {
    let id: String
    let time: String
}

struct SlotDayModel: Identifiable {
    let id: String      // = dateKey, used as ForEach key
    let dateKey: String // "2026-05-26"
    let slots: [SlotEntry]
}

// MARK: - Card data

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
    let availableSlotDays: [SlotDayModel]

    var fullName: String { "Dr. \(firstName) \(lastName)" }
    var initials: String {
        let f = firstName.first.map(String.init) ?? ""
        let l = lastName.first.map(String.init) ?? ""
        return "\(f)\(l)".uppercased()
    }

    var firstSlotId: String? {
        availableSlotDays.first?.slots.first?.id
    }
}

// MARK: - Main card

struct PractitionerCard: View {
    let data: PractitionerCardData
    let onSeeProfile: () -> Void
    let onBook: (String) -> Void
    let onFavorite: () -> Void
    var namespace: Namespace.ID? = nil

    var body: some View {
        AppCard {
            VStack(alignment: .leading, spacing: Spacing.m) {
                // Header row
                HStack(alignment: .top, spacing: Spacing.m) {
                    AvatarCircle(initials: data.initials, size: Sizing.avatarLg)
                        .heroTransition(id: "\(data.id)-avatar", in: namespace)

                    VStack(alignment: .leading, spacing: Spacing.xxs) {
                        AppSubtitleText(text: data.fullName)
                            .heroTransition(id: "\(data.id)-name", in: namespace)
                        AppLabelText(text: data.specialty, color: .appPrimary)
                        AppCaptionText(text: data.clinicName)
                    }

                    Spacer()

                    VStack(alignment: .trailing, spacing: Spacing.tiny) {
                        Button(action: onFavorite) {
                            Image(systemName: data.isFavorite ? "heart.fill" : "heart")
                                .font(.system(size: Dimens.iconCompact))
                                .foregroundColor(data.isFavorite ? Color.appFavoriteRed : Color.appTextSecondary)
                        }

                        if data.acceptingNewPatients {
                            AppCaptionText(
                                text: NSLocalizedString("label_accepting_patients", value: "Accepting patients", comment: ""),
                                color: .appSuccess
                            )
                            .padding(.horizontal, Spacing.s)
                            .padding(.vertical, Spacing.xxs)
                            .background(Color.appSuccessBg)
                            .cornerRadius(Radius.pill)
                        }
                    }
                }

                // Address row
                HStack(spacing: Spacing.xs) {
                    Image(systemName: "mappin")
                        .font(.system(size: Dimens.iconSm))
                        .foregroundColor(.appTextSecondary)
                    AppCaptionText(text: "\(data.address), \(data.city)")
                        .lineLimit(1)
                }

                Divider()

                // 5-day schedule strip
                WeekScheduleStrip(availableSlotDays: data.availableSlotDays, onBook: onBook)

                Divider()

                // Action buttons
                HStack(spacing: Spacing.m) {
                    SecondaryButton(
                        title: NSLocalizedString("action_see_profile", value: "See Profile", comment: ""),
                        action: onSeeProfile
                    )
                    PrimaryButton(
                        title: NSLocalizedString("action_book", value: "Book", comment: ""),
                        action: {
                            if let slotId = data.firstSlotId { onBook(slotId) }
                        }
                    )
                }
            }
        }
    }
}

// MARK: - Week schedule strip

private struct WeekScheduleStrip: View {
    let availableSlotDays: [SlotDayModel]
    let onBook: (String) -> Void

    private var todayKey: String { DateUtil.todayKey() }

    /// Mon–Fri of the current ISO week.
    private var weekDays: [WeekDayItem] {
        let monday = DateUtil.currentWeekMonday()
        return DateUtil.weekDays(startingFrom: monday, count: 5).map { date in
            WeekDayItem(
                dateKey: DateUtil.dateToIso(date),
                dayAbbr: String(DateUtil.formatIsoDate(DateUtil.dateToIso(date), pattern: DateUtil.PATTERN_DAY_ABBR).prefix(3)),
                dayNum: "\(DateUtil.dayOfMonth(date))"
            )
        }
    }

    private var slotsByDate: [String: [SlotEntry]] {
        Dictionary(uniqueKeysWithValues: availableSlotDays.map { ($0.dateKey, $0.slots) })
    }

    var body: some View {
        let today = todayKey
        VStack(alignment: .leading, spacing: Spacing.s) {
            AppCaptionText(
                text: NSLocalizedString("label_available_slots", value: "Available slots", comment: ""),
                color: .appTextSecondary
            )

            HStack(alignment: .top, spacing: Spacing.xxs) {
                ForEach(weekDays, id: \.dateKey) { day in
                    WeekDayColumn(
                        day: day,
                        isToday: day.dateKey == today,
                        slots: Array((slotsByDate[day.dateKey] ?? []).prefix(2)),
                        onBook: onBook
                    )
                }
            }
        }
    }
}

private struct WeekDayItem {
    let dateKey: String
    let dayAbbr: String
    let dayNum: String
}

private struct WeekDayColumn: View {
    let day: WeekDayItem
    let isToday: Bool
    let slots: [SlotEntry]
    let onBook: (String) -> Void

    var body: some View {
        VStack(spacing: Spacing.xxs) {
            AppCaptionText(text: day.dayAbbr, color: .appTextHint)

            ZStack {
                Circle()
                    .fill(isToday ? Color.appPrimary : Color.clear)
                    .frame(width: Dimens.scheduleDayCircleSize, height: Dimens.scheduleDayCircleSize)
                AppCaptionText(
                    text: day.dayNum,
                    color: isToday ? .white : .appTextPrimary
                )
            }

            if slots.isEmpty {
                AppCaptionText(text: "–", color: .appTextHint)
                    .padding(.vertical, Spacing.xxs)
            } else {
                ForEach(slots, id: \.id) { slot in
                    Button(action: { onBook(slot.id) }) {
                        AppCaptionText(text: slot.time, color: .appPrimary)
                            .frame(maxWidth: .infinity)
                            .padding(.vertical, Spacing.xxs)
                            .background(Color.appPrimaryLight)
                            .cornerRadius(Radius.sm)
                            .overlay(
                                RoundedRectangle(cornerRadius: Radius.sm)
                                    .stroke(Color.appPrimary.opacity(0.3), lineWidth: Dimens.strokeThin)
                            )
                    }
                }
            }
        }
        .frame(maxWidth: .infinity)
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
            availableSlotDays: [
                SlotDayModel(id: "2026-05-26", dateKey: "2026-05-26", slots: [
                    SlotEntry(id: "s1", time: "09:00"),
                    SlotEntry(id: "s2", time: "10:30")
                ]),
                SlotDayModel(id: "2026-05-27", dateKey: "2026-05-27", slots: [
                    SlotEntry(id: "s3", time: "14:00")
                ])
            ]
        ),
        onSeeProfile: {},
        onBook: { _ in },
        onFavorite: {}
    )
    .padding()
    .background(Color.appBackground)
}
