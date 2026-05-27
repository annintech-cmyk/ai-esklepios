package lu.esklepios.app.core.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import lu.esklepios.app.core.ui.theme.Dimens
import lu.esklepios.app.core.ui.theme.PrimaryMid
import lu.esklepios.app.core.ui.theme.TextPrimary
import lu.esklepios.app.core.ui.theme.TextSecondary

@Composable
fun InfoRow(
    icon: ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(vertical = Dimens.paddingXS),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null, // a11y: decorative — labelled by adjacent Text
            tint = PrimaryMid,
            modifier = Modifier.size(Dimens.iconSizeMd),
        )
        Spacer(modifier = Modifier.width(Dimens.paddingM))
        Column(modifier = Modifier.weight(1f)) {
            if (label.isNotEmpty()) {
                AppCaptionText(
                    text = label,
                    color = TextSecondary,
                )
            }
            AppBodyText(
                text = value,
                color = TextPrimary,
            )
        }
    }
}
