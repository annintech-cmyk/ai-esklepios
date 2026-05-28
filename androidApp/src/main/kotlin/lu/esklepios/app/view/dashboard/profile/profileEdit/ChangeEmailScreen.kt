package lu.esklepios.app.view.dashboard.profile.profileEdit

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import lu.esklepios.app.R
import lu.esklepios.app.core.ui.components.AppCaptionText
import lu.esklepios.app.core.ui.components.AppFormScreen
import lu.esklepios.app.core.ui.components.AppIcon
import lu.esklepios.app.core.ui.components.FormFieldLabel
import lu.esklepios.app.core.ui.components.PrimaryButton
import lu.esklepios.app.core.ui.components.SecondaryButton
import lu.esklepios.app.core.ui.theme.BorderColor
import lu.esklepios.app.core.ui.theme.Danger
import lu.esklepios.app.core.ui.theme.Dimens
import lu.esklepios.app.core.ui.theme.FieldBackground
import lu.esklepios.app.core.ui.theme.Primary
import lu.esklepios.app.core.ui.theme.PrimaryLight
import lu.esklepios.app.core.ui.theme.Success
import lu.esklepios.app.core.ui.theme.TextHint
import lu.esklepios.app.core.ui.theme.TextSecondary
import lu.esklepios.app.presentation.viewmodel.ChangeEmailViewModel
import lu.esklepios.app.util.ValidationUtil
import org.koin.androidx.compose.koinViewModel

@Composable
fun ChangeEmailScreen(
    navController: NavController,
    viewModel: ChangeEmailViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val successMessage = stringResource(R.string.change_email_success)

    val isNewEmailValid = ValidationUtil.isValidEmail(uiState.newEmail)
    val emailsMatch = ValidationUtil.emailsMatch(uiState.newEmail, uiState.confirmEmail)

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            snackbarHostState.showSnackbar(successMessage)
            navController.popBackStack()
        }
    }

    AppFormScreen(
        title = stringResource(R.string.screen_change_email),
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
                    .background(PrimaryLight, RoundedCornerShape(Dimens.radiusMd))
                    .padding(Dimens.paddingL),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(Dimens.paddingM),
        ) {
            AppIcon(
                Icons.Filled.ForwardToInbox,
                // a11y: decorative — labelled by adjacent Text
                contentDescription = null,
                tint = Primary,
                size = Dimens.paddingXXL + Dimens.paddingXS,
                modifier = Modifier.padding(top = Dimens.paddingXS / 2),
            )
            AppCaptionText(
                text = stringResource(R.string.change_email_banner),
                color = Primary,
            )
        }

        Spacer(Modifier.height(Dimens.paddingXXL))

        FormFieldLabel(text = stringResource(R.string.label_current_email))
        Spacer(Modifier.height(Dimens.paddingS))
        OutlinedTextField(
            value = uiState.currentEmail.ifBlank { stringResource(R.string.change_email_not_available) },
            onValueChange = {},
            readOnly = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(Dimens.radiusMd),
            leadingIcon = {
                Icon(
                    Icons.Filled.Email,
                    contentDescription = null,
                    tint = TextHint,
                    modifier = Modifier.size(Dimens.iconSizeMd),
                ) // a11y: decorative — labelled by adjacent Text
            },
            colors =
                OutlinedTextFieldDefaults.colors(
                    disabledTextColor = TextSecondary,
                    disabledBorderColor = BorderColor,
                    disabledLeadingIconColor = TextHint,
                    disabledContainerColor = FieldBackground,
                ),
            enabled = false,
        )

        Spacer(Modifier.height(Dimens.paddingXL))

        FormFieldLabel(text = stringResource(R.string.label_new_email_field), required = true)
        Spacer(Modifier.height(Dimens.paddingS))
        OutlinedTextField(
            value = uiState.newEmail,
            onValueChange = { viewModel.updateNewEmail(it) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(Dimens.radiusMd),
            leadingIcon = {
                Icon(
                    Icons.Filled.Email,
                    contentDescription = null,
                    tint = TextSecondary,
                    modifier = Modifier.size(Dimens.iconSizeMd),
                ) // a11y: decorative — labelled by adjacent Text
            },
            placeholder = {
                AppCaptionText(
                    text = stringResource(R.string.label_new_email),
                    color = TextHint,
                )
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            colors =
                OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Primary,
                    unfocusedBorderColor = BorderColor,
                ),
            singleLine = true,
        )
        if (isNewEmailValid) {
            Spacer(Modifier.height(Dimens.paddingS))
            Row(verticalAlignment = Alignment.CenterVertically) {
                AppIcon(
                    Icons.Filled.CheckCircle,
                    contentDescription = null,
                    tint = Success,
                    size = Dimens.iconSizeSm,
                ) // a11y: decorative — labelled by adjacent Text
                Spacer(Modifier.width(Dimens.paddingXS))
                AppCaptionText(
                    text = stringResource(R.string.change_email_valid),
                    color = Success,
                )
            }
        }

        Spacer(Modifier.height(Dimens.paddingXL))

        FormFieldLabel(text = stringResource(R.string.label_confirm_email), required = true)
        Spacer(Modifier.height(Dimens.paddingS))
        OutlinedTextField(
            value = uiState.confirmEmail,
            onValueChange = { viewModel.updateConfirmEmail(it) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(Dimens.radiusMd),
            leadingIcon = {
                Icon(
                    Icons.Filled.MarkEmailUnread,
                    contentDescription = null,
                    tint = TextSecondary,
                    modifier = Modifier.size(Dimens.iconSizeMd),
                ) // a11y: decorative — labelled by adjacent Text
            },
            placeholder = {
                AppCaptionText(
                    text = stringResource(R.string.label_confirm_email),
                    color = TextHint,
                )
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            colors =
                OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = if (uiState.confirmEmail.isNotBlank() && !emailsMatch) Danger else Primary,
                    unfocusedBorderColor = if (uiState.confirmEmail.isNotBlank() && !emailsMatch) Danger else BorderColor,
                ),
            singleLine = true,
        )
        if (emailsMatch) {
            Spacer(Modifier.height(Dimens.paddingS))
            Row(verticalAlignment = Alignment.CenterVertically) {
                AppIcon(
                    Icons.Filled.CheckCircle,
                    contentDescription = null,
                    tint = Success,
                    size = Dimens.iconSizeSm,
                ) // a11y: decorative — labelled by adjacent Text
                Spacer(Modifier.width(Dimens.paddingXS))
                AppCaptionText(
                    text = stringResource(R.string.change_email_match),
                    color = Success,
                )
            }
        } else if (uiState.confirmEmail.isNotBlank()) {
            Spacer(Modifier.height(Dimens.paddingS))
            Row(verticalAlignment = Alignment.CenterVertically) {
                AppIcon(
                    Icons.Filled.Cancel,
                    contentDescription = null,
                    tint = Danger,
                    size = Dimens.iconSizeSm,
                ) // a11y: decorative — labelled by adjacent Text
                Spacer(Modifier.width(Dimens.paddingXS))
                AppCaptionText(
                    text = stringResource(R.string.change_email_no_match),
                    color = Danger,
                )
            }
        }

        Spacer(Modifier.height(Dimens.paddingXXXL))

        PrimaryButton(
            text = stringResource(R.string.action_save),
            onClick = { viewModel.changeEmail() },
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
