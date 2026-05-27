import SwiftUI

/// Semi-transparent white capsule button for use on gradient hero backgrounds.
struct GlassButton: View {
    let text: String
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            AppButtonText(text: text)
                .padding(.horizontal, Spacing.xxl)
                .padding(.vertical, Spacing.s + Spacing.xs)
                .background(Color.white.opacity(0.18), in: Capsule())
        }
    }
}

#Preview {
    ZStack {
        Color.appPrimary.ignoresSafeArea()
        GlassButton(text: "Sign In") {}
    }
}