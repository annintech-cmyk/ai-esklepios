import SwiftUI

// MARK: - AppToolbar View

struct AppToolbar: View {
    let title: String
    var onBack: (() -> Void)? = nil
    var trailingContent: AnyView? = nil
    var useGradient: Bool = false
    var titleAlignment: HorizontalAlignment = .center

    var body: some View {
        ZStack {
            if useGradient {
                AppGradient.primary.ignoresSafeArea(edges: .top)
            } else {
                Color.appSurface.ignoresSafeArea(edges: .top)
                    .shadow(color: Color.black.opacity(0.06), radius: Dimens.cardElevation, y: Dimens.shadowY)
            }

            HStack(spacing: Dimens.paddingM) {
                if let back = onBack {
                    Button(action: back) {
                        Image(systemName: "chevron.left")
                            .font(.system(size: Dimens.iconMd, weight: .semibold))
                            .foregroundColor(useGradient ? .white : .appTextPrimary)
                            .frame(width: Sizing.toolbarSlot, height: Sizing.toolbarSlot)
                            .background(
                                Circle()
                                    .fill(useGradient
                                          ? Color.white.opacity(0.18)
                                          : Color.appBackground)
                            )
                    }
                } else {
                    Spacer().frame(width: Sizing.toolbarSlot)
                }

                Spacer()

                AppToolbarTitle(text: title, color: useGradient ? .white : .appTextPrimary)

                Spacer()

                if let trailing = trailingContent {
                    trailing
                        .frame(width: Sizing.toolbarSlot)
                } else {
                    Spacer().frame(width: Sizing.toolbarSlot)
                }
            }
            .padding(.horizontal, Dimens.paddingL)
            .frame(height: Dimens.toolbarHeight)
        }
        .frame(height: Dimens.toolbarHeight)
    }
}

// MARK: - Toolbar Modifier

struct AppToolbarModifier: ViewModifier {
    let title: String
    var onBack: (() -> Void)? = nil
    var trailingContent: AnyView? = nil
    var useGradient: Bool = false

    func body(content: Content) -> some View {
        VStack(spacing: Spacing.none) {
            AppToolbar(
                title: title,
                onBack: onBack,
                trailingContent: trailingContent,
                useGradient: useGradient
            )
            content
        }
    }
}

extension View {
    func appToolbar(
        title: String,
        onBack: (() -> Void)? = nil,
        trailing: AnyView? = nil,
        useGradient: Bool = false
    ) -> some View {
        modifier(AppToolbarModifier(
            title: title,
            onBack: onBack,
            trailingContent: trailing,
            useGradient: useGradient
        ))
    }
}

// MARK: - Preview

#Preview {
    VStack {
        AppToolbar(title: "Settings", onBack: {})
        AppToolbar(title: "Doctors", onBack: {}, useGradient: true)
        Spacer()
    }
}
