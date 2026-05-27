package lu.esklepios.app.core.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import lu.esklepios.app.R
import lu.esklepios.app.core.ui.theme.Danger
import lu.esklepios.app.core.ui.theme.Dimens
import lu.esklepios.app.core.ui.theme.TextSecondary

@Composable
fun ErrorView(
    modifier: Modifier = Modifier,
    message: String,
    onRetry: (() -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(Dimens.paddingXXXL),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Filled.Warning,
            contentDescription = null, // a11y: decorative — labelled by adjacent Text
            tint = Danger,
            modifier = Modifier.size(Dimens.avatarSizeXl)
        )
        Spacer(modifier = Modifier.height(Dimens.paddingL))
        AppBodyText(
            text = message,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )
        if (onRetry != null) {
            Spacer(modifier = Modifier.height(Dimens.paddingXXL))
            PrimaryButton(
                text = stringResource(R.string.action_retry),
                onClick = onRetry,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
