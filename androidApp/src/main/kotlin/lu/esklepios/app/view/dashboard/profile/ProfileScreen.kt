package lu.esklepios.app.view.dashboard.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import lu.esklepios.app.R
import lu.esklepios.app.core.navigation.NavDestination
import lu.esklepios.app.core.ui.components.AppBodyText
import lu.esklepios.app.core.ui.components.AppButtonText
import lu.esklepios.app.core.ui.components.AppCaptionText
import lu.esklepios.app.core.ui.components.AppCard
import lu.esklepios.app.core.ui.components.AppGradientHeader
import lu.esklepios.app.core.ui.components.AppIcon
import lu.esklepios.app.core.ui.components.AppLabelText
import lu.esklepios.app.core.ui.components.AppSubtitleText
import lu.esklepios.app.core.ui.components.AppTitleText
import lu.esklepios.app.core.ui.components.HSpace
import lu.esklepios.app.core.ui.components.HeaderAction
import lu.esklepios.app.core.ui.components.HeaderProfile
import lu.esklepios.app.core.ui.components.VSpace
import lu.esklepios.app.core.ui.theme.Background
import lu.esklepios.app.core.ui.theme.BorderColor
import lu.esklepios.app.core.ui.theme.Danger
import lu.esklepios.app.core.ui.theme.DangerBg
import lu.esklepios.app.core.ui.theme.Dimens
import lu.esklepios.app.core.ui.theme.IconBgBlue
import lu.esklepios.app.core.ui.theme.Primary
import lu.esklepios.app.core.ui.theme.TextHint
import lu.esklepios.app.core.ui.theme.TextPrimary
import lu.esklepios.app.presentation.viewmodel.ProfileViewModel
import lu.esklepios.app.util.CnsFormatter
import lu.esklepios.app.util.Gender
import lu.esklepios.app.util.supportedLanguages
import lu.esklepios.app.utils.DateUtil
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    onMenuClick: () -> Unit = {},
    viewModel: ProfileViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showLogoutDialog by remember { mutableStateOf(false) }
    var notificationsEnabled by remember { mutableStateOf(true) }
    var selectedLanguage by remember { mutableStateOf("fr") }
    var languageMenuExpanded by remember { mutableStateOf(false) }

    val languageOptions =
        supportedLanguages.map { lang ->
            lang.code to "${lang.flagEmoji}  ${lang.englishName}"
        }
    val languageLabel =
        languageOptions.find { it.first == selectedLanguage }?.second
            ?: languageOptions.firstOrNull()?.second ?: ""

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
            title = {
                AppTitleText(text = stringResource(R.string.action_logout))
            },
            text = {
                AppBodyText(text = stringResource(R.string.profile_logout_confirm))
            },
            confirmButton = {
                // UI-14 exemption: AlertDialog confirmButton slot is a Material-API slot
                // — PrimaryButton (full-width pill) doesn't fit the dialog layout.
                Button(
                    onClick = {
                        showLogoutDialog = false
                        viewModel.logout()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Danger),
                ) {
                    AppButtonText(text = stringResource(R.string.action_logout))
                }
            },
            dismissButton = {
                // UI-14 exemption: AlertDialog dismissButton slot — see above.
                TextButton(onClick = { showLogoutDialog = false }) {
                    AppButtonText(
                        text = stringResource(R.string.action_cancel),
                        color = Primary,
                    )
                }
            },
        )
    }

    val user = uiState.user
    val genderDisplay =
        user
            ?.gender?.takeIf { it.isNotBlank() }
            ?.let { raw ->
                when (Gender.fromApiString(raw)) {
                    Gender.MALE -> stringResource(R.string.gender_male)
                    Gender.FEMALE -> stringResource(R.string.gender_female)
                    Gender.OTHER -> stringResource(R.string.gender_other)
                }
            } ?: "—"
    val dobDisplay =
        user?.dateOfBirth?.takeIf { it.isNotBlank() }
            ?.let { dob -> DateUtil.formatIsoDate(dob, DateUtil.PATTERN_DOB) }
            ?: "—"
    val maskedCns =
        user?.cnsNumber
            ?.takeIf { it.isNotBlank() }
            ?.let { CnsFormatter.mask(it) }
            ?: "—"

    Column(modifier = Modifier.fillMaxSize().background(Background)) {
        AppGradientHeader(
            roundedBottom = true,
            leadingAction =
                HeaderAction.IconButtonAction(
                    icon = Icons.Filled.Menu,
                    contentDescription = stringResource(R.string.cd_menu),
                    onClick = onMenuClick,
                ),
            centerAction =
                HeaderAction.TitleAction(
                    text = stringResource(R.string.screen_profile),
                    style = MaterialTheme.typography.headlineSmall,
                ),
            trailingAction =
                HeaderAction.IconButtonAction(
                    icon = Icons.AutoMirrored.Filled.ExitToApp,
                    contentDescription = stringResource(R.string.cd_sign_out),
                    onClick = { showLogoutDialog = true },
                ),
            profile =
                HeaderProfile.Centered(
                    initials = user?.initials ?: "?",
                    avatarSize = Dimens.avatarSizeXl,
                    name = user?.fullName ?: stringResource(R.string.profile_loading_name),
                    email = user?.email,
                ),
        )

        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = Dimens.paddingL),
        ) {
            VSpace(Dimens.paddingL)

            AppCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Row(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(start = Dimens.paddingL, end = Dimens.paddingM, top = Dimens.paddingM, bottom = Dimens.paddingM),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        AppIcon(
                            imageVector = Icons.Filled.Person,
                            // a11y: decorative — labelled by adjacent Text
                            contentDescription = null,
                            tint = Primary,
                            size = Dimens.iconSizeCompact,
                        )
                        HSpace(Dimens.paddingS)
                        AppLabelText(
                            text = stringResource(R.string.profile_section_personal),
                            color = Primary,
                            modifier = Modifier.weight(1f),
                        )
                        // UI-14 exemption: compact section-header "Edit" pill — height = filterChipHeight (32dp)
                        // doesn't match SecondaryButton (52dp pill). Pattern appears only once in the project;
                        // promote to a shared `EditChipButton` component when a 3rd call site appears.
                        OutlinedButton(
                            onClick = { navController.navigate(NavDestination.EditProfile.route) },
                            shape = RoundedCornerShape(Dimens.radiusXl),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Primary),
                            border = BorderStroke(Dimens.borderThin, Primary),
                            contentPadding = PaddingValues(horizontal = Dimens.paddingM, vertical = Dimens.paddingNone),
                            modifier = Modifier.height(Dimens.filterChipHeight),
                        ) {
                            AppIcon(
                                imageVector = Icons.Filled.Edit,
                                // a11y: decorative — labelled by adjacent Text
                                contentDescription = null,
                                tint = Primary,
                                size = Dimens.iconSizeChevron,
                            )
                            HSpace(Dimens.paddingXS)
                            AppLabelText(
                                text = stringResource(R.string.action_edit),
                                color = Primary,
                            )
                        }
                    }
                    HorizontalDivider(color = BorderColor)
                    ProfileInfoRow(
                        Icons.Filled.Person,
                        Primary,
                        IconBgBlue,
                        stringResource(R.string.profile_label_full_name),
                        user?.fullName ?: "—",
                    )
                    HorizontalDivider(color = BorderColor, modifier = Modifier.padding(horizontal = Dimens.paddingL))
                    ProfileInfoRow(Icons.Filled.Face, Primary, IconBgBlue, stringResource(R.string.profile_label_gender), genderDisplay)
                    HorizontalDivider(color = BorderColor, modifier = Modifier.padding(horizontal = Dimens.paddingL))
                    ProfileInfoRow(Icons.Filled.Cake, Primary, IconBgBlue, stringResource(R.string.profile_label_dob), dobDisplay)
                    HorizontalDivider(color = BorderColor, modifier = Modifier.padding(horizontal = Dimens.paddingL))
                    ProfileInfoRow(
                        Icons.Filled.Phone,
                        Primary,
                        IconBgBlue,
                        stringResource(R.string.profile_label_phone),
                        user?.phone?.ifBlank { "—" } ?: "—",
                    )
                    HorizontalDivider(color = BorderColor, modifier = Modifier.padding(horizontal = Dimens.paddingL))
                    ProfileInfoRow(Icons.Filled.VpnKey, Primary, IconBgBlue, stringResource(R.string.profile_label_cns), maskedCns)
                }
            }

            VSpace(Dimens.paddingM)

            AppCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Row(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(horizontal = Dimens.paddingL, vertical = Dimens.paddingM),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        AppIcon(
                            imageVector = Icons.Filled.Notifications,
                            // a11y: decorative — labelled by adjacent Text
                            contentDescription = null,
                            tint = Primary,
                            size = Dimens.iconSizeCompact,
                        )
                        HSpace(Dimens.paddingS)
                        AppLabelText(
                            text = stringResource(R.string.profile_section_preferences),
                            color = Primary,
                        )
                    }
                    HorizontalDivider(color = BorderColor)
                    Row(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(horizontal = Dimens.paddingL, vertical = Dimens.paddingM),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier =
                                    Modifier
                                        .size(Dimens.paddingXXXL + Dimens.paddingS)
                                        .background(IconBgBlue, RoundedCornerShape(Dimens.radiusMd)),
                                contentAlignment = Alignment.Center,
                            ) {
                                AppIcon(
                                    imageVector = Icons.Filled.Email,
                                    // a11y: decorative — labelled by adjacent Text
                                    contentDescription = null,
                                    tint = Primary,
                                    size = Dimens.iconSizeMd,
                                )
                            }
                            HSpace(Dimens.paddingL)
                            Column {
                                AppCaptionText(text = stringResource(R.string.profile_label_notifications))
                                AppBodyText(
                                    text = stringResource(R.string.profile_label_notifications_email),
                                    color = TextPrimary,
                                )
                            }
                        }
                        Switch(
                            checked = notificationsEnabled,
                            onCheckedChange = { notificationsEnabled = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Primary),
                        )
                    }
                    HorizontalDivider(color = BorderColor, modifier = Modifier.padding(horizontal = Dimens.paddingL))
                    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = Dimens.paddingL, vertical = Dimens.paddingM)) {
                        AppCaptionText(text = stringResource(R.string.profile_label_language))
                        VSpace(Dimens.paddingS)
                        Box(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .height(56.dp)
                                    .border(1.dp, IconBgBlue, RoundedCornerShape(Dimens.radiusMd))
                                    .background(Color.White, RoundedCornerShape(Dimens.radiusMd))
                                    .clickable { languageMenuExpanded = !languageMenuExpanded }
                                    .padding(horizontal = Dimens.paddingL),
                            contentAlignment = Alignment.CenterStart,
                        ) {
                            AppBodyText(text = languageLabel)
                        }
                    }
                }
            }

            VSpace(Dimens.paddingM)

            AppCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Row(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(horizontal = Dimens.paddingL, vertical = Dimens.paddingM),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        AppIcon(
                            imageVector = Icons.Filled.Shield,
                            // a11y: decorative — labelled by adjacent Text
                            contentDescription = null,
                            tint = Primary,
                            size = Dimens.iconSizeCompact,
                        )
                        HSpace(Dimens.paddingS)
                        AppLabelText(
                            text = stringResource(R.string.profile_section_security),
                            color = Primary,
                        )
                    }
                    HorizontalDivider(color = BorderColor)
                    SecurityRow(
                        icon = Icons.Filled.Email,
                        iconTint = Primary,
                        iconBg = IconBgBlue,
                        label = stringResource(R.string.profile_label_login_email),
                        value = user?.email ?: "—",
                        onClick = { navController.navigate(NavDestination.ChangeEmail.route) },
                    )
                    HorizontalDivider(color = BorderColor, modifier = Modifier.padding(horizontal = Dimens.paddingL))
                    SecurityRow(
                        icon = Icons.Filled.VpnKey,
                        iconTint = Primary,
                        iconBg = IconBgBlue,
                        label = stringResource(R.string.profile_label_security),
                        value = stringResource(R.string.profile_label_change_password),
                        onClick = { navController.navigate(NavDestination.ChangePassword.route) },
                    )
                }
            }

            VSpace(Dimens.paddingM)

            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .background(color = DangerBg, shape = RoundedCornerShape(Dimens.radiusMd))
                        .clickable { showLogoutDialog = true }
                        .padding(Dimens.paddingL),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                AppIcon(
                    imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                    // a11y: decorative — labelled by adjacent Text
                    contentDescription = null,
                    tint = Danger,
                    size = Dimens.iconSizeMd,
                )
                HSpace(Dimens.paddingS)
                AppSubtitleText(
                    text = stringResource(R.string.action_logout),
                    color = Danger,
                )
            }

            VSpace(Dimens.paddingXXXL)
        }
    }
}

