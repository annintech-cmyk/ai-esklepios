import SwiftUI

extension Font {
    static func appSerif(_ size: CGFloat, weight: Font.Weight = .regular) -> Font {
        .custom("DM Serif Display", size: size).weight(weight)
    }

    static func appSans(_ size: CGFloat, weight: Font.Weight = .regular) -> Font {
        .system(size: size, weight: weight, design: .default)
    }

    static let displayLarge = appSerif(34, weight: .bold)
    static let heading1 = appSerif(28, weight: .bold)
    static let heading2 = appSans(22, weight: .bold)
    static let profileName: Font = .system(size: 20, weight: .bold)
    static let heading3 = appSans(18, weight: .semibold)
    static let heading4 = appSans(16, weight: .semibold)
    static let heading5 = appSans(14, weight: .semibold)
    static let body = appSans(14)
    static let bodyMedium = appSans(14, weight: .medium)
    static let bodyLarge = appSans(16)
    static let caption = appSans(12)
    static let captionBold = appSans(12, weight: .semibold)
    static let micro = appSans(11, weight: .semibold)
    static let nano = appSans(10, weight: .semibold)
    static let label = appSans(13, weight: .medium)
    static let cardTitle = appSans(15, weight: .bold)
    static let captionMedium = appSans(12, weight: .medium)

    // Hero display — gradient header hero text (compound annotated string, matches Android displaySmall)
    static let heroDisplay = appSans(30, weight: .heavy)

    // Splash-specific
    static let splashTitle    = appSans(46, weight: .bold)
    static let splashSubtitle = appSans(15)
}
