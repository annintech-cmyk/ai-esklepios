package lu.esklepios.app.util

/**
 * Availability filter buckets shown as chips on the search screens.
 *
 * TODAY / WITHIN_THREE_DAYS correspond to [DateFilter.TODAY] / [DateFilter.WITHIN_3_DAYS].
 * OPEN_TO_NEW_PATIENTS maps to the `showOpenToNewPatientsOnly` flag in HomeUiState.
 *
 * RULE A-13: This enum is the single source of truth for availability chip variants.
 * Never define an equivalent in platform UI files.
 */
enum class AvailabilityFilter(val labelKey: String) {
    TODAY("home_filter_today"),
    WITHIN_THREE_DAYS("home_filter_3days"),
    OPEN_TO_NEW_PATIENTS("home_filter_new_patients");
}