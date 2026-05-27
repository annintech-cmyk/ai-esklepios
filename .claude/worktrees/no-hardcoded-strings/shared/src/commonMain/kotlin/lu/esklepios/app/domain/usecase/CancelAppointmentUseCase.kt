package lu.esklepios.app.domain.usecase

import lu.esklepios.app.domain.repository.AppointmentRepository

class CancelAppointmentUseCase(private val repository: AppointmentRepository) {
    suspend operator fun invoke(id: String): Result<Unit> {
        return repository.cancelAppointment(id)
    }
}
