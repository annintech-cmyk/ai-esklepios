package lu.esklepios.app.view.dashboard.appointments.booking

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import lu.esklepios.app.R
import lu.esklepios.app.core.navigation.NavDestination
import lu.esklepios.app.core.ui.components.AppBodyText
import lu.esklepios.app.core.ui.components.AppCaptionText
import lu.esklepios.app.core.ui.components.AppCard
import lu.esklepios.app.core.ui.components.AppIcon
import lu.esklepios.app.core.ui.components.AppLabelText
import lu.esklepios.app.core.ui.components.AppSubtitleText
import lu.esklepios.app.core.ui.components.AppTitleText
import lu.esklepios.app.core.ui.components.GhostButton
import lu.esklepios.app.core.ui.components.PrimaryButton
import lu.esklepios.app.core.ui.theme.Background
import lu.esklepios.app.core.ui.theme.Dimens
import lu.esklepios.app.core.ui.theme.Primary
import lu.esklepios.app.core.ui.theme.Success
import lu.esklepios.app.core.ui.theme.SuccessBg
import lu.esklepios.app.core.ui.theme.TextPrimary
import lu.esklepios.app.core.ui.theme.TextSecondary
import lu.esklepios.app.presentation.viewmodel.AppointmentSuccessViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun AppointmentSuccessScreen(
    navController: NavController,
    appointmentId: String,
    viewModel: AppointmentSuccessViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(appointmentId) {
        viewModel.setAppointmentData(
            id = appointmentId,
            practitionerName = uiState.practitionerName.ifBlank { "Your Practitioner" },
            dateTime = uiState.dateTime.ifBlank { "Scheduled" },
            clinicName = uiState.clinicName.ifBlank { "Clinic" },
        )
    }

    val scale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "checkmark_scale",
    )

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(Background)
                .padding(horizontal = Dimens.paddingXXL),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier =
                Modifier
                    .size(Dimens.avatarSizeXl + Dimens.paddingXXXL + Dimens.paddingXS)
                    .scale(scale)
                    .background(color = SuccessBg, shape = CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            AppIcon(
                Icons.Filled.CheckCircle,
                // a11y: decorative — labelled by adjacent Text
                contentDescription = null,
                tint = Success,
                size = Dimens.avatarSizeLg + Dimens.paddingS,
            )
        }

        Spacer(Modifier.height(Dimens.cardOverlap))

        AppTitleText(
            text = stringResource(R.string.success_title),
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.height(Dimens.paddingM))

        AppSubtitleText(
            text = stringResource(R.string.success_subtitle),
            color = TextSecondary,
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.height(Dimens.paddingXXXL))

        AppCard(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(Dimens.paddingXL),
                verticalArrangement = Arrangement.spacedBy(Dimens.paddingM),
            ) {
                if (uiState.practitionerName.isNotBlank()) {
                    SuccessDetailRow(
                        icon = Icons.Filled.Person,
                        label = stringResource(R.string.label_practitioner),
                        value = uiState.practitionerName,
                    )
                }
                if (uiState.dateTime.isNotBlank()) {
                    SuccessDetailRow(
                        icon = Icons.Filled.CalendarMonth,
                        label = stringResource(R.string.label_date_time),
                        value = uiState.dateTime,
                    )
                }
                if (uiState.clinicName.isNotBlank()) {
                    SuccessDetailRow(
                        icon = Icons.Filled.Business,
                        label = stringResource(R.string.label_clinic),
                        value = uiState.clinicName,
                    )
                }
                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .background(color = SuccessBg, shape = RoundedCornerShape(Dimens.radiusSm))
                            .padding(Dimens.paddingM),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    AppLabelText(
                        text = stringResource(R.string.success_confirmed_badge),
                        color = Success,
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
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(Modifier.height(Dimens.paddingM))

        GhostButton(
            text = stringResource(R.string.success_back_home),
            onClick = {
                navController.popBackStack()
            },
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun SuccessDetailRow(
    icon: ImageVector,
    label: String,
    value: String,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        // a11y: decorative — labelled by adjacent Text
        AppIcon(
            icon,
            contentDescription = null,
            tint = Primary,
            size = Dimens.iconSizeCompact,
        )
        Spacer(Modifier.width(Dimens.paddingM))
        Column {
            AppCaptionText(text = label)
            AppBodyText(text = value, color = TextPrimary)
        }
    }
}
