package lu.esklepios.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import lu.esklepios.app.R
import lu.esklepios.app.presentation.viewmodel.ProfileViewModel
import lu.esklepios.app.ui.components.*
import lu.esklepios.app.ui.navigation.NavDestination
import lu.esklepios.app.ui.theme.*
import org.koin.androidx.compose.koinViewModel
import java.time.format.TextStyle
import java.util.Locale

private val IconBgBlue   = Color(0xFFEEF0FD)
private val IconBgPink   = Color(0xFFFFEBF0)
private val IconBgOrange = Color(0xFFFFF3E0)
private val IconBgTeal   = Color(0xFFE0F7FA)
private val IconBgGreen  = Color(0xFFE8F5E9)

private val IconTintPink   = Color(0xFFE91E63)
private val IconTintOrange = Color(0xFFFF9800)
private val IconTintTeal   = Color(0xFF00ACC1)
private val IconTintGreen  = Color(0xFF43A047)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    onMenuClick: () -> Unit = {},
    viewModel: ProfileViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showLogoutDialog by remember { mutableStateOf(false) }
    var notificationsEnabled by remember { mutableStateOf(true) }
    var selectedLanguage by remember { mutableStateOf("fr") }
    var languageMenuExpanded by remember { mutableStateOf(false) }

    val languages = listOf(
        "fr" to "🇫🇷  French",
        "en" to "🇬🇧  English",
        "de" to "🇩🇪  German",
        "lb" to "🇱🇺  Luxembourgish"
    )
    val languageLabel = languages.find { it.first == selectedLanguage }?.second ?: "🇫🇷  French"

    LaunchedEffect(uiState.user) {
        uiState.user?.language?.takeIf { it.isNotBlank() }?.let { selectedLanguage = it }
    }

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
            title = { Text(stringResource(R.string.profile_sign_out), fontWeight = FontWeight.Bold) },
            text = { Text(stringResource(R.string.profile_logout_confirm)) },
            confirmButton = {
                Button(
                    onClick = { showLogoutDialog = false; viewModel.logout() },
                    colors = ButtonDefaults.buttonColors(containerColor = Danger)
                ) { Text(stringResource(R.string.profile_sign_out), color = Color.White) }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) { Text(stringResource(R.string.action_cancel), color = Primary) }
            }
        )
    }

    val user = uiState.user
    val genderDisplay = when (user?.gender?.lowercase()) {
        "male", "man" -> stringResource(R.string.gender_male)
        "female", "woman" -> stringResource(R.string.gender_female)
        "other" -> stringResource(R.string.gender_other)
        else -> user?.gender?.ifBlank { "—" } ?: "—"
    }
    val dobDisplay = user?.dateOfBirth?.let { dob ->
        if (dob.isBlank()) return@let "—"
        val parts = dob.split("-")
        if (parts.size == 3) {
            val m = parts[1].toIntOrNull() ?: 0
            val monthName = try {
                java.time.Month.of(m).getDisplayName(TextStyle.FULL, Locale.getDefault())
            } catch (e: Exception) { "?" }
            "$monthName ${parts[2].trimStart('0')}, ${parts[0]}"
        } else dob
    } ?: "—"
    val maskedCns = user?.cnsNumber?.let {
        if (it.isBlank()) "—" else if (it.length > 9) "${it.take(9)} ••••" else it
    } ?: "—"

    Column(modifier = Modifier.fillMaxSize().background(Background)) {
        GradientHeader(roundedBottom = true) {
            Box(modifier = Modifier.fillMaxWidth()) {
                IconButton(
                    onClick = onMenuClick,
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Icon(Icons.Filled.Menu, contentDescription = stringResource(R.string.cd_open_menu), tint = Color.White)
                }
                Text(
                    text = stringResource(R.string.screen_profile),
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Center)
                )
                IconButton(
                    onClick = { showLogoutDialog = true },
                    modifier = Modifier.align(Alignment.CenterEnd)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = stringResource(R.string.cd_sign_out), tint = Color.White)
                }
            }

            Spacer(Modifier.height(12.dp))

            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Box(modifier = Modifier.wrapContentSize()) {
                    Box(
                        modifier = Modifier
                            .size(88.dp)
                            .align(Alignment.Center)
                            .border(3.dp, Color.White.copy(alpha = 0.45f), CircleShape)
                    )
                    Box(modifier = Modifier.align(Alignment.Center)) {
                        AvatarCircle(
                            initials = user?.initials ?: "?",
                            size = 80.dp,
                            fontSize = 26.sp
                        )
                    }
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .offset(x = 2.dp, y = 2.dp)
                            .size(30.dp)
                            .background(PrimaryDark, CircleShape)
                            .border(2.dp, Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Filled.CameraAlt,
                            contentDescription = stringResource(R.string.cd_change_photo),
                            tint = Color.White,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(10.dp))
            Text(
                text = user?.fullName ?: stringResource(R.string.profile_loading_name),
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = user?.email ?: "",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            Spacer(Modifier.height(16.dp))

            AppCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, end = 12.dp, top = 12.dp, bottom = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.Person, contentDescription = null, tint = Primary, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text(
                            stringResource(R.string.profile_personal_info_label),
                            color = Primary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedButton(
                            onClick = { navController.navigate(NavDestination.EditProfile.route) },
                            shape = RoundedCornerShape(20.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Primary),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Primary),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                            modifier = Modifier.height(30.dp)
                        ) {
                            Icon(Icons.Filled.Edit, contentDescription = null, modifier = Modifier.size(13.dp))
                            Spacer(Modifier.width(4.dp))
                            Text(stringResource(R.string.profile_edit_button), fontSize = 12.sp)
                        }
                    }
                    HorizontalDivider(color = BorderColor)
                    ProfileInfoRow(Icons.Filled.Person, Primary, IconBgBlue, stringResource(R.string.profile_full_name_label), user?.fullName ?: "—")
                    HorizontalDivider(color = BorderColor, modifier = Modifier.padding(horizontal = 16.dp))
                    ProfileInfoRow(Icons.Filled.Face, IconTintPink, IconBgPink, stringResource(R.string.profile_gender_label), genderDisplay)
                    HorizontalDivider(color = BorderColor, modifier = Modifier.padding(horizontal = 16.dp))
                    ProfileInfoRow(Icons.Filled.Cake, IconTintOrange, IconBgOrange, stringResource(R.string.profile_dob_label), dobDisplay)
                    HorizontalDivider(color = BorderColor, modifier = Modifier.padding(horizontal = 16.dp))
                    ProfileInfoRow(Icons.Filled.Phone, IconTintTeal, IconBgTeal, stringResource(R.string.profile_phone_label), user?.phone?.ifBlank { "—" } ?: "—")
                    HorizontalDivider(color = BorderColor, modifier = Modifier.padding(horizontal = 16.dp))
                    ProfileInfoRow(Icons.Filled.Badge, IconTintGreen, IconBgGreen, stringResource(R.string.profile_cns_label), maskedCns)
                }
            }

            Spacer(Modifier.height(12.dp))

            AppCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.Notifications, contentDescription = null, tint = Primary, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text(stringResource(R.string.profile_preferences_label), color = Primary, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
                    }
                    HorizontalDivider(color = BorderColor)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(IconBgTeal, RoundedCornerShape(10.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Filled.Email, contentDescription = null, tint = IconTintTeal, modifier = Modifier.size(20.dp))
                            }
                            Spacer(Modifier.width(14.dp))
                            Column {
                                Text(stringResource(R.string.profile_notifications_label), color = TextSecondary, fontSize = 11.sp, letterSpacing = 0.5.sp)
                                Text(stringResource(R.string.profile_email_notifications), color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                            }
                        }
                        Switch(
                            checked = notificationsEnabled,
                            onCheckedChange = { notificationsEnabled = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Primary)
                        )
                    }
                    HorizontalDivider(color = BorderColor, modifier = Modifier.padding(horizontal = 16.dp))
                    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp)) {
                        Text(stringResource(R.string.profile_language_label), color = TextSecondary, fontSize = 11.sp, letterSpacing = 0.5.sp)
                        Spacer(Modifier.height(8.dp))
                        ExposedDropdownMenuBox(
                            expanded = languageMenuExpanded,
                            onExpandedChange = { languageMenuExpanded = !languageMenuExpanded }
                        ) {
                            OutlinedTextField(
                                value = languageLabel,
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = languageMenuExpanded) },
                                modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable),
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Primary,
                                    unfocusedBorderColor = BorderColor
                                )
                            )
                            ExposedDropdownMenu(
                                expanded = languageMenuExpanded,
                                onDismissRequest = { languageMenuExpanded = false }
                            ) {
                                languages.forEach { (code, label) ->
                                    DropdownMenuItem(
                                        text = { Text(label, fontSize = 14.sp) },
                                        onClick = { selectedLanguage = code; languageMenuExpanded = false }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            AppCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.Shield, contentDescription = null, tint = Primary, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text(stringResource(R.string.profile_login_security), color = Primary, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
                    }
                    HorizontalDivider(color = BorderColor)
                    SecurityRow(
                        icon = Icons.Filled.AlternateEmail,
                        iconTint = IconTintTeal,
                        iconBg = IconBgTeal,
                        label = stringResource(R.string.profile_login_email_label),
                        value = user?.email ?: "—",
                        onClick = { navController.navigate(NavDestination.ChangeEmail.route) }
                    )
                    HorizontalDivider(color = BorderColor, modifier = Modifier.padding(horizontal = 16.dp))
                    SecurityRow(
                        icon = Icons.Filled.Key,
                        iconTint = IconTintPink,
                        iconBg = IconBgPink,
                        label = stringResource(R.string.profile_security_label),
                        value = stringResource(R.string.profile_change_password),
                        onClick = { navController.navigate(NavDestination.ChangePassword.route) }
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = DangerBg, shape = RoundedCornerShape(12.dp))
                    .clickable { showLogoutDialog = true }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null, tint = Danger, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text(stringResource(R.string.profile_sign_out), color = Danger, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun ProfileInfoRow(
    icon: ImageVector,
    iconTint: Color,
    iconBg: Color,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(iconBg, RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(20.dp))
        }
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(label, color = TextSecondary, fontSize = 11.sp, letterSpacing = 0.5.sp)
            Text(value, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Medium)
        }
        Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = TextHint, modifier = Modifier.size(18.dp))
    }
}

@Composable
private fun SecurityRow(
    icon: ImageVector,
    iconTint: Color,
    iconBg: Color,
    label: String,
    value: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(iconBg, RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(20.dp))
        }
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(label, color = TextSecondary, fontSize = 11.sp, letterSpacing = 0.5.sp)
            Text(value, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Medium)
        }
        Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = TextHint, modifier = Modifier.size(18.dp))
    }
}
