package lu.esklepios.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import lu.esklepios.app.presentation.viewmodel.BookAppointmentViewModel
import lu.esklepios.app.ui.components.*
import lu.esklepios.app.ui.navigation.NavDestination
import lu.esklepios.app.ui.theme.*
import org.koin.androidx.compose.koinViewModel

@Composable
fun BookAppointmentScreen(
    navController: NavController,
    practitionerId: String,
    slotId: String,
    viewModel: BookAppointmentViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(practitionerId, slotId) {
        viewModel.loadData(practitionerId, slotId, null)
    }

    LaunchedEffect(uiState.isConfirmed) {
        if (uiState.isConfirmed) {
            navController.navigate(NavDestination.AppointmentSuccess.createRoute(uiState.confirmedAppointmentId)) {
                popUpTo(NavDestination.Home.route)
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
            AppToolbar(title = "Book Appointment", onNavigateBack = { navController.popBackStack() })

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(Modifier.height(16.dp))

                // Booking summary card
                AppCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "Appointment Summary",
                            fontWeight = FontWeight.SemiBold,
                            color = Primary,
                            fontSize = 14.sp
                        )
                        Spacer(Modifier.height(12.dp))

                        val practitioner = uiState.practitioner
                        val slot = uiState.selectedSlot

                        if (practitioner != null) {
                            SummaryRow(icon = Icons.Filled.Person, label = "Practitioner", value = practitioner.fullName)
                            Spacer(Modifier.height(8.dp))
                            SummaryRow(icon = Icons.Filled.MedicalServices, label = "Specialty", value = practitioner.specialty)
                            Spacer(Modifier.height(8.dp))
                            SummaryRow(icon = Icons.Filled.Business, label = "Clinic", value = practitioner.clinicName)
                        } else {
                            Text("Loading appointment details…", color = TextSecondary, fontSize = 14.sp)
                        }

                        if (slot != null) {
                            Spacer(Modifier.height(8.dp))
                            SummaryRow(icon = Icons.Filled.CalendarMonth, label = "Date & Time", value = slot.dateTime)
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))

                // Cancellation policy warning
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color = WarningBg, shape = RoundedCornerShape(12.dp))
                        .padding(12.dp),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(Icons.Filled.Info, contentDescription = null, tint = Warning, modifier = Modifier.size(18.dp))
                    Text(
                        "Cancellations must be made at least 24 hours in advance.",
                        color = Warning,
                        fontSize = 13.sp
                    )
                }

                Spacer(Modifier.height(16.dp))

                // Consultation reason
                FormField(
                    label = "Reason for consultation",
                    value = uiState.consultationReason,
                    onValueChange = { viewModel.updateConsultationReason(it) },
                    placeholder = "Briefly describe the reason for your visit",
                    isRequired = true
                )

                Spacer(Modifier.height(14.dp))

                // Message to doctor
                Column {
                    Text(
                        "Message to doctor (optional)",
                        style = MaterialTheme.typography.labelMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(Modifier.height(4.dp))
                    OutlinedTextField(
                        value = uiState.messageToDoctor,
                        onValueChange = { viewModel.updateMessageToDoctor(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        placeholder = { Text("Any additional information for the doctor…", color = TextHint) },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Primary,
                            unfocusedBorderColor = BorderColor,
                            focusedContainerColor = Surface,
                            unfocusedContainerColor = Surface
                        )
                    )
                }

                Spacer(Modifier.height(20.dp))

                // Auth required banner
                if (!uiState.isAuthenticated) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(color = PrimaryLight, shape = RoundedCornerShape(12.dp))
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(Icons.Filled.Lock, contentDescription = null, tint = Primary, modifier = Modifier.size(20.dp))
                        Column {
                            Text("Sign in required", fontWeight = FontWeight.SemiBold, color = Primary, fontSize = 14.sp)
                            Text("You need an account to book appointments.", color = TextSecondary, fontSize = 12.sp)
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                }

                PrimaryButton(
                    text = if (uiState.isAuthenticated) "Confirm Booking" else "Sign In to Book",
                    onClick = {
                        if (!uiState.isAuthenticated) {
                            navController.navigate(NavDestination.Login.route)
                        } else {
                            viewModel.confirm()
                        }
                    },
                    isLoading = uiState.isLoading,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun SummaryRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = Primary, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(10.dp))
        Column {
            Text(label, color = TextSecondary, fontSize = 11.sp)
            Text(value, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Medium)
        }
    }
}
