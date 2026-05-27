import SwiftUI
import Combine

enum ThemeMode: String, CaseIterable {
    case light, dark, system

    var displayName: String {
        switch self {
        case .light: return NSLocalizedString("theme_light", comment: "")
        case .dark: return NSLocalizedString("theme_dark", comment: "")
        case .system: return NSLocalizedString("theme_system", comment: "")
        }
    }
}

class ThemeManager: ObservableObject {
    static let shared = ThemeManager()
    @Published var themeMode: ThemeMode = .system
    private let storage = KeychainStorage.shared

    private init() {
        if let saved = storage.get(key: "theme_mode"),
           let mode = ThemeMode(rawValue: saved) {
            themeMode = mode
        }
    }

    func setTheme(_ mode: ThemeMode) {
        themeMode = mode
        storage.set(key: "theme_mode", value: mode.rawValue)
    }

    var colorScheme: ColorScheme? {
        switch themeMode {
        case .light: return .light
        case .dark: return .dark
        case .system: return nil
        }
    }
}
