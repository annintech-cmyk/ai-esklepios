import SwiftUI

/// A labeled value row with a colored check-circle badge on the leading edge.
/// Mirrors Android's `CheckRow` component. Use `accentColor: .appOldApptAmberIcon`
/// for the "old appointment" variant.
struct CheckRow: View {
    let label: String
    let value: String
    var accentColor: Color = .appPrimary
    var isLast: Bool = false

    var body: some View {
        VStack(spacing: Spacing.none) {
            HStack(alignment: .top, spacing: Spacing.s) {
                ZStack {
                    Circle()
                        .fill(accentColor)
                        .frame(width: Sizing.iconCompact, height: Sizing.iconCompact)
                    Image(systemName: "checkmark")
                        .font(.system(size: Dimens.iconXxs, weight: .bold))
                        .foregroundColor(.white)
                        .accessibilityHidden(true)
                }
                VStack(alignment: .leading, spacing: Spacing.xxs) {
                    AppCaptionText(text: label, color: .appTextHint)
                    AppFieldValueText(text: value)
                }
            }
            .padding(.horizontal, Spacing.plus)
            .padding(.vertical, Spacing.s)
            if !isLast {
                Divider()
                    .padding(.horizontal, Spacing.plus)
            }
        }
    }
}

#Preview("Primary accent") {
    VStack(spacing: 0) {
        CheckRow(label: "Reason", value: "Consultation")
        CheckRow(label: "Date & time", value: "Tue, May 26, 2026 · 09:00")
        CheckRow(label: "Institute", value: "Al Esch Medical Center", isLast: true)
    }
    .padding()
}

#Preview("Amber accent") {
    VStack(spacing: 0) {
        CheckRow(label: "Reason", value: "Follow-up", accentColor: .appOldApptAmberIcon)
        CheckRow(label: "Institute", value: "Centre Médical", accentColor: .appOldApptAmberIcon, isLast: true)
    }
    .padding()
}