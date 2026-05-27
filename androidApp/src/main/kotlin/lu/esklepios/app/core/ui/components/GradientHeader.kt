package lu.esklepios.app.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import lu.esklepios.app.core.ui.theme.Dimens
import lu.esklepios.app.core.ui.theme.Gradients

@Composable
fun GradientHeader(
    modifier: Modifier = Modifier,
    roundedBottom: Boolean = false,
    topPadding: Dp = Dimens.paddingM,
    bottomPadding: Dp = Dimens.paddingXXL,
    content: @Composable ColumnScope.() -> Unit,
) {
    val shape =
        if (roundedBottom) {
            RoundedCornerShape(bottomStart = Dimens.paddingXXXL, bottomEnd = Dimens.paddingXXXL)
        } else {
            RoundedCornerShape(Dimens.cornerNone)
        }

    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(shape)
                .background(brush = Gradients.primaryBrush),
    ) {
        // Decorative orb circles
        Box(
            modifier =
                Modifier
                    .offset(x = -Dimens.orbXs, y = -Dimens.orbXs)
                    .size(Dimens.orbXs)
                    .background(
                        color = Color.White.copy(alpha = 0.05f),
                        shape = CircleShape,
                    ),
        )
        Box(
            modifier =
                Modifier
                    .offset(x = Dimens.orbOffsetMainX, y = -Dimens.orbOffsetMainY)
                    .size(Dimens.orbMd)
                    .background(
                        color = Color.White.copy(alpha = 0.07f),
                        shape = CircleShape,
                    ),
        )
        Box(
            modifier =
                Modifier
                    .offset(x = Dimens.orbOffsetSecondaryX, y = Dimens.orbOffsetSecondaryY)
                    .size(Dimens.orbSm)
                    .background(
                        color = Color.White.copy(alpha = 0.04f),
                        shape = CircleShape,
                    ),
        )

        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(
                        start = Dimens.paddingL,
                        end = Dimens.paddingL,
                        top = topPadding,
                        bottom = bottomPadding,
                    ),
            content = content,
        )
    }
}
