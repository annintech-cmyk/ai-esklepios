import SwiftUI
import shared

extension PasswordStrength {
    var displayLabel: String { NSLocalizedString(labelKey, comment: "") }

    var displayColor: Color {
        switch self {
        case .strong: return .appSuccess
        case .good:   return .appStrengthGood
        case .fair:   return .appWarning
        default:      return .appDanger
        }
    }
}