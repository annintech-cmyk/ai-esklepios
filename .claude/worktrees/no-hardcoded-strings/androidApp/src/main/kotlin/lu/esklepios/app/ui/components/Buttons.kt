package lu.esklepios.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import lu.esklepios.app.ui.theme.*

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
        contentPadding = PaddingValues(horizontal = Dimens.paddingXXL, vertical = 0.dp),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(Dimens.iconSizeLg),
                color = Color.White,
                strokeWidth = 2.dp
            )
        } else {
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge.copy(
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                )
            )
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
        border = BorderStroke(1.5.dp, if (enabled) Primary else Color.Gray),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = Surface,
            contentColor = Primary,
            disabledContainerColor = Surface,
            disabledContentColor = Color.Gray
        ),
        contentPadding = PaddingValues(horizontal = Dimens.paddingXXL, vertical = 0.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.SemiBold
            )
        )
    }
}

@Composable
fun GhostButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TextButton(
        onClick = onClick,
        modifier = modifier.height(Dimens.buttonHeight),
        colors = ButtonDefaults.textButtonColors(
            contentColor = Primary
        ),
        contentPadding = PaddingValues(horizontal = Dimens.paddingXXL, vertical = 0.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge.copy(
                color = Primary,
                fontWeight = FontWeight.Medium
            )
        )
    }
}
