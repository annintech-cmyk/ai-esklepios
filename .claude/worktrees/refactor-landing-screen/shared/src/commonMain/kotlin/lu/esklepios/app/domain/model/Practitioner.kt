package lu.esklepios.app.domain.model

data class Practitioner(
    val id: String,
    val firstName: String,
    val lastName: String,
    val specialty: String,
    val clinicName: String,
    val address: String,
    val city: String,
    val phone: String,
    val email: String,
    val latitude: Double,
    val longitude: Double,
    val acceptingNewPatients: Boolean,
    val availableSlots: List<AppointmentSlot>,
    val schedule: List<ScheduleDay>,
    val paymentMethods: List<String>,
    val diplomas: List<String>,
    val isFavorite: Boolean
) {
    val fullName: String get() = "Dr. $firstName $lastName"
    val initials: String get() = "${firstName.firstOrNull() ?: ""}${lastName.firstOrNull() ?: ""}".uppercase()
}
