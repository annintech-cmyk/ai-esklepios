package lu.esklepios.app.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import lu.esklepios.app.R
import lu.esklepios.app.core.ui.theme.*

@Composable
fun MapPreviewCard(
    address: String,
    onOpenMaps: () -> Unit,
    modifier: Modifier = Modifier
) {
    AppCard(
        modifier = modifier.fillMaxWidth(),
        onClick = onOpenMaps
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(Dimens.mapPreviewHeight)
                .background(brush = Gradients.primaryBrush)
        ) {
            // Grid overlay lines
            Column(modifier = Modifier.fillMaxSize()) {
                repeat(6) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(bottom = Dimens.borderThin)
                            .background(Color.White.copy(alpha = 0.05f))
                    )
                }
            }
            // Vertical grid lines
            Row(modifier = Modifier.fillMaxSize()) {
                repeat(4) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(1f)
                            .padding(end = Dimens.borderThin)
                            .background(Color.White.copy(alpha = 0.05f))
                    )
                }
            }
            // Location pin icon
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(Dimens.iconButtonSize)
                        .background(color = Color.White, shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    AppIcon(
                        imageVector = Icons.Filled.LocationOn,
                        contentDescription = null, // a11y: decorative — labelled by adjacent Text
                        tint = Danger,
                        size = Dimens.iconSizeLg
                    )
                }
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.paddingL),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                AppCaptionText(
                    text = stringResource(R.string.label_location),
                    color = TextSecondary
                )
                AppBodyText(
                    text = address,
                    color = TextPrimary
                )
            }
            HSpace(Dimens.paddingM)
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(Dimens.radiusSm))
                    .background(PrimaryLight)
                    .clickable(onClick = onOpenMaps)
                    .padding(horizontal = Dimens.paddingM, vertical = Dimens.paddingS)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Dimens.paddingXS)
                ) {
                    AppIcon(
                        imageVector = Icons.Filled.Map,
                        contentDescription = null, // a11y: decorative — labelled by adjacent Text
                        tint = Primary,
                        size = Dimens.iconSizeSm
                    )
                    AppCaptionText(
                        text = stringResource(R.string.action_open_maps),
                        color = Primary
                    )
                }
            }
        }
    }
}
