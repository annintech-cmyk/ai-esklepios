package lu.esklepios.app.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import lu.esklepios.app.R
import lu.esklepios.app.core.ui.theme.Dimens
import lu.esklepios.app.core.ui.theme.Gradients
import lu.esklepios.app.core.ui.theme.Surface
import lu.esklepios.app.core.ui.theme.TextPrimary

@Composable
fun AppToolbar(
    title: String,
    onNavigateBack: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
    useGradient: Boolean = true,
) {
    val backgroundModifier =
        if (useGradient) {
            Modifier.background(brush = Gradients.primaryBrush)
        } else {
            Modifier.background(color = Surface)
        }
    val contentColor = if (useGradient) Color.White else TextPrimary

    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(Dimens.appBarHeight)
                .then(backgroundModifier),
    ) {
        // Back button on left
        if (onNavigateBack != null) {
            AppIconButton(
                icon = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.cd_back),
                onClick = onNavigateBack,
                tint = contentColor,
                iconSize = Dimens.iconSizeLg,
                modifier =
                    Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = Dimens.paddingS),
            )
        }

        // Title centered
        AppToolbarTitle(
            text = title,
            color = contentColor,
            modifier = Modifier.align(Alignment.Center),
        )

        // Actions on right
        Row(
            modifier =
                Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = Dimens.paddingS),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            actions()
        }
    }
}
