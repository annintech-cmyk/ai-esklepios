package lu.esklepios.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import lu.esklepios.app.domain.model.AppointmentSlot
import lu.esklepios.app.presentation.viewmodel.PractitionerDetailViewModel
import lu.esklepios.app.ui.components.*
import lu.esklepios.app.ui.navigation.NavDestination
import lu.esklepios.app.ui.theme.*
import org.koin.androidx.compose.koinViewModel

@Composable
fun PractitionerDetailScreen(
    navController: NavController,
    practitionerId: String,
    viewModel: PractitionerDetailViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(practitionerId) {
        viewModel.loadPractitioner(practitionerId)
    }

    Box(modifier = Modifier.fillMaxSize().background(Background)) {
        when {
            uiState.isLoading -> LoadingIndicator(message = "Loading practitioner…")
            uiState.error != null -> ErrorView(
                message = uiState.error ?: "Error",
                onRetry = { viewModel.loadPractitioner(practitionerId) }
            )
            uiState.practitioner == null -> EmptyStateView(
                icon = Icons.Filled.Person,
                title = "Not found",
                subtitle = "Practitioner not found"
            )
            else -> {
                val practitioner = uiState.practitioner!!
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    // Gradient header
                    GradientHeader(roundedBottom = true) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                            }
                            Spacer(Modifier.weight(1f))
                            IconButton(onClick = { viewModel.toggleFavorite() }) {
                                Icon(
                                    if (uiState.isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                    contentDescription = "Favorite",
                                    tint = if (uiState.isFavorite) Color(0xFFFF6B6B) else Color.White
                                )
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            AvatarCircle(initials = practitioner.initials, size = 64.dp, fontSize = 22.sp)
                            Spacer(Modifier.width(16.dp))
                            Column {
                                Text(practitioner.fullName, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                                Text(practitioner.specialty, color = Color.White.copy(alpha = 0.85f), fontSize = 14.sp)
                                Text(practitioner.clinicName, color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                            }
                        }
                        Spacer(Modifier.height(16.dp))
                    }

                    Spacer(Modifier.height(16.dp))

                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                        // Contact card
                        AppCard(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Contact", fontWeight = FontWeight.SemiBold, color = Primary, fontSize = 13.sp)
                                Spacer(Modifier.height(12.dp))
                                InfoRow(icon = Icons.Filled.Phone, label = "Phone", value = practitioner.phone)
                                Spacer(Modifier.height(8.dp))
                                InfoRow(icon = Icons.Filled.Email, label = "Email", value = practitioner.email)
                                Spacer(Modifier.height(8.dp))
                                InfoRow(icon = Icons.Filled.LocationOn, label = "Address", value = "${practitioner.address}, ${practitioner.city}")
                                Spacer(Modifier.height(14.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    SecondaryButton(text = "Call", onClick = {}, modifier = Modifier.weight(1f))
                                    SecondaryButton(text = "Email", onClick = {}, modifier = Modifier.weight(1f))
                                }
                            }
                        }

                        Spacer(Modifier.height(12.dp))

                        // Emergency banner
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(color = DangerBg, shape = RoundedCornerShape(12.dp))
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(Icons.Filled.Warning, contentDescription = null, tint = Danger, modifier = Modifier.size(20.dp))
                            Text("For emergencies, call 112 immediately", color = Danger, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        }

                        Spacer(Modifier.height(12.dp))

                        // Schedule card
                        if (practitioner.schedule.isNotEmpty()) {
                            AppCard(modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text("Schedule", fontWeight = FontWeight.SemiBold, color = Primary, fontSize = 13.sp)
                                    Spacer(Modifier.height(12.dp))
                                    practitioner.schedule.forEach { day ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 4.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                day.dayOfWeek,
                                                color = if (day.isOpen) TextPrimary else TextSecondary,
                                                fontWeight = if (day.isOpen) FontWeight.Medium else FontWeight.Normal,
                                                fontSize = 14.sp
                                            )
                                            Text(
                                                if (day.isOpen) "${day.openTime} – ${day.closeTime}" else "Closed",
                                                color = if (day.isOpen) TextPrimary else TextHint,
                                                fontSize = 14.sp
                                            )
                                        }
                                    }
                                }
                            }
                            Spacer(Modifier.height(12.dp))
                        }

                        // Map preview
                        MapPreviewCard(
                            address = "${practitioner.address}, ${practitioner.city}",
                            onOpenMaps = {}
                        )

                        Spacer(Modifier.height(12.dp))

                        // Payment methods
                        if (practitioner.paymentMethods.isNotEmpty()) {
                            AppCard(modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text("Payment Methods", fontWeight = FontWeight.SemiBold, color = Primary, fontSize = 13.sp)
                                    Spacer(Modifier.height(10.dp))
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                                        practitioner.paymentMethods.forEach { method ->
                                            Box(
                                                modifier = Modifier
                                                    .background(color = PrimaryLight, shape = RoundedCornerShape(8.dp))
                                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                                            ) {
                                                Text(method, color = Primary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                            }
                                        }
                                    }
                                }
                            }
                            Spacer(Modifier.height(12.dp))
                        }

                        // Diplomas
                        if (practitioner.diplomas.isNotEmpty()) {
                            AppCard(modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text("Diplomas & Certifications", fontWeight = FontWeight.SemiBold, color = Primary, fontSize = 13.sp)
                                    Spacer(Modifier.height(10.dp))
                                    practitioner.diplomas.forEach { diploma ->
                                        Row(
                                            modifier = Modifier.padding(vertical = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = Success, modifier = Modifier.size(16.dp))
                                            Spacer(Modifier.width(8.dp))
                                            Text(diploma, color = TextPrimary, fontSize = 14.sp)
                                        }
                                    }
                                }
                            }
                            Spacer(Modifier.height(12.dp))
                        }

                        // Available slots
                        val availableSlots = practitioner.availableSlots.filter { it.available }
                        if (availableSlots.isNotEmpty()) {
                            AppCard(modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text("Available Slots", fontWeight = FontWeight.SemiBold, color = Primary, fontSize = 13.sp)
                                    Spacer(Modifier.height(10.dp))
                                    availableSlots.take(6).forEach { slot ->
                                        DetailSlotChip(
                                            slot = slot,
                                            isSelected = uiState.selectedSlot?.id == slot.id,
                                            onClick = { viewModel.selectSlot(slot) }
                                        )
                                        Spacer(Modifier.height(6.dp))
                                    }
                                }
                            }
                        }

                        Spacer(Modifier.height(100.dp))
                    }
                }

                // Floating Book Appointment button
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .background(Surface.copy(alpha = 0.95f))
                        .padding(16.dp)
                ) {
                    PrimaryButton(
                        text = "Book an Appointment",
                        onClick = {
                            val slot = uiState.selectedSlot ?: practitioner.availableSlots.firstOrNull { it.available }
                            if (slot != null) {
                                navController.navigate(NavDestination.BookAppointment.createRoute(practitioner.id, slot.id))
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
private fun DetailSlotChip(slot: AppointmentSlot, isSelected: Boolean, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = if (isSelected) PrimaryLight else Surface,
            contentColor = if (isSelected) Primary else TextPrimary
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isSelected) Primary else BorderColor
        )
    ) {
        Text(slot.dateTime, fontSize = 13.sp)
    }
}

@Composable
private fun InfoRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Row(verticalAlignment = Alignment.Top) {
        Icon(icon, contentDescription = null, tint = Primary, modifier = Modifier.size(18.dp).padding(top = 2.dp))
        Spacer(Modifier.width(10.dp))
        Column {
            Text(label, color = TextSecondary, fontSize = 11.sp)
            Text(value, color = TextPrimary, fontSize = 14.sp)
        }
    }
}
