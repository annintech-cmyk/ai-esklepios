package lu.esklepios.app

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import lu.esklepios.app.data.network.AppointmentDto
import lu.esklepios.app.data.network.AppointmentSlotDto
import lu.esklepios.app.data.network.ChangeEmailRequest
import lu.esklepios.app.data.network.ChangePasswordRequest
import lu.esklepios.app.data.network.CreateAppointmentRequest
import lu.esklepios.app.data.network.ForgotPasswordRequest
import lu.esklepios.app.data.network.ForgotPasswordResponse
import lu.esklepios.app.data.network.LoginRequest
import lu.esklepios.app.data.network.LoginResponse
import lu.esklepios.app.data.network.ModifyAppointmentRequest
import lu.esklepios.app.data.network.PractitionerDto
import lu.esklepios.app.data.network.RefreshTokenRequest
import lu.esklepios.app.data.network.RefreshTokenResponse
import lu.esklepios.app.data.network.RegisterRequest
import lu.esklepios.app.data.network.RegisterResponse
import lu.esklepios.app.data.network.ScheduleDayDto
import lu.esklepios.app.data.network.UpdateProfileRequest
import lu.esklepios.app.data.network.UserDto
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Verifies every @Serializable DTO can round-trip through JSON.
 * Guards against the "Serializer for class 'X' is not found" crash in R8 release builds.
 */
class SerializationSmokeTest {
    private val json =
        Json {
            ignoreUnknownKeys = true
            isLenient = true
        }

    private val sampleUserDto =
        UserDto(
            id = "u1", firstName = "Sophie", lastName = "Müller",
            email = "sophie@test.lu", phone = "+352 621 000",
            gender = "Female", dateOfBirth = "1990-03-14",
            cnsNumber = "1990 0314 0001", profileType = "PATIENT", language = "en",
        )
    private val sampleSlotDto =
        AppointmentSlotDto(
            id = "slot_1",
            practitionerId = "p1",
            dateTime = "2026-06-01T09:00:00",
            isAvailable = true,
            durationMinutes = 30,
        )
    private val sampleScheduleDto =
        ScheduleDayDto(
            dayOfWeek = "MONDAY",
            openTime = "08:00",
            closeTime = "17:00",
            isOpen = true,
        )

    @Test fun `LoginRequest round-trips`() = assertRoundTrip(LoginRequest("a@b.lu", "pwd"))

    @Test fun `LoginResponse round-trips`() =
        assertRoundTrip(
            LoginResponse(token = "jwt", refreshToken = "ref", user = sampleUserDto),
        )

    @Test fun `RegisterRequest round-trips`() =
        assertRoundTrip(
            RegisterRequest("Sophie", "Müller", "a@b.lu", "pass", "+352 0", "1990-01-01", "Female", "0000", "PATIENT", "en"),
        )

    @Test fun `RegisterResponse round-trips`() = assertRoundTrip(RegisterResponse(user = sampleUserDto))

    @Test fun `ForgotPasswordRequest round-trips`() = assertRoundTrip(ForgotPasswordRequest("a@b.lu"))

    @Test fun `ForgotPasswordResponse round-trips`() = assertRoundTrip(ForgotPasswordResponse("Check email"))

    @Test fun `RefreshTokenRequest round-trips`() = assertRoundTrip(RefreshTokenRequest("ref-token"))

    @Test fun `RefreshTokenResponse round-trips`() =
        assertRoundTrip(
            RefreshTokenResponse(token = "new-jwt", refreshToken = "new-ref"),
        )

    @Test fun `AppointmentSlotDto round-trips`() = assertRoundTrip(sampleSlotDto)

    @Test fun `ScheduleDayDto round-trips`() = assertRoundTrip(sampleScheduleDto)

    @Test fun `PractitionerDto round-trips`() =
        assertRoundTrip(
            PractitionerDto(
                id = "p1", firstName = "Marie", lastName = "Dubois", specialty = "Cardiology",
                clinicName = "HealthCare LU", address = "1 Rue de la Santé", city = "Luxembourg",
                phone = "+352 200 100", email = "marie@clinic.lu",
                latitude = 49.611, longitude = 6.131,
                acceptingNewPatients = true,
                availableSlots = listOf(sampleSlotDto),
                schedule = listOf(sampleScheduleDto),
                paymentMethods = listOf("CNS", "Cash"),
                diplomas = listOf("MD"),
                isFavorite = false,
            ),
        )

    @Test fun `AppointmentDto round-trips`() =
        assertRoundTrip(
            AppointmentDto(
                id = "apt1", practitionerId = "p1", practitionerName = "Dr. Dubois",
                clinicName = "HealthCare LU", specialty = "Cardiology",
                dateTime = "2026-06-01T09:00:00", status = "CONFIRMED",
                messageToDoctor = "Hello", consultationReason = "Check-up",
            ),
        )

    @Test fun `UserDto round-trips`() = assertRoundTrip(sampleUserDto)

    @Test fun `CreateAppointmentRequest round-trips`() =
        assertRoundTrip(
            CreateAppointmentRequest(
                practitionerId = "p1",
                dateTime = "2026-06-01T09:00:00",
                messageToDoctor = "Hello",
                consultationReason = "Check-up",
            ),
        )

    @Test fun `ModifyAppointmentRequest round-trips`() =
        assertRoundTrip(
            ModifyAppointmentRequest(
                dateTime = "2026-06-02T10:00:00",
                messageToDoctor = "Reschedule",
                consultationReason = "Follow-up",
            ),
        )

    @Test fun `UpdateProfileRequest round-trips`() =
        assertRoundTrip(
            UpdateProfileRequest(
                firstName = "Sophie",
                lastName = "Müller",
                phone = "+352 621 000",
                gender = "Female",
                dateOfBirth = "1990-03-14",
                cnsNumber = "1990 0314 0001",
                language = "en",
            ),
        )

    @Test fun `ChangeEmailRequest round-trips`() =
        assertRoundTrip(
            ChangeEmailRequest(newEmail = "new@test.lu", password = "pwd"),
        )

    @Test fun `ChangePasswordRequest round-trips`() =
        assertRoundTrip(
            ChangePasswordRequest(oldPassword = "old", newPassword = "new"),
        )

    private inline fun <reified T> assertRoundTrip(original: T) {
        val encoded = json.encodeToString(original)
        val decoded = json.decodeFromString<T>(encoded)
        assertEquals(original, decoded, "Round-trip failed for ${T::class.simpleName}")
    }
}
