package lu.esklepios.app.domain.usecase

import lu.esklepios.app.domain.model.Appointment
import lu.esklepios.app.domain.repository.AppointmentRepository

class GetPastAppointmentsUseCase(private val repository: AppointmentRepository) {
    suspend operator fun invoke(userId: String): Result<List<Appointment>> {
        return repository.getPastAppointments(userId)
    }
}
