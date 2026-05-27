package lu.esklepios.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import lu.esklepios.app.R
import lu.esklepios.app.ui.theme.*

@Composable
fun AppToolbar(
    title: String,
    onNavigateBack: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
    useGradient: Boolean = true
) {
    val backgroundModifier = if (useGradient) {
        Modifier.background(brush = Gradients.primaryBrush)
    } else {
        Modifier.background(color = Surface)
    }
    val contentColor = if (useGradient) Color.White else TextPrimary

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(Dimens.appBarHeight)
            .then(backgroundModifier)
            .statusBarsPadding()
    ) {
        // Back button on left
        if (onNavigateBack != null) {
            IconButton(
                onClick = onNavigateBack,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = Dimens.paddingS)
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.cd_back),
                    tint = contentColor,
                    modifier = Modifier.size(Dimens.iconSizeLg)
                )
            }
        }

        // Title centered
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge.copy(
                color = contentColor,
                fontWeight = FontWeight.SemiBold
            ),
            modifier = Modifier.align(Alignment.Center)
        )

        // Actions on right
        Row(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = Dimens.paddingS),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            actions()
        }
    }
}
