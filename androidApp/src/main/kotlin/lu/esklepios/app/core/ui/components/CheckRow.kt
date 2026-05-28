package lu.esklepios.app.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import lu.esklepios.app.core.ui.theme.BorderLight
import lu.esklepios.app.core.ui.theme.Dimens
import lu.esklepios.app.core.ui.theme.Primary
import lu.esklepios.app.core.ui.theme.TextHint

/**
 * A labeled value row with a colored check-circle badge on the leading edge.
 * Used in the booking confirmation flow to summarise appointment details.
 *
 * [accentColor] controls the circle background — use [Primary] for the new
 * appointment and [OldApptAmberIcon] for the appointment being replaced.
 */
@Composable
fun CheckRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    isLast: Boolean = false,
    accentColor: Color = Primary,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimens.paddingPlus, vertical = Dimens.paddingS),
            verticalAlignment = Alignment.Top,
        ) {
            Box(
                modifier =
                    Modifier
                        .size(Dimens.iconSizeCompact)
                        .background(accentColor, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                AppIcon(
                    imageVector = Icons.Filled.Check,
                    // a11y: decorative — labelled by adjacent Text
                    contentDescription = null,
                    tint = Color.White,
                    size = Dimens.iconSizeXxs,
                )
            }
            HSpace(Dimens.paddingS)
            Column {
                AppCaptionText(text = label, color = TextHint)
                AppFieldValueText(text = value)
            }
        }
        if (!isLast) {
            HorizontalDivider(
                thickness = Dimens.borderHairline,
                color = BorderLight,
                modifier = Modifier.padding(horizontal = Dimens.paddingPlus),
            )
        }
    }
}
