import SwiftUI

struct AvatarCircle: View {
    let initials: String
    var size: CGFloat = Dimens.avatarMd

    var body: some View {
        ZStack {
            Circle()
                .fill(
                    LinearGradient(
                        colors: [Color.appPrimaryLight, Color.appAvatarGradientEnd],
                        startPoint: .topLeading,
                        endPoint: .bottomTrailing
                    )
                )
            // Proportional font — size is derived from the `size` parameter (size * 0.33), not a literal. Raw Text() is intentional.
            Text(String(initials.prefix(2)).uppercased())
                .font(.system(size: size * 0.33, weight: .bold))
                .foregroundColor(.appPrimaryDark)
        }
        .frame(width: size, height: size)
    }
}

#Preview {
    HStack(spacing: Spacing.l) {
        AvatarCircle(initials: "AT", size: Sizing.toolbarSlot)
        AvatarCircle(initials: "JD", size: Dimens.emptyIconSmSize)
        AvatarCircle(initials: "DR", size: Dimens.avatarMd + Dimens.avatarSm / 2)
    }
    .padding()
    .background(Color.appBackground)
}
