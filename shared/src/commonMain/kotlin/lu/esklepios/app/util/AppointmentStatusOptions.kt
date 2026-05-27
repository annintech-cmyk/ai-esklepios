package lu.esklepios.app.util

import lu.esklepios.app.domain.model.AppointmentStatus

/**
 * Semantic colour scheme for an appointment status badge. Platforms map this
 * to concrete `Color` tokens — shared can't reach platform colour APIs.
 */
enum class AppointmentStatusColorScheme { SUCCESS, WARNING, DANGER, PRIMARY }

/**
 * Single source of truth for the status → display mapping.
 *
 * RULE A-13: Inline `when (status) { CONFIRMED -> Triple(...) … }` mappings
 * in a screen or view file are forbidden — call into this object instead.
 *
 * Exposed as an `object` (so iOS gets `AppointmentStatusOptions.shared.labelKey(status:)`)
 * with extension-function shortcuts for ergonomic Android call sites
 * (`appointment.status.labelKey()`).
 */
object AppointmentStatusOptions {
    /** Twine key for the localized status label. */
    fun labelKey(status: AppointmentStatus): String = when (status) {
        AppointmentStatus.CONFIRMED -> "status_confirmed"
        AppointmentStatus.PENDING   -> "status_reserved"
        AppointmentStatus.CANCELLED -> "status_cancelled"
        AppointmentStatus.COMPLETED -> "status_completed"
        AppointmentStatus.NO_SHOW   -> "status_no_show"
    }

    /** Semantic colour bucket — platform UI resolves to actual `Color`. */
    fun colorScheme(status: AppointmentStatus): AppointmentStatusColorScheme = when (status) {
        AppointmentStatus.CONFIRMED -> AppointmentStatusColorScheme.SUCCESS
        AppointmentStatus.PENDING   -> AppointmentStatusColorScheme.WARNING
        AppointmentStatus.CANCELLED -> AppointmentStatusColorScheme.DANGER
        AppointmentStatus.COMPLETED -> AppointmentStatusColorScheme.PRIMARY
        AppointmentStatus.NO_SHOW   -> AppointmentStatusColorScheme.DANGER
    }
}

// ── Android-side ergonomic extensions ────────────────────────────────────────

fun AppointmentStatus.labelKey(): String = AppointmentStatusOptions.labelKey(this)
fun AppointmentStatus.colorScheme(): AppointmentStatusColorScheme =
    AppointmentStatusOptions.colorScheme(this)
