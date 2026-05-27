import Foundation

enum DateUtil {
    static let PATTERN_ISO                  = "yyyy-MM-dd"
    static let PATTERN_ISO_DATETIME         = "yyyy-MM-dd'T'HH:mm:ss"
    static let PATTERN_DAY_ABBR             = "EEE"
    static let PATTERN_DISPLAY_FULL         = "EEE, MMM d, yyyy"
    static let PATTERN_DISPLAY_LONG         = "EEEE, MMMM d"
    static let PATTERN_DOB                  = "MMMM d, yyyy"
    static let PATTERN_APPOINTMENT_DATETIME = "EEE, MMM d, HH:mm"

    static func today() -> Date { Date() }

    static func todayKey() -> String { dateToIso(today()) }

    static func extractSlotTime(_ slotId: String) -> String {
        let parts = slotId.split(separator: "_")
        guard parts.count >= 4 else { return slotId }
        let hhmm = String(parts.last!)
        guard hhmm.count == 4 else { return slotId }
        let h = hhmm.prefix(2)
        let m = hhmm.suffix(2)
        return "\(h):\(m)"
    }

    static func compactToIso(_ compact: String) -> String {
        guard compact.count == 8 else { return compact }
        let y = compact.prefix(4)
        let m = compact.dropFirst(4).prefix(2)
        let d = compact.suffix(2)
        return "\(y)-\(m)-\(d)"
    }

    static func isoToDate(_ isoDate: String) -> Date? {
        formatter(PATTERN_ISO).date(from: isoDate)
    }

    static func dateToIso(_ date: Date) -> String {
        formatter(PATTERN_ISO).string(from: date)
    }

    static func formatIsoDate(_ isoDate: String, pattern: String) -> String {
        guard let date = isoToDate(isoDate) else { return isoDate }
        return formatter(pattern).string(from: date)
    }

    static func weekDays(startingFrom date: Date, count: Int = 5) -> [Date] {
        let calendar = Calendar.current
        return (0..<count).compactMap { calendar.date(byAdding: .day, value: $0, to: date) }
    }

    /** Extracts the ISO date portion from a full ISO datetime string, e.g. "2026-05-26T08:30:00" → "2026-05-26". */
    static func dateFromDateTime(_ datetime: String) -> String { String(datetime.prefix(10)) }

    /** Extracts the HH:MM time portion from a full ISO datetime string, e.g. "2026-05-26T08:30:00" → "08:30". */
    static func timeFromDateTime(_ datetime: String) -> String { String(datetime.dropFirst(11).prefix(5)) }

    /// Formats a full ISO-8601 datetime as a booking slot summary.
    /// "2026-05-26T08:30:00" → "Tue, May 26, 2026 · 08:30". Falls back to the raw input on parse failure.
    static func formatSlotSummary(_ dateTime: String) -> String {
        guard let date = formatter(PATTERN_ISO_DATETIME).date(from: dateTime) else { return dateTime }
        let datePart = formatter(PATTERN_DISPLAY_FULL).string(from: date)
        let timePart = formatter("HH:mm").string(from: date)
        return "\(datePart) · \(timePart)"
    }

    /// Formats an ISO-8601 appointment datetime for display.
    /// "2026-05-26T14:30:00" → "Tue, May 26, 14:30". Falls back to the raw input on parse failure.
    static func formatAppointmentDateTime(_ isoDateTime: String) -> String {
        guard let date = formatter(PATTERN_ISO_DATETIME).date(from: isoDateTime) else { return isoDateTime }
        return formatter(PATTERN_APPOINTMENT_DATETIME).string(from: date)
    }

    static func currentWeekMonday() -> Date {
        let calendar = Calendar.current
        let today = today()
        let daysSinceMonday = (calendar.component(.weekday, from: today) + 5) % 7
        return calendar.date(byAdding: .day, value: -daysSinceMonday, to: today) ?? today
    }

    static func dayOfMonth(_ date: Date) -> Int {
        Calendar.current.component(.day, from: date)
    }

    private static func formatter(_ pattern: String) -> DateFormatter {
        let f = DateFormatter()
        f.dateFormat = pattern
        f.locale = Locale(identifier: "en_US_POSIX")
        return f
    }
}