@Composable
private fun ProfileInfoRow(
    icon: ImageVector,
    iconTint: Color,
    iconBg: Color = IconBgBlue,
    label: String,
    value: String,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimens.paddingL, vertical = Dimens.paddingM),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier =
                Modifier
                    .size(Dimens.paddingXXXL + Dimens.paddingS)
                    .background(iconBg, RoundedCornerShape(Dimens.radiusMd)),
            contentAlignment = Alignment.Center,
        ) {
            AppIcon(
                imageVector = icon,
                // a11y: decorative — labelled by adjacent Text
                contentDescription = null,
                tint = iconTint,
                size = Dimens.iconSizeSm,
            )
        }
        HSpace(Dimens.paddingL)
        Column(modifier = Modifier.weight(1f)) {
            AppCaptionText(text = label)
            AppBodyText(text = value, color = TextPrimary)
        }
        AppIcon(
            imageVector = Icons.Filled.ChevronRight,
            // a11y: decorative — labelled by adjacent Text
            contentDescription = null,
            tint = TextHint,
            size = Dimens.iconSizeCompact,
        )
    }
}

@Composable
private fun SecurityRow(
    icon: ImageVector,
    iconTint: Color,
    iconBg: Color,
    label: String,
    value: String,
    onClick: () -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(horizontal = Dimens.paddingL, vertical = Dimens.paddingM),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier =
                Modifier
                    .size(Dimens.paddingXXXL + Dimens.paddingS)
                    .background(iconBg, RoundedCornerShape(Dimens.radiusMd)),
            contentAlignment = Alignment.Center,
        ) {
            AppIcon(
                imageVector = icon,
                // a11y: decorative — labelled by adjacent Text
                contentDescription = null,
                tint = iconTint,
                size = Dimens.iconSizeMd,
            )
        }
        HSpace(Dimens.paddingL)
        Column(modifier = Modifier.weight(1f)) {
            AppCaptionText(text = label)
            AppBodyText(text = value, color = TextPrimary)
        }
        AppIcon(
            imageVector = Icons.Filled.ChevronRight,
            // a11y: decorative — labelled by adjacent Text
            contentDescription = null,
            tint = TextHint,
            size = Dimens.iconSizeCompact,
        )
    }
}
