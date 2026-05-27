import SwiftUI

struct AppCard<Content: View>: View {
    var padding: CGFloat = Dimens.paddingL
    var cornerRadius: CGFloat = Dimens.radiusLg
    @ViewBuilder let content: () -> Content

    var body: some View {
        VStack(alignment: .leading, spacing: 0) {
            content()
        }
        .padding(padding)
        .background(Color.appSurface)
        .cornerRadius(cornerRadius)
        .shadow(
            color: Color.appTextPrimary.opacity(0.07),
            radius: Dimens.shadowRadius,
            y: Dimens.shadowY
        )
    }
}

struct AppCardSection<Content: View>: View {
    let title: String
    var icon: String? = nil
    @ViewBuilder let content: () -> Content

    var body: some View {
        AppCard {
            HStack(spacing: Dimens.paddingS) {
                if let icon = icon {
                    Image(systemName: icon)
                        .font(.system(size: Dimens.iconMd))
                        .foregroundColor(.appPrimary)
                }
                Text(title)
                    .font(.heading4)
                    .foregroundColor(.appTextPrimary)
                Spacer()
            }
            .padding(.bottom, Dimens.paddingM)

            content()
        }
    }
}

#Preview {
    VStack(spacing: Dimens.paddingL) {
        AppCard {
            Text("Hello, card!")
                .font(.body)
                .foregroundColor(.appTextPrimary)
        }
        AppCardSection(title: "Contact Info", icon: "phone") {
            Text("Some content here")
        }
    }
    .padding()
    .background(Color.appBackground)
}
