package lu.esklepios.app.view.auth.forgotpassword

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
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
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import lu.esklepios.app.R
import lu.esklepios.app.core.ui.components.AppBodyText
import lu.esklepios.app.core.ui.components.AppFormScreen
import lu.esklepios.app.core.ui.components.AppIcon
import lu.esklepios.app.core.ui.components.AppTitleText
import lu.esklepios.app.core.ui.components.FormField
import lu.esklepios.app.core.ui.components.PrimaryButton
import lu.esklepios.app.core.ui.theme.Dimens
import lu.esklepios.app.core.ui.theme.Primary
import lu.esklepios.app.core.ui.theme.PrimaryLight
import lu.esklepios.app.core.ui.theme.Success
import lu.esklepios.app.presentation.viewmodel.AuthField
import lu.esklepios.app.presentation.viewmodel.AuthViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun ForgotPasswordScreen(
    navController: NavController,
    viewModel: AuthViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val sentMessage = stringResource(R.string.forgot_password_sent)

    LaunchedEffect(uiState.forgotPasswordSent) {
        if (uiState.forgotPasswordSent) {
            snackbarHostState.showSnackbar(sentMessage)
        }
    }

    AppFormScreen(
        title = stringResource(R.string.screen_forgot_password),
        onNavigateBack = { navController.popBackStack() },
        error = uiState.error,
        onErrorDismissed = { viewModel.clearError() },
        snackbarHostState = snackbarHostState,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(Dimens.paddingXXXL + Dimens.paddingS))

        Box(
            modifier =
                Modifier
                    .size(Dimens.avatarSizeLg + Dimens.paddingXXXL)
                    .background(color = PrimaryLight, shape = CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            if (uiState.forgotPasswordSent) {
                AppIcon(
                    Icons.Filled.CheckCircle,
                    // a11y: decorative — labelled by adjacent Text
                    contentDescription = null,
                    tint = Success,
                    size = Dimens.avatarSizeMd,
                )
            } else {
                AppIcon(
                    Icons.Filled.Lock,
                    // a11y: decorative — labelled by adjacent Text
                    contentDescription = null,
                    tint = Primary,
                    size = Dimens.avatarSizeMd,
                )
            }
        }

        Spacer(Modifier.height(Dimens.paddingXXL))

        if (uiState.forgotPasswordSent) {
            AppTitleText(
                text = stringResource(R.string.forgot_password_sent_title),
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(Dimens.paddingM))
            AppBodyText(
                text = stringResource(R.string.forgot_password_sent_subtitle, uiState.email),
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(Dimens.paddingXXXL))
            PrimaryButton(
                text = stringResource(R.string.forgot_password_back),
                onClick = { navController.popBackStack() },
                modifier = Modifier.fillMaxWidth(),
            )
        } else {
            AppTitleText(
                text = stringResource(R.string.forgot_password_title),
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(Dimens.paddingM))
            AppBodyText(
                text = stringResource(R.string.forgot_password_description),
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(Dimens.paddingXXXL))

            FormField(
                label = stringResource(R.string.label_email_address),
                value = uiState.email,
                onValueChange = { viewModel.updateField(AuthField.EMAIL, it) },
                placeholder = "your@email.com",
                leadingIcon = Icons.Filled.Email,
                keyboardType = KeyboardType.Email,
                isRequired = true,
            )

            Spacer(Modifier.height(Dimens.paddingXXL))

            PrimaryButton(
                text = stringResource(R.string.forgot_password_send_link),
                onClick = { viewModel.forgotPassword() },
                isLoading = uiState.isLoading,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        Spacer(Modifier.height(Dimens.paddingXXL))
    }
}
