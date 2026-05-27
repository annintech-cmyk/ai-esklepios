package lu.esklepios.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import lu.esklepios.app.presentation.viewmodel.ChangePasswordViewModel
import lu.esklepios.app.ui.components.*
import lu.esklepios.app.ui.theme.*
import org.koin.androidx.compose.koinViewModel

@Composable
fun ChangePasswordScreen(
    navController: NavController,
    viewModel: ChangePasswordViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            snackbarHostState.showSnackbar("Password updated successfully!")
            navController.popBackStack()
        }
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let { error ->
            snackbarHostState.showSnackbar(error)
            viewModel.clearError()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Background)
        ) {
            AppToolbar(title = "Change Password", onNavigateBack = { navController.popBackStack() })

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(Modifier.height(24.dp))

                FormField(
                    label = "Current password",
                    value = uiState.oldPassword,
                    onValueChange = { viewModel.updateOldPassword(it) },
                    placeholder = "Enter your current password",
                    isPassword = true,
                    isRequired = true
                )

                Spacer(Modifier.height(14.dp))

                FormField(
                    label = "New password",
                    value = uiState.newPassword,
                    onValueChange = { viewModel.updateNewPassword(it) },
                    placeholder = "At least 8 characters",
                    isPassword = true,
                    isRequired = true
                )

                // Password strength indicator
                if (uiState.newPassword.isNotBlank()) {
                    Spacer(Modifier.height(8.dp))
                    PasswordStrengthIndicator(password = uiState.newPassword)
                }

                Spacer(Modifier.height(14.dp))

                FormField(
                    label = "Confirm new password",
                    value = uiState.confirmPassword,
                    onValueChange = { viewModel.updateConfirmPassword(it) },
                    placeholder = "Re-enter your new password",
                    isPassword = true,
                    isRequired = true,
                    errorMessage = if (uiState.confirmPassword.isNotBlank() && uiState.confirmPassword != uiState.newPassword)
                        "Passwords do not match" else null
                )

                Spacer(Modifier.height(28.dp))

                PrimaryButton(
                    text = "Update Password",
                    onClick = { viewModel.changePassword() },
                    isLoading = uiState.isLoading,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun PasswordStrengthIndicator(password: String) {
    val strength = when {
        password.length >= 12 && password.any { it.isDigit() } && password.any { it.isUpperCase() } && password.any { !it.isLetterOrDigit() } -> 4
        password.length >= 10 && (password.any { it.isDigit() } || password.any { it.isUpperCase() }) -> 3
        password.length >= 8 -> 2
        else -> 1
    }

    val (color, label) = when (strength) {
        4 -> Pair(Success, "Strong")
        3 -> Pair(Color(0xFF65A30D), "Good")
        2 -> Pair(Warning, "Fair")
        else -> Pair(Danger, "Weak")
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        repeat(4) { index ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(4.dp)
                    .background(
                        color = if (index < strength) color else BorderColor,
                        shape = RoundedCornerShape(2.dp)
                    )
            )
        }
    }
    Spacer(Modifier.height(4.dp))
    Text(label, color = color, fontSize = 12.sp, fontWeight = FontWeight.Medium)
}
