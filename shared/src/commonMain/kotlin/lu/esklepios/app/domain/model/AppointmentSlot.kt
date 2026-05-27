package lu.esklepios.app.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class AppointmentSlot(
    val id: String,
    val practitionerId: String,
    val dateTime: String,
    val available: Boolean,
    val durationMinutes: Int = 30,
)
