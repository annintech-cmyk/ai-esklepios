package lu.esklepios.app.util

/**
 * Date-based filter buckets for the practitioner search screens.
 *
 * `apiKey` is the stable identifier persisted in `HomeUiState.selectedDateFilter`
 * and read by the search use-case. `labelKey` is the Twine string-resource key
 * for the chip label.
 *
 * RULE A-13: Hardcoding `"All" to "All"`, `"Today" to "Today"`, or
 * `"Within 3 Days" to "Within 3 Days"` in a screen is forbidden — iterate
 * [DateFilter.entries] instead.
 */
enum class DateFilter(val apiKey: String, val labelKey: String) {
    ALL("All", "home_filter_all"),
    TODAY("Today", "home_filter_today"),
    WITHIN_3_DAYS("Within 3 Days", "home_filter_3days"),
    ;

    companion object {
        fun fromApiKey(value: String): DateFilter = entries.firstOrNull { it.apiKey.equals(value, ignoreCase = true) } ?: ALL
    }
}
