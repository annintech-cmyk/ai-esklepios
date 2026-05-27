import SwiftUI

struct SectionTitle: View {
    let text: String
    var subtitle: String? = nil

    var body: some View {
        VStack(alignment: .leading, spacing: 4) {
            Text(text)
                .font(.system(size: 16, weight: .semibold))
                .foregroundColor(.appTextPrimary)
                .frame(maxWidth: .infinity, alignment: .leading)
            if let subtitle = subtitle {
                Text(subtitle)
                    .font(.system(size: 12))
                    .foregroundColor(.appTextSecondary)
                    .frame(maxWidth: .infinity, alignment: .leading)
            }
        }
        .padding(.horizontal, Dimens.paddingL)
    }
}

#Preview {
    VStack {
        SectionTitle(text: "Practitioners")
        SectionTitle(text: "Upcoming Appointments", subtitle: "Your next 3 appointments")
    }
    .background(Color.appBackground)
}
