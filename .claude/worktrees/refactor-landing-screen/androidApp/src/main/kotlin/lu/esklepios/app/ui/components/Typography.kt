package lu.esklepios.app.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import lu.esklepios.app.ui.theme.TextPrimary
import lu.esklepios.app.ui.theme.TextSecondary

@Composable
fun SectionTitle(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleSmall.copy(
            color = TextPrimary,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        ),
        modifier = modifier
    )
}

@Composable
fun BodyText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = TextPrimary
) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium.copy(
            color = color,
            fontSize = 14.sp
        ),
        modifier = modifier
    )
}

@Composable
fun CaptionText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = TextSecondary
) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodySmall.copy(
            color = color,
            fontSize = 12.sp
        ),
        modifier = modifier
    )
}

@Composable
fun LabelText(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text.uppercase(),
        style = MaterialTheme.typography.labelSmall.copy(
            color = TextSecondary,
            fontWeight = FontWeight.SemiBold,
            fontSize = 11.sp,
            letterSpacing = 0.8.sp
        ),
        modifier = modifier
    )
}
