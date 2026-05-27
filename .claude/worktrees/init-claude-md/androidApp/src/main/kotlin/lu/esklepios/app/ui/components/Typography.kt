package lu.esklepios.app.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import lu.esklepios.app.ui.theme.Danger
import lu.esklepios.app.ui.theme.TextPrimary
import lu.esklepios.app.ui.theme.TextSecondary

// ── App*Text — canonical 8-component typography system ──────────────────────

@Composable
fun AppTitleText(text: String, modifier: Modifier = Modifier, color: Color = TextPrimary, textAlign: TextAlign? = null, maxLines: Int = Int.MAX_VALUE) {
    Text(text = text, style = MaterialTheme.typography.headlineMedium, color = color, textAlign = textAlign, maxLines = maxLines, modifier = modifier)
}

@Composable
fun AppSubtitleText(text: String, modifier: Modifier = Modifier, color: Color = TextPrimary, textAlign: TextAlign? = null, maxLines: Int = Int.MAX_VALUE) {
    Text(text = text, style = MaterialTheme.typography.titleMedium, color = color, textAlign = textAlign, maxLines = maxLines, modifier = modifier)
}

@Composable
fun AppBodyText(text: String, modifier: Modifier = Modifier, color: Color = TextSecondary, textAlign: TextAlign? = null, maxLines: Int = Int.MAX_VALUE) {
    Text(text = text, style = MaterialTheme.typography.bodyMedium, color = color, textAlign = textAlign, maxLines = maxLines, modifier = modifier)
}

@Composable
fun AppCaptionText(text: String, modifier: Modifier = Modifier, color: Color = TextSecondary, textAlign: TextAlign? = null, maxLines: Int = Int.MAX_VALUE) {
    Text(text = text, style = MaterialTheme.typography.bodySmall, color = color, textAlign = textAlign, maxLines = maxLines, modifier = modifier)
}

@Composable
fun AppToolbarTitle(text: String, modifier: Modifier = Modifier, color: Color = TextPrimary) {
    Text(text = text, style = MaterialTheme.typography.titleLarge, color = color, modifier = modifier)
}

@Composable
fun AppButtonText(text: String, modifier: Modifier = Modifier, color: Color = Color.White) {
    Text(text = text, style = MaterialTheme.typography.labelLarge, color = color, modifier = modifier)
}

@Composable
fun AppLabelText(text: String, modifier: Modifier = Modifier, color: Color = TextPrimary, textAlign: TextAlign? = null) {
    Text(text = text, style = MaterialTheme.typography.labelMedium, color = color, textAlign = textAlign, modifier = modifier)
}

@Composable
fun AppErrorText(text: String, modifier: Modifier = Modifier, textAlign: TextAlign? = null) {
    Text(text = text, style = MaterialTheme.typography.bodySmall, color = Danger, textAlign = textAlign, modifier = modifier)
}

// ── Legacy components (kept for backward compatibility) ──────────────────────

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
