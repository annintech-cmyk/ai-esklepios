package lu.esklepios.app.core.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import lu.esklepios.app.core.ui.theme.*

@Composable
fun AppTextLink(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TextButton(
        onClick = onClick,
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = Dimens.paddingXS, vertical = Dimens.paddingNone),
    ) {
        AppButtonText(
            text = text,
            color = Primary,
        )
    }
}

@Composable
fun DividerWithLabel(
    label: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        HorizontalDivider(modifier = Modifier.weight(1f), color = BorderColor)
        AppCaptionText(text = "  $label  ")
        HorizontalDivider(modifier = Modifier.weight(1f), color = BorderColor)
    }
}
