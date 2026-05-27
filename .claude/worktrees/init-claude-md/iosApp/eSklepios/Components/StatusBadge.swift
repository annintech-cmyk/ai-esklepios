import SwiftUI

enum AppointmentStatusDisplay: String {
    case confirmed = "CONFIRMED"
    case pending = "PENDING"
    case cancelled = "CANCELLED"
    case completed = "COMPLETED"
    case noShow = "NO_SHOW"

    var label: String {
        switch self {
        case .confirmed: return "Confirmed"
        case .pending: return "Pending"
        case .cancelled: return "Cancelled"
        case .completed: return "Completed"
        case .noShow: return "No Show"
        }
    }

    var backgroundColor: Color {
        switch self {
        case .confirmed: return Color.appSuccessBg
        case .pending: return Color.appWarningBg
        case .cancelled: return Color.appDangerBg
        case .completed: return Color.appPrimaryLight
        case .noShow: return Color.appDangerBg
        }
    }

    var textColor: Color {
        switch self {
        case .confirmed: return Color.appSuccess
        case .pending: return Color.appWarning
        case .cancelled: return Color.appDanger
        case .completed: return Color.appPrimary
        case .noShow: return Color.appDanger
        }
    }

    static func from(string: String) -> AppointmentStatusDisplay {
        return AppointmentStatusDisplay(rawValue: string.uppercased()) ?? .pending
    }
}

struct StatusBadge: View {
    let status: AppointmentStatusDisplay

    var body: some View {
        Text(status.label)
            .font(.system(size: 11, weight: .semibold))
            .padding(.horizontal, Dimens.paddingM)
            .padding(.vertical, Dimens.paddingXS)
            .background(status.backgroundColor)
            .foregroundColor(status.textColor)
            .cornerRadius(Dimens.radiusPill)
    }
}

#Preview {
    VStack(spacing: 8) {
        StatusBadge(status: .confirmed)
        StatusBadge(status: .pending)
        StatusBadge(status: .cancelled)
        StatusBadge(status: .completed)
    }
    .padding()
    .background(Color.appBackground)
}
