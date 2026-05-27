package lu.esklepios.app.view.auth.register

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.navigation.NavController
import lu.esklepios.app.R
import lu.esklepios.app.presentation.viewmodel.AuthField
import lu.esklepios.app.presentation.viewmodel.AuthViewModel
import lu.esklepios.app.core.ui.components.*
import lu.esklepios.app.core.navigation.NavDestination
import lu.esklepios.app.core.ui.theme.*
import lu.esklepios.app.util.Gender
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel

@Composable
fun RegisterScreen(
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
        title = stringResource(R.string.screen_register),
        onNavigateBack = {
            if (uiState.step > 1) viewModel.setStep(uiState.step - 1)
            else navController.popBackStack()
        },
        error = uiState.error,
        onErrorDismissed = { viewModel.clearError() }
    ) {
        Column(modifier = Modifier.padding(vertical = Dimens.paddingL)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Dimens.paddingS)
            ) {
                repeat(3) { index ->
                    val isActive = index < uiState.step
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(Dimens.progressBarHeight)
                            .background(
                                color = if (isActive) Primary else BorderColor,
                                shape = RoundedCornerShape(Dimens.progressBarRadius)
                            )
                    )
                }
            }
            Spacer(Modifier.height(Dimens.paddingS))
            AppCaptionText(text = stringResource(R.string.register_step_of, uiState.step, 3))
        }

        when (uiState.step) {
            1 -> RegisterStep1(viewModel, uiState.firstName, uiState.lastName, uiState.dateOfBirth, uiState.gender, uiState.cnsNumber)
            2 -> RegisterStep2(viewModel, uiState.email, uiState.phone)
            3 -> RegisterStep3(viewModel, uiState.password, uiState.confirmPassword)
        }

        Spacer(Modifier.height(Dimens.paddingXXL))

        if (uiState.step < 3) {
            PrimaryButton(
                text = stringResource(R.string.action_next),
                onClick = { viewModel.setStep(uiState.step + 1) },
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            PrimaryButton(
                text = stringResource(R.string.action_register),
                onClick = { viewModel.register() },
                isLoading = uiState.isLoading,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(Modifier.height(Dimens.paddingL))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AppBodyText(text = stringResource(R.string.register_have_account) + " ", color = TextSecondary)
            AppTextLink(
                text = stringResource(R.string.action_sign_in),
                onClick = { navController.navigate(NavDestination.Home.route) }
            )
        }

        Spacer(Modifier.height(Dimens.paddingXXL))
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
    AppTitleText(text = stringResource(R.string.register_step_personal))
    Spacer(Modifier.height(Dimens.paddingXL))

    FormField(
        label = stringResource(R.string.label_first_name),
        value = firstName,
        onValueChange = { viewModel.updateField(AuthField.FIRST_NAME, it) },
        placeholder = "John",
        leadingIcon = Icons.Filled.Person,
        isRequired = true
    )
    Spacer(Modifier.height(Dimens.paddingL))
    FormField(
        label = stringResource(R.string.label_last_name),
        value = lastName,
        onValueChange = { viewModel.updateField(AuthField.LAST_NAME, it) },
        placeholder = "Doe",
        leadingIcon = Icons.Filled.Person,
        isRequired = true
    )
    Spacer(Modifier.height(Dimens.paddingL))
    FormField(
        label = stringResource(R.string.label_date_of_birth),
        value = dateOfBirth,
        onValueChange = { viewModel.updateField(AuthField.DATE_OF_BIRTH, it) },
        placeholder = "DD/MM/YYYY",
        isRequired = true
    )
    Spacer(Modifier.height(Dimens.paddingL))

    AppLabelText(text = stringResource(R.string.label_gender))
    Spacer(Modifier.height(Dimens.paddingS))
    Row(horizontalArrangement = Arrangement.spacedBy(Dimens.paddingM)) {
        Gender.entries.forEach { g ->
            val label = stringResource(when (g) {
                Gender.MALE   -> R.string.gender_male
                Gender.FEMALE -> R.string.gender_female
                Gender.OTHER  -> R.string.gender_other
            })
            // Material3 FilterChip (not our custom FilterChip) — label slot requires a Composable
            // and uses its own text slot API. AppCaptionText used inside the label lambda.
            FilterChip(
                selected = gender == g.apiValue,
                onClick = { viewModel.updateField(AuthField.GENDER, g.apiValue) },
                label = { AppCaptionText(text = label) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Primary,
                    selectedLabelColor = Color.White
                )
            )
        }
    }

    Spacer(Modifier.height(Dimens.paddingL))
    FormField(
        label = stringResource(R.string.label_cns_number),
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
    AppTitleText(text = stringResource(R.string.register_step_contact))
    Spacer(Modifier.height(Dimens.paddingXL))

    FormField(
        label = stringResource(R.string.label_email_address),
        value = email,
        onValueChange = { viewModel.updateField(AuthField.EMAIL, it) },
        placeholder = "your@email.com",
        leadingIcon = Icons.Filled.Email,
        keyboardType = KeyboardType.Email,
        isRequired = true
    )
    Spacer(Modifier.height(Dimens.paddingL))
    FormField(
        label = stringResource(R.string.label_phone_number),
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
    confirmPassword: String
) {
    AppTitleText(text = stringResource(R.string.register_step_account))
    Spacer(Modifier.height(Dimens.paddingXL))

    FormField(
        label = stringResource(R.string.label_password),
        value = password,
        onValueChange = { viewModel.updateField(AuthField.PASSWORD, it) },
        placeholder = stringResource(R.string.error_password_too_short),
        leadingIcon = Icons.Filled.Lock,
        isPassword = true,
        isRequired = true
    )
    Spacer(Modifier.height(Dimens.paddingL))
    FormField(
        label = stringResource(R.string.label_confirm_password),
        value = confirmPassword,
        onValueChange = { viewModel.updateField(AuthField.CONFIRM_PASSWORD, it) },
        placeholder = stringResource(R.string.error_password_too_short),
        leadingIcon = Icons.Filled.Lock,
        isPassword = true,
        isRequired = true
    )
}
