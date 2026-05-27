package lu.esklepios.app.data.network

import lu.esklepios.app.domain.model.Appointment
import lu.esklepios.app.domain.model.AppointmentSlot
import lu.esklepios.app.domain.model.AppointmentStatus
import lu.esklepios.app.domain.model.Practitioner
import lu.esklepios.app.domain.model.ProfileType
import lu.esklepios.app.domain.model.ScheduleDay
import lu.esklepios.app.domain.model.ScheduleEntry
import lu.esklepios.app.domain.model.User

// AppointmentSlotDto -> domain
fun AppointmentSlotDto.toDomain(): AppointmentSlot =
    AppointmentSlot(
        id = id,
        practitionerId = practitionerId,
        dateTime = dateTime,
        available = isAvailable,
        durationMinutes = durationMinutes,
    )

fun AppointmentSlot.toDto(): AppointmentSlotDto =
    AppointmentSlotDto(
        id = id,
        practitionerId = practitionerId,
        dateTime = dateTime,
        isAvailable = available,
        durationMinutes = durationMinutes,
    )

// ScheduleDayDto -> domain
fun ScheduleDayDto.toDomain(): ScheduleDay =
    ScheduleDay(
        dayOfWeek = dayOfWeek,
        openTime = openTime,
        closeTime = closeTime,
        isOpen = isOpen,
    )

fun ScheduleDay.toDto(): ScheduleDayDto =
    ScheduleDayDto(
        dayOfWeek = dayOfWeek,
        openTime = openTime,
        closeTime = closeTime,
        isOpen = isOpen,
    )

fun ScheduleDayDto.toScheduleEntry(): ScheduleEntry =
    ScheduleEntry(
        day = dayOfWeek,
        hours = if (isOpen) "$openTime – $closeTime" else "Fermé",
    )

fun ScheduleEntry.toScheduleDayDto(): ScheduleDayDto =
    ScheduleDayDto(
        dayOfWeek = day,
        openTime = hours,
        closeTime = "",
        isOpen = true,
    )

// PractitionerDto -> domain
fun PractitionerDto.toDomain(): Practitioner =
    Practitioner(
        id = id,
        firstName = firstName,
        lastName = lastName,
        specialty = specialty,
        clinicName = clinicName,
        address = address,
        city = city,
        phone = phone,
        email = email,
        latitude = latitude,
        longitude = longitude,
        acceptingNewPatients = acceptingNewPatients,
        availableSlots = availableSlots.map { it.toDomain() },
        schedule = schedule.map { it.toScheduleEntry() },
        paymentMethods = paymentMethods,
        diplomas = diplomas,
        isFavorite = isFavorite,
    )

fun Practitioner.toDto(): PractitionerDto =
    PractitionerDto(
        id = id,
        firstName = firstName,
        lastName = lastName,
        specialty = specialty,
        clinicName = clinicName,
        address = address,
        city = city,
        phone = phone,
        email = email,
        latitude = latitude,
        longitude = longitude,
        acceptingNewPatients = acceptingNewPatients,
        availableSlots = availableSlots.map { it.toDto() },
        schedule = schedule.map { it.toScheduleDayDto() },
        paymentMethods = paymentMethods,
        diplomas = diplomas,
        isFavorite = isFavorite,
    )

// AppointmentDto -> domain
fun AppointmentDto.toDomain(): Appointment =
    Appointment(
        id = id,
        practitionerId = practitionerId,
        practitionerName = practitionerName,
        clinicName = clinicName,
        specialty = specialty,
        dateTime = dateTime,
        status = AppointmentStatus.fromString(status),
        messageToDoctor = messageToDoctor,
        consultationReason = consultationReason,
    )

fun Appointment.toDto(): AppointmentDto =
    AppointmentDto(
        id = id,
        practitionerId = practitionerId,
        practitionerName = practitionerName,
        clinicName = clinicName,
        specialty = specialty,
        dateTime = dateTime,
        status = status.name,
        messageToDoctor = messageToDoctor,
        consultationReason = consultationReason,
    )

fun Appointment.toCreateRequest(): CreateAppointmentRequest =
    CreateAppointmentRequest(
        practitionerId = practitionerId,
        dateTime = dateTime,
        messageToDoctor = messageToDoctor,
        consultationReason = consultationReason,
    )

fun Appointment.toModifyRequest(): ModifyAppointmentRequest =
    ModifyAppointmentRequest(
        dateTime = dateTime,
        messageToDoctor = messageToDoctor,
        consultationReason = consultationReason,
    )

// UserDto -> domain
fun UserDto.toDomain(): User =
    User(
        id = id,
        firstName = firstName,
        lastName = lastName,
        email = email,
        phone = phone,
        gender = gender,
        dateOfBirth = dateOfBirth,
        cnsNumber = cnsNumber,
        profileType = ProfileType.fromString(profileType),
        language = language,
    )

fun User.toDto(): UserDto =
    UserDto(
        id = id,
        firstName = firstName,
        lastName = lastName,
        email = email,
        phone = phone,
        gender = gender,
        dateOfBirth = dateOfBirth,
        cnsNumber = cnsNumber,
        profileType = profileType.name,
        language = language,
    )

fun User.toUpdateRequest(): UpdateProfileRequest =
    UpdateProfileRequest(
        firstName = firstName,
        lastName = lastName,
        phone = phone,
        gender = gender,
        dateOfBirth = dateOfBirth,
        cnsNumber = cnsNumber,
        language = language,
    )

fun User.toRegisterRequest(password: String): RegisterRequest =
    RegisterRequest(
        firstName = firstName,
        lastName = lastName,
        email = email,
        password = password,
        phone = phone,
        gender = gender,
        dateOfBirth = dateOfBirth,
        cnsNumber = cnsNumber,
        profileType = profileType.name,
        language = language,
    )
