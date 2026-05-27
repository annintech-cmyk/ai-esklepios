package lu.esklepios.app.core.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import lu.esklepios.app.R
import lu.esklepios.app.core.ui.theme.Dimens
import lu.esklepios.app.core.ui.theme.TextPrimary

/**
 * Project icon-button wrapper. **Required** in all screen / feature code (Rule UI-14).
 *
 * Unlike [AppIcon], `contentDescription` here is **non-nullable** — icon buttons are
 * always interactive, so they always need an accessibility label (Rule AC-1).
 *
 * The default tint is `TextPrimary` so the button reads against light backgrounds. For
 * gradient headers and dark backgrounds, pass `tint = Color.White`.
 */
@Composable
fun AppIconButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tint: Color = TextPrimary,
    iconSize: Dp = Dimens.iconSizeMd,
    enabled: Boolean = true,
) {
    IconButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
    ) {
        AppIcon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = tint,
            size = iconSize,
        )
    }
}

@Preview
@Composable
private fun AppIconButtonDefaultPreview() {
    AppIconButton(
        icon = Icons.AutoMirrored.Filled.ArrowBack,
        contentDescription = stringResource(R.string.cd_back),
        onClick = {},
    )
}

@Preview
@Composable
private fun AppIconButtonGradientPreview() {
    AppIconButton(
        icon = Icons.AutoMirrored.Filled.ArrowBack,
        contentDescription = stringResource(R.string.cd_back),
        onClick = {},
        tint = Color.White,
    )
}
