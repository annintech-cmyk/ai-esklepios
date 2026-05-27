package lu.esklepios.app.core.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import lu.esklepios.app.core.ui.theme.Dimens
import lu.esklepios.app.core.ui.theme.Primary
import lu.esklepios.app.core.ui.theme.TextPrimary
import lu.esklepios.app.core.ui.theme.TextSecondary

@Composable
fun EmptyStateView(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    title: String,
    subtitle: String,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(Dimens.paddingXXXL),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier =
                Modifier
                    .size(Dimens.emptyIconSize)
                    .padding(Dimens.paddingS),
            contentAlignment = Alignment.Center,
        ) {
            AppIcon(
                imageVector = icon,
                contentDescription = null, // a11y: decorative — labelled by adjacent Text
                tint = Primary.copy(alpha = 0.3f),
                size = Dimens.emptyIconSmSize,
            )
        }
        VSpace(Dimens.paddingL)
        AppTitleText(
            text = title,
            color = TextPrimary,
            textAlign = TextAlign.Center,
        )
        VSpace(Dimens.paddingS)
        AppBodyText(
            text = subtitle,
            color = TextSecondary,
            textAlign = TextAlign.Center,
        )
        if (actionLabel != null && onAction != null) {
            VSpace(Dimens.paddingXXL)
            PrimaryButton(
                text = actionLabel,
                onClick = onAction,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
