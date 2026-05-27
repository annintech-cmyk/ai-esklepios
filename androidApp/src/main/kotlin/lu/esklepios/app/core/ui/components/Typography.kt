package lu.esklepios.app.core.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import lu.esklepios.app.core.ui.theme.Danger
import lu.esklepios.app.core.ui.theme.Dimens
import lu.esklepios.app.core.ui.theme.TextPrimary
import lu.esklepios.app.core.ui.theme.TextSecondary

@Composable
fun AppTitleText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = TextPrimary,
    textAlign: TextAlign? = null,
    maxLines: Int = Int.MAX_VALUE,
) {
    Text(
        text = text,
        style = MaterialTheme.typography.headlineMedium,
        color = color,
        textAlign = textAlign,
        maxLines = maxLines,
        overflow = if (maxLines < Int.MAX_VALUE) TextOverflow.Ellipsis else TextOverflow.Clip,
        modifier = modifier,
    )
}

@Composable
fun AppSubtitleText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = TextPrimary,
    textAlign: TextAlign? = null,
    maxLines: Int = Int.MAX_VALUE,
) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        color = color,
        textAlign = textAlign,
        maxLines = maxLines,
        overflow = if (maxLines < Int.MAX_VALUE) TextOverflow.Ellipsis else TextOverflow.Clip,
        modifier = modifier,
    )
}

@Composable
fun AppBodyText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = TextSecondary,
    textAlign: TextAlign? = null,
    maxLines: Int = Int.MAX_VALUE,
) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        color = color,
        textAlign = textAlign,
        maxLines = maxLines,
        overflow = if (maxLines < Int.MAX_VALUE) TextOverflow.Ellipsis else TextOverflow.Clip,
        modifier = modifier,
    )
}

@Composable
fun AppCaptionText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = TextSecondary,
    textAlign: TextAlign? = null,
    maxLines: Int = Int.MAX_VALUE,
) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodySmall,
        color = color,
        textAlign = textAlign,
        maxLines = maxLines,
        overflow = if (maxLines < Int.MAX_VALUE) TextOverflow.Ellipsis else TextOverflow.Clip,
        modifier = modifier,
    )
}

@Composable
fun AppToolbarTitle(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = TextPrimary,
) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleLarge,
        color = color,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier,
    )
}

@Composable
fun AppButtonText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.White,
    textAlign: TextAlign? = null,
) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge,
        color = color,
        textAlign = textAlign,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier,
    )
}

@Composable
fun AppLabelText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = TextPrimary,
    textAlign: TextAlign? = null,
) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge,
        color = color,
        textAlign = textAlign,
        modifier = modifier,
    )
}

@Composable
fun AppErrorText(
    text: String,
    modifier: Modifier = Modifier,
    textAlign: TextAlign? = null,
) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodySmall,
        color = Danger,
        textAlign = textAlign,
        modifier = modifier,
    )
}

@Composable
fun AppSectionHeaderText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = TextPrimary,
) {
    Text(
        text = text,
        fontSize = Dimens.fontSizeTiny,
        fontWeight = FontWeight.Bold,
        letterSpacing = Dimens.letterSpacingWide,
        color = color,
        modifier = modifier,
    )
}

@Composable
fun AppFieldValueText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = TextPrimary,
) {
    Text(
        text = text,
        fontSize = Dimens.fontSizeXs,
        fontWeight = FontWeight.SemiBold,
        color = color,
        modifier = modifier,
    )
}

// ── Multi-span / annotated text ──────────────────────────────────────────────

data class TextSpan(
    val text: String,
    val spanStyle: SpanStyle? = null,
)

@Composable
fun AppSpannedText(
    spans: List<TextSpan>,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.bodyMedium,
) {
    Text(
        text =
            buildAnnotatedString {
                spans.forEach { span ->
                    if (span.spanStyle != null) {
                        withStyle(span.spanStyle) { append(span.text) }
                    } else {
                        append(span.text)
                    }
                }
            },
        modifier = modifier,
        style = style,
    )
}

// ── Layout components that use App*Text internally ────────────────────────────

@Composable
fun FormFieldLabel(
    text: String,
    required: Boolean = false,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier) {
        AppLabelText(text = text)
        if (required) AppLabelText(text = " *", color = Danger)
    }
}
