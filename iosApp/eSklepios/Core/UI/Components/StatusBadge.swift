import SwiftUI
import shared

/// Platform-side enum used by `AppointmentCard` and friends. Display-related
/// properties (`label`, `backgroundColor`, `textColor`) all derive from the
/// shared `AppointmentStatus` enum via `AppointmentStatusOptions` (Rule A-13) —
/// this enum is just the Swift adapter that fronts callers' existing API.
enum AppointmentStatusDisplay: String {
    case confirmed = "CONFIRMED"
    case pending = "PENDING"
    case cancelled = "CANCELLED"
    case completed = "COMPLETED"
    case noShow = "NO_SHOW"

    /// Bridges to the shared KMP enum so all label/colour logic flows from a single source.
    private var sharedStatus: AppointmentStatus {
        switch self {
        case .confirmed: return .confirmed
        case .pending:   return .pending
        case .cancelled: return .cancelled
        case .completed: return .completed
        case .noShow:    return .noShow
        }
    }

    /// Twine-localized label. Fallback `value:` mirrors the legacy hardcoded strings.
    var label: String {
        let key = AppointmentStatusOptions.shared.labelKey(status: sharedStatus)
        let fallback: String = {
            switch self {
            case .confirmed: return "Confirmed"
            case .pending:   return "Pending"
            case .cancelled: return "Cancelled"
            case .completed: return "Completed"
            case .noShow:    return "No Show"
            }
        }()
        return NSLocalizedString(key, value: fallback, comment: "")
    }

    var backgroundColor: Color {
        Self.background(for: AppointmentStatusOptions.shared.colorScheme(status: sharedStatus))
    }

    var textColor: Color {
        Self.foreground(for: AppointmentStatusOptions.shared.colorScheme(status: sharedStatus))
    }

    /// Parse the KMP enum's `.name` string into this display enum.
    /// Kept for backwards compatibility with existing call-sites.
    static func from(string: String) -> AppointmentStatusDisplay {
        AppointmentStatusDisplay(rawValue: string.uppercased()) ?? .pending
    }

    // MARK: - Scheme → colour resolver

    private static func background(for scheme: AppointmentStatusColorScheme) -> Color {
        switch scheme {
        case .success: return Color.appSuccessBg
        case .warning: return Color.appWarningBg
        case .danger:  return Color.appDangerBg
        case .primary: return Color.appPrimaryLight
        default:       return Color.appPrimaryLight
        }
    }

    private static func foreground(for scheme: AppointmentStatusColorScheme) -> Color {
        switch scheme {
        case .success: return Color.appSuccess
        case .warning: return Color.appWarning
        case .danger:  return Color.appDanger
        case .primary: return Color.appPrimary
        default:       return Color.appPrimary
        }
    }
}

struct StatusBadge: View {
    let status: AppointmentStatusDisplay

    var body: some View {
        AppCaptionText(text: status.label, color: status.textColor)
            .padding(.horizontal, Spacing.m)
            .padding(.vertical, Spacing.xs)
            .background(status.backgroundColor)
            .cornerRadius(Radius.pill)
    }
}

#Preview {
    VStack(spacing: Spacing.s) {
        StatusBadge(status: .confirmed)
        StatusBadge(status: .pending)
        StatusBadge(status: .cancelled)
        StatusBadge(status: .completed)
        StatusBadge(status: .noShow)
    }
    .padding()
    .background(Color.appBackground)
}
