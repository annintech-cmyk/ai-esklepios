import SwiftUI

struct PrimaryButton: View {
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
                        .progressViewStyle(CircularProgressViewStyle(tint: .white))
                        .scaleEffect(0.9)
                } else {
                    HStack(spacing: Dimens.paddingS) {
                        if let icon = icon {
                            Image(systemName: icon)
                                .font(.system(size: Dimens.iconMd, weight: .semibold))
                                .foregroundColor(.white)
                        }
                        AppButtonText(text: title)
                    }
                }
            }
            .frame(maxWidth: .infinity)
            .frame(height: Dimens.buttonHeight)
            .background(
                Group {
                    if isEnabled && !isLoading {
                        AppGradient.primary
                    } else {
                        LinearGradient(colors: [Color.appTextHint, Color.appTextHint],
                                       startPoint: .leading, endPoint: .trailing)
                    }
                }
            )
            .clipShape(Capsule())
            .shadow(
                color: isEnabled ? Color.appPrimary.opacity(0.3) : .clear,
                radius: 8, y: 4
            )
        }
        .buttonStyle(PlainButtonStyle())
        .disabled(!isEnabled || isLoading)
    }
}

#Preview {
    VStack(spacing: Dimens.paddingL) {
        PrimaryButton(title: "Book Appointment") {}
        PrimaryButton(title: "Loading...", isLoading: true) {}
        PrimaryButton(title: "Disabled", isEnabled: false) {}
        PrimaryButton(title: "With Icon", icon: "calendar") {}
    }
    .padding()
}
