package lu.esklepios.app.view.auth.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.navigation.NavController
import lu.esklepios.app.R
import lu.esklepios.app.presentation.viewmodel.AuthField
import lu.esklepios.app.presentation.viewmodel.AuthViewModel
import lu.esklepios.app.core.ui.components.*
import lu.esklepios.app.core.navigation.NavDestination
import lu.esklepios.app.core.ui.theme.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel
import androidx.compose.material.icons.filled.Lock

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: AuthViewModel = koinViewModel()
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
        onErrorDismissed = { viewModel.clearError() }
    ) {
        Spacer(Modifier.height(Dimens.paddingXXL))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = WarningBg, shape = RoundedCornerShape(Dimens.radiusMd))
                .padding(Dimens.paddingM),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Dimens.paddingM)
        ) {
            AppIcon(Icons.Filled.Shield, contentDescription = null, tint = Warning, size = Dimens.iconSizeMd) // a11y: decorative — labelled by adjacent Text
            AppCaptionText(
                text = stringResource(R.string.auth_security_banner),
                color = Warning
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
            isRequired = true
        )

        Spacer(Modifier.height(Dimens.paddingL))

        FormField(
            label = stringResource(R.string.label_password),
            value = uiState.password,
            onValueChange = { viewModel.updateField(AuthField.PASSWORD, it) },
            placeholder = stringResource(R.string.label_password),
            leadingIcon = Icons.Filled.Lock,
            isPassword = true,
            isRequired = true
        )

        Spacer(Modifier.height(Dimens.paddingS))

        AppTextLink(
            text = stringResource(R.string.login_forgot_password),
            onClick = { navController.navigate(NavDestination.ForgotPassword.route) },
            modifier = Modifier.align(Alignment.End)
        )

        Spacer(Modifier.height(Dimens.paddingXL))

        PrimaryButton(
            text = stringResource(R.string.action_sign_in),
            onClick = { viewModel.login() },
            isLoading = uiState.isLoading,
            modifier = Modifier.fillMaxWidth()
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
            verticalAlignment = Alignment.CenterVertically
        ) {
            AppBodyText(
                text = stringResource(R.string.login_no_account) + " ",
                color = TextSecondary
            )
            AppTextLink(
                text = stringResource(R.string.action_register),
                onClick = { navController.navigate(NavDestination.Register.route) }
            )
        }

        Spacer(Modifier.height(Dimens.paddingXXL))
    }
}
