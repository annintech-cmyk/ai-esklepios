import SwiftUI

struct AppTitleText: View {
    let text: String
    var color: Color = .appTextPrimary
    var alignment: TextAlignment = .leading
    var maxLines: Int? = nil

    var body: some View {
        Text(text)
            .font(.heading2)
            .foregroundColor(color)
            .multilineTextAlignment(alignment)
            .lineLimit(maxLines)
    }
}

struct AppSubtitleText: View {
    let text: String
    var color: Color = .appTextPrimary
    var alignment: TextAlignment = .leading
    var maxLines: Int? = nil

    var body: some View {
        Text(text)
            .font(.heading4)
            .foregroundColor(color)
            .multilineTextAlignment(alignment)
            .lineLimit(maxLines)
    }
}

struct AppBodyText: View {
    let text: String
    var color: Color = .appTextSecondary
    var alignment: TextAlignment = .leading
    var maxLines: Int? = nil

    var body: some View {
        Text(text)
            .font(.body)
            .foregroundColor(color)
            .multilineTextAlignment(alignment)
            .lineLimit(maxLines)
    }
}

struct AppCaptionText: View {
    let text: String
    var color: Color = .appTextSecondary
    var alignment: TextAlignment = .leading
    var maxLines: Int? = nil

    var body: some View {
        Text(text)
            .font(.caption)
            .foregroundColor(color)
            .multilineTextAlignment(alignment)
            .lineLimit(maxLines)
    }
}

struct AppToolbarTitle: View {
    let text: String
    var color: Color = .appTextPrimary

    var body: some View {
        Text(text)
            .font(.heading3)
            .foregroundColor(color)
            .lineLimit(1)
    }
}

struct AppButtonText: View {
    let text: String
    var color: Color = .white

    var body: some View {
        Text(text)
            .font(.heading5)
            .foregroundColor(color)
            .lineLimit(1)
    }
}

struct AppLabelText: View {
    let text: String
    var color: Color = .appTextPrimary
    var alignment: TextAlignment = .leading
    var maxLines: Int? = nil

    var body: some View {
        Text(text)
            .font(.label)
            .foregroundColor(color)
            .multilineTextAlignment(alignment)
            .lineLimit(maxLines)
    }
}

struct AppErrorText: View {
    let text: String
    var alignment: TextAlignment = .leading

    var body: some View {
        Text(text)
            .font(.caption)
            .foregroundColor(.appDanger)
            .multilineTextAlignment(alignment)
    }
}

struct FormFieldLabel: View {
    let label: String
    var required: Bool = false
    var body: some View {
        HStack(spacing: Spacing.xxs) {
            AppLabelText(text: label)
            if required {
                AppLabelText(text: " *", color: .appDanger)
            }
        }
    }
}

struct ValidationCaption: View {
    let text: String
    let isValid: Bool
    var body: some View {
        HStack(spacing: Dimens.paddingXS) {
            Image(systemName: isValid ? "checkmark.circle.fill" : "xmark.circle.fill")
                .font(.system(size: Dimens.iconSm))
                .foregroundColor(isValid ? .appSuccess : .appDanger)
            AppCaptionText(text: text, color: isValid ? .appSuccess : .appDanger)
        }
    }
}

struct AppSectionHeaderText: View {
    let text: String
    var color: Color = .appTextPrimary

    var body: some View {
        Text(text)
            .font(.caption)
            .fontWeight(.bold)
            .kerning(0.8)
            .foregroundColor(color)
    }
}

struct AppFieldValueText: View {
    let text: String
    var color: Color = .appTextPrimary

    var body: some View {
        Text(text)
            .font(.captionBold)
            .foregroundColor(color)
    }
}
