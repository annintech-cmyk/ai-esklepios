package lu.esklepios.app.view.dashboard.home

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import lu.esklepios.app.core.ui.components.PractitionerCard
import lu.esklepios.app.core.ui.components.PractitionerUiModel
import lu.esklepios.app.core.ui.components.SlotDayUiModel
import lu.esklepios.app.domain.model.Practitioner
import lu.esklepios.app.utils.DateUtil

@Composable
fun HomePractitionerCard(
    practitioner: Practitioner,
    onBook: (slotId: String) -> Unit,
    onSeeProfile: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiModel =
        PractitionerUiModel(
            id = practitioner.id,
            firstName = practitioner.firstName,
            lastName = practitioner.lastName,
            specialty = practitioner.specialty,
            clinic = practitioner.clinicName,
            address = "${practitioner.address}, ${practitioner.city}",
            isAcceptingNewPatients = practitioner.acceptingNewPatients,
            availableSlots =
                practitioner.availableSlots
                    .filter { it.available }
                    .groupBy { DateUtil.dateFromDateTime(it.dateTime) }
                    .map { (day, slots) ->
                        SlotDayUiModel(
                            dayLabel = day,
                            slots = slots.map { it.id },
                        )
                    },
        )
    PractitionerCard(
        practitioner = uiModel,
        onBook = onBook,
        onSeeProfile = onSeeProfile,
        modifier = modifier,
    )
}
