package lu.esklepios.app.core.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import lu.esklepios.app.R
import lu.esklepios.app.core.ui.theme.BorderColor
import lu.esklepios.app.core.ui.theme.BorderLight
import lu.esklepios.app.core.ui.theme.Dimens
import lu.esklepios.app.core.ui.theme.Primary
import lu.esklepios.app.core.ui.theme.PrimaryLight
import lu.esklepios.app.core.ui.theme.Surface
import lu.esklepios.app.core.ui.theme.TextHint
import lu.esklepios.app.core.ui.theme.TextPrimary
import lu.esklepios.app.core.ui.theme.TextSecondary
import lu.esklepios.app.utils.DateUtil
import java.time.LocalDate
import java.util.Locale
import java.time.format.TextStyle as JavaTextStyle

data class PractitionerUiModel(
    val id: String,
    val firstName: String,
    val lastName: String,
    val specialty: String,
    val clinic: String,
    val address: String,
    val isAcceptingNewPatients: Boolean,
    val availableSlots: List<SlotDayUiModel>,
) {
    val initials: String get() = "${firstName.firstOrNull() ?: ""}${lastName.firstOrNull() ?: ""}"
    val fullName: String get() = "Dr. $firstName $lastName"
}

data class SlotDayUiModel(
    // ISO date "2026-05-26"
    val dayLabel: String,
    // slot IDs
    val slots: List<String>,
)

