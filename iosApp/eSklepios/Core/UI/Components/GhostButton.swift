import SwiftUI

struct GhostButton: View {
    let title: String
    var icon: String? = nil
    var color: Color = .appPrimary
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            HStack(spacing: Dimens.paddingXS) {
                if let icon = icon {
                    Image(systemName: icon)
                        .font(.system(size: Dimens.iconMd))
                        .foregroundColor(color)
                }
                AppButtonText(text: title, color: color)
            }
            .frame(maxWidth: .infinity)
            .frame(height: Dimens.buttonHeight)
        }
        .buttonStyle(PlainButtonStyle())
    }
}

#Preview {
    VStack {
        GhostButton(title: "Continue as Guest") {}
        GhostButton(title: "Forgot Password", color: .appTextSecondary) {}
    }
    .padding()
}
