import SwiftUI

struct ErrorView: View {
    let message: String
    var retry: (() -> Void)? = nil

    var body: some View {
        VStack(spacing: Spacing.l) {
            Image(systemName: "exclamationmark.triangle.fill")
                .font(.system(size: Dimens.inputHeightSmall))
                .foregroundColor(.appDanger)
            AppSubtitleText(text: "Something went wrong")
            AppBodyText(text: message, alignment: .center)
            if let retry = retry {
                PrimaryButton(title: "Retry", action: retry)
                    .padding(.top, Spacing.xs)
            }
        }
        .padding(Spacing.xxxl)
        .frame(maxWidth: .infinity, maxHeight: .infinity)
    }
}

#Preview {
    ErrorView(message: "Network connection failed. Please check your internet connection.", retry: {})
        .background(Color.appBackground)
}
