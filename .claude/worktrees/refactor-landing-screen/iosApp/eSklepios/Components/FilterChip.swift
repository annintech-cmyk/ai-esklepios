import SwiftUI

struct FilterChip: View {
    let label: String
    let isSelected: Bool
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            Text(label)
                .font(.system(size: 12, weight: .semibold))
                .padding(.horizontal, 13)
                .padding(.vertical, 6)
                .background(isSelected ? Color.appPrimary : Color.appSurface)
                .foregroundColor(isSelected ? .white : Color.appTextSecondary)
                .cornerRadius(Dimens.radiusPill)
                .overlay(
                    RoundedRectangle(cornerRadius: Dimens.radiusPill)
                        .stroke(isSelected ? Color.appPrimary : Color.appTextHint.opacity(0.5), lineWidth: 1.5)
                )
        }
    }
}

#Preview {
    HStack(spacing: 8) {
        FilterChip(label: "All", isSelected: true, action: {})
        FilterChip(label: "Today", isSelected: false, action: {})
        FilterChip(label: "Cardiology", isSelected: false, action: {})
    }
    .padding()
    .background(Color.appBackground)
}
