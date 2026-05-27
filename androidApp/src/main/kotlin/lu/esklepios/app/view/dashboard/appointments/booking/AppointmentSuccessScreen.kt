package lu.esklepios.app.view.dashboard.appointments.booking

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavController
import lu.esklepios.app.R
import lu.esklepios.app.presentation.viewmodel.AppointmentSuccessViewModel
import lu.esklepios.app.core.ui.components.*
import lu.esklepios.app.core.navigation.NavDestination
import lu.esklepios.app.core.ui.theme.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel

@Composable
fun AppointmentSuccessScreen(
    navController: NavController,
    appointmentId: String,
    viewModel: AppointmentSuccessViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(appointmentId) {
        viewModel.setAppointmentData(
            id = appointmentId,
            practitionerName = uiState.practitionerName.ifBlank { "Your Practitioner" },
            dateTime = uiState.dateTime.ifBlank { "Scheduled" },
            clinicName = uiState.clinicName.ifBlank { "Clinic" }
        )
    }

    val scale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "checkmark_scale"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(horizontal = Dimens.paddingXXL),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(Dimens.avatarSizeXl + Dimens.paddingXXXL + Dimens.paddingXS)
                .scale(scale)
                .background(color = SuccessBg, shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            AppIcon(
                Icons.Filled.CheckCircle,
                contentDescription = null, // a11y: decorative — labelled by adjacent Text
                tint = Success,
                size = Dimens.avatarSizeLg + Dimens.paddingS
            )
        }

        Spacer(Modifier.height(Dimens.cardOverlap))

        AppTitleText(
            text = stringResource(R.string.success_title),
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(Dimens.paddingM))

        AppSubtitleText(
            text = stringResource(R.string.success_subtitle),
            color = TextSecondary,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(Dimens.paddingXXXL))

        AppCard(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(Dimens.paddingXL),
                verticalArrangement = Arrangement.spacedBy(Dimens.paddingM)
            ) {
                if (uiState.practitionerName.isNotBlank()) {
                    SuccessDetailRow(
                        icon = Icons.Filled.Person,
                        label = stringResource(R.string.label_practitioner),
                        value = uiState.practitionerName
                    )
                }
                if (uiState.dateTime.isNotBlank()) {
                    SuccessDetailRow(
                        icon = Icons.Filled.CalendarMonth,
                        label = stringResource(R.string.label_date_time),
                        value = uiState.dateTime
                    )
                }
                if (uiState.clinicName.isNotBlank()) {
                    SuccessDetailRow(
                        icon = Icons.Filled.Business,
                        label = stringResource(R.string.label_clinic),
                        value = uiState.clinicName
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color = SuccessBg, shape = RoundedCornerShape(Dimens.radiusSm))
                        .padding(Dimens.paddingM),
                    horizontalArrangement = Arrangement.Center
                ) {
                    AppLabelText(
                        text = stringResource(R.string.success_confirmed_badge),
                        color = Success
                    )
                }
            }
        }

        Spacer(Modifier.height(Dimens.paddingXXXL))

        PrimaryButton(
            text = stringResource(R.string.success_view_appointments),
            onClick = {
                navController.navigate(NavDestination.MyAppointments.route) {
                    popUpTo(NavDestination.Home.route) { inclusive = false }
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(Dimens.paddingM))

        GhostButton(
            text = stringResource(R.string.success_back_home),
            onClick = {
                navController.navigate(NavDestination.Home.route) {
                    popUpTo(0) { inclusive = true }
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun SuccessDetailRow(
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        AppIcon(icon, contentDescription = null, tint = Primary, size = Dimens.iconSizeCompact) // a11y: decorative — labelled by adjacent Text
        Spacer(Modifier.width(Dimens.paddingM))
        Column {
            AppCaptionText(text = label)
            AppBodyText(text = value, color = TextPrimary)
        }
    }
}
