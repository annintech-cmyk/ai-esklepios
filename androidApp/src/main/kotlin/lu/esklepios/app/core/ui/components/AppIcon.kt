package lu.esklepios.app.core.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import lu.esklepios.app.core.ui.theme.Dimens
import lu.esklepios.app.core.ui.theme.Primary
import lu.esklepios.app.core.ui.theme.TextSecondary

/**
 * Project icon wrapper. **Required** in all screen / feature code (Rule UI-14).
 *
 * - Decorative icons (label provided by adjacent text): pass `contentDescription = null`
 *   AND add the inline comment `// a11y: decorative — labelled by adjacent Text` (Rule AC-2).
 * - Informative icons: pass a localized `stringResource(R.string.cd_*)` (Rule AC-1).
 *
 * Default tint is `TextSecondary`; default size is `Dimens.iconSizeMd`. Pass explicit values
 * when the design requires them — never reach for the raw `Icon(...)` primitive in screens.
 */
@Composable
fun AppIcon(
    imageVector: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    tint: Color = TextSecondary,
    size: Dp = Dimens.iconSizeMd,
) {
    Icon(
        imageVector = imageVector,
        contentDescription = contentDescription,
        tint = tint,
        modifier = modifier.size(size),
    )
}

@Preview
@Composable
private fun AppIconDefaultPreview() {
    AppIcon(
        imageVector = Icons.Filled.Star,
        contentDescription = null, // a11y: decorative — preview only
    )
}

@Preview
@Composable
private fun AppIconInformativePreview() {
    AppIcon(
        imageVector = Icons.Filled.Warning,
        contentDescription = "Warning",
        tint = Primary,
        size = Dimens.iconSizeLg,
    )
}
