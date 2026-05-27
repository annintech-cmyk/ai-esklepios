package lu.esklepios.app.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class ScheduleDay(
    val dayOfWeek: String,
    val openTime: String,
    val closeTime: String,
    val isOpen: Boolean,
)
