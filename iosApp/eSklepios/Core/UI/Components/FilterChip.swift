import SwiftUI

struct FilterChip: View {
    let label: String
    let isSelected: Bool
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            AppCaptionText(text: label, color: isSelected ? .white : .appTextSecondary)
                .padding(.horizontal, Spacing.m)
                .padding(.vertical, Spacing.tiny)
                .background(isSelected ? Color.appPrimary : Color.appSurface)
                .cornerRadius(Radius.pill)
                .overlay(
                    RoundedRectangle(cornerRadius: Radius.pill)
                        .stroke(isSelected ? Color.appPrimary : Color.appTextHint.opacity(0.5), lineWidth: Dimens.strokeMedium)
                )
        }
    }
}

#Preview {
    HStack(spacing: Spacing.s) {
        FilterChip(label: "All", isSelected: true, action: {})
        FilterChip(label: "Today", isSelected: false, action: {})
        FilterChip(label: "Cardiology", isSelected: false, action: {})
    }
    .padding()
    .background(Color.appBackground)
}
