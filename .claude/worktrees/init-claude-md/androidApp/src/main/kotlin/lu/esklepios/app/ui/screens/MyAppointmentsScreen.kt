package lu.esklepios.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import lu.esklepios.app.R
import lu.esklepios.app.domain.model.Appointment
import lu.esklepios.app.domain.model.AppointmentStatus
import lu.esklepios.app.presentation.viewmodel.MyAppointmentsViewModel
import lu.esklepios.app.ui.components.*
import lu.esklepios.app.ui.theme.*
import org.koin.androidx.compose.koinViewModel

@Composable
fun MyAppointmentsScreen(
    navController: NavController,
    viewModel: MyAppointmentsViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var cancelTargetId by remember { mutableStateOf<String?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.error) {
        uiState.error?.let { error ->
            snackbarHostState.showSnackbar(error)
            viewModel.clearError()
        }
    }

    cancelTargetId?.let { id ->
        AlertDialog(
            onDismissRequest = { cancelTargetId = null },
            title = { AppTitleText(text = stringResource(R.string.appointments_cancel_title)) },
            text = { AppBodyText(text = stringResource(R.string.appointments_cancel_confirm)) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.cancelAppointment(id)
                        cancelTargetId = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Danger)
                ) {
                    AppButtonText(text = stringResource(R.string.appointments_cancel_yes))
                }
            },
            dismissButton = {
                TextButton(onClick = { cancelTargetId = null }) {
                    AppButtonText(
                        text = stringResource(R.string.appointments_cancel_keep),
                        color = Primary
                    )
                }
            }
        )
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
            GradientHeader {
                Spacer(Modifier.height(Dimens.paddingS))
                AppToolbarTitle(
                    text = stringResource(R.string.screen_appointments),
                    color = Color.White
                )
                Spacer(Modifier.height(Dimens.paddingL))
            }

            TabRow(
                selectedTabIndex = uiState.selectedTab,
                containerColor = Surface,
                contentColor = Primary,
            ) {
                Tab(
                    selected = uiState.selectedTab == 0,
                    onClick = { viewModel.selectTab(0) },
                    text = {
                        AppCaptionText(
                            text = stringResource(R.string.appointments_upcoming),
                            color = if (uiState.selectedTab == 0) Primary else TextSecondary
                        )
                    }
                )
                Tab(
                    selected = uiState.selectedTab == 1,
                    onClick = { viewModel.selectTab(1) },
                    text = {
                        AppCaptionText(
                            text = stringResource(R.string.appointments_past),
                            color = if (uiState.selectedTab == 1) Primary else TextSecondary
                        )
                    }
                )
            }

            when {
                uiState.isLoading -> LoadingIndicator(message = stringResource(R.string.appointments_loading))
                else -> {
                    val appointments = if (uiState.selectedTab == 0) uiState.upcomingAppointments else uiState.pastAppointments
                    if (appointments.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            EmptyStateView(
                                icon = Icons.Filled.CalendarMonth,
                                title = if (uiState.selectedTab == 0)
                                    stringResource(R.string.appointments_empty_upcoming)
                                else
                                    stringResource(R.string.appointments_empty_past),
                                subtitle = if (uiState.selectedTab == 0)
                                    stringResource(R.string.appointments_empty_upcoming_sub)
                                else
                                    stringResource(R.string.appointments_empty_past_sub),
                                actionLabel = if (uiState.selectedTab == 0)
                                    stringResource(R.string.appointments_find_practitioners)
                                else null,
                                onAction = if (uiState.selectedTab == 0) {
                                    { navController.navigate("home") }
                                } else null
                            )
                        }
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(horizontal = Dimens.paddingL, vertical = Dimens.paddingM),
                            verticalArrangement = Arrangement.spacedBy(Dimens.paddingM)
                        ) {
                            items(appointments, key = { it.id }) { appointment ->
                                AppointmentItemCard(
                                    appointment = appointment,
                                    isUpcoming = uiState.selectedTab == 0,
                                    onCancel = { cancelTargetId = appointment.id },
                                    onModify = {}
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AppointmentItemCard(
    appointment: Appointment,
    isUpcoming: Boolean,
    onCancel: () -> Unit,
    onModify: () -> Unit
) {
    AppCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(Dimens.paddingL)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    AppSubtitleText(text = appointment.practitionerName)
                    AppLabelText(text = appointment.specialty, color = Primary)
                    AppCaptionText(text = appointment.clinicName)
                }
                AppointmentStatusBadge(status = appointment.status)
            }

            Spacer(Modifier.height(Dimens.paddingM))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Filled.CalendarMonth,
                    contentDescription = null,
                    tint = TextSecondary,
                    modifier = Modifier.size(Dimens.iconSizeSm)
                )
                Spacer(Modifier.width(Dimens.paddingTiny))
                AppCaptionText(text = appointment.dateTime)
            }

            if (isUpcoming && appointment.status != AppointmentStatus.CANCELLED) {
                Spacer(Modifier.height(Dimens.paddingPlus))
                Row(horizontalArrangement = Arrangement.spacedBy(Dimens.paddingM)) {
                    SecondaryButton(
                        text = stringResource(R.string.appointments_modify),
                        onClick = onModify,
                        modifier = Modifier.weight(1f)
                    )
                    Button(
                        onClick = onCancel,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DangerBg,
                            contentColor = Danger
                        ),
                        shape = RoundedCornerShape(Dimens.radiusPill)
                    ) {
                        AppButtonText(
                            text = stringResource(R.string.action_cancel),
                            color = Danger
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AppointmentStatusBadge(status: AppointmentStatus) {
    val (bgColor, textColor, label) = when (status) {
        AppointmentStatus.CONFIRMED -> Triple(SuccessBg, Success, stringResource(R.string.status_confirmed))
        AppointmentStatus.PENDING -> Triple(WarningBg, Warning, stringResource(R.string.status_reserved))
        AppointmentStatus.CANCELLED -> Triple(DangerBg, Danger, stringResource(R.string.status_cancelled))
        AppointmentStatus.COMPLETED -> Triple(PrimaryLight, Primary, stringResource(R.string.status_completed))
        AppointmentStatus.NO_SHOW -> Triple(DangerBg, Danger, stringResource(R.string.status_no_show))
    }
    Box(
        modifier = Modifier
            .background(color = bgColor, shape = RoundedCornerShape(Dimens.radiusPill))
            .padding(horizontal = Dimens.paddingCompact, vertical = Dimens.paddingXS)
    ) {
        AppCaptionText(text = label, color = textColor)
    }
}
