import SwiftUI
import shared

// MARK: - Date filter segmented picker
struct PractitionerDateFilterPicker: View {
    let filters: [(key: String, label: String)]
    let selectedFilter: String
    let onFilterSelected: (String) -> Void

    var body: some View {
        Picker("", selection: Binding(
            get: { selectedFilter },
            set: { onFilterSelected($0) }
        )) {
            ForEach(filters, id: \.key) { filter in
                // Picker items require raw Text with .tag() (SwiftUI system API)
                Text(filter.label).tag(filter.key)
            }
        }
        .pickerStyle(.segmented)
        .padding(.horizontal, Dimens.paddingL)
        .padding(.vertical, Dimens.paddingM)
        .background(Color.appSurface)
    }
}

// MARK: - "Open to new patients" toggle pill
struct PractitionerNewPatientsTogglePill: View {
    let checked: Bool
    let label: String
    let onToggle: () -> Void

    var body: some View {
        HStack(spacing: Spacing.xs) {
            AppIcon(
                systemName: checked ? "checkmark.circle.fill" : "circle",
                tint: checked ? .appSuccess : .appTextSecondary,
                size: Dimens.iconSm
            )
            AppCaptionText(text: label, color: checked ? .appSuccess : .appTextSecondary)
        }
        .padding(.horizontal, Spacing.m)
        .padding(.vertical, Spacing.xs)
        .background(checked ? Color.appSuccessBg : Color.clear)
        .clipShape(Capsule())
        .overlay(Capsule().stroke(checked ? Color.appSuccess : Color.appTextHint.opacity(0.4), lineWidth: Dimens.strokeThin))
        .onTapGesture(perform: onToggle)
    }
}

// MARK: - Slot-day mapping helper (shared by HomeView and PractitionerListView)
func slotDays(from slots: [AppointmentSlot]) -> [SlotDayModel] {
    let available = slots.filter { $0.available }
    let grouped = Dictionary(grouping: available) { DateUtil.dateFromDateTime($0.dateTime) }
    return grouped
        .map { dateKey, daySlots in
            SlotDayModel(
                id: dateKey,
                dateKey: dateKey,
                slots: daySlots
                    .map { SlotEntry(id: $0.id, time: DateUtil.timeFromDateTime($0.dateTime)) }
                    .sorted { $0.time < $1.time }
            )
        }
        .sorted { $0.dateKey < $1.dateKey }
}
