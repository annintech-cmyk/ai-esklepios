package lu.esklepios.app.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import lu.esklepios.app.data.network.ApiService
import lu.esklepios.app.data.network.toCreateRequest
import lu.esklepios.app.data.network.toDomain
import lu.esklepios.app.data.network.toModifyRequest
import lu.esklepios.app.db.ESklepiosDatabase
import lu.esklepios.app.domain.model.Appointment
import lu.esklepios.app.domain.model.AppointmentStatus
import lu.esklepios.app.domain.repository.AppointmentRepository

class AppointmentRepositoryImpl(
    private val apiService: ApiService,
    private val database: ESklepiosDatabase,
) : AppointmentRepository {
    override suspend fun createAppointment(appointment: Appointment): Result<Appointment> {
        return apiService.createAppointment(appointment.toCreateRequest())
            .map { dto ->
                val created = dto.toDomain()
                cacheAppointment(created)
                created
            }
    }

    override fun getAppointments(userId: String): Flow<List<Appointment>> {
        return database.appointmentsQueries.selectAll()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { entities -> entities.map { it.toDomain() } }
    }

    override suspend fun getUpcomingAppointments(userId: String): Result<List<Appointment>> {
        return runCatching {
            val upcoming =
                database.appointmentsQueries.selectUpcoming()
                    .executeAsList()
                    .map { it.toDomain() }

            // Refresh from API in background
            apiService.getAppointments(userId).onSuccess { dtos ->
                dtos.forEach { dto -> cacheAppointment(dto.toDomain()) }
            }

            upcoming
        }
    }

    override suspend fun getPastAppointments(userId: String): Result<List<Appointment>> {
        return runCatching {
            database.appointmentsQueries.selectPast()
                .executeAsList()
                .map { it.toDomain() }
        }
    }

    override suspend fun modifyAppointment(appointment: Appointment): Result<Appointment> {
        return apiService.modifyAppointment(appointment.id, appointment.toModifyRequest())
            .map { dto ->
                val modified = dto.toDomain()
                cacheAppointment(modified)
                modified
            }
    }

    override suspend fun cancelAppointment(id: String): Result<Unit> {
        return apiService.cancelAppointment(id).onSuccess {
            database.appointmentsQueries.updateStatus(
                status = AppointmentStatus.CANCELLED.name,
                id = id,
            )
        }
    }

    private fun cacheAppointment(appointment: Appointment) {
        database.appointmentsQueries.insertOrReplace(
            id = appointment.id,
            practitionerId = appointment.practitionerId,
            practitionerName = appointment.practitionerName,
            clinicName = appointment.clinicName,
            specialty = appointment.specialty,
            dateTime = appointment.dateTime,
            status = appointment.status.name,
            messageToDoctor = appointment.messageToDoctor,
            consultationReason = appointment.consultationReason,
        )
    }

    private fun lu.esklepios.app.db.AppointmentEntity.toDomain(): Appointment =
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
}
