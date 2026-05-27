import SwiftUI

/// Project icon wrapper. **Required** in all feature view files (Rule UI-14).
///
/// - Decorative icons (label provided by adjacent text): omit `accessibilityLabel`
///   or pass `nil` — the icon is hidden from VoiceOver via `.accessibilityHidden(true)`.
/// - Informative icons: pass a localized label via `NSLocalizedString("cd_*", ...)`.
///
/// Default tint is `.appTextSecondary`; default size is `Dimens.iconMd`. Override
/// only when the design requires it — never use raw `Image(systemName:)` in screens.
struct AppIcon: View {
    let systemName: String
    var accessibilityLabel: String? = nil
    var tint: Color = .appTextSecondary
    var size: CGFloat = Dimens.iconMd
    var weight: Font.Weight = .regular

    var body: some View {
        let image = Image(systemName: systemName)
            .font(.system(size: size, weight: weight))
            .foregroundColor(tint)

        if let label = accessibilityLabel {
            image.accessibilityLabel(label)
        } else {
            image.accessibilityHidden(true)
        }
    }
}

#Preview {
    VStack(spacing: Spacing.m) {
        AppIcon(systemName: "star.fill")
        AppIcon(systemName: "exclamationmark.triangle",
                accessibilityLabel: "Warning",
                tint: .appPrimary,
                size: Dimens.iconLg)
        AppIcon(systemName: "chevron.left",
                tint: .white,
                weight: .semibold)
            .padding()
            .background(Color.appPrimary)
    }
    .padding()
}
