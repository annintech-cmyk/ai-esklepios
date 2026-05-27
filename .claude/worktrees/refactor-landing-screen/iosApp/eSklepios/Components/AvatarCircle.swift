import SwiftUI

struct AvatarCircle: View {
    let initials: String
    var size: CGFloat = 48

    var body: some View {
        ZStack {
            Circle()
                .fill(
                    LinearGradient(
                        colors: [Color.appPrimaryLight, Color(hex: "DDE1FB")],
                        startPoint: .topLeading,
                        endPoint: .bottomTrailing
                    )
                )
            Text(String(initials.prefix(2)).uppercased())
                .font(.system(size: size * 0.33, weight: .bold))
                .foregroundColor(.appPrimaryDark)
        }
        .frame(width: size, height: size)
    }
}

#Preview {
    HStack(spacing: 16) {
        AvatarCircle(initials: "AT", size: 40)
        AvatarCircle(initials: "JD", size: 56)
        AvatarCircle(initials: "DR", size: 72)
    }
    .padding()
    .background(Color.appBackground)
}
