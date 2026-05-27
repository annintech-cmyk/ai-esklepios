package lu.esklepios.app.core.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import lu.esklepios.app.core.ui.theme.*

@Composable
fun SearchInputField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    leadingIcon: ImageVector,
    modifier: Modifier = Modifier,
    iconTint: Color = TextHint,
    variant: SearchInputVariant = SearchInputVariant.Light,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        placeholder = {
            AppCaptionText(
                text = placeholder,
                color =
                    when (variant) {
                        SearchInputVariant.Light -> TextHint
                        SearchInputVariant.Dark -> Color.White.copy(alpha = 0.6f)
                    },
            )
        },
        leadingIcon = {
            Icon(
                leadingIcon,
                contentDescription = null, // a11y: decorative — labelled by adjacent Text
                tint =
                    when (variant) {
                        SearchInputVariant.Light -> iconTint
                        SearchInputVariant.Dark -> Color.White.copy(alpha = 0.8f)
                    },
                modifier = Modifier.size(Dimens.iconSizeMd),
            )
        },
        singleLine = true,
        shape = RoundedCornerShape(Dimens.radiusMd),
        colors =
            when (variant) {
                SearchInputVariant.Light ->
                    OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BorderColor,
                        unfocusedBorderColor = BorderColor,
                        focusedContainerColor = FieldBackground,
                        unfocusedContainerColor = FieldBackground,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                    )
                SearchInputVariant.Dark ->
                    OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.White.copy(alpha = 0.5f),
                        unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                        focusedContainerColor = Color.White.copy(alpha = 0.15f),
                        unfocusedContainerColor = Color.White.copy(alpha = 0.1f),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                    )
            },
    )
}
