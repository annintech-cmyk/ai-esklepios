import SwiftUI

struct AppTextLink: View {
    let text: String
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            AppButtonText(text: text, color: .appPrimary)
        }
        .buttonStyle(.plain)
    }
}

struct DividerWithLabel: View {
    let label: String

    var body: some View {
        HStack(spacing: Spacing.none) {
            Rectangle()
                .frame(height: Dimens.dividerThickness)
                .foregroundColor(.appTextHint.opacity(0.3))
            AppCaptionText(text: label, color: .appTextSecondary)
                .padding(.horizontal, Dimens.paddingS)
            Rectangle()
                .frame(height: Dimens.dividerThickness)
                .foregroundColor(.appTextHint.opacity(0.3))
        }
    }
}