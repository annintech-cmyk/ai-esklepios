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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
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

    // Cancel confirmation dialog
    cancelTargetId?.let { id ->
        AlertDialog(
            onDismissRequest = { cancelTargetId = null },
            title = { Text("Cancel Appointment", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to cancel this appointment?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.cancelAppointment(id)
                        cancelTargetId = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Danger)
                ) {
                    Text("Yes, Cancel", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { cancelTargetId = null }) {
                    Text("Keep It", color = Primary)
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
                Spacer(Modifier.height(8.dp))
                Text("My Appointments", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(16.dp))
            }

            // Tab row
            TabRow(
                selectedTabIndex = uiState.selectedTab,
                containerColor = Surface,
                contentColor = Primary,
            ) {
                Tab(
                    selected = uiState.selectedTab == 0,
                    onClick = { viewModel.selectTab(0) },
                    text = { Text("Upcoming", fontWeight = if (uiState.selectedTab == 0) FontWeight.Bold else FontWeight.Normal) }
                )
                Tab(
                    selected = uiState.selectedTab == 1,
                    onClick = { viewModel.selectTab(1) },
                    text = { Text("Past", fontWeight = if (uiState.selectedTab == 1) FontWeight.Bold else FontWeight.Normal) }
                )
            }

            when {
                uiState.isLoading -> LoadingIndicator(message = "Loading appointments…")
                else -> {
                    val appointments = if (uiState.selectedTab == 0) uiState.upcomingAppointments else uiState.pastAppointments
                    if (appointments.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            EmptyStateView(
                                icon = Icons.Filled.CalendarMonth,
                                title = if (uiState.selectedTab == 0) "No upcoming appointments" else "No past appointments",
                                subtitle = if (uiState.selectedTab == 0) "Book your first appointment to get started" else "Your appointment history will appear here",
                                actionLabel = if (uiState.selectedTab == 0) "Find Practitioners" else null,
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
                        text = "Modify",
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
                        Text("Cancel", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

@Composable
private fun AppointmentStatusBadge(status: AppointmentStatus) {
    val (bgColor, textColor, label) = when (status) {
        AppointmentStatus.CONFIRMED -> Triple(SuccessBg, Success, "Confirmed")
        AppointmentStatus.PENDING -> Triple(WarningBg, Warning, "Pending")
        AppointmentStatus.CANCELLED -> Triple(DangerBg, Danger, "Cancelled")
        AppointmentStatus.COMPLETED -> Triple(PrimaryLight, Primary, "Completed")
        AppointmentStatus.NO_SHOW -> Triple(DangerBg, Danger, "No Show")
    }
    Box(
        modifier = Modifier
            .background(color = bgColor, shape = RoundedCornerShape(50.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(label, color = textColor, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
    }
}
