package lu.esklepios.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import lu.esklepios.app.ui.theme.*

@Composable
fun AppCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val shape = RoundedCornerShape(18.dp)
    Card(
        modifier = modifier
            .then(
                if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier
            ),
        shape = shape,
        colors = CardDefaults.cardColors(containerColor = Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = Dimens.cardElevation),
        border = BorderStroke(0.5.dp, BorderColor)
    ) {
        Column(content = content)
    }
}

@Composable
fun InfoCard(
    modifier: Modifier = Modifier,
    title: String,
    icon: ImageVector,
    content: @Composable ColumnScope.() -> Unit
) {
    AppCard(modifier = modifier) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(PrimaryLight)
                .padding(horizontal = Dimens.paddingL, vertical = Dimens.paddingM),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Dimens.paddingS)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Primary,
                modifier = Modifier.size(Dimens.iconSizeMd)
            )
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall.copy(
                    color = Primary,
                    fontWeight = FontWeight.SemiBold
                )
            )
        }
        // Body
        Column(
            modifier = Modifier.padding(Dimens.paddingL),
            content = content
        )
    }
}
