package lu.esklepios.app.data.repository

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import lu.esklepios.app.data.network.ApiService
import lu.esklepios.app.data.network.toDomain
import lu.esklepios.app.db.ESklepiosDatabase
import lu.esklepios.app.domain.model.AppointmentSlot
import lu.esklepios.app.domain.model.Practitioner
import lu.esklepios.app.domain.model.ScheduleDay
import lu.esklepios.app.domain.repository.PractitionerRepository

class PractitionerRepositoryImpl(
    private val apiService: ApiService,
    private val database: ESklepiosDatabase
) : PractitionerRepository {

    private val json = Json { ignoreUnknownKeys = true }

    override suspend fun searchPractitioners(
        location: String,
        specialty: String,
        page: Int,
        limit: Int
    ): Result<List<Practitioner>> {
        return apiService.searchPractitioners(location, specialty, page, limit)
            .map { dtos ->
                val practitioners = dtos.map { it.toDomain() }
                // Cache results in database
                practitioners.forEach { cachePractitioner(it) }
                practitioners
            }
    }

    override suspend fun getPractitionerById(id: String): Result<Practitioner> {
        // Check cache first
        val cached = database.practitionersQueries.selectById(id).executeAsOneOrNull()
        if (cached != null) {
            return Result.success(cached.toDomain())
        }
        // Fall back to API
        return apiService.getPractitioner(id).map { dto ->
            val practitioner = dto.toDomain()
            cachePractitioner(practitioner)
            practitioner
        }
    }

    override suspend fun toggleFavorite(practitionerId: String): Result<Unit> {
        return try {
            val entity = database.practitionersQueries.selectById(practitionerId).executeAsOneOrNull()
            if (entity != null) {
                val newFavoriteState = if (entity.isFavorite == 0L) 1L else 0L
                database.practitionersQueries.updateFavorite(newFavoriteState, practitionerId)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun cachePractitioner(practitioner: Practitioner) {
        database.practitionersQueries.insertOrReplace(
            id = practitioner.id,
            firstName = practitioner.firstName,
            lastName = practitioner.lastName,
            specialty = practitioner.specialty,
            clinicName = practitioner.clinicName,
            address = practitioner.address,
            city = practitioner.city,
            phone = practitioner.phone,
            email = practitioner.email,
            latitude = practitioner.latitude,
            longitude = practitioner.longitude,
            acceptingNewPatients = if (practitioner.acceptingNewPatients) 1L else 0L,
            availableSlotsJson = json.encodeToString(practitioner.availableSlots),
            scheduleJson = json.encodeToString(practitioner.schedule),
            paymentMethodsJson = json.encodeToString(practitioner.paymentMethods),
            diplomasJson = json.encodeToString(practitioner.diplomas),
            isFavorite = if (practitioner.isFavorite) 1L else 0L
        )
    }

    private fun lu.esklepios.app.db.PractitionerEntity.toDomain(): Practitioner {
        val slots = try {
            json.decodeFromString<List<AppointmentSlot>>(availableSlotsJson)
        } catch (e: Exception) {
            emptyList()
        }
        val schedule = try {
            json.decodeFromString<List<ScheduleDay>>(scheduleJson)
        } catch (e: Exception) {
            emptyList()
        }
        val paymentMethods = try {
            json.decodeFromString<List<String>>(paymentMethodsJson)
        } catch (e: Exception) {
            emptyList()
        }
        val diplomas = try {
            json.decodeFromString<List<String>>(diplomasJson)
        } catch (e: Exception) {
            emptyList()
        }
        return Practitioner(
            id = id,
            firstName = firstName,
            lastName = lastName,
            specialty = specialty,
            clinicName = clinicName,
            address = address,
            city = city,
            phone = phone,
            email = email,
            latitude = latitude,
            longitude = longitude,
            acceptingNewPatients = acceptingNewPatients != 0L,
            availableSlots = slots,
            schedule = schedule,
            paymentMethods = paymentMethods,
            diplomas = diplomas,
            isFavorite = isFavorite != 0L
        )
    }
}
