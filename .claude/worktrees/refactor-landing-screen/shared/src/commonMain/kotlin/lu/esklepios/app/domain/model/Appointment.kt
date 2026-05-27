package lu.esklepios.app.domain.model

data class Appointment(
    val id: String,
    val practitionerId: String,
    val practitionerName: String,
    val clinicName: String,
    val specialty: String,
    val dateTime: String,
    val status: AppointmentStatus,
    val messageToDoctor: String = "",
    val consultationReason: String = ""
)

enum class AppointmentStatus {
    PENDING,
    CONFIRMED,
    CANCELLED,
    COMPLETED,
    NO_SHOW;

    companion object {
        fun fromString(value: String): AppointmentStatus = entries.find {
            it.name.equals(value, ignoreCase = true)
        } ?: PENDING
    }
}
