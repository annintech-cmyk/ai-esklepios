package lu.esklepios.app.view.dashboard.profile.profile_edit

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.navigation.NavController
import lu.esklepios.app.R
import lu.esklepios.app.core.ui.components.AppCaptionText
import lu.esklepios.app.core.ui.components.AppFormScreen
import lu.esklepios.app.core.ui.components.FormFieldLabel
import lu.esklepios.app.core.ui.components.PrimaryButton
import lu.esklepios.app.core.ui.components.SecondaryButton
import lu.esklepios.app.core.ui.theme.BorderColor
import lu.esklepios.app.core.ui.theme.Danger
import lu.esklepios.app.core.ui.theme.DangerBg
import lu.esklepios.app.core.ui.theme.Dimens
import lu.esklepios.app.core.ui.theme.Primary
import lu.esklepios.app.core.ui.theme.Success
import lu.esklepios.app.core.ui.theme.TextHint
import lu.esklepios.app.core.ui.theme.TextSecondary
import lu.esklepios.app.presentation.viewmodel.ChangePasswordViewModel
import lu.esklepios.app.util.ValidationUtil
import lu.esklepios.app.utils.strengthColor
import lu.esklepios.app.utils.strengthLabel
import org.koin.androidx.compose.koinViewModel

@Composable
fun ChangePasswordScreen(
    navController: NavController,
    viewModel: ChangePasswordViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val successMessage = stringResource(R.string.change_password_success)
    var showCurrentPw by remember { mutableStateOf(false) }
    var showNewPw by remember { mutableStateOf(false) }
    var showConfirmPw by remember { mutableStateOf(false) }

    val newPw = uiState.newPassword
    val criteriaResult = ValidationUtil.passwordCriteriaResult(newPw)
    val hasMinLength = criteriaResult.minLength
    val hasMixedCase = criteriaResult.mixedCase
    val hasNumAndSymbol = criteriaResult.numAndSymbol
    val strength = ValidationUtil.passwordStrength(newPw)
    val strengthPercent = strength.percent
    val strengthColor = strength.strengthColor()
    val strengthLabel = strength.strengthLabel()
    val passwordsMatch = ValidationUtil.passwordsMatch(uiState.newPassword, uiState.confirmPassword)

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            snackbarHostState.showSnackbar(successMessage)
            navController.popBackStack()
        }
    }

    AppFormScreen(
        title = stringResource(R.string.screen_change_password),
        onNavigateBack = { navController.popBackStack() },
        error = uiState.error,
        onErrorDismissed = { viewModel.clearError() },
        snackbarHostState = snackbarHostState,
    ) {
        Spacer(Modifier.height(Dimens.paddingXL))

        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .background(DangerBg, RoundedCornerShape(Dimens.radiusMd))
                    .padding(Dimens.paddingL),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Dimens.paddingM),
        ) {
            Icon(
                Icons.Filled.Shield,
                contentDescription = null,
                tint = Danger,
                modifier = Modifier.size(Dimens.iconSizeLgInner),
            ) // a11y: decorative — labelled by adjacent Text
            Column {
                AppCaptionText(
                    text = stringResource(R.string.change_password_banner_title),
                    color = Danger,
                )
                AppCaptionText(
                    text = stringResource(R.string.change_password_banner_body),
                    color = Danger.copy(alpha = 0.8f),
                )
            }
        }

        Spacer(Modifier.height(Dimens.paddingXXL))

        FormFieldLabel(text = stringResource(R.string.label_current_password_field), required = true)
        Spacer(Modifier.height(Dimens.paddingS))
        PasswordInputField(
            value = uiState.oldPassword,
            onValueChange = { viewModel.updateOldPassword(it) },
            placeholder = stringResource(R.string.label_current_password_field),
            showPassword = showCurrentPw,
            onToggleShow = { showCurrentPw = !showCurrentPw },
            // a11y: decorative — labelled by adjacent Text
            leadingIcon = {
                Icon(Icons.Filled.Lock, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(Dimens.iconSizeMd))
            },
        )

        Spacer(Modifier.height(Dimens.paddingXL))

        FormFieldLabel(text = stringResource(R.string.label_new_password_field), required = true)
        Spacer(Modifier.height(Dimens.paddingS))
        PasswordInputField(
            value = newPw,
            onValueChange = { viewModel.updateNewPassword(it) },
            placeholder = stringResource(R.string.label_new_password_field),
            showPassword = showNewPw,
            onToggleShow = { showNewPw = !showNewPw },
            // a11y: decorative — labelled by adjacent Text
            leadingIcon = {
                Icon(Icons.Filled.Lock, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(Dimens.iconSizeMd))
            },
        )

        if (newPw.isNotBlank()) {
            Spacer(Modifier.height(Dimens.paddingM))
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(Dimens.paddingTiny)
                        .background(BorderColor, RoundedCornerShape(Dimens.radiusSm / 2)),
            ) {
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth(strengthPercent)
                            .fillMaxHeight()
                            .background(strengthColor, RoundedCornerShape(Dimens.radiusSm / 2)),
                )
            }
            Spacer(Modifier.height(Dimens.paddingS))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                AppCaptionText(text = "${stringResource(R.string.change_password_strength)} $strengthLabel", color = strengthColor)
                AppCaptionText(text = "${(strengthPercent * 100).toInt()}%", color = strengthColor)
            }
            Spacer(Modifier.height(Dimens.paddingM))
            PasswordCriterionRow(stringResource(R.string.change_password_criteria_length), hasMinLength)
            Spacer(Modifier.height(Dimens.paddingXS))
            PasswordCriterionRow(stringResource(R.string.change_password_criteria_case), hasMixedCase)
            Spacer(Modifier.height(Dimens.paddingXS))
            PasswordCriterionRow(stringResource(R.string.change_password_criteria_symbol), hasNumAndSymbol)
        }

        Spacer(Modifier.height(Dimens.paddingXL))

        FormFieldLabel(text = stringResource(R.string.label_confirm_password_field), required = true)
        Spacer(Modifier.height(Dimens.paddingS))
        PasswordInputField(
            value = uiState.confirmPassword,
            onValueChange = { viewModel.updateConfirmPassword(it) },
            placeholder = stringResource(R.string.label_confirm_password_field),
            showPassword = showConfirmPw,
            onToggleShow = { showConfirmPw = !showConfirmPw },
            isError = uiState.confirmPassword.isNotBlank() && !passwordsMatch,
            // a11y: decorative — labelled by adjacent Text
            leadingIcon = {
                Icon(Icons.Filled.Shield, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(Dimens.iconSizeMd))
            },
        )
        if (passwordsMatch) {
            Spacer(Modifier.height(Dimens.paddingS))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Filled.CheckCircle,
                    contentDescription = null,
                    tint = Success,
                    modifier = Modifier.size(Dimens.iconSizeSm),
                ) // a11y: decorative — labelled by adjacent Text
                Spacer(Modifier.width(Dimens.paddingXS))
                AppCaptionText(
                    text = stringResource(R.string.change_password_match),
                    color = Success,
                )
            }
        } else if (uiState.confirmPassword.isNotBlank()) {
            Spacer(Modifier.height(Dimens.paddingS))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Filled.Clear,
                    contentDescription = null,
                    tint = Danger,
                    modifier = Modifier.size(Dimens.iconSizeSm),
                ) // a11y: decorative — labelled by adjacent Text
                Spacer(Modifier.width(Dimens.paddingXS))
                AppCaptionText(
                    text = stringResource(R.string.error_password_mismatch),
                    color = Danger,
                )
            }
        }

        Spacer(Modifier.height(Dimens.paddingXXXL))

        PrimaryButton(
            text = stringResource(R.string.action_save),
            onClick = { viewModel.changePassword() },
            modifier = Modifier.fillMaxWidth(),
            isLoading = uiState.isLoading,
        )

        Spacer(Modifier.height(Dimens.paddingM))

        SecondaryButton(
            text = stringResource(R.string.action_cancel),
            onClick = { navController.popBackStack() },
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(Modifier.height(Dimens.paddingXXXL))
    }
}

