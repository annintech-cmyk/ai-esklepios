package lu.esklepios.app.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import lu.esklepios.app.core.ui.theme.*

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
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimens.paddingPlus, vertical = Dimens.paddingS),
            verticalAlignment = Alignment.Top,
        ) {
            Box(
                modifier = Modifier
                    .size(Dimens.iconSizeCompact)
                    .background(accentColor, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                AppIcon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = null, // a11y: decorative — labelled by adjacent Text
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

@Preview(showBackground = true)
@Composable
private fun CheckRowPreviewPrimary() {
    AppCard {
        CheckRow(label = "Reason", value = "Consultation")
        CheckRow(label = "Date & time", value = "Tue, May 26 · 09:00")
        CheckRow(label = "Institute", value = "Al Esch Medical Center", isLast = true)
    }
}

@Preview(showBackground = true)
@Composable
private fun CheckRowPreviewAmber() {
    AppCard {
        CheckRow(label = "Reason", value = "Follow-up", accentColor = OldApptAmberIcon)
        CheckRow(label = "Institute", value = "Centre Médical", accentColor = OldApptAmberIcon, isLast = true)
    }
}