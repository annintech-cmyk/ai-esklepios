package lu.esklepios.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import lu.esklepios.app.R
import lu.esklepios.app.presentation.viewmodel.ProfileViewModel
import lu.esklepios.app.ui.theme.*
import org.koin.androidx.compose.koinViewModel

@Composable
fun AppDrawerContent(
    onLogout: () -> Unit,
    onCloseDrawer: () -> Unit = {},
    viewModel: ProfileViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val user = uiState.user

    val firstName = user?.firstName ?: ""
    val lastName = user?.lastName ?: ""
    val email = user?.email ?: ""
    val initials = "${firstName.firstOrNull() ?: ""}${lastName.firstOrNull() ?: ""}".uppercase()
        .ifEmpty { "?" }

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(300.dp)
            .background(Surface)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(brush = Gradients.primaryBrush)
                .statusBarsPadding()
                .padding(24.dp)
        ) {
            Column {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.25f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = initials,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    )
                }
                Spacer(Modifier.height(12.dp))
                Text(
                    text = if (firstName.isNotBlank()) "$firstName $lastName" else stringResource(R.string.drawer_guest),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                if (email.isNotBlank()) {
                    Text(
                        text = email,
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 13.sp
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            DrawerSection(title = stringResource(R.string.drawer_about_us)) {
                DrawerItem(Icons.Outlined.Info, stringResource(R.string.drawer_who_are_we), onCloseDrawer)
                DrawerItem(Icons.Outlined.Description, stringResource(R.string.drawer_terms), onCloseDrawer)
                DrawerItem(Icons.Outlined.Shield, stringResource(R.string.drawer_privacy_policy), onCloseDrawer)
                DrawerItem(Icons.Outlined.MailOutline, stringResource(R.string.drawer_contact_us), onCloseDrawer)
            }

            DrawerSection(title = stringResource(R.string.drawer_assistance)) {
                DrawerItem(Icons.Outlined.HelpOutline, stringResource(R.string.drawer_contact_us), onCloseDrawer)
            }

            DrawerSection(title = stringResource(R.string.drawer_for_caregivers)) {
                DrawerItem(Icons.Outlined.MedicalServices, stringResource(R.string.drawer_esklepios_pro), onCloseDrawer)
            }

            DrawerSection(title = stringResource(R.string.drawer_relevant_links)) {
                DrawerItem(Icons.Outlined.LocalHospital, stringResource(R.string.drawer_emergencies), onCloseDrawer)
                DrawerItem(Icons.Outlined.LocalPharmacy, stringResource(R.string.drawer_pharmacy_on_call), onCloseDrawer)
                DrawerItem(Icons.Outlined.AccountBalance, stringResource(R.string.drawer_national_health_fund), onCloseDrawer)
                DrawerItem(Icons.Outlined.Apartment, stringResource(R.string.drawer_ministry_of_health), onCloseDrawer)
                DrawerItem(Icons.Outlined.FavoriteBorder, stringResource(R.string.drawer_health_portal), onCloseDrawer)
                DrawerItem(Icons.Outlined.Groups, stringResource(R.string.drawer_health_professionals), onCloseDrawer)
            }
        }

        HorizontalDivider(color = BorderColor)
        DrawerItem(
            icon = Icons.Outlined.Logout,
            label = stringResource(R.string.drawer_logout),
            tint = Danger,
            onClick = {
                onCloseDrawer()
                onLogout()
            }
        )
        Spacer(Modifier.navigationBarsPadding())
    }
}

@Composable
private fun DrawerSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column {
        Text(
            text = title,
            color = TextSecondary,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.8.sp,
            modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 4.dp)
        )
        content()
        HorizontalDivider(
            color = BorderColor,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun DrawerItem(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    tint: Color = TextPrimary
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = tint,
            modifier = Modifier.size(22.dp)
        )
        Text(
            text = label,
            color = tint,
            fontSize = 15.sp,
            fontWeight = FontWeight.Normal
        )
    }
}
