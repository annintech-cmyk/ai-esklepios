package lu.esklepios.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import lu.esklepios.app.R
import lu.esklepios.app.ui.theme.*

data class PractitionerUiModel(
    val id: String,
    val firstName: String,
    val lastName: String,
    val specialty: String,
    val clinic: String,
    val address: String,
    val isAcceptingNewPatients: Boolean,
    val availableSlots: List<SlotDayUiModel>
) {
    val initials: String get() = "${firstName.firstOrNull() ?: ""}${lastName.firstOrNull() ?: ""}"
    val fullName: String get() = "Dr. $firstName $lastName"
}

data class SlotDayUiModel(
    val dayLabel: String,
    val slots: List<String>
)

@Composable
fun PractitionerCard(
    practitioner: PractitionerUiModel,
    onSeeProfile: () -> Unit,
    onBook: (slotId: String) -> Unit,
    modifier: Modifier = Modifier
) {
    AppCard(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(Dimens.paddingL)) {
            // Header row: avatar + name + badge
            Row(verticalAlignment = Alignment.Top) {
                AvatarCircle(
                    initials = practitioner.initials,
                    size = Dimens.avatarSizeLg,
                    fontSize = 20.sp
                )
                Spacer(modifier = Modifier.width(Dimens.paddingM))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = practitioner.fullName,
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = practitioner.specialty,
                        style = MaterialTheme.typography.bodySmall.copy(color = Primary)
                    )
                    Text(
                        text = practitioner.clinic,
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                    )
                }
                if (practitioner.isAcceptingNewPatients) {
                    Box(
                        modifier = Modifier
                            .background(color = SuccessBg, shape = RoundedCornerShape(Dimens.radiusPill))
                            .padding(horizontal = Dimens.paddingS, vertical = 2.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.label_accepting_patients),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Success,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(Dimens.paddingM))

            // Address row
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.LocationOn,
                    contentDescription = null,
                    tint = TextSecondary,
                    modifier = Modifier.size(Dimens.iconSizeSm)
                )
                Spacer(modifier = Modifier.width(Dimens.paddingXS))
                Text(
                    text = practitioner.address,
                    style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                )
            }

            Spacer(modifier = Modifier.height(Dimens.paddingM))
            Divider(color = BorderLight)
            Spacer(modifier = Modifier.height(Dimens.paddingM))

            // Available slots
            if (practitioner.availableSlots.isNotEmpty()) {
                Text(
                    text = stringResource(R.string.label_available_slots),
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = TextSecondary,
                        fontWeight = FontWeight.SemiBold
                    )
                )
                Spacer(modifier = Modifier.height(Dimens.paddingS))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(Dimens.paddingM)
                ) {
                    items(practitioner.availableSlots) { daySlot ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(Dimens.paddingXS)
                        ) {
                            Text(
                                text = daySlot.dayLabel,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = TextSecondary,
                                    fontSize = 10.sp
                                )
                            )
                            daySlot.slots.forEach { slot ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(Dimens.radiusSm))
                                        .background(PrimaryLight)
                                        .border(
                                            width = 1.dp,
                                            color = Primary.copy(alpha = 0.3f),
                                            shape = RoundedCornerShape(Dimens.radiusSm)
                                        )
                                        .clickable { onBook(slot) }
                                        .padding(
                                            horizontal = Dimens.paddingS,
                                            vertical = Dimens.paddingXS
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = slot,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = Primary,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(Dimens.paddingM))
            }

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Dimens.paddingM)
            ) {
                SecondaryButton(
                    text = stringResource(R.string.action_see_profile),
                    onClick = onSeeProfile,
                    modifier = Modifier.weight(1f)
                )
                PrimaryButton(
                    text = stringResource(R.string.action_book),
                    onClick = { practitioner.availableSlots.firstOrNull()?.slots?.firstOrNull()?.let { onBook(it) } },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
