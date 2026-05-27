package lu.esklepios.app.core.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import lu.esklepios.app.R
import lu.esklepios.app.core.ui.theme.*

@Composable
fun FormField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "",
    isRequired: Boolean = false,
    isPassword: Boolean = false,
    leadingIcon: ImageVector? = null,
    errorMessage: String? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    modifier: Modifier = Modifier,
) {
    var passwordVisible by remember { mutableStateOf(false) }
    val isError = errorMessage != null

    Column(modifier = modifier) {
        // Label
        Row {
            AppLabelText(
                text = label,
                color = if (isError) Danger else TextPrimary,
            )
            if (isRequired) {
                AppLabelText(
                    text = " *",
                    color = Danger,
                )
            }
        }
        Spacer(modifier = Modifier.height(Dimens.paddingXS))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                AppCaptionText(
                    text = placeholder,
                    color = TextHint,
                )
            },
            leadingIcon =
                if (leadingIcon != null) {
                    {
                        Icon(
                            imageVector = leadingIcon,
                            contentDescription = null, // a11y: decorative — labelled by adjacent Text
                            tint = if (isError) Danger else PrimaryMid,
                            modifier = Modifier.size(Dimens.iconSizeMd),
                        )
                    }
                } else {
                    null
                },
            trailingIcon =
                if (isPassword) {
                    {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                contentDescription =
                                    if (passwordVisible) {
                                        stringResource(R.string.cd_hide_password)
                                    } else {
                                        stringResource(R.string.cd_show_password)
                                    },
                                tint = TextSecondary,
                            )
                        }
                    }
                } else {
                    null
                },
            visualTransformation =
                if (isPassword && !passwordVisible) {
                    PasswordVisualTransformation()
                } else {
                    VisualTransformation.None
                },
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            isError = isError,
            singleLine = true,
            shape = RoundedCornerShape(Dimens.radiusMd),
            colors =
                OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Primary,
                    unfocusedBorderColor = BorderColor,
                    errorBorderColor = Danger,
                    focusedContainerColor = Surface,
                    unfocusedContainerColor = Surface,
                    errorContainerColor = DangerBg.copy(alpha = 0.3f),
                ),
        )
        if (isError && errorMessage != null) {
            Spacer(modifier = Modifier.height(Dimens.paddingXS))
            AppErrorText(text = errorMessage)
        }
    }
}

@Composable
fun TextAreaField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "",
    isRequired: Boolean = false,
    minLines: Int = 3,
    errorMessage: String? = null,
    modifier: Modifier = Modifier,
) {
    val isError = errorMessage != null
    Column(modifier = modifier) {
        Row {
            AppLabelText(text = label, color = if (isError) Danger else TextPrimary)
            if (isRequired) AppLabelText(text = " *", color = Danger)
        }
        Spacer(modifier = Modifier.height(Dimens.paddingXS))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { AppCaptionText(text = placeholder, color = TextHint) },
            minLines = minLines,
            singleLine = false,
            shape = RoundedCornerShape(Dimens.radiusMd),
            colors =
                OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Primary,
                    unfocusedBorderColor = BorderColor,
                    errorBorderColor = Danger,
                    focusedContainerColor = Surface,
                    unfocusedContainerColor = Surface,
                ),
            textStyle = MaterialTheme.typography.bodySmall.copy(color = TextPrimary),
            isError = isError,
        )
        if (isError && errorMessage != null) {
            Spacer(modifier = Modifier.height(Dimens.paddingXS))
            AppErrorText(text = errorMessage)
        }
    }
}
