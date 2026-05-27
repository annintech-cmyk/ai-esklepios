package lu.esklepios.app.utils

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

object DateUtil {

    const val PATTERN_DISPLAY_FULL         = "EEE, MMM d, yyyy"
    const val PATTERN_DISPLAY_LONG         = "EEEE, MMMM d"
    const val PATTERN_DOB                  = "MMMM d, yyyy"
    const val PATTERN_APPOINTMENT_DATETIME = "EEE, MMM d, HH:mm"

    fun extractSlotTime(slotId: String): String {
        val parts = slotId.split("_")
        return if (parts.size >= 4) {
            val hhmm = parts.last()
            if (hhmm.length == 4) "${hhmm.substring(0, 2)}:${hhmm.substring(2, 4)}" else slotId
        } else slotId
    }

    fun compactToIso(compactDate: String): String =
        "${compactDate.substring(0, 4)}-${compactDate.substring(4, 6)}-${compactDate.substring(6, 8)}"

    fun formatIsoDate(
        isoDate: String,
        pattern: String,
        locale: Locale = Locale.ENGLISH
    ): String = try {
        LocalDate.parse(isoDate).format(DateTimeFormatter.ofPattern(pattern, locale))
    } catch (_: Exception) {
        isoDate
    }

    fun today(): LocalDate = LocalDate.now()

    /** Extracts the ISO date portion from a full ISO datetime string, e.g. "2026-05-26T08:30:00" → "2026-05-26". */
    fun dateFromDateTime(datetime: String): String = datetime.take(10)

    fun formatSlotSummary(isoDate: String, slotId: String): String =
        "${formatIsoDate(isoDate, PATTERN_DISPLAY_FULL)} · ${extractSlotTime(slotId)}"

    /** Formats a full ISO-8601 datetime as a booking slot summary.
     * "2026-05-26T08:30:00" → "Tue, May 26, 2026 · 08:30". Falls back to raw input on parse failure. */
    fun formatSlotSummary(isoDateTime: String): String = try {
        val dt = java.time.LocalDateTime.parse(isoDateTime)
        val datePart = dt.toLocalDate().format(java.time.format.DateTimeFormatter.ofPattern(PATTERN_DISPLAY_FULL, Locale.ENGLISH))
        val timePart = dt.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"))
        "$datePart · $timePart"
    } catch (_: Exception) { isoDateTime }

    fun formatDateTimeSummary(dateLabel: String, timeLabel: String): String =
        "$dateLabel · $timeLabel"

    /**
     * Formats an ISO-8601 appointment datetime for display.
     * "2026-05-26T14:30:00" → "Tue, May 26, 14:30".
     * Falls back to the raw input on parse failure.
     */
    fun formatAppointmentDateTime(
        isoDateTime: String,
        locale: Locale = Locale.ENGLISH
    ): String = try {
        LocalDateTime.parse(isoDateTime)
            .format(DateTimeFormatter.ofPattern(PATTERN_APPOINTMENT_DATETIME, locale))
    } catch (_: Exception) {
        isoDateTime
    }
}
