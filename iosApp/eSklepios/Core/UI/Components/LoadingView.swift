import SwiftUI

struct LoadingView: View {
    var message: String = "Loading..."

    var body: some View {
        VStack(spacing: Spacing.l) {
            ProgressView()
                .scaleEffect(1.5)
                .tint(.appPrimary)
            AppBodyText(text: message)
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .background(Color.appBackground)
    }
}

#Preview {
    LoadingView(message: "Finding practitioners…")
}
