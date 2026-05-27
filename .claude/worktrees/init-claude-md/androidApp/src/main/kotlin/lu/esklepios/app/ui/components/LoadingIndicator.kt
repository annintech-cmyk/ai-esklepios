package lu.esklepios.app.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import lu.esklepios.app.ui.theme.Dimens
import lu.esklepios.app.ui.theme.Primary
import lu.esklepios.app.ui.theme.TextSecondary

@Composable
fun LoadingIndicator(
    modifier: Modifier = Modifier,
    message: String? = null
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(Dimens.paddingXXL),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(
            color = Primary,
            strokeWidth = Dimens.paddingXS / 2
        )
        if (message != null) {
            Spacer(modifier = Modifier.height(Dimens.paddingL))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary)
            )
        }
    }
}
