package lu.esklepios.app.domain.usecase

import lu.esklepios.app.domain.repository.PractitionerRepository

class ToggleFavoriteUseCase(private val repository: PractitionerRepository) {
    suspend operator fun invoke(practitionerId: String): Result<Unit> {
        return repository.toggleFavorite(practitionerId)
    }
}