@Composable
fun PractitionerCard(
    practitioner: PractitionerUiModel,
    onBook: (slotId: String) -> Unit,
    onSeeProfile: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var weekOffset by remember { mutableIntStateOf(0) }
    var expanded by remember { mutableStateOf(false) }

    val today = DateUtil.today()
    val startDay = today.plusDays((weekOffset * 5).toLong())
    val weekDays = (0..4).map { startDay.plusDays(it.toLong()) }

    val lastDay = weekDays.last()
    val monthLabel =
        if (startDay.month == lastDay.month) {
            "${startDay.month.getDisplayName(JavaTextStyle.FULL, Locale.ENGLISH)} ${startDay.year}"
        } else {
            "${startDay.month.getDisplayName(JavaTextStyle.SHORT, Locale.ENGLISH)} / " +
                "${lastDay.month.getDisplayName(JavaTextStyle.SHORT, Locale.ENGLISH)} ${lastDay.year}"
        }

    val slotsByDate: Map<String, List<Pair<String, String>>> =
        practitioner.availableSlots
            .groupBy { it.dayLabel }
            .mapValues { (_, daySlots) ->
                daySlots.flatMap { day ->
                    day.slots.map { id -> Pair(id, DateUtil.extractSlotTime(id)) }
                }
            }

    val maxSlotsInView = weekDays.maxOf { day -> slotsByDate[day.toString()]?.size ?: 0 }
    val showMoreEnabled = maxSlotsInView > 4
    val noSlotsInView = weekDays.all { day -> (slotsByDate[day.toString()]?.size ?: 0) == 0 }

    val nextAvailableDate: LocalDate? =
        if (noSlotsInView) {
            practitioner.availableSlots
                .map { it.dayLabel }
                .mapNotNull { runCatching { LocalDate.parse(it) }.getOrNull() }
                .filter { !it.isBefore(today) }
                .minOrNull()
        } else {
            null
        }

    AppCard(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // ── Header (clickable → onSeeProfile) ─────────────────────────────
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .clickable { onSeeProfile() }
                        .padding(
                            start = Dimens.paddingL,
                            end = Dimens.paddingL,
                            top = Dimens.paddingL,
                        ),
                verticalAlignment = Alignment.Top,
            ) {
                AvatarCircle(
                    initials = practitioner.initials,
                    size = Dimens.avatarSizeLg,
                )
                HSpace(Dimens.paddingM)
                Column(Modifier.weight(1f)) {
                    AppSubtitleText(
                        text = practitioner.fullName,
                        color = TextPrimary,
                    )
                    AppCaptionText(
                        text = practitioner.specialty,
                        color = Primary,
                        maxLines = 1,
                    )
                }
            }

            VSpace(Dimens.paddingXS)

            // ── Clinic name ───────────────────────────────────────────────────
            Row(
                modifier = Modifier.padding(horizontal = Dimens.paddingL),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                AppIcon(
                    imageVector = Icons.Filled.Business,
                    // a11y: decorative — labelled by adjacent Text
                    contentDescription = null,
                    tint = TextSecondary,
                    size = Dimens.iconSizeSm,
                )
                HSpace(Dimens.paddingXXS)
                AppCaptionText(
                    text = practitioner.clinic,
                    color = TextSecondary,
                    maxLines = 1,
                )
            }

            VSpace(Dimens.paddingXXS)

            // ── Address ───────────────────────────────────────────────────────
            Row(
                modifier = Modifier.padding(horizontal = Dimens.paddingL),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                AppIcon(
                    imageVector = Icons.Filled.LocationOn,
                    // a11y: decorative — labelled by adjacent Text
                    contentDescription = null,
                    tint = TextSecondary,
                    size = Dimens.iconSizeSm,
                )
                HSpace(Dimens.paddingXXS)
                AppCaptionText(
                    text = practitioner.address,
                    color = TextSecondary,
                    maxLines = 1,
                )
            }

            VSpace(Dimens.paddingM)

            // ── Slot strip — edge-to-edge, light-blue background ──────────────
            Box(modifier = Modifier.fillMaxWidth().background(PrimaryLight)) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // ── Month label ───────────────────────────────────────────
                    // fontSizeTiny/Xxs texts in this section are intentionally small for the
                    // compact calendar grid — no matching typography wrapper exists at this size.
                    Text(
                        text = monthLabel,
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(top = Dimens.paddingS),
                        fontSize = Dimens.fontSizeXxs,
                        fontWeight = FontWeight.SemiBold,
                        color = TextSecondary,
                        textAlign = TextAlign.Center,
                    )

                    // ── Date area with flanking chevrons ──────────────────────
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        AppIconButton(
                            icon = Icons.Filled.ChevronLeft,
                            contentDescription = stringResource(R.string.card_schedule_prev),
                            onClick = { if (weekOffset > 0) weekOffset-- },
                            enabled = weekOffset > 0,
                            tint = TextSecondary,
                            iconSize = Dimens.iconSizeLg,
                            modifier =
                                Modifier
                                    .size(Dimens.iconButtonSize)
                                    .alpha(if (weekOffset > 0) 1f else 0.35f),
                        )

                        Row(
                            modifier =
                                Modifier
                                    .weight(1f)
                                    .padding(vertical = Dimens.paddingS),
                        ) {
                            weekDays.forEach { day ->
                                val isToday = day == today
                                val dayAbbr =
                                    day.dayOfWeek
                                        .getDisplayName(JavaTextStyle.SHORT, Locale.ENGLISH)
                                        .take(3)

                                Column(
                                    modifier = Modifier.weight(1f),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(Dimens.paddingXXS),
                                ) {
                                    Text(
                                        text = dayAbbr,
                                        fontSize = Dimens.fontSizeTiny,
                                        color = TextHint,
                                        textAlign = TextAlign.Center,
                                    )
                                    Box(
                                        modifier =
                                            Modifier
                                                .size(Dimens.scheduleDayCircleSize)
                                                .background(
                                                    color = if (isToday) Primary else Color.Transparent,
                                                    shape = CircleShape,
                                                ),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        Text(
                                            text = day.dayOfMonth.toString(),
                                            fontSize = Dimens.fontSizeTiny,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isToday) Color.White else TextPrimary,
                                            textAlign = TextAlign.Center,
                                        )
                                    }
                                }
                            }
                        }

                        AppIconButton(
                            icon = Icons.Filled.ChevronRight,
                            contentDescription = stringResource(R.string.card_schedule_next),
                            onClick = { weekOffset++ },
                            tint = TextSecondary,
                            iconSize = Dimens.iconSizeLg,
                            modifier = Modifier.size(Dimens.iconButtonSize),
                        )
                    }

                    // ── Slot buttons area (overlay covers this when noSlotsInView) ──
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = Dimens.paddingS),
                            verticalAlignment = Alignment.Top,
                        ) {
                            // Fixed-width spacers align slot columns with day header columns above
                            HSpace(Dimens.iconButtonSize)
                            Row(modifier = Modifier.weight(1f)) {
                                weekDays.forEach { day ->
                                    val dateKey = day.toString()
                                    val allSlots = slotsByDate[dateKey] ?: emptyList()

                                    val slotsToDisplay: List<Pair<String, String>?> =
                                        if (expanded) {
                                            if (allSlots.size >= 4) {
                                                allSlots
                                            } else {
                                                allSlots + List(4 - allSlots.size) { null }
                                            }
                                        } else {
                                            val real = allSlots.take(4)
                                            real + List(maxOf(0, 4 - real.size)) { null }
                                        }

                                    Column(
                                        modifier = Modifier.weight(1f),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(Dimens.paddingXXS),
                                    ) {
                                        slotsToDisplay.forEach { slot ->
                                            if (slot != null) {
                                                OutlinedButton(
                                                    onClick = { onBook(slot.first) },
                                                    border = BorderStroke(Dimens.borderThin, Primary),
                                                    shape = RoundedCornerShape(Dimens.radiusXs),
                                                    contentPadding = PaddingValues(Dimens.paddingNone),
                                                    colors =
                                                        ButtonDefaults.outlinedButtonColors(
                                                            containerColor = Surface,
                                                            contentColor = Primary,
                                                        ),
                                                    modifier =
                                                        Modifier
                                                            .fillMaxWidth()
                                                            .padding(horizontal = Dimens.borderThin),
                                                ) {
                                                    Text(
                                                        text = slot.second,
                                                        fontSize = Dimens.fontSizeTiny,
                                                        fontWeight = FontWeight.Bold,
                                                        color = Primary,
                                                        textAlign = TextAlign.Center,
                                                    )
                                                }
                                            } else {
                                                OutlinedButton(
                                                    onClick = {},
                                                    enabled = false,
                                                    border =
                                                        BorderStroke(
                                                            Dimens.borderThin,
                                                            TextHint.copy(alpha = 0.3f),
                                                        ),
                                                    shape = RoundedCornerShape(Dimens.radiusXs),
                                                    contentPadding = PaddingValues(Dimens.paddingNone),
                                                    colors =
                                                        ButtonDefaults.outlinedButtonColors(
                                                            disabledContainerColor = Color.Transparent,
                                                            disabledContentColor = TextHint,
                                                        ),
                                                    modifier =
                                                        Modifier
                                                            .fillMaxWidth()
                                                            .padding(horizontal = Dimens.borderThin),
                                                ) {
                                                    Text(
                                                        text = stringResource(R.string.home_schedule_no_slots),
                                                        fontSize = Dimens.fontSizeTiny,
                                                        fontWeight = FontWeight.Bold,
                                                        color = TextHint,
                                                        textAlign = TextAlign.Center,
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            HSpace(Dimens.iconButtonSize)
                        }

                        // Overlay card when no slots visible in this week
                        if (noSlotsInView) {
                            Box(
                                modifier =
                                    Modifier
                                        .matchParentSize()
                                        .background(PrimaryLight.copy(alpha = 0.92f)),
                                contentAlignment = Alignment.Center,
                            ) {
                                Card(
                                    shape = RoundedCornerShape(Dimens.radiusMd),
                                    colors = CardDefaults.cardColors(containerColor = Surface),
                                    elevation =
                                        CardDefaults.cardElevation(
                                            defaultElevation = Dimens.cardElevation,
                                        ),
                                ) {
                                    Column(
                                        modifier =
                                            Modifier.padding(
                                                horizontal = Dimens.paddingL,
                                                vertical = Dimens.paddingS,
                                            ),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                    ) {
                                        if (nextAvailableDate != null) {
                                            AppCaptionText(
                                                text = stringResource(R.string.card_next_appointment_on),
                                                color = TextSecondary,
                                                textAlign = TextAlign.Center,
                                            )
                                            AppCaptionText(
                                                text =
                                                    DateUtil.formatIsoDate(
                                                        nextAvailableDate.toString(),
                                                        DateUtil.PATTERN_DISPLAY_LONG,
                                                    ),
                                                color = TextPrimary,
                                                textAlign = TextAlign.Center,
                                            )
                                        } else {
                                            AppCaptionText(
                                                text = stringResource(R.string.card_slots_unavailable),
                                                color = TextPrimary,
                                                textAlign = TextAlign.Center,
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            VSpace(Dimens.paddingM)
            HorizontalDivider(color = BorderLight)

            // ── Show more / week-nav row ───────────────────────────────────────
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Dimens.paddingXXS, vertical = Dimens.paddingS),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                AppIconButton(
                    icon = Icons.Filled.ChevronLeft,
                    contentDescription = stringResource(R.string.card_schedule_prev),
                    onClick = { if (weekOffset > 0) weekOffset-- },
                    enabled = weekOffset > 0,
                    tint = TextSecondary,
                    iconSize = Dimens.iconSizeLg,
                    modifier =
                        Modifier
                            .size(Dimens.iconButtonSize)
                            .alpha(if (weekOffset > 0) 1f else 0.35f),
                )

                OutlinedButton(
                    onClick = { expanded = !expanded },
                    enabled = showMoreEnabled,
                    border = BorderStroke(Dimens.borderThin, BorderColor),
                    shape = RoundedCornerShape(Dimens.radiusXl),
                    contentPadding =
                        PaddingValues(
                            horizontal = Dimens.paddingM,
                            vertical = Dimens.paddingXXS,
                        ),
                    modifier =
                        Modifier
                            .weight(1f)
                            .alpha(if (showMoreEnabled) 1f else 0.4f),
                ) {
                    AppLabelText(
                        text =
                            stringResource(
                                if (expanded) {
                                    R.string.card_show_fewer_schedules
                                } else {
                                    R.string.card_show_more_schedules
                                },
                            ),
                        color = TextPrimary,
                        textAlign = TextAlign.Center,
                    )
                }

                AppIconButton(
                    icon = Icons.Filled.ChevronRight,
                    contentDescription = stringResource(R.string.card_schedule_next),
                    onClick = { weekOffset++ },
                    tint = TextSecondary,
                    iconSize = Dimens.iconSizeLg,
                    modifier = Modifier.size(Dimens.iconButtonSize),
                )
            }

            // ── View Profile button ───────────────────────────────────────────
            OutlinedButton(
                onClick = onSeeProfile,
                border = BorderStroke(Dimens.borderThin, Primary),
                shape = RoundedCornerShape(Dimens.radiusPill),
                contentPadding =
                    PaddingValues(
                        horizontal = Dimens.paddingXL,
                        vertical = Dimens.paddingXXS,
                    ),
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Dimens.paddingL)
                        .height(Dimens.filterChipHeight),
            ) {
                AppLabelText(
                    text = stringResource(R.string.action_see_profile),
                    color = Primary,
                )
            }

            VSpace(Dimens.paddingM)
        }
    }
}
