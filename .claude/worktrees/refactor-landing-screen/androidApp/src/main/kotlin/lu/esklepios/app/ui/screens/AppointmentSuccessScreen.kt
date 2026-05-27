package lu.esklepios.app.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import lu.esklepios.app.presentation.viewmodel.AppointmentSuccessViewModel
import lu.esklepios.app.ui.components.*
import lu.esklepios.app.ui.navigation.NavDestination
import lu.esklepios.app.ui.theme.*
import org.koin.androidx.compose.koinViewModel

@Composable
fun AppointmentSuccessScreen(
    navController: NavController,
    appointmentId: String,
    viewModel: AppointmentSuccessViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(appointmentId) {
        viewModel.setAppointmentData(
            id = appointmentId,
            practitionerName = uiState.practitionerName.ifBlank { "Your Practitioner" },
            dateTime = uiState.dateTime.ifBlank { "Scheduled" },
            clinicName = uiState.clinicName.ifBlank { "Clinic" }
        )
    }

    // Scale animation for checkmark
    val scale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "checkmark_scale"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Success animation circle
        Box(
            modifier = Modifier
                .size(120.dp)
                .scale(scale)
                .background(color = SuccessBg, shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Filled.CheckCircle,
                contentDescription = null,
                tint = Success,
                modifier = Modifier.size(72.dp)
            )
        }

        Spacer(Modifier.height(28.dp))

        Text(
            "Appointment Confirmed!",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(10.dp))

        Text(
            "Your appointment has been successfully booked.",
            color = TextSecondary,
            fontSize = 15.sp,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(32.dp))

        // Appointment details card
        AppCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                if (uiState.practitionerName.isNotBlank()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Person, contentDescription = null, tint = Primary, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text("Practitioner", color = TextSecondary, fontSize = 11.sp)
                            Text(uiState.practitionerName, color = TextPrimary, fontWeight = FontWeight.Medium, fontSize = 14.sp)
                        }
                    }
                }
                if (uiState.dateTime.isNotBlank()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.CalendarMonth, contentDescription = null, tint = Primary, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text("Date & Time", color = TextSecondary, fontSize = 11.sp)
                            Text(uiState.dateTime, color = TextPrimary, fontWeight = FontWeight.Medium, fontSize = 14.sp)
                        }
                    }
                }
                if (uiState.clinicName.isNotBlank()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Business, contentDescription = null, tint = Primary, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text("Clinic", color = TextSecondary, fontSize = 11.sp)
                            Text(uiState.clinicName, color = TextPrimary, fontWeight = FontWeight.Medium, fontSize = 14.sp)
                        }
                    }
                }
                // Status badge
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color = SuccessBg, shape = RoundedCornerShape(8.dp))
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text("CONFIRMED", color = Success, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }

        Spacer(Modifier.height(32.dp))

        PrimaryButton(
            text = "View My Appointments",
            onClick = {
                navController.navigate(NavDestination.MyAppointments.route) {
                    popUpTo(NavDestination.Home.route) { inclusive = false }
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        GhostButton(
            text = "Back to Home",
            onClick = {
                navController.navigate(NavDestination.Home.route) {
                    popUpTo(0) { inclusive = true }
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}
