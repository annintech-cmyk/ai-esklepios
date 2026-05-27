import SwiftUI

struct EmptyStateView: View {
    let icon: String
    let title: String
    let message: String
    var actionLabel: String? = nil
    var onAction: (() -> Void)? = nil

    var body: some View {
        VStack(spacing: Spacing.l) {
            Image(systemName: icon)
                .font(.system(size: Dimens.buttonHeight))
                .foregroundColor(.appPrimary.opacity(0.35))
            AppSubtitleText(text: title)
            AppBodyText(text: message, alignment: .center)
            if let label = actionLabel, let action = onAction {
                PrimaryButton(title: label, action: action)
                    .padding(.top, Spacing.s)
            }
        }
        .padding(Spacing.xxxl)
        .frame(maxWidth: .infinity, maxHeight: .infinity)
    }
}

#Preview {
    EmptyStateView(
        icon: "calendar.badge.exclamationmark",
        title: "No appointments",
        message: "You don't have any upcoming appointments.",
        actionLabel: "Find Practitioners",
        onAction: {}
    )
    .background(Color.appBackground)
}
