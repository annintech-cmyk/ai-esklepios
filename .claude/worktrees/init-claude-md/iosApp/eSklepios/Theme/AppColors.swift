import SwiftUI

extension Color {
    static let appPrimary = Color(hex: "3B4FE8")
    static let appPrimaryDark = Color(hex: "1A2580")
    static let appPrimaryLight = Color(hex: "EEF0FD")
    static let appPrimaryMid = Color(hex: "6B7BED")
    static let appBackground = Color(hex: "F4F6FB")
    static let appSurface = Color.white
    static let appTextPrimary = Color(hex: "1A1D2E")
    static let appTextSecondary = Color(hex: "6B7280")
    static let appTextHint = Color(hex: "9CA3AF")
    static let appSuccess = Color(hex: "3B6D11")
    static let appSuccessBg = Color(hex: "EAF3DE")
    static let appDanger = Color(hex: "D83B3B")
    static let appDangerBg = Color(hex: "FFF3F3")
    static let appWarning = Color(hex: "F59E0B")
    static let appWarningBg = Color(hex: "FAEEDA")

    init(hex: String) {
        let hex = hex.trimmingCharacters(in: CharacterSet.alphanumerics.inverted)
        var int: UInt64 = 0
        Scanner(string: hex).scanHexInt64(&int)
        let a, r, g, b: UInt64
        switch hex.count {
        case 3: (a, r, g, b) = (255, (int >> 8) * 17, (int >> 4 & 0xF) * 17, (int & 0xF) * 17)
        case 6: (a, r, g, b) = (255, int >> 16, int >> 8 & 0xFF, int & 0xFF)
        case 8: (a, r, g, b) = (int >> 24, int >> 16 & 0xFF, int >> 8 & 0xFF, int & 0xFF)
        default: (a, r, g, b) = (255, 0, 0, 0)
        }
        self.init(.sRGB, red: Double(r) / 255, green: Double(g) / 255, blue: Double(b) / 255, opacity: Double(a) / 255)
    }
}

struct AppGradient {
    static let primary = LinearGradient(
        colors: [Color(hex: "2C3AEF"), Color(hex: "1A2580")],
        startPoint: .topLeading,
        endPoint: .bottomTrailing
    )

    static let primaryVertical = LinearGradient(
        colors: [Color(hex: "3B4FE8"), Color(hex: "1A2580")],
        startPoint: .top,
        endPoint: .bottom
    )

    static let surface = LinearGradient(
        colors: [Color.white, Color(hex: "F4F6FB")],
        startPoint: .top,
        endPoint: .bottom
    )
}
