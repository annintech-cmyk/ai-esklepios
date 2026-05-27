package lu.esklepios.app.core.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import lu.esklepios.app.core.ui.theme.BorderColor
import lu.esklepios.app.core.ui.theme.Dimens
import lu.esklepios.app.core.ui.theme.Primary
import lu.esklepios.app.core.ui.theme.PrimaryLight
import lu.esklepios.app.core.ui.theme.Surface

@Composable
fun AppCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val shape = RoundedCornerShape(Dimens.radiusCard)
    Card(
        modifier =
            modifier
                .then(
                    if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier,
                ),
        shape = shape,
        colors = CardDefaults.cardColors(containerColor = Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = Dimens.cardElevation),
        border = BorderStroke(Dimens.borderHairline, BorderColor),
    ) {
        Column(content = content)
    }
}

@Composable
fun InfoCard(
    modifier: Modifier = Modifier,
    title: String,
    icon: ImageVector,
    content: @Composable ColumnScope.() -> Unit,
) {
    AppCard(modifier = modifier) {
        // Header
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .background(PrimaryLight)
                    .padding(horizontal = Dimens.paddingL, vertical = Dimens.paddingM),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Dimens.paddingS),
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null, // a11y: decorative — labelled by adjacent Text
                tint = Primary,
                modifier = Modifier.size(Dimens.iconSizeMd),
            )
            AppLabelText(
                text = title,
                color = Primary,
            )
        }
        // Body
        Column(
            modifier = Modifier.padding(Dimens.paddingL),
            content = content,
        )
    }
}
