package lu.esklepios.app.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import lu.esklepios.app.core.ui.theme.AvatarGradientEnd
import lu.esklepios.app.core.ui.theme.Dimens
import lu.esklepios.app.core.ui.theme.PrimaryDark
import lu.esklepios.app.core.ui.theme.PrimaryLight

@Composable
fun AvatarCircle(
    initials: String,
    size: Dp = Dimens.avatarSizeMd,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .size(size)
                .clip(CircleShape)
                .background(
                    brush =
                        Brush.linearGradient(
                            colors = listOf(PrimaryLight, AvatarGradientEnd),
                        ),
                )
                .border(width = Dimens.borderMedium, color = Color.White, shape = CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        AppLabelText(
            text = initials.uppercase().take(2),
            color = PrimaryDark,
        )
    }
}
