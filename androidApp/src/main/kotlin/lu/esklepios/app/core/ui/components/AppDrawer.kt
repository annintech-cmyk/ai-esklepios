package lu.esklepios.app.core.ui.components

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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import lu.esklepios.app.R
import lu.esklepios.app.core.ui.theme.*
import lu.esklepios.app.presentation.viewmodel.ProfileViewModel
import lu.esklepios.app.util.AppUrls
import org.koin.androidx.compose.koinViewModel

@Composable
fun AppDrawerContent(
    onLogout: () -> Unit,
    onCloseDrawer: () -> Unit = {},
    onOpenUrl: (String) -> Unit = {},
    onOpenEmail: () -> Unit = {},
    viewModel: ProfileViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val user = uiState.user

    val displayName =
        user?.takeIf { it.firstName.isNotBlank() }?.fullName
            ?: stringResource(R.string.drawer_guest)
    val initials = user?.initials?.ifEmpty { "?" } ?: "?"
    val email = user?.email.orEmpty()

    Column(
        modifier =
            Modifier
                .fillMaxHeight()
                .width(Dimens.drawerWidth)
                .background(Surface),
    ) {
        // ── User header ──────────────────────────────────────────────────────
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .background(brush = Gradients.primaryBrush)
                    .padding(top = Dimens.drawerHeaderTop, start = Dimens.drawerHeaderStart),
        ) {
            Column {
                Box(
                    modifier =
                        Modifier
                            .size(Dimens.avatarSizeLg)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.25f)),
                    contentAlignment = Alignment.Center,
                ) {
                    AppTitleText(text = initials, color = Color.White)
                }
                Spacer(Modifier.height(Dimens.paddingM))
                AppSubtitleText(text = displayName, color = Color.White)
                if (email.isNotBlank()) {
                    AppCaptionText(text = email, color = Color.White.copy(alpha = 0.8f))
                }
            }
        }

        // ── Scrollable sections ───────────────────────────────────────────────
        Column(
            modifier =
                Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
        ) {
            DrawerSection(title = stringResource(R.string.drawer_section_about_us)) {
                DrawerItem(Icons.Outlined.Description, stringResource(R.string.profile_menu_terms), {
                    onOpenUrl(AppUrls.TERMS_AND_CONDITIONS)
                    onCloseDrawer()
                })
                DrawerItem(Icons.Outlined.Shield, stringResource(R.string.profile_privacy), {
                    onOpenUrl(AppUrls.PRIVACY_POLICY)
                    onCloseDrawer()
                })
                DrawerItem(Icons.Outlined.MailOutline, stringResource(R.string.drawer_contact_us), {
                    onOpenEmail()
                    onCloseDrawer()
                })
            }

            DrawerSection(title = stringResource(R.string.drawer_section_assistance)) {
                DrawerItem(Icons.Outlined.HelpOutline, stringResource(R.string.drawer_contact_us), {
                    onOpenEmail()
                    onCloseDrawer()
                })
            }

            DrawerSection(title = stringResource(R.string.drawer_section_caregivers)) {
                DrawerItem(
                    Icons.Outlined.MedicalServices,
                    stringResource(R.string.drawer_esklepios_pro),
                    {
                        onOpenUrl(AppUrls.WEBSITE)
                        onCloseDrawer()
                    },
                )
            }

            DrawerSection(title = stringResource(R.string.drawer_section_links)) {
                DrawerItem(Icons.Outlined.LocalHospital, stringResource(R.string.profile_menu_emergencies), {
                    onOpenUrl(AppUrls.EMERGENCY_SERVICES)
                    onCloseDrawer()
                })
                DrawerItem(Icons.Outlined.LocalPharmacy, stringResource(R.string.profile_menu_pharmacy), {
                    onOpenUrl(AppUrls.PHARMACY_SERVICES)
                    onCloseDrawer()
                })
                DrawerItem(Icons.Outlined.AccountBalance, stringResource(R.string.profile_menu_health_fund), {
                    onOpenUrl(AppUrls.HEALTH_FUND)
                    onCloseDrawer()
                })
                DrawerItem(Icons.Outlined.Apartment, stringResource(R.string.profile_menu_ministry), {
                    onOpenUrl(AppUrls.MINISTRY)
                    onCloseDrawer()
                })
                DrawerItem(Icons.Outlined.FavoriteBorder, stringResource(R.string.drawer_health_portal), {
                    onOpenUrl(AppUrls.HEALTH_PORTAL)
                    onCloseDrawer()
                })
                DrawerItem(Icons.Outlined.Groups, stringResource(R.string.drawer_health_professionals), {
                    onOpenUrl(AppUrls.MEDICAL_PROFESSIONALS)
                    onCloseDrawer()
                })
            }
        }

        // ── Logout ────────────────────────────────────────────────────────────
        HorizontalDivider(color = BorderColor)
        DrawerItem(
            icon = Icons.Outlined.Logout,
            label = stringResource(R.string.action_logout),
            tint = Danger,
            onClick = {
                onCloseDrawer()
                onLogout()
            },
        )
        Spacer(Modifier.navigationBarsPadding())
    }
}

@Composable
private fun DrawerSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column {
        AppCaptionText(
            text = title,
            color = TextSecondary,
            modifier =
                Modifier.padding(
                    start = Dimens.drawerItemHorizontal,
                    end = Dimens.drawerItemHorizontal,
                    top = Dimens.drawerSectionTop,
                    bottom = Dimens.drawerSectionVertical,
                ),
        )
        content()
        HorizontalDivider(
            color = BorderColor,
            modifier =
                Modifier.padding(
                    horizontal = Dimens.drawerSectionHorizontal,
                    vertical = Dimens.drawerSectionVertical,
                ),
        )
    }
}

@Composable
private fun DrawerItem(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    tint: Color = TextPrimary,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(horizontal = Dimens.drawerItemHorizontal, vertical = Dimens.drawerItemVertical),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Dimens.paddingL),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = tint,
            modifier = Modifier.size(Dimens.iconSizeDrawerItem),
        )
        AppBodyText(text = label, color = tint)
    }
}
