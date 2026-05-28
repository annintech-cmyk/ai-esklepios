package lu.esklepios.app.core.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import lu.esklepios.app.R
import lu.esklepios.app.core.ui.theme.BorderColor
import lu.esklepios.app.core.ui.theme.Danger
import lu.esklepios.app.core.ui.theme.DangerBg
import lu.esklepios.app.core.ui.theme.Dimens
import lu.esklepios.app.core.ui.theme.Primary
import lu.esklepios.app.core.ui.theme.PrimaryMid
import lu.esklepios.app.core.ui.theme.Surface
import lu.esklepios.app.core.ui.theme.TextHint
import lu.esklepios.app.core.ui.theme.TextPrimary
import lu.esklepios.app.core.ui.theme.TextSecondary

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
                            // a11y: decorative — labelled by adjacent Text
                            contentDescription = null,
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
