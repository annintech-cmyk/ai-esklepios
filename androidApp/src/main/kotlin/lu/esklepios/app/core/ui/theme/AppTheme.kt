package lu.esklepios.app.core.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf

private val LightColorScheme =
    lightColorScheme(
        primary = Primary,
        onPrimary = Surface,
        primaryContainer = PrimaryLight,
        onPrimaryContainer = PrimaryDark,
        secondary = PrimaryMid,
        onSecondary = Surface,
        secondaryContainer = PrimaryLight,
        onSecondaryContainer = PrimaryDark,
        tertiary = Success,
        onTertiary = Surface,
        tertiaryContainer = SuccessBg,
        onTertiaryContainer = Success,
        error = Danger,
        onError = Surface,
        errorContainer = DangerBg,
        onErrorContainer = Danger,
        background = Background,
        onBackground = TextPrimary,
        surface = Surface,
        onSurface = TextPrimary,
        surfaceVariant = PrimaryLight,
        onSurfaceVariant = TextSecondary,
        outline = BorderColor,
        outlineVariant = BorderLight,
    )

private val DarkColorScheme =
    darkColorScheme(
        primary = PrimaryMid,
        onPrimary = Surface,
        primaryContainer = PrimaryDark,
        onPrimaryContainer = PrimaryLight,
        secondary = PrimaryMid,
        onSecondary = PrimaryDark,
        secondaryContainer = PrimaryDark,
        onSecondaryContainer = PrimaryLight,
        tertiary = Success,
        onTertiary = Surface,
        tertiaryContainer = Success,
        onTertiaryContainer = SuccessBg,
        error = Danger,
        onError = Surface,
        errorContainer = DangerBg,
        onErrorContainer = Danger,
        background = PrimaryDark,
        onBackground = Surface,
        surface = Surface,
        onSurface = TextPrimary,
        surfaceVariant = PrimaryDark,
        onSurfaceVariant = TextSecondary,
        outline = BorderColor,
        outlineVariant = BorderLight,
    )

val LocalESklepiosDimens = staticCompositionLocalOf { Dimens }

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content,
    )
}
