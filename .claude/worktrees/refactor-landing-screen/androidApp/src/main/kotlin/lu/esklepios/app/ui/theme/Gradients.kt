package lu.esklepios.app.ui.theme

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush

object Gradients {
    val primaryBrush = Brush.linearGradient(
        colors = listOf(GradientStart, GradientEnd),
        start = Offset(0f, 0f),
        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
    )
    val softBrush = Brush.linearGradient(
        colors = listOf(GradientStart, GradientEnd),
    )
    val verticalBrush = Brush.verticalGradient(
        colors = listOf(GradientStart, GradientEnd)
    )
}
