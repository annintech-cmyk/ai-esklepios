package lu.esklepios.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Shield
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
import lu.esklepios.app.ui.navigation.NavDestination
import lu.esklepios.app.ui.theme.*
import org.koin.androidx.compose.koinViewModel

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: AuthViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.isLoggedIn) {
        if (uiState.isLoggedIn) {
            navController.navigate(NavDestination.Home.route) {
                popUpTo(NavDestination.Landing.route) { inclusive = true }
            }
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
            AppToolbar(title = "Sign In", onNavigateBack = { navController.popBackStack() })

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
            ) {
                Spacer(Modifier.height(24.dp))

                // Security banner
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color = WarningBg, shape = RoundedCornerShape(12.dp))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(Icons.Filled.Shield, contentDescription = null, tint = Warning, modifier = Modifier.size(20.dp))
                    Text(
                        text = "This is the official eSklepios app. Never share your password.",
                        fontSize = 12.sp,
                        color = Warning,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(Modifier.height(24.dp))

                FormField(
                    label = "Email address",
                    value = uiState.email,
                    onValueChange = { viewModel.updateField(AuthField.EMAIL, it) },
                    placeholder = "your@email.com",
                    leadingIcon = Icons.Filled.Email,
                    keyboardType = KeyboardType.Email,
                    isRequired = true
                )

                Spacer(Modifier.height(16.dp))

                FormField(
                    label = "Password",
                    value = uiState.password,
                    onValueChange = { viewModel.updateField(AuthField.PASSWORD, it) },
                    placeholder = "Enter your password",
                    isPassword = true,
                    isRequired = true
                )

                Spacer(Modifier.height(8.dp))

                TextButton(
                    onClick = { navController.navigate(NavDestination.ForgotPassword.route) },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Forgot password?", color = Primary, fontSize = 13.sp)
                }

                Spacer(Modifier.height(20.dp))

                PrimaryButton(
                    text = "Sign In",
                    onClick = { viewModel.login() },
                    isLoading = uiState.isLoading,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Divider(modifier = Modifier.weight(1f), color = BorderColor)
                    Text(
                        text = "  or  ",
                        color = TextSecondary,
                        fontSize = 13.sp
                    )
                    Divider(modifier = Modifier.weight(1f), color = BorderColor)
                }

                Spacer(Modifier.height(16.dp))

                GoogleSignInButton(
                    onClick = {},
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(10.dp))

                AppleSignInButton(
                    onClick = {},
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(32.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("No account? ", color = TextSecondary, fontSize = 14.sp)
                    TextButton(onClick = { navController.navigate(NavDestination.Register.route) }) {
                        Text("Register", color = Primary, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    }
                }

                Spacer(Modifier.height(24.dp))
            }
        }
    }
}
