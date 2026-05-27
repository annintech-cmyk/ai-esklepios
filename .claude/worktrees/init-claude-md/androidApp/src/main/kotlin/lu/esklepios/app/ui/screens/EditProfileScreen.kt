package lu.esklepios.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import lu.esklepios.app.presentation.viewmodel.EditProfileViewModel
import lu.esklepios.app.presentation.viewmodel.ProfileField
import lu.esklepios.app.ui.components.*
import lu.esklepios.app.ui.theme.*
import org.koin.androidx.compose.koinViewModel

@Composable
fun EditProfileScreen(
    navController: NavController,
    viewModel: EditProfileViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            snackbarHostState.showSnackbar("Profile saved successfully!")
            navController.popBackStack()
        }
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let { error ->
            snackbarHostState.showSnackbar(error)
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
                title = "Edit Profile",
                onNavigateBack = { navController.popBackStack() },
                actions = {
                    IconButton(onClick = { viewModel.save() }) {
                        Icon(Icons.Filled.Check, contentDescription = "Save", tint = Color.White)
                    }
                }
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(Modifier.height(20.dp))

                FormField(
                    label = "First name",
                    value = uiState.firstName,
                    onValueChange = { viewModel.updateField(ProfileField.FIRST_NAME, it) },
                    placeholder = "John",
                    isRequired = true
                )
                Spacer(Modifier.height(14.dp))

                FormField(
                    label = "Last name",
                    value = uiState.lastName,
                    onValueChange = { viewModel.updateField(ProfileField.LAST_NAME, it) },
                    placeholder = "Doe",
                    isRequired = true
                )
                Spacer(Modifier.height(14.dp))

                FormField(
                    label = "Phone number",
                    value = uiState.phone,
                    onValueChange = { viewModel.updateField(ProfileField.PHONE, it) },
                    placeholder = "+352 000 000 000",
                    leadingIcon = Icons.Filled.Phone,
                    keyboardType = KeyboardType.Phone
                )
                Spacer(Modifier.height(14.dp))

                Text(
                    "Gender",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.Medium
                )
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    listOf("Male", "Female", "Other").forEach { g ->
                        FilterChip(
                            selected = uiState.gender == g,
                            onClick = { viewModel.updateField(ProfileField.GENDER, g) },
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
                    label = "Date of birth",
                    value = uiState.dateOfBirth,
                    onValueChange = { viewModel.updateField(ProfileField.DATE_OF_BIRTH, it) },
                    placeholder = "DD/MM/YYYY"
                )
                Spacer(Modifier.height(14.dp))

                FormField(
                    label = "CNS number",
                    value = uiState.cnsNumber,
                    onValueChange = { viewModel.updateField(ProfileField.CNS_NUMBER, it) },
                    placeholder = "0000000000",
                    keyboardType = KeyboardType.Number
                )
                Spacer(Modifier.height(14.dp))

                Text(
                    "Preferred language",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.Medium
                )
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("en" to "English", "fr" to "Français", "de" to "Deutsch", "lb" to "Lëtzebuergesch").forEach { (code, label) ->
                        FilterChip(
                            selected = uiState.language == code,
                            onClick = { viewModel.updateField(ProfileField.LANGUAGE, code) },
                            label = { Text(label, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Primary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                Spacer(Modifier.height(32.dp))

                PrimaryButton(
                    text = "Save Changes",
                    onClick = { viewModel.save() },
                    isLoading = uiState.isLoading,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(24.dp))
            }
        }
    }
}
