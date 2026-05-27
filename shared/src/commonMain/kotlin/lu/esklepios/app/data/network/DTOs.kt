package lu.esklepios.app.data.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    @SerialName("email") val email: String,
    @SerialName("password") val password: String
)

@Serializable
data class LoginResponse(
    @SerialName("token") val token: String,
    @SerialName("refresh_token") val refreshToken: String,
    @SerialName("user") val user: UserDto
)

@Serializable
data class RegisterRequest(
    @SerialName("first_name") val firstName: String,
    @SerialName("last_name") val lastName: String,
    @SerialName("email") val email: String,
    @SerialName("password") val password: String,
    @SerialName("phone") val phone: String,
    @SerialName("gender") val gender: String,
    @SerialName("date_of_birth") val dateOfBirth: String,
    @SerialName("cns_number") val cnsNumber: String,
    @SerialName("profile_type") val profileType: String,
    @SerialName("language") val language: String
)

@Serializable
data class RegisterResponse(
    @SerialName("user") val user: UserDto
)

@Serializable
data class ForgotPasswordRequest(
    @SerialName("email") val email: String
)

@Serializable
data class ForgotPasswordResponse(
    @SerialName("message") val message: String
)

@Serializable
data class RefreshTokenRequest(
    @SerialName("refresh_token") val refreshToken: String
)

@Serializable
data class RefreshTokenResponse(
    @SerialName("token") val token: String,
    @SerialName("refresh_token") val refreshToken: String
)

@Serializable
data class AppointmentSlotDto(
    @SerialName("id") val id: String,
    @SerialName("practitioner_id") val practitionerId: String,
    @SerialName("date_time") val dateTime: String,
    @SerialName("is_available") val isAvailable: Boolean,
    @SerialName("duration_minutes") val durationMinutes: Int = 30
)

@Serializable
data class ScheduleDayDto(
    @SerialName("day_of_week") val dayOfWeek: String,
    @SerialName("open_time") val openTime: String,
    @SerialName("close_time") val closeTime: String,
    @SerialName("is_open") val isOpen: Boolean
)

@Serializable
data class PractitionerDto(
    @SerialName("id") val id: String,
    @SerialName("first_name") val firstName: String,
    @SerialName("last_name") val lastName: String,
    @SerialName("specialty") val specialty: String,
    @SerialName("clinic_name") val clinicName: String,
    @SerialName("address") val address: String,
    @SerialName("city") val city: String,
    @SerialName("phone") val phone: String,
    @SerialName("email") val email: String,
    @SerialName("latitude") val latitude: Double,
    @SerialName("longitude") val longitude: Double,
    @SerialName("accepting_new_patients") val acceptingNewPatients: Boolean,
    @SerialName("available_slots") val availableSlots: List<AppointmentSlotDto> = emptyList(),
    @SerialName("schedule") val schedule: List<ScheduleDayDto> = emptyList(),
    @SerialName("payment_methods") val paymentMethods: List<String> = emptyList(),
    @SerialName("diplomas") val diplomas: List<String> = emptyList(),
    @SerialName("is_favorite") val isFavorite: Boolean = false
)

@Serializable
data class AppointmentDto(
    @SerialName("id") val id: String,
    @SerialName("practitioner_id") val practitionerId: String,
    @SerialName("practitioner_name") val practitionerName: String,
    @SerialName("clinic_name") val clinicName: String,
    @SerialName("specialty") val specialty: String,
    @SerialName("date_time") val dateTime: String,
    @SerialName("status") val status: String,
    @SerialName("message_to_doctor") val messageToDoctor: String = "",
    @SerialName("consultation_reason") val consultationReason: String = ""
)

@Serializable
data class UserDto(
    @SerialName("id") val id: String,
    @SerialName("first_name") val firstName: String,
    @SerialName("last_name") val lastName: String,
    @SerialName("email") val email: String,
    @SerialName("phone") val phone: String,
    @SerialName("gender") val gender: String,
    @SerialName("date_of_birth") val dateOfBirth: String,
    @SerialName("cns_number") val cnsNumber: String,
    @SerialName("profile_type") val profileType: String,
    @SerialName("language") val language: String
)

@Serializable
data class CreateAppointmentRequest(
    @SerialName("practitioner_id") val practitionerId: String,
    @SerialName("date_time") val dateTime: String,
    @SerialName("message_to_doctor") val messageToDoctor: String,
    @SerialName("consultation_reason") val consultationReason: String
)

@Serializable
data class ModifyAppointmentRequest(
    @SerialName("date_time") val dateTime: String,
    @SerialName("message_to_doctor") val messageToDoctor: String,
    @SerialName("consultation_reason") val consultationReason: String
)

@Serializable
data class UpdateProfileRequest(
    @SerialName("first_name") val firstName: String,
    @SerialName("last_name") val lastName: String,
    @SerialName("phone") val phone: String,
    @SerialName("gender") val gender: String,
    @SerialName("date_of_birth") val dateOfBirth: String,
    @SerialName("cns_number") val cnsNumber: String,
    @SerialName("language") val language: String
)

@Serializable
data class ChangeEmailRequest(
    @SerialName("new_email") val newEmail: String,
    @SerialName("password") val password: String
)

@Serializable
data class ChangePasswordRequest(
    @SerialName("old_password") val oldPassword: String,
    @SerialName("new_password") val newPassword: String
)
