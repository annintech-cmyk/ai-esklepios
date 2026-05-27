package lu.esklepios.app.domain.usecase

import lu.esklepios.app.domain.model.Practitioner
import lu.esklepios.app.domain.repository.PractitionerRepository

class GetPractitionerDetailUseCase(private val repository: PractitionerRepository) {
    suspend operator fun invoke(id: String): Result<Practitioner> {
        return repository.getPractitionerById(id)
    }
}
