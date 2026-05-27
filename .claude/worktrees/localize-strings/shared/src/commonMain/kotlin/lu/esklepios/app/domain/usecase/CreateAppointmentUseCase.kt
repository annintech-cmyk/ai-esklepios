package lu.esklepios.app.domain.usecase

import lu.esklepios.app.domain.model.Appointment
import lu.esklepios.app.domain.repository.AppointmentRepository

class CreateAppointmentUseCase(private val repository: AppointmentRepository) {
    suspend operator fun invoke(appointment: Appointment): Result<Appointment> {
        return repository.createAppointment(appointment)
    }
}
