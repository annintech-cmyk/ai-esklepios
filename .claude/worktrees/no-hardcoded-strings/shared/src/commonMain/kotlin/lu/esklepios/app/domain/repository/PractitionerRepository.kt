package lu.esklepios.app.domain.repository

import lu.esklepios.app.domain.model.Practitioner

interface PractitionerRepository {
    suspend fun searchPractitioners(
        location: String,
        specialty: String,
        page: Int,
        limit: Int
    ): Result<List<Practitioner>>

    suspend fun getPractitionerById(id: String): Result<Practitioner>

    suspend fun toggleFavorite(practitionerId: String): Result<Unit>
}
