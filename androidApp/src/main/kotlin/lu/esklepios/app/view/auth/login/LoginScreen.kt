package lu.esklepios.app.view.auth.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import lu.esklepios.app.R
import lu.esklepios.app.core.navigation.NavDestination
import lu.esklepios.app.core.ui.components.AppBodyText
import lu.esklepios.app.core.ui.components.AppCaptionText
import lu.esklepios.app.core.ui.components.AppFormScreen
import lu.esklepios.app.core.ui.components.AppIcon
import lu.esklepios.app.core.ui.components.AppTextLink
import lu.esklepios.app.core.ui.components.AppleSignInButton
import lu.esklepios.app.core.ui.components.DividerWithLabel
import lu.esklepios.app.core.ui.components.FormField
import lu.esklepios.app.core.ui.components.GoogleSignInButton
import lu.esklepios.app.core.ui.components.PrimaryButton
import lu.esklepios.app.core.ui.theme.Dimens
import lu.esklepios.app.core.ui.theme.TextSecondary
import lu.esklepios.app.core.ui.theme.Warning
import lu.esklepios.app.core.ui.theme.WarningBg
import lu.esklepios.app.presentation.viewmodel.AuthField
import lu.esklepios.app.presentation.viewmodel.AuthViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: AuthViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.isLoggedIn) {
        if (uiState.isLoggedIn) {
            navController.navigate(NavDestination.Home.route) {
                popUpTo(NavDestination.Landing.route) { inclusive = true }
            }
        }
    }

    AppFormScreen(
        title = stringResource(R.string.screen_login),
        onNavigateBack = { navController.popBackStack() },
        error = uiState.error,
        onErrorDismissed = { viewModel.clearError() },
    ) {
        Spacer(Modifier.height(Dimens.paddingXXL))

        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .background(color = WarningBg, shape = RoundedCornerShape(Dimens.radiusMd))
                    .padding(Dimens.paddingM),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Dimens.paddingM),
        ) {
            AppIcon(
                Icons.Filled.Shield,
                contentDescription = null,
                tint = Warning,
                size = Dimens.iconSizeMd,
            ) // a11y: decorative — labelled by adjacent Text
            AppCaptionText(
                text = stringResource(R.string.auth_security_banner),
                color = Warning,
            )
        }

        Spacer(Modifier.height(Dimens.paddingXXL))

        FormField(
            label = stringResource(R.string.label_email_address),
            value = uiState.email,
            onValueChange = { viewModel.updateField(AuthField.EMAIL, it) },
            placeholder = "your@email.com",
            leadingIcon = Icons.Filled.Email,
            keyboardType = KeyboardType.Email,
            isRequired = true,
        )

        Spacer(Modifier.height(Dimens.paddingL))

        FormField(
            label = stringResource(R.string.label_password),
            value = uiState.password,
            onValueChange = { viewModel.updateField(AuthField.PASSWORD, it) },
            placeholder = stringResource(R.string.label_password),
            leadingIcon = Icons.Filled.Lock,
            isPassword = true,
            isRequired = true,
        )

        Spacer(Modifier.height(Dimens.paddingS))

        AppTextLink(
            text = stringResource(R.string.login_forgot_password),
            onClick = { navController.navigate(NavDestination.ForgotPassword.route) },
            modifier = Modifier.align(Alignment.End),
        )

        Spacer(Modifier.height(Dimens.paddingXL))

        PrimaryButton(
            text = stringResource(R.string.action_sign_in),
            onClick = { viewModel.login() },
            isLoading = uiState.isLoading,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(Modifier.height(Dimens.paddingXXL))

        DividerWithLabel(label = stringResource(R.string.login_or))

        Spacer(Modifier.height(Dimens.paddingL))

        GoogleSignInButton(onClick = {}, modifier = Modifier.fillMaxWidth())

        Spacer(Modifier.height(Dimens.paddingM))

        AppleSignInButton(onClick = {}, modifier = Modifier.fillMaxWidth())

        Spacer(Modifier.height(Dimens.paddingXXXL))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AppBodyText(
                text = stringResource(R.string.login_no_account) + " ",
                color = TextSecondary,
            )
            AppTextLink(
                text = stringResource(R.string.action_register),
                onClick = { navController.navigate(NavDestination.Register.route) },
            )
        }

        Spacer(Modifier.height(Dimens.paddingXXL))
    }

    if (uiState.showSaveCredentialsDialog) {
        AlertDialog(
            title = { Text("Save Credentials?") },
            text = { Text("Would you like to save your email and password for faster login next time?") },
            onDismissRequest = { viewModel.skipSaveCredentials() },
            confirmButton = {
                PrimaryButton(
                    text = "Save",
                    onClick = { viewModel.saveCredentials() },
                )
            },
            dismissButton = {
                PrimaryButton(
                    text = "Not Now",
                    onClick = { viewModel.skipSaveCredentials() },
                )
            },
        )
    }
}
