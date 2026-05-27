package lu.esklepios.app.core.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import lu.esklepios.app.core.ui.theme.*

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false
) {
    val pillShape = RoundedCornerShape(Dimens.radiusPill)
    Button(
        onClick = onClick,
        enabled = enabled && !isLoading,
        modifier = modifier
            .height(Dimens.buttonHeight)
            .background(
                brush = if (enabled && !isLoading) Gradients.primaryBrush else androidx.compose.ui.graphics.SolidColor(Color.Gray),
                shape = pillShape
            ),
        shape = pillShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = Color.White,
            disabledContainerColor = Color.Transparent,
            disabledContentColor = Color.White.copy(alpha = 0.6f)
        ),
        contentPadding = PaddingValues(horizontal = Dimens.paddingXXL, vertical = Dimens.paddingNone),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = Dimens.elevationNone)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(Dimens.iconSizeLg),
                color = Color.White,
                strokeWidth = Dimens.borderMedium
            )
        } else {
            AppButtonText(text = text)
        }
    }
}

@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val pillShape = RoundedCornerShape(Dimens.radiusPill)
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.height(Dimens.buttonHeight),
        shape = pillShape,
        border = BorderStroke(Dimens.borderMedium, if (enabled) Primary else Color.Gray),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = Surface,
            contentColor = Primary,
            disabledContainerColor = Surface,
            disabledContentColor = Color.Gray
        ),
        contentPadding = PaddingValues(horizontal = Dimens.paddingXXL, vertical = Dimens.paddingNone)
    ) {
        AppButtonText(text = text, color = Primary)
    }
}

@Composable
fun GhostButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    textColor: Color = Primary
) {
    TextButton(
        onClick = onClick,
        modifier = modifier.height(Dimens.buttonHeight),
        colors = ButtonDefaults.textButtonColors(
            contentColor = textColor
        ),
        contentPadding = PaddingValues(horizontal = Dimens.paddingXXL, vertical = Dimens.paddingNone)
    ) {
        AppButtonText(text = text, color = textColor)
    }
}

@Composable
fun GlassButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(Dimens.radiusPill),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White.copy(alpha = 0.18f),
            contentColor = Color.White,
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = Dimens.elevationNone),
        contentPadding = PaddingValues(
            horizontal = Dimens.paddingXXL,
            vertical = Dimens.paddingS + Dimens.paddingXS,
        ),
    ) {
        AppButtonText(text = text)
    }
}
