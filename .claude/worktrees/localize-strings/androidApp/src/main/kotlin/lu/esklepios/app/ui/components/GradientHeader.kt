package lu.esklepios.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import lu.esklepios.app.ui.theme.Dimens
import lu.esklepios.app.ui.theme.Gradients

@Composable
fun GradientHeader(
    modifier: Modifier = Modifier,
    roundedBottom: Boolean = false,
    content: @Composable ColumnScope.() -> Unit
) {
    val shape = if (roundedBottom) {
        RoundedCornerShape(bottomStart = Dimens.paddingXXXL, bottomEnd = Dimens.paddingXXXL)
    } else {
        RoundedCornerShape(0.dp)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(brush = Gradients.primaryBrush)
    ) {
        // Decorative orb circles
        Box(
            modifier = Modifier
                .offset(x = (-40).dp, y = (-40).dp)
                .size(180.dp)
                .background(
                    color = Color.White.copy(alpha = 0.05f),
                    shape = CircleShape
                )
        )
        Box(
            modifier = Modifier
                .offset(x = 200.dp, y = (-20).dp)
                .size(120.dp)
                .background(
                    color = Color.White.copy(alpha = 0.07f),
                    shape = CircleShape
                )
        )
        Box(
            modifier = Modifier
                .offset(x = 100.dp, y = 80.dp)
                .size(80.dp)
                .background(
                    color = Color.White.copy(alpha = 0.04f),
                    shape = CircleShape
                )
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(
                    start = Dimens.paddingL,
                    end = Dimens.paddingL,
                    top = Dimens.paddingM,
                    bottom = Dimens.paddingXXL
                ),
            content = content
        )
    }
}
