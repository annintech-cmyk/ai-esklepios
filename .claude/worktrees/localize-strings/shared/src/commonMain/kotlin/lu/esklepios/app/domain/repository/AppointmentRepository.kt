package lu.esklepios.app.domain.repository

import kotlinx.coroutines.flow.Flow
import lu.esklepios.app.domain.model.Appointment

interface AppointmentRepository {
    suspend fun createAppointment(appointment: Appointment): Result<Appointment>

    fun getAppointments(userId: String): Flow<List<Appointment>>

    suspend fun getUpcomingAppointments(userId: String): Result<List<Appointment>>

    suspend fun getPastAppointments(userId: String): Result<List<Appointment>>

    suspend fun modifyAppointment(appointment: Appointment): Result<Appointment>

    suspend fun cancelAppointment(id: String): Result<Unit>
}
