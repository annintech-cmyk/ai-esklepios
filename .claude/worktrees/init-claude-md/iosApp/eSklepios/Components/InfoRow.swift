import SwiftUI

struct InfoRow: View {
    let icon: String
    let label: String
    let value: String

    var body: some View {
        HStack(spacing: Dimens.paddingM) {
            Image(systemName: icon)
                .font(.system(size: Dimens.iconMd))
                .foregroundColor(.appPrimary)
                .frame(width: 22)
            VStack(alignment: .leading, spacing: 2) {
                Text(label)
                    .font(.system(size: 11))
                    .foregroundColor(.appTextSecondary)
                Text(value)
                    .font(.system(size: 14))
                    .foregroundColor(.appTextPrimary)
            }
            Spacer()
        }
    }
}

#Preview {
    VStack(spacing: 12) {
        InfoRow(icon: "phone", label: "Phone", value: "+352 123 456 789")
        InfoRow(icon: "envelope", label: "Email", value: "doctor@clinic.lu")
        InfoRow(icon: "mappin", label: "Address", value: "1 Main Street, Luxembourg")
    }
    .padding()
    .background(Color.appBackground)
}
