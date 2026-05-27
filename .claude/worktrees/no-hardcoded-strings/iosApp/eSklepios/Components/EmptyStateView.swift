import SwiftUI

struct EmptyStateView: View {
    let icon: String
    let title: String
    let message: String
    var actionLabel: String? = nil
    var onAction: (() -> Void)? = nil

    var body: some View {
        VStack(spacing: 16) {
            Image(systemName: icon)
                .font(.system(size: 52))
                .foregroundColor(.appPrimary.opacity(0.35))
            Text(title)
                .font(.system(size: 18, weight: .semibold))
                .foregroundColor(.appTextPrimary)
            Text(message)
                .font(.system(size: 14))
                .foregroundColor(.appTextSecondary)
                .multilineTextAlignment(.center)
            if let label = actionLabel, let action = onAction {
                PrimaryButton(title: label, action: action)
                    .padding(.top, 8)
            }
        }
        .padding(Dimens.paddingXXXL)
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
