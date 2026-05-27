import SwiftUI

/// Project icon-button wrapper. **Required** in all feature view files (Rule UI-14).
///
/// Unlike `AppIcon`, `accessibilityLabel` here is **non-optional** — icon buttons are
/// always interactive, so they always need an accessibility label (Rule AC-1). The
/// button also gets a 44pt minimum tap target (Rule AC-3).
struct AppIconButton: View {
    let systemName: String
    let accessibilityLabel: String
    let action: () -> Void
    var tint: Color = .appTextPrimary
    var iconSize: CGFloat = Dimens.iconMd
    var weight: Font.Weight = .semibold
    var isEnabled: Bool = true

    var body: some View {
        Button(action: action) {
            Image(systemName: systemName)
                .font(.system(size: iconSize, weight: weight))
                .foregroundColor(tint)
                .frame(minWidth: Dimens.toolbarSlot, minHeight: Dimens.toolbarSlot)
        }
        .accessibilityLabel(accessibilityLabel)
        .disabled(!isEnabled)
    }
}

#Preview {
    HStack(spacing: Spacing.m) {
        AppIconButton(systemName: "chevron.left",
                      accessibilityLabel: "Back",
                      action: {})
        AppIconButton(systemName: "star.fill",
                      accessibilityLabel: "Favourite",
                      action: {},
                      tint: .appFavoriteRed)
        AppIconButton(systemName: "ellipsis",
                      accessibilityLabel: "More options",
                      action: {},
                      isEnabled: false)
    }
    .padding()
}
