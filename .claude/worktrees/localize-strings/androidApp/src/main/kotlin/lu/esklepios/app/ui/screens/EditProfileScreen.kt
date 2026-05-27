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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import lu.esklepios.app.R
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
    val savedMessage = stringResource(R.string.edit_saved_snackbar)

    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            snackbarHostState.showSnackbar(savedMessage)
            navController.popBackStack()
        }
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let { error ->
            snackbarHostState.showSnackbar(error)
        }
    }

    // Stored values are language-neutral keys; display labels are localized
    val genderOptions = listOf(
        "Male" to R.string.edit_gender_man,
        "Female" to R.string.edit_gender_woman,
        "Other" to R.string.edit_gender_other
    )

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
                title = stringResource(R.string.screen_edit_profile),
                onNavigateBack = { navController.popBackStack() },
                actions = {
                    IconButton(onClick = { viewModel.save() }) {
                        Icon(Icons.Filled.Check, contentDescription = stringResource(R.string.action_save), tint = Color.White)
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
                    label = stringResource(R.string.edit_first_name_label),
                    value = uiState.firstName,
                    onValueChange = { viewModel.updateField(ProfileField.FIRST_NAME, it) },
                    placeholder = "John",
                    isRequired = true
                )
                Spacer(Modifier.height(14.dp))

                FormField(
                    label = stringResource(R.string.edit_last_name_label),
                    value = uiState.lastName,
                    onValueChange = { viewModel.updateField(ProfileField.LAST_NAME, it) },
                    placeholder = "Doe",
                    isRequired = true
                )
                Spacer(Modifier.height(14.dp))

                FormField(
                    label = stringResource(R.string.edit_phone_label),
                    value = uiState.phone,
                    onValueChange = { viewModel.updateField(ProfileField.PHONE, it) },
                    placeholder = "+352 000 000 000",
                    leadingIcon = Icons.Filled.Phone,
                    keyboardType = KeyboardType.Phone
                )
                Spacer(Modifier.height(14.dp))

                Text(
                    stringResource(R.string.edit_gender_label),
                    style = MaterialTheme.typography.labelMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.Medium
                )
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    genderOptions.forEach { (value, labelRes) ->
                        FilterChip(
                            selected = uiState.gender == value,
                            onClick = { viewModel.updateField(ProfileField.GENDER, value) },
                            label = { Text(stringResource(labelRes), fontSize = 13.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Primary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
                Spacer(Modifier.height(14.dp))

                FormField(
                    label = stringResource(R.string.edit_dob_label),
                    value = uiState.dateOfBirth,
                    onValueChange = { viewModel.updateField(ProfileField.DATE_OF_BIRTH, it) },
                    placeholder = stringResource(R.string.edit_dob_placeholder)
                )
                Spacer(Modifier.height(14.dp))

                FormField(
                    label = stringResource(R.string.edit_cns_number),
                    value = uiState.cnsNumber,
                    onValueChange = { viewModel.updateField(ProfileField.CNS_NUMBER, it) },
                    placeholder = "0000000000",
                    keyboardType = KeyboardType.Number
                )
                Spacer(Modifier.height(14.dp))

                Text(
                    stringResource(R.string.label_language),
                    style = MaterialTheme.typography.labelMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.Medium
                )
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Language names are native names shown in their own script — intentionally not translated
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
                    text = stringResource(R.string.action_save),
                    onClick = { viewModel.save() },
                    isLoading = uiState.isLoading,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(24.dp))
            }
        }
    }
}
