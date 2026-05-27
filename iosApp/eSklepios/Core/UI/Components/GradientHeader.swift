import SwiftUI

struct GradientHeader<Content: View>: View {
    var minHeight: CGFloat = Dimens.headerMinHeightRounded
    var onBack: (() -> Void)? = nil
    var trailingAction: (() -> Void)? = nil
    var trailingIcon: String? = nil
    @ViewBuilder let content: () -> Content

    var body: some View {
        ZStack(alignment: .topLeading) {
            // Gradient background
            AppGradient.primary
                .ignoresSafeArea(edges: .top)

            // Decorative orbs
            Circle()
                .fill(Color.white.opacity(0.06))
                .frame(width: Dimens.orbLg, height: Dimens.orbLg)
                .offset(x: Dimens.orbOffsetMainNeg, y: Dimens.orbOffsetMainNeg)

            Circle()
                .fill(Color.white.opacity(0.04))
                .frame(width: Dimens.orbMd, height: Dimens.orbMd)
                .offset(x: UIScreen.main.bounds.width - Dimens.orbSm, y: Dimens.orbOffsetSecondaryTop)

            Circle()
                .fill(Color.white.opacity(0.05))
                .frame(width: Dimens.orbSm, height: Dimens.orbSm)
                .offset(x: UIScreen.main.bounds.width - Dimens.orbOffsetTertiaryRight, y: minHeight - Dimens.orbOffsetTertiaryBottom)

            // Toolbar row
            HStack {
                if let back = onBack {
                    Button(action: back) {
                        Image(systemName: "chevron.left")
                            .font(.system(size: Dimens.iconMd, weight: .semibold))
                            .foregroundColor(.white)
                            .frame(width: Sizing.toolbarSlot, height: Sizing.toolbarSlot)
                            .background(Color.white.opacity(0.18), in: Circle())
                    }
                }
                Spacer()
                if let action = trailingAction, let icon = trailingIcon {
                    Button(action: action) {
                        Image(systemName: icon)
                            .font(.system(size: Dimens.iconMd, weight: .semibold))
                            .foregroundColor(.white)
                            .frame(width: Sizing.toolbarSlot, height: Sizing.toolbarSlot)
                            .background(Color.white.opacity(0.18), in: Circle())
                    }
                }
            }
            .padding(.horizontal, Spacing.l)
            .padding(.top, Spacing.l)

            // Main content
            VStack(spacing: Spacing.none) {
                Spacer().frame(height: Dimens.toolbarHeight + Spacing.xl)
                content()
                    .padding(.horizontal, Spacing.xxl)
                    .padding(.bottom, Spacing.xxl)
            }
        }
        .frame(minHeight: minHeight)
    }
}

#Preview {
    VStack(spacing: Spacing.none) {
        GradientHeader(onBack: {}) {
            AppTitleText(text: "Dr. Sarah Johnson", color: .white)
            AppBodyText(text: "Cardiologist", color: .white.opacity(0.8))
        }
        Spacer()
    }
    .background(Color.appBackground)
}
