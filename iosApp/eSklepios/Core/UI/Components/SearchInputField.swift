import SwiftUI

enum SearchInputVariant {
    case light, dark
}

struct SearchInputField: View {
    let systemIcon: String
    let placeholder: String
    @Binding var text: String
    var iconColor: Color? = nil
    var variant: SearchInputVariant = .light
    var onSubmit: (() -> Void)? = nil

    private var resolvedIconColor: Color {
        if let color = iconColor { return color }
        return variant == .dark ? .white.opacity(0.8) : .appTextHint
    }

    private var placeholderColor: Color {
        variant == .dark ? .white.opacity(0.5) : .appTextHint
    }

    private var textColor: Color {
        variant == .dark ? .white : .appTextPrimary
    }

    private var backgroundColor: Color {
        variant == .dark ? .white.opacity(0.15) : .appInputBackground
    }

    private var borderColor: Color {
        variant == .dark ? .white.opacity(0.3) : .clear
    }

    var body: some View {
        HStack(spacing: Dimens.paddingS + Dimens.paddingXS) {
            Image(systemName: systemIcon)
                .font(.system(size: Dimens.iconMd))
                .foregroundColor(resolvedIconColor)
                .frame(width: Dimens.iconMd + Dimens.paddingXS)
            TextField("", text: $text)
                .placeholder(when: text.isEmpty) {
                    AppBodyText(text: placeholder, color: placeholderColor)
                }
                .font(.bodyLarge)
                .foregroundColor(textColor)
                .autocorrectionDisabled()
                .textInputAutocapitalization(.never)
                .onSubmit { onSubmit?() }
        }
        .padding(.horizontal, Dimens.paddingL)
        .frame(height: Dimens.inputHeight)
        .background(backgroundColor)
        .clipShape(RoundedRectangle(cornerRadius: Dimens.radiusMd))
        .overlay(
            RoundedRectangle(cornerRadius: Dimens.radiusMd)
                .stroke(borderColor, lineWidth: Dimens.strokeThin)
        )
    }
}