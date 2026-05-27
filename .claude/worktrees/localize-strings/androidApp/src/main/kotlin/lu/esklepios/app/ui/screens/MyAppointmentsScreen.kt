package lu.esklepios.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    onMenuClick: () -> Unit = {},
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
            title = { Text(stringResource(R.string.appointments_cancel_title), fontWeight = FontWeight.Bold) },
            text = { Text(stringResource(R.string.appointments_cancel_confirm)) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.cancelAppointment(id)
                        cancelTargetId = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Danger)
                ) {
                    Text(stringResource(R.string.appointments_cancel_yes), color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { cancelTargetId = null }) {
                    Text(stringResource(R.string.appointments_keep), color = Primary)
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onMenuClick) {
                        Icon(Icons.Filled.Menu, contentDescription = stringResource(R.string.cd_open_menu), tint = Color.White)
                    }
                }
                Text(stringResource(R.string.screen_appointments), color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(16.dp))
            }

            TabRow(
                selectedTabIndex = uiState.selectedTab,
                containerColor = Surface,
                contentColor = Primary,
            ) {
                Tab(
                    selected = uiState.selectedTab == 0,
                    onClick = { viewModel.selectTab(0) },
                    text = { Text(stringResource(R.string.appointments_upcoming), fontWeight = if (uiState.selectedTab == 0) FontWeight.Bold else FontWeight.Normal) }
                )
                Tab(
                    selected = uiState.selectedTab == 1,
                    onClick = { viewModel.selectTab(1) },
                    text = { Text(stringResource(R.string.appointments_past), fontWeight = if (uiState.selectedTab == 1) FontWeight.Bold else FontWeight.Normal) }
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
                                title = if (uiState.selectedTab == 0) stringResource(R.string.appointments_empty_upcoming) else stringResource(R.string.appointments_empty_past),
                                subtitle = if (uiState.selectedTab == 0) stringResource(R.string.appointments_empty_upcoming_sub) else stringResource(R.string.appointments_empty_past_sub),
                                actionLabel = if (uiState.selectedTab == 0) stringResource(R.string.appointments_find_practitioners) else null,
                                onAction = if (uiState.selectedTab == 0) {
                                    { navController.navigate("home") }
                                } else null
                            )
                        }
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
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
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        appointment.practitionerName,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        fontSize = 15.sp
                    )
                    Text(appointment.specialty, color = Primary, fontSize = 13.sp)
                    Text(appointment.clinicName, color = TextSecondary, fontSize = 12.sp)
                }
                AppointmentStatusBadge(status = appointment.status)
            }

            Spacer(Modifier.height(10.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.CalendarMonth, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(15.dp))
                Spacer(Modifier.width(6.dp))
                Text(appointment.dateTime, color = TextSecondary, fontSize = 13.sp)
            }

            if (isUpcoming && appointment.status != AppointmentStatus.CANCELLED) {
                Spacer(Modifier.height(14.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
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
                        shape = RoundedCornerShape(50.dp)
                    ) {
                        Text(stringResource(R.string.appointments_cancel), fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

@Composable
private fun AppointmentStatusBadge(status: AppointmentStatus) {
    val (bgColor, textColor, labelRes) = when (status) {
        AppointmentStatus.CONFIRMED -> Triple(SuccessBg, Success, R.string.status_confirmed)
        AppointmentStatus.PENDING -> Triple(WarningBg, Warning, R.string.status_reserved)
        AppointmentStatus.CANCELLED -> Triple(DangerBg, Danger, R.string.status_cancelled)
        AppointmentStatus.COMPLETED -> Triple(PrimaryLight, Primary, R.string.status_completed)
        AppointmentStatus.NO_SHOW -> Triple(DangerBg, Danger, R.string.status_no_show)
    }
    Box(
        modifier = Modifier
            .background(color = bgColor, shape = RoundedCornerShape(50.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(stringResource(labelRes), color = textColor, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
    }
}
