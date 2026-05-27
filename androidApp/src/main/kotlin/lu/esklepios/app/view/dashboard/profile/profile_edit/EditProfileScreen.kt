package lu.esklepios.app.view.dashboard.profile.profile_edit

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import lu.esklepios.app.R
import lu.esklepios.app.core.ui.components.*
import lu.esklepios.app.core.ui.theme.*
import lu.esklepios.app.presentation.viewmodel.EditProfileViewModel
import lu.esklepios.app.util.Gender
import lu.esklepios.app.util.PhoneParser
import lu.esklepios.app.util.supportedDialCodes
import org.koin.androidx.compose.koinViewModel
import java.time.Instant
import java.time.ZoneOffset

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    navController: NavController,
    viewModel: EditProfileViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val successMessage = stringResource(R.string.edit_profile_saved)

    var selectedGender by remember { mutableStateOf<Gender?>(null) }
    var dobValue by remember { mutableStateOf("") }
    var firstNameValue by remember { mutableStateOf("") }
    var lastNameValue by remember { mutableStateOf("") }
    var selectedDialCode by remember { mutableStateOf(supportedDialCodes.first()) }
    var phoneNumber by remember { mutableStateOf("") }
    var cnsValue by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }
    var prefixExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.firstName, uiState.lastName) {
        if (firstNameValue.isBlank() && uiState.firstName.isNotBlank()) {
            firstNameValue = uiState.firstName
            lastNameValue = uiState.lastName
        }
    }
    LaunchedEffect(uiState.phone) {
        if (phoneNumber.isBlank() && uiState.phone.isNotBlank()) {
            val (dialCode, rest) = PhoneParser.parse(uiState.phone)
            selectedDialCode = dialCode ?: supportedDialCodes.first()
            phoneNumber = rest
        }
    }
    LaunchedEffect(uiState.gender) {
        if (selectedGender == null) {
            selectedGender = Gender.fromApiString(uiState.gender)
        }
    }
    LaunchedEffect(uiState.dateOfBirth) {
        if (dobValue.isBlank() && uiState.dateOfBirth.isNotBlank()) dobValue = uiState.dateOfBirth
    }
    LaunchedEffect(uiState.cnsNumber) {
        if (cnsValue.isBlank() && uiState.cnsNumber.isNotBlank()) cnsValue = uiState.cnsNumber
    }

    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            snackbarHostState.showSnackbar(successMessage)
            navController.popBackStack()
        }
    }

    val dobDisplay =
        dobValue.takeIf { it.isNotBlank() }?.let { dob ->
            val parts = dob.split("-")
            if (parts.size == 3) "${parts[2]} / ${parts[1]} / ${parts[0]}" else dob
        } ?: ""

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                GhostButton(
                    text = stringResource(R.string.action_confirm),
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val date = Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC).toLocalDate()
                            dobValue = "${date.year}-${date.monthValue.toString().padStart(2, '0')}-${date.dayOfMonth.toString().padStart(2, '0')}"
                        }
                        showDatePicker = false
                    },
                )
            },
            dismissButton = {
                GhostButton(
                    text = stringResource(R.string.action_cancel),
                    onClick = { showDatePicker = false },
                    textColor = TextSecondary,
                )
            },
        ) {
            DatePicker(state = datePickerState)
        }
    }

    fun save() {
        viewModel.saveProfile(
            firstName = firstNameValue,
            lastName = lastNameValue,
            phone = "${selectedDialCode.code} $phoneNumber",
            dateOfBirth = dobValue,
            gender = selectedGender?.apiValue ?: Gender.OTHER.apiValue,
            address = "",
        )
    }

    AppFormScreen(
        title = stringResource(R.string.screen_edit_profile),
        onNavigateBack = { navController.popBackStack() },
        error = uiState.error,
        onErrorDismissed = { viewModel.clearError() },
        snackbarHostState = snackbarHostState,
    ) {
        VSpace(Dimens.paddingXXL)

        FormFieldLabel(text = stringResource(R.string.label_gender), required = true)
        VSpace(Dimens.paddingS)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(Dimens.paddingNone)) {
            Gender.entries.forEachIndexed { index, gender ->
                val isSelected = selectedGender == gender
                val genderLabel =
                    when (gender) {
                        Gender.MALE -> stringResource(R.string.gender_male)
                        Gender.FEMALE -> stringResource(R.string.gender_female)
                        Gender.OTHER -> stringResource(R.string.gender_other)
                    }
                val shape =
                    when (index) {
                        0 -> RoundedCornerShape(topStart = Dimens.radiusMd, bottomStart = Dimens.radiusMd)
                        Gender.entries.lastIndex -> RoundedCornerShape(topEnd = Dimens.radiusMd, bottomEnd = Dimens.radiusMd)
                        else -> RoundedCornerShape(Dimens.cornerNone)
                    }
                Box(
                    modifier =
                        Modifier
                            .weight(1f)
                            .height(Dimens.paddingXL + Dimens.paddingXXL)
                            .background(if (isSelected) Primary else Surface, shape)
                            .border(Dimens.borderThin, if (isSelected) Primary else BorderColor, shape)
                            .clickable { selectedGender = gender },
                    contentAlignment = Alignment.Center,
                ) {
                    AppBodyText(
                        text = genderLabel,
                        color = if (isSelected) Color.White else TextPrimary,
                    )
                }
            }
        }

        VSpace(Dimens.paddingXL)

        FormFieldLabel(text = stringResource(R.string.label_date_of_birth), required = true)
        VSpace(Dimens.paddingS)
        OutlinedTextField(
            value = dobDisplay,
            onValueChange = {},
            readOnly = true,
            modifier = Modifier.fillMaxWidth().clickable { showDatePicker = true },
            shape = RoundedCornerShape(Dimens.radiusMd),
            leadingIcon = {
                Icon(
                    Icons.Filled.CalendarMonth,
                    contentDescription = null,
                    tint = TextSecondary,
                    modifier = Modifier.size(Dimens.iconSizeMd),
                ) // a11y: decorative — labelled by adjacent Text
            },
            placeholder = {
                AppCaptionText(text = stringResource(R.string.edit_placeholder_dob), color = TextHint)
            },
            colors =
                OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Primary,
                    unfocusedBorderColor = BorderColor,
                    disabledBorderColor = BorderColor,
                ),
            enabled = false,
        )

        VSpace(Dimens.paddingXL)

        FormFieldLabel(text = stringResource(R.string.label_first_name), required = true)
        VSpace(Dimens.paddingS)
        OutlinedTextField(
            value = firstNameValue,
            onValueChange = { firstNameValue = it },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(Dimens.radiusMd),
            placeholder = { AppCaptionText(text = stringResource(R.string.edit_placeholder_first_name), color = TextHint) },
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary, unfocusedBorderColor = BorderColor),
        )

        VSpace(Dimens.paddingXL)

        FormFieldLabel(text = stringResource(R.string.label_last_name), required = true)
        VSpace(Dimens.paddingS)
        OutlinedTextField(
            value = lastNameValue,
            onValueChange = { lastNameValue = it },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(Dimens.radiusMd),
            placeholder = { AppCaptionText(text = stringResource(R.string.edit_placeholder_last_name), color = TextHint) },
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary, unfocusedBorderColor = BorderColor),
        )

        VSpace(Dimens.paddingXL)

        FormFieldLabel(text = stringResource(R.string.label_phone_number), required = true)
        VSpace(Dimens.paddingS)
        Row(horizontalArrangement = Arrangement.spacedBy(Dimens.paddingS)) {
            ExposedDropdownMenuBox(
                expanded = prefixExpanded,
                onExpandedChange = { prefixExpanded = !prefixExpanded },
                modifier = Modifier.width(Dimens.countryCodeWidth),
            ) {
                OutlinedTextField(
                    value = "${selectedDialCode.flagEmoji} ${selectedDialCode.code}",
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = prefixExpanded) },
                    modifier = Modifier.menuAnchor(),
                    shape = RoundedCornerShape(Dimens.radiusMd),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary, unfocusedBorderColor = BorderColor),
                )
                ExposedDropdownMenu(expanded = prefixExpanded, onDismissRequest = { prefixExpanded = false }) {
                    supportedDialCodes.forEach { dial ->
                        DropdownMenuItem(
                            text = { AppBodyText(text = "${dial.flagEmoji} ${dial.code}") },
                            onClick = {
                                selectedDialCode = dial
                                prefixExpanded = false
                            },
                        )
                    }
                }
            }
            OutlinedTextField(
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(Dimens.radiusMd),
                placeholder = { AppCaptionText(text = stringResource(R.string.edit_placeholder_phone), color = TextHint) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary, unfocusedBorderColor = BorderColor),
            )
        }

        VSpace(Dimens.paddingXL)

        FormFieldLabel(text = stringResource(R.string.label_cns_number), required = false)
        VSpace(Dimens.paddingS)
        OutlinedTextField(
            value = cnsValue,
            onValueChange = { cnsValue = it },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(Dimens.radiusMd),
            leadingIcon = {
                Icon(
                    Icons.Filled.Badge,
                    contentDescription = null,
                    tint = TextSecondary,
                    modifier = Modifier.size(Dimens.iconSizeMd),
                ) // a11y: decorative — labelled by adjacent Text
            },
            placeholder = { AppCaptionText(text = stringResource(R.string.edit_placeholder_cns), color = TextHint) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary, unfocusedBorderColor = BorderColor),
        )

        VSpace(Dimens.paddingXXXL)

        PrimaryButton(
            text = stringResource(R.string.action_save),
            onClick = { save() },
            modifier = Modifier.fillMaxWidth(),
            isLoading = uiState.isLoading,
        )

        VSpace(Dimens.paddingM)

        SecondaryButton(
            text = stringResource(R.string.action_cancel),
            onClick = { navController.popBackStack() },
            modifier = Modifier.fillMaxWidth(),
        )

        VSpace(Dimens.paddingXXXL)
    }
}
