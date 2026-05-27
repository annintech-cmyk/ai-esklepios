package lu.esklepios.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import lu.esklepios.app.ui.theme.PrimaryDark
import lu.esklepios.app.ui.theme.PrimaryLight

private val AvatarGradientEnd = Color(0xFFDDE1FB)

@Composable
fun AvatarCircle(
    initials: String,
    size: Dp = 48.dp,
    fontSize: TextUnit = 15.sp
) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(PrimaryLight, AvatarGradientEnd)
                )
            )
            .border(width = 2.dp, color = Color.White, shape = CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initials.uppercase().take(2),
            style = MaterialTheme.typography.labelLarge.copy(
                color = PrimaryDark,
                fontWeight = FontWeight.Bold,
                fontSize = fontSize
            )
        )
    }
}
