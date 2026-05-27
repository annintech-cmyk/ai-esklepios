package lu.esklepios.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import lu.esklepios.app.ui.components.*
import lu.esklepios.app.ui.navigation.NavDestination
import lu.esklepios.app.ui.theme.*

@Composable
fun LandingScreen(navController: NavController) {
    var searchQuery by remember { mutableStateOf("") }
    var locationQuery by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .verticalScroll(rememberScrollState())
    ) {
        // Hero gradient header
        GradientHeader(roundedBottom = true) {
            Spacer(Modifier.height(24.dp))
            Text(
                text = "eSklepios",
                color = Color.White,
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "Your Health, Connected",
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 16.sp
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Find practitioners, book appointments, manage your healthcare",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 13.sp
            )
            Spacer(Modifier.height(24.dp))
        }

        Spacer(Modifier.height((-20).dp))

        // Search card
        AppCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Search specialty, doctor name…", color = TextHint) },
                    leadingIcon = {
                        Icon(Icons.Filled.Search, contentDescription = null, tint = PrimaryMid)
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Primary,
                        unfocusedBorderColor = BorderColor,
                        focusedContainerColor = Surface,
                        unfocusedContainerColor = Surface
                    )
                )
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = locationQuery,
                    onValueChange = { locationQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("City or postal code", color = TextHint) },
                    leadingIcon = {
                        Icon(Icons.Filled.LocationOn, contentDescription = null, tint = PrimaryMid)
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Primary,
                        unfocusedBorderColor = BorderColor,
                        focusedContainerColor = Surface,
                        unfocusedContainerColor = Surface
                    )
                )
                Spacer(Modifier.height(16.dp))
                PrimaryButton(
                    text = "Find Practitioners",
                    onClick = { navController.navigate(NavDestination.Home.route) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(Modifier.height(28.dp))

        // Trust indicators row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            TrustIndicator(icon = Icons.Filled.MedicalServices, label = "Licensed")
            TrustIndicator(icon = Icons.Filled.Lock, label = "Secure")
            TrustIndicator(icon = Icons.Filled.MedicalServices, label = "Free")
        }

        Spacer(Modifier.height(32.dp))

        // Auth buttons
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            GhostButton(
                text = "Sign In",
                onClick = { navController.navigate(NavDestination.Login.route) },
                modifier = Modifier.fillMaxWidth()
            )
            TextButton(onClick = { navController.navigate(NavDestination.Register.route) }) {
                Text(
                    text = "Create Account",
                    color = TextSecondary,
                    fontSize = 14.sp
                )
            }
        }

        Spacer(Modifier.height(32.dp))
    }
}

@Composable
private fun TrustIndicator(icon: ImageVector, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(color = PrimaryLight, shape = RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = Primary, modifier = Modifier.size(24.dp))
        }
        Spacer(Modifier.height(6.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Medium
        )
    }
}
