package lu.esklepios.app.debug

import lu.esklepios.app.domain.model.Practitioner
import lu.esklepios.app.domain.repository.PractitionerRepository

/**
 * In-memory fake implementation used during development.
 * Replace [AndroidModule] binding with the real [PractitionerRepositoryImpl]
 * when the API is available.
 */
class FakePractitionerRepository : PractitionerRepository {
    override suspend fun searchPractitioners(
        location: String,
        specialty: String,
        page: Int,
        limit: Int,
    ): Result<List<Practitioner>> {
        val results =
            DummyPractitioners.all.filter { practitioner ->
                val matchesLocation =
                    location.isBlank() ||
                        practitioner.city.contains(location, ignoreCase = true) ||
                        practitioner.address.contains(location, ignoreCase = true)
                val matchesSpecialty =
                    specialty.isBlank() ||
                        practitioner.specialty.contains(specialty, ignoreCase = true) ||
                        practitioner.fullName.contains(specialty, ignoreCase = true)
                matchesLocation && matchesSpecialty
            }
        return Result.success(results)
    }

    override suspend fun getPractitionerById(id: String): Result<Practitioner> {
        val practitioner = DummyPractitioners.all.find { it.id == id }
        return if (practitioner != null) {
            Result.success(practitioner)
        } else {
            Result.failure(NoSuchElementException("Practitioner $id not found"))
        }
    }

    override suspend fun toggleFavorite(practitionerId: String): Result<Unit> = Result.success(Unit)
}
