package lu.esklepios.app.domain.usecase

import lu.esklepios.app.domain.model.Practitioner
import lu.esklepios.app.domain.repository.PractitionerRepository

class SearchPractitionersUseCase(private val repository: PractitionerRepository) {
    suspend operator fun invoke(
        location: String,
        specialty: String,
        page: Int = 1,
        limit: Int = 20,
    ): Result<List<Practitioner>> {
        return repository.searchPractitioners(location, specialty, page, limit)
    }
}
