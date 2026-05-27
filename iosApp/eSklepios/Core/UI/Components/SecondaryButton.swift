import SwiftUI

struct SecondaryButton: View {
    let title: String
    var icon: String? = nil
    var isLoading: Bool = false
    var isEnabled: Bool = true
    let action: () -> Void

    var body: some View {
        Button(action: {
            guard !isLoading && isEnabled else { return }
            action()
        }) {
            ZStack {
                if isLoading {
                    ProgressView()
                        .progressViewStyle(CircularProgressViewStyle(tint: .appPrimary))
                        .scaleEffect(0.9)
                } else {
                    HStack(spacing: Dimens.paddingS) {
                        if let icon = icon {
                            Image(systemName: icon)
                                .font(.system(size: Dimens.iconMd, weight: .semibold))
                                .foregroundColor(isEnabled ? .appPrimary : .appTextHint)
                        }
                        AppButtonText(text: title, color: isEnabled ? .appPrimary : .appTextHint)
                    }
                }
            }
            .frame(maxWidth: .infinity)
            .frame(height: Dimens.buttonHeight)
            .background(Color.appSurface)
            .overlay(
                Capsule()
                    .stroke(isEnabled ? Color.appPrimary : Color.appTextHint, lineWidth: Dimens.strokeMedium)
            )
            .clipShape(Capsule())
        }
        .buttonStyle(PlainButtonStyle())
        .disabled(!isEnabled || isLoading)
    }
}

#Preview {
    VStack(spacing: Dimens.paddingL) {
        SecondaryButton(title: "Cancel Appointment") {}
        SecondaryButton(title: "Loading...", isLoading: true) {}
    }
    .padding()
}
