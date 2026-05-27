package lu.esklepios.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import lu.esklepios.app.presentation.viewmodel.ProfileViewModel
import lu.esklepios.app.ui.components.*
import lu.esklepios.app.ui.navigation.NavDestination
import lu.esklepios.app.ui.theme.*
import org.koin.androidx.compose.koinViewModel

@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showLogoutDialog by remember { mutableStateOf(false) }
    var notificationsEnabled by remember { mutableStateOf(true) }

    LaunchedEffect(uiState.isLoggedOut) {
        if (uiState.isLoggedOut) {
            navController.navigate(NavDestination.Landing.route) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Sign Out", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to sign out?") },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutDialog = false
                        viewModel.logout()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Danger)
                ) {
                    Text("Sign Out", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel", color = Primary)
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        // Gradient header with avatar
        GradientHeader(roundedBottom = true) {
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                AvatarCircle(
                    initials = uiState.user?.initials ?: "?",
                    size = 64.dp,
                    fontSize = 22.sp
                )
                Spacer(Modifier.width(16.dp))
                Column {
                    Text(
                        uiState.user?.fullName ?: "Loading…",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        uiState.user?.email ?: "",
                        color = Color.White.copy(alpha = 0.75f),
                        fontSize = 13.sp
                    )
                    uiState.user?.profileType?.let { pt ->
                        Text(
                            pt.name.lowercase().replaceFirstChar { it.uppercase() },
                            color = Color.White.copy(alpha = 0.6f),
                            fontSize = 12.sp
                        )
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            Spacer(Modifier.height(16.dp))

            // Personal info section
            ProfileSectionHeader("Personal Information")
            ProfileItem(
                icon = Icons.Filled.Person,
                title = "Edit Profile",
                subtitle = "Update your personal details",
                onClick = { navController.navigate(NavDestination.EditProfile.route) }
            )

            Spacer(Modifier.height(12.dp))

            // Security section
            ProfileSectionHeader("Security")
            ProfileItem(
                icon = Icons.Filled.Email,
                title = "Change Email",
                subtitle = uiState.user?.email ?: "",
                onClick = { navController.navigate(NavDestination.ChangeEmail.route) }
            )
            Divider(color = BorderLight, modifier = Modifier.padding(horizontal = 4.dp))
            ProfileItem(
                icon = Icons.Filled.Lock,
                title = "Change Password",
                subtitle = "Update your password",
                onClick = { navController.navigate(NavDestination.ChangePassword.route) }
            )

            Spacer(Modifier.height(12.dp))

            // Preferences section
            ProfileSectionHeader("Preferences")
            AppCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Notifications, contentDescription = null, tint = Primary, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text("Notifications", fontWeight = FontWeight.Medium, color = TextPrimary, fontSize = 14.sp)
                                Text("Appointment reminders", color = TextSecondary, fontSize = 12.sp)
                            }
                        }
                        Switch(
                            checked = notificationsEnabled,
                            onCheckedChange = { notificationsEnabled = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Primary)
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            // Legal section
            ProfileSectionHeader("Legal")
            ProfileItem(icon = Icons.Filled.Info, title = "About eSklepios", subtitle = "Version 1.0.0", onClick = {})
            Divider(color = BorderLight, modifier = Modifier.padding(horizontal = 4.dp))
            ProfileItem(icon = Icons.Filled.Description, title = "Terms of Service", subtitle = "", onClick = {})
            Divider(color = BorderLight, modifier = Modifier.padding(horizontal = 4.dp))
            ProfileItem(icon = Icons.Filled.Security, title = "Privacy Policy", subtitle = "", onClick = {})

            Spacer(Modifier.height(20.dp))

            // Danger zone
            Button(
                onClick = { showLogoutDialog = true },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DangerBg, contentColor = Danger),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
            ) {
                Icon(Icons.Filled.Logout, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Sign Out", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun ProfileSectionHeader(title: String) {
    Text(
        title,
        color = TextSecondary,
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp)
    )
}

@Composable
private fun ProfileItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    AppCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(color = PrimaryLight, shape = RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = Primary, modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Medium, color = TextPrimary, fontSize = 14.sp)
                if (subtitle.isNotBlank()) {
                    Text(subtitle, color = TextSecondary, fontSize = 12.sp)
                }
            }
            Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = TextHint, modifier = Modifier.size(20.dp))
        }
    }
}