@Composable
private fun PasswordInputField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    showPassword: Boolean,
    onToggleShow: () -> Unit,
    isError: Boolean = false,
    leadingIcon: @Composable (() -> Unit)? = null,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Dimens.radiusMd),
        visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
        leadingIcon = leadingIcon,
        trailingIcon = {
            IconButton(onClick = onToggleShow) {
                Icon(
                    if (showPassword) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                    contentDescription =
                        if (showPassword) {
                            stringResource(
                                R.string.cd_hide_password,
                            )
                        } else {
                            stringResource(R.string.cd_show_password)
                        },
                    tint = TextHint,
                    modifier = Modifier.size(Dimens.iconSizeMd),
                )
            }
        },
        placeholder = {
            AppCaptionText(text = placeholder, color = TextHint)
        },
        isError = isError,
        singleLine = true,
        colors =
            OutlinedTextFieldDefaults.colors(
                focusedBorderColor = if (isError) Danger else Primary,
                unfocusedBorderColor = if (isError) Danger else BorderColor,
                errorBorderColor = Danger,
            ),
    )
}

@Composable
private fun PasswordCriterionRow(
    label: String,
    met: Boolean,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            if (met) Icons.Filled.CheckCircle else Icons.Filled.Circle,
            // a11y: decorative — labelled by adjacent Text
            contentDescription = null,
            tint = if (met) Success else TextHint,
            modifier = Modifier.size(Dimens.iconSizeSm),
        )
        Spacer(Modifier.width(Dimens.paddingS))
        AppCaptionText(text = label, color = if (met) Success else TextHint)
    }
}
