package lu.esklepios.app.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class ScheduleEntry(
    val day: String,
    val hours: String,
)

data class Practitioner(
    val id: String,
    val firstName: String,
    val lastName: String,
    val specialty: String,
    val clinicName: String,
    val address: String,
    val city: String,
    val postalCode: String = "",
    val phone: String,
    val email: String,
    val latitude: Double,
    val longitude: Double,
    val acceptingNewPatients: Boolean,
    val availableSlots: List<AppointmentSlot>,
    val schedule: List<ScheduleEntry> = emptyList(),
    val paymentMethods: List<String> = emptyList(),
    val diplomas: List<String> = emptyList(),
    val presentation: String = "",
    val isFavorite: Boolean,
) {
    val fullName: String get() = "Dr. $firstName $lastName"
    val initials: String get() = "${firstName.firstOrNull() ?: ""}${lastName.firstOrNull() ?: ""}".uppercase()
}
