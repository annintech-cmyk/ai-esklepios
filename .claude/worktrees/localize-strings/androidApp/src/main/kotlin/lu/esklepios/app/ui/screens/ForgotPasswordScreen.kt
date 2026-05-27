package lu.esklepios.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import lu.esklepios.app.presentation.viewmodel.AuthField
import lu.esklepios.app.presentation.viewmodel.AuthViewModel
import lu.esklepios.app.ui.components.*
import lu.esklepios.app.ui.theme.*
import org.koin.androidx.compose.koinViewModel

@Composable
fun ForgotPasswordScreen(
    navController: NavController,
    viewModel: AuthViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.forgotPasswordSent) {
        if (uiState.forgotPasswordSent) {
            snackbarHostState.showSnackbar("Reset link sent! Check your inbox.")
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
            AppToolbar(title = "Forgot Password", onNavigateBack = { navController.popBackStack() })

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(40.dp))

                // Lock icon illustration
                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .background(color = PrimaryLight, shape = RoundedCornerShape(48.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    if (uiState.forgotPasswordSent) {
                        Icon(
                            Icons.Filled.CheckCircle,
                            contentDescription = null,
                            tint = Success,
                            modifier = Modifier.size(48.dp)
                        )
                    } else {
                        Icon(
                            Icons.Filled.Lock,
                            contentDescription = null,
                            tint = Primary,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))

                if (uiState.forgotPasswordSent) {
                    Text(
                        text = "Check your email",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = "We've sent a password reset link to ${uiState.email}",
                        color = TextSecondary,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(32.dp))
                    PrimaryButton(
                        text = "Back to Sign In",
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    Text(
                        text = "Reset your password",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = "Enter your email address and we'll send you a reset link.",
                        color = TextSecondary,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(32.dp))

                    FormField(
                        label = "Email address",
                        value = uiState.email,
                        onValueChange = { viewModel.updateField(AuthField.EMAIL, it) },
                        placeholder = "your@email.com",
                        leadingIcon = Icons.Filled.Email,
                        keyboardType = KeyboardType.Email,
                        isRequired = true
                    )

                    Spacer(Modifier.height(24.dp))

                    PrimaryButton(
                        text = "Send Reset Link",
                        onClick = { viewModel.forgotPassword() },
                        isLoading = uiState.isLoading,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(Modifier.height(24.dp))
            }
        }
    }
}
