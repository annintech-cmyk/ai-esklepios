package lu.esklepios.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import lu.esklepios.app.domain.model.ProfileType
import lu.esklepios.app.presentation.viewmodel.AuthField
import lu.esklepios.app.presentation.viewmodel.AuthViewModel
import lu.esklepios.app.ui.components.*
import lu.esklepios.app.ui.navigation.NavDestination
import lu.esklepios.app.ui.theme.*
import org.koin.androidx.compose.koinViewModel

@Composable
fun RegisterScreen(
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
            AppToolbar(
                title = "Create Account",
                onNavigateBack = {
                    if (uiState.step > 1) viewModel.setStep(uiState.step - 1)
                    else navController.popBackStack()
                }
            )

            // Progress indicator
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    repeat(3) { index ->
                        val isActive = index < uiState.step
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(4.dp)
                                .background(
                                    color = if (isActive) Primary else BorderColor,
                                    shape = RoundedCornerShape(2.dp)
                                )
                        )
                    }
                }
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "Step ${uiState.step} of 3",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
            ) {
                when (uiState.step) {
                    1 -> RegisterStep1(viewModel, uiState.firstName, uiState.lastName, uiState.dateOfBirth, uiState.gender, uiState.cnsNumber)
                    2 -> RegisterStep2(viewModel, uiState.email, uiState.phone)
                    3 -> RegisterStep3(viewModel, uiState.password, uiState.profileType)
                }

                Spacer(Modifier.height(24.dp))

                if (uiState.step < 3) {
                    PrimaryButton(
                        text = "Next",
                        onClick = { viewModel.setStep(uiState.step + 1) },
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    PrimaryButton(
                        text = "Create Account",
                        onClick = { viewModel.register() },
                        isLoading = uiState.isLoading,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Already have an account? ", color = TextSecondary, fontSize = 14.sp)
                    TextButton(onClick = { navController.navigate(NavDestination.Login.route) }) {
                        Text("Sign In", color = Primary, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    }
                }

                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun RegisterStep1(
    viewModel: AuthViewModel,
    firstName: String,
    lastName: String,
    dateOfBirth: String,
    gender: String,
    cnsNumber: String
) {
    Text("Personal Information", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
    Spacer(Modifier.height(20.dp))

    FormField(
        label = "First name",
        value = firstName,
        onValueChange = { viewModel.updateField(AuthField.FIRST_NAME, it) },
        placeholder = "John",
        leadingIcon = Icons.Filled.Person,
        isRequired = true
    )
    Spacer(Modifier.height(14.dp))
    FormField(
        label = "Last name",
        value = lastName,
        onValueChange = { viewModel.updateField(AuthField.LAST_NAME, it) },
        placeholder = "Doe",
        leadingIcon = Icons.Filled.Person,
        isRequired = true
    )
    Spacer(Modifier.height(14.dp))
    FormField(
        label = "Date of birth",
        value = dateOfBirth,
        onValueChange = { viewModel.updateField(AuthField.DATE_OF_BIRTH, it) },
        placeholder = "DD/MM/YYYY",
        isRequired = true
    )
    Spacer(Modifier.height(14.dp))

    Text("Gender", style = MaterialTheme.typography.labelMedium, color = TextPrimary, fontWeight = FontWeight.Medium)
    Spacer(Modifier.height(8.dp))
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        listOf("Male", "Female", "Other").forEach { g ->
            FilterChip(
                selected = gender == g,
                onClick = { viewModel.updateField(AuthField.GENDER, g) },
                label = { Text(g, fontSize = 13.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Primary,
                    selectedLabelColor = Color.White
                )
            )
        }
    }

    Spacer(Modifier.height(14.dp))
    FormField(
        label = "CNS number",
        value = cnsNumber,
        onValueChange = { viewModel.updateField(AuthField.CNS_NUMBER, it) },
        placeholder = "0000000000",
        keyboardType = KeyboardType.Number
    )
}

@Composable
private fun RegisterStep2(
    viewModel: AuthViewModel,
    email: String,
    phone: String
) {
    Text("Contact Information", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
    Spacer(Modifier.height(20.dp))

    FormField(
        label = "Email address",
        value = email,
        onValueChange = { viewModel.updateField(AuthField.EMAIL, it) },
        placeholder = "your@email.com",
        leadingIcon = Icons.Filled.Email,
        keyboardType = KeyboardType.Email,
        isRequired = true
    )
    Spacer(Modifier.height(14.dp))
    FormField(
        label = "Phone number",
        value = phone,
        onValueChange = { viewModel.updateField(AuthField.PHONE, it) },
        placeholder = "+352 000 000 000",
        leadingIcon = Icons.Filled.Phone,
        keyboardType = KeyboardType.Phone
    )
}

@Composable
private fun RegisterStep3(
    viewModel: AuthViewModel,
    password: String,
    profileType: ProfileType
) {
    Text("Account Setup", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
    Spacer(Modifier.height(20.dp))

    FormField(
        label = "Password",
        value = password,
        onValueChange = { viewModel.updateField(AuthField.PASSWORD, it) },
        placeholder = "At least 8 characters",
        isPassword = true,
        isRequired = true
    )

    Spacer(Modifier.height(20.dp))

    Text("Profile type", style = MaterialTheme.typography.labelMedium, color = TextPrimary, fontWeight = FontWeight.Medium)
    Spacer(Modifier.height(8.dp))

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        listOf(
            ProfileType.PATIENT to "Patient",
            ProfileType.PRACTITIONER to "Practitioner"
        ).forEach { (type, label) ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = profileType == type,
                    onClick = {},
                    colors = RadioButtonDefaults.colors(selectedColor = Primary)
                )
                Spacer(Modifier.width(8.dp))
                Text(label, color = TextPrimary, fontSize = 14.sp)
            }
        }
    }
}
