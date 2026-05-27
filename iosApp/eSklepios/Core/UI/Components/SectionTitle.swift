import SwiftUI

struct SectionTitle: View {
    let text: String
    var subtitle: String? = nil

    var body: some View {
        VStack(alignment: .leading, spacing: Spacing.xs) {
            AppSubtitleText(text: text)
                .frame(maxWidth: .infinity, alignment: .leading)
            if let subtitle = subtitle {
                AppCaptionText(text: subtitle)
                    .frame(maxWidth: .infinity, alignment: .leading)
            }
        }
        .padding(.horizontal, Spacing.l)
    }
}

#Preview {
    VStack {
        SectionTitle(text: "Practitioners")
        SectionTitle(text: "Upcoming Appointments", subtitle: "Your next 3 appointments")
    }
    .background(Color.appBackground)
}
