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
                    .shadow(color: Color.black.opacity(0.06), radius: 4, y: 2)
            }

            HStack(spacing: Dimens.paddingM) {
                if let back = onBack {
                    Button(action: back) {
                        Image(systemName: "chevron.left")
                            .font(.system(size: Dimens.iconMd, weight: .semibold))
                            .foregroundColor(useGradient ? .white : .appTextPrimary)
                            .frame(width: 40, height: 40)
                            .background(
                                Circle()
                                    .fill(useGradient
                                          ? Color.white.opacity(0.18)
                                          : Color.appBackground)
                            )
                    }
                } else {
                    Spacer().frame(width: 40)
                }

                Spacer()

                Text(title)
                    .font(.heading4)
                    .foregroundColor(useGradient ? .white : .appTextPrimary)
                    .lineLimit(1)

                Spacer()

                if let trailing = trailingContent {
                    trailing
                        .frame(width: 40)
                } else {
                    Spacer().frame(width: 40)
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
        VStack(spacing: 0) {
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
