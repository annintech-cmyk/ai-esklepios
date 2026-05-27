import SwiftUI

struct ErrorView: View {
    let message: String
    var retry: (() -> Void)? = nil

    var body: some View {
        VStack(spacing: 16) {
            Image(systemName: "exclamationmark.triangle.fill")
                .font(.system(size: 44))
                .foregroundColor(.appDanger)
            Text("Something went wrong")
                .font(.system(size: 16, weight: .semibold))
                .foregroundColor(.appTextPrimary)
            Text(message)
                .font(.system(size: 14))
                .foregroundColor(.appTextSecondary)
                .multilineTextAlignment(.center)
            if let retry = retry {
                PrimaryButton(title: "Retry", action: retry)
                    .padding(.top, 4)
            }
        }
        .padding(Dimens.paddingXXXL)
        .frame(maxWidth: .infinity, maxHeight: .infinity)
    }
}

#Preview {
    ErrorView(message: "Network connection failed. Please check your internet connection.", retry: {})
        .background(Color.appBackground)
}
