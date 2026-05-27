package lu.esklepios.app.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import lu.esklepios.app.core.ui.theme.*
import lu.esklepios.app.util.AvailabilityFilter

@Composable
fun AvailabilityChip(
    filter: AvailabilityFilter,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(Dimens.radiusPill)
    val (bgColor, textColor, borderColor) =
        when (filter) {
            AvailabilityFilter.TODAY ->
                Triple(
                    if (isSelected) SuccessBg else Surface,
                    if (isSelected) Success else TextSecondary,
                    if (isSelected) Success else BorderColor,
                )
            AvailabilityFilter.WITHIN_THREE_DAYS ->
                Triple(
                    if (isSelected) WarningBg else Surface,
                    if (isSelected) Warning else TextSecondary,
                    if (isSelected) Warning else BorderColor,
                )
            AvailabilityFilter.OPEN_TO_NEW_PATIENTS ->
                Triple(
                    if (isSelected) PrimaryLight else Surface,
                    if (isSelected) Primary else TextSecondary,
                    if (isSelected) Primary else BorderColor,
                )
        }

    Box(
        modifier =
            Modifier
                .height(Dimens.filterChipHeight)
                .clip(shape)
                .background(color = bgColor, shape = shape)
                .border(width = Dimens.borderThin, color = borderColor, shape = shape)
                .clickable(onClick = onClick)
                .padding(horizontal = Dimens.paddingM),
        contentAlignment = Alignment.Center,
    ) {
        AppCaptionText(
            text = label,
            color = textColor,
        )
    }
}
