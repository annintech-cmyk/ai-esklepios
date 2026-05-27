package lu.esklepios.app.core.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import lu.esklepios.app.R
import lu.esklepios.app.core.ui.theme.BorderColor
import lu.esklepios.app.core.ui.theme.Dimens
import lu.esklepios.app.core.ui.theme.GoogleBlue
import lu.esklepios.app.core.ui.theme.Surface
import lu.esklepios.app.core.ui.theme.TextPrimary

@Composable
fun GoogleSignInButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(Dimens.buttonHeight),
        shape = RoundedCornerShape(Dimens.radiusMd),
        border = BorderStroke(Dimens.borderThin, BorderColor),
        colors =
            ButtonDefaults.outlinedButtonColors(
                containerColor = Surface,
                contentColor = TextPrimary,
            ),
        contentPadding = PaddingValues(horizontal = Dimens.paddingL),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            // Google "G" icon placeholder using colored text
            AppTitleText(
                text = "G",
                color = GoogleBlue,
            )
            Spacer(modifier = Modifier.width(Dimens.paddingM))
            AppButtonText(
                text = stringResource(R.string.auth_sign_in_google),
                color = TextPrimary,
            )
        }
    }
}

@Composable
fun AppleSignInButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(Dimens.buttonHeight),
        shape = RoundedCornerShape(Dimens.radiusMd),
        border = BorderStroke(Dimens.borderThin, BorderColor),
        colors =
            ButtonDefaults.outlinedButtonColors(
                containerColor = Surface,
                contentColor = TextPrimary,
            ),
        contentPadding = PaddingValues(horizontal = Dimens.paddingL),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            // Apple icon placeholder
            AppSubtitleText(
                text = "",
                color = TextPrimary,
            )
            Spacer(modifier = Modifier.width(Dimens.paddingM))
            AppButtonText(
                text = stringResource(R.string.auth_sign_in_apple),
                color = TextPrimary,
            )
        }
    }
}
