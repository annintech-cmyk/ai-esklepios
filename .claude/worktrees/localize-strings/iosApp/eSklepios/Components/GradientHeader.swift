import SwiftUI

struct GradientHeader<Content: View>: View {
    var minHeight: CGFloat = 220
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
                .frame(width: 180, height: 180)
                .offset(x: -40, y: -40)

            Circle()
                .fill(Color.white.opacity(0.04))
                .frame(width: 120, height: 120)
                .offset(x: UIScreen.main.bounds.width - 80, y: 20)

            Circle()
                .fill(Color.white.opacity(0.05))
                .frame(width: 80, height: 80)
                .offset(x: UIScreen.main.bounds.width - 30, y: minHeight - 40)

            // Toolbar row
            HStack {
                if let back = onBack {
                    Button(action: back) {
                        Image(systemName: "chevron.left")
                            .font(.system(size: Dimens.iconMd, weight: .semibold))
                            .foregroundColor(.white)
                            .frame(width: 40, height: 40)
                            .background(Color.white.opacity(0.18), in: Circle())
                    }
                }
                Spacer()
                if let action = trailingAction, let icon = trailingIcon {
                    Button(action: action) {
                        Image(systemName: icon)
                            .font(.system(size: Dimens.iconMd, weight: .semibold))
                            .foregroundColor(.white)
                            .frame(width: 40, height: 40)
                            .background(Color.white.opacity(0.18), in: Circle())
                    }
                }
            }
            .padding(.horizontal, Dimens.paddingL)
            .padding(.top, Dimens.paddingL)

            // Main content
            VStack(spacing: 0) {
                Spacer().frame(height: Dimens.toolbarHeight + Dimens.paddingXL)
                content()
                    .padding(.horizontal, Dimens.paddingXXL)
                    .padding(.bottom, Dimens.paddingXXL)
            }
        }
        .frame(minHeight: minHeight)
    }
}

#Preview {
    VStack(spacing: 0) {
        GradientHeader(onBack: {}) {
            Text("Dr. Sarah Johnson")
                .font(.heading1)
                .foregroundColor(.white)
            Text("Cardiologist")
                .font(.body)
                .foregroundColor(.white.opacity(0.8))
        }
        Spacer()
    }
    .background(Color.appBackground)
}
