package lu.esklepios.app.utils

import androidx.annotation.StringRes
import lu.esklepios.app.R
import lu.esklepios.app.domain.model.AppointmentStatus

/**
 * Maps [AppointmentStatus] to its Android string resource ID.
 *
 * This is the Android-platform resolver for the Twine key produced by
 * [AppointmentStatusOptions.labelKey]. Centralising it here means badge
 * components never embed a `when (key: String)` lookup.
 */
@StringRes
fun AppointmentStatus.labelStringRes(): Int = when (this) {
    AppointmentStatus.CONFIRMED -> R.string.status_confirmed
    AppointmentStatus.PENDING   -> R.string.status_reserved
    AppointmentStatus.CANCELLED -> R.string.status_cancelled
    AppointmentStatus.COMPLETED -> R.string.status_completed
    AppointmentStatus.NO_SHOW   -> R.string.status_no_show
}