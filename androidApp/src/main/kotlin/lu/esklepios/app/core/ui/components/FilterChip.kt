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
import androidx.compose.ui.graphics.vector.ImageVector
import lu.esklepios.app.core.ui.theme.*

@Composable
fun FilterChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    icon: ImageVector? = null,
) {
    val shape = RoundedCornerShape(Dimens.radiusPill)
    val bgColor = if (isSelected) PrimaryLight else Surface
    val textColor = if (isSelected) Primary else TextSecondary
    val borderColor = if (isSelected) Primary else BorderColor

    Row(
        modifier =
            Modifier
                .height(Dimens.filterChipHeight)
                .clip(shape)
                .background(color = bgColor, shape = shape)
                .border(width = Dimens.borderThin, color = borderColor, shape = shape)
                .clickable(onClick = onClick)
                .padding(horizontal = Dimens.paddingM, vertical = Dimens.paddingXS),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Dimens.paddingXS),
    ) {
        if (icon != null) {
            AppIcon(
                imageVector = icon,
                contentDescription = null, // a11y: decorative — labelled by adjacent Text
                tint = textColor,
                size = Dimens.iconSizeSm,
            )
        }
        AppLabelText(
            text = text,
            color = textColor,
        )
    }
}
