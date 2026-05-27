import SwiftUI

struct InfoRow: View {
    let icon: String
    let label: String
    let value: String

    var body: some View {
        HStack(spacing: Spacing.m) {
            Image(systemName: icon)
                .font(.system(size: Dimens.iconMd))
                .foregroundColor(.appPrimary)
                .frame(width: Dimens.infoRowIconColumn)
            VStack(alignment: .leading, spacing: Spacing.xxs) {
                AppCaptionText(text: label)
                AppBodyText(text: value, color: .appTextPrimary)
            }
            Spacer()
        }
    }
}

#Preview {
    VStack(spacing: Spacing.m) {
        InfoRow(icon: "phone", label: "Phone", value: "+352 123 456 789")
        InfoRow(icon: "envelope", label: "Email", value: "doctor@clinic.lu")
        InfoRow(icon: "mappin", label: "Address", value: "1 Main Street, Luxembourg")
    }
    .padding()
    .background(Color.appBackground)
}
