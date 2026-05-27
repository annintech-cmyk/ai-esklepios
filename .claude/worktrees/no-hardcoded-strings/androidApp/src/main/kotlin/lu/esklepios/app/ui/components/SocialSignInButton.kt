package lu.esklepios.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
fun GoogleSignInButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(Dimens.buttonHeight),
        shape = RoundedCornerShape(Dimens.radiusMd),
        border = BorderStroke(1.dp, BorderColor),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = Surface,
            contentColor = TextPrimary
        ),
        contentPadding = PaddingValues(horizontal = Dimens.paddingL)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            // Google "G" icon placeholder using colored text
            Text(
                text = "G",
                style = MaterialTheme.typography.titleMedium.copy(
                    color = Color(0xFF4285F4),
                    fontWeight = FontWeight.Bold
                )
            )
            Spacer(modifier = Modifier.width(Dimens.paddingM))
            Text(
                text = stringResource(R.string.auth_sign_in_google),
                style = MaterialTheme.typography.labelLarge.copy(
                    color = TextPrimary,
                    fontWeight = FontWeight.Medium
                )
            )
        }
    }
}

@Composable
fun AppleSignInButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(Dimens.buttonHeight),
        shape = RoundedCornerShape(Dimens.radiusMd),
        border = BorderStroke(1.dp, BorderColor),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = Surface,
            contentColor = TextPrimary
        ),
        contentPadding = PaddingValues(horizontal = Dimens.paddingL)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            // Apple icon placeholder
            Text(
                text = "",
                style = MaterialTheme.typography.titleMedium.copy(
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            )
            Spacer(modifier = Modifier.width(Dimens.paddingM))
            Text(
                text = stringResource(R.string.auth_sign_in_apple),
                style = MaterialTheme.typography.labelLarge.copy(
                    color = TextPrimary,
                    fontWeight = FontWeight.Medium
                )
            )
        }
    }
}
