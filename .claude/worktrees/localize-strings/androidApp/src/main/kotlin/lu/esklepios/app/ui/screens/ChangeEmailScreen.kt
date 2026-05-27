package lu.esklepios.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import lu.esklepios.app.R
import lu.esklepios.app.presentation.viewmodel.ChangeEmailViewModel
import lu.esklepios.app.ui.components.*
import lu.esklepios.app.ui.theme.*
import org.koin.androidx.compose.koinViewModel

@Composable
fun ChangeEmailScreen(
    navController: NavController,
    viewModel: ChangeEmailViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val successMessage = stringResource(R.string.change_email_success)

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            snackbarHostState.showSnackbar(successMessage)
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
            AppToolbar(title = stringResource(R.string.screen_change_email), onNavigateBack = { navController.popBackStack() })

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(Modifier.height(24.dp))

                AppCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(stringResource(R.string.change_email_current_label), color = TextSecondary, fontSize = 12.sp)
                        Spacer(Modifier.height(4.dp))
                        Text(
                            uiState.currentEmail.ifBlank { stringResource(R.string.change_email_not_available) },
                            fontWeight = FontWeight.Medium,
                            color = TextPrimary,
                            fontSize = 15.sp
                        )
                    }
                }

                Spacer(Modifier.height(20.dp))

                FormField(
                    label = stringResource(R.string.label_new_email),
                    value = uiState.newEmail,
                    onValueChange = { viewModel.updateNewEmail(it) },
                    placeholder = stringResource(R.string.change_email_new_placeholder),
                    leadingIcon = Icons.Filled.Email,
                    keyboardType = KeyboardType.Email,
                    isRequired = true
                )

                Spacer(Modifier.height(14.dp))

                FormField(
                    label = stringResource(R.string.label_current_password),
                    value = uiState.password,
                    onValueChange = { viewModel.updatePassword(it) },
                    placeholder = stringResource(R.string.change_password_current_placeholder),
                    isPassword = true,
                    isRequired = true
                )

                Spacer(Modifier.height(28.dp))

                PrimaryButton(
                    text = stringResource(R.string.action_save),
                    onClick = { viewModel.changeEmail() },
                    isLoading = uiState.isLoading,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(24.dp))
            }
        }
    }
}
