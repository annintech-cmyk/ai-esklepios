package lu.esklepios.app.view.dashboard.appointments.booking

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import lu.esklepios.app.R
import lu.esklepios.app.core.navigation.NavDestination
import lu.esklepios.app.core.ui.components.*
import lu.esklepios.app.core.ui.theme.*
import lu.esklepios.app.presentation.viewmodel.BookAppointmentViewModel
import lu.esklepios.app.utils.DateUtil
import org.koin.androidx.compose.koinViewModel

@Composable
fun BookingScreen(
    navController: NavController,
    doctorId: String,
    slotId: String,
    isChange: Boolean = false,
    bookingViewModel: BookAppointmentViewModel = koinViewModel()
) {
    val bookingUiState by bookingViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        bookingViewModel.loadData(doctorId, slotId)
    }

    val practitioner = bookingUiState.practitioner
    val slotSummary = bookingUiState.selectedSlot?.let { DateUtil.formatSlotSummary(it.dateTime) } ?: ""

    var consultationReason by remember { mutableStateOf("") }
    var reason by remember { mutableStateOf("") }

    LaunchedEffect(bookingUiState.isConfirmed) {
        if (bookingUiState.isConfirmed) {
            navController.navigate(
                NavDestination.AppointmentSuccess.createRoute(bookingUiState.confirmedAppointmentId)
            ) {
                popUpTo(NavDestination.Home.route) { inclusive = false }
            }
        }
    }

    val isAuthenticated = bookingUiState.isAuthenticated
    val screenTitle = if (isChange) stringResource(R.string.booking_title_change)
    else stringResource(R.string.booking_title)
    val buttonText = if (isChange) stringResource(R.string.booking_button_validate_change)
    else stringResource(R.string.booking_button_validate)
    val ctaText = if (!isAuthenticated) stringResource(R.string.booking_signin_to_book) else buttonText

    AppFormScreen(
        title = screenTitle,
        onNavigateBack = { navController.popBackStack() },
        error = bookingUiState.error,
        onErrorDismissed = { bookingViewModel.clearError() },
        horizontalPadding = Dimens.paddingNone,
        bottomBar = {
            Surface(
                shadowElevation = Dimens.cardElevation,
                color = Color.White
            ) {
                PrimaryButton(
                    text = ctaText,
                    enabled = !bookingUiState.isLoading,
                    isLoading = bookingUiState.isLoading,
                    onClick = {
                        if (!isAuthenticated) {
                            navController.navigate(NavDestination.Login.route)
                        } else {
                            bookingViewModel.confirmBooking(
                                message = reason,
                                reason = consultationReason
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Dimens.paddingL, vertical = Dimens.paddingM)
                )
            }
        }
    ) {
        VSpace(Dimens.paddingL)

        // ── Card 1: Doctor summary ────────────────────────────────────
        AppCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimens.paddingL)
        ) {
            Row(
                modifier = Modifier.padding(Dimens.paddingL),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AvatarCircle(
                    initials = practitioner?.initials ?: "--",
                    size = Dimens.avatarSizeMd,
                    fontSize = MaterialTheme.typography.titleMedium.fontSize
                )
                HSpace(Dimens.paddingM)
                Column {
                    AppSubtitleText(
                        text = practitioner?.fullName ?: doctorId,
                        color = TextPrimary
                    )
                    if (practitioner != null) {
                        AppCaptionText(text = practitioner.specialty, color = Primary)
                    }
                }
            }
        }

        VSpace(Dimens.paddingM)

        // ── Card 2: Appointment selected ──────────────────────────────
        AppCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimens.paddingL)
        ) {
            Column {
                BookingCardHeader(
                    icon = Icons.Filled.CalendarMonth,
                    label = stringResource(R.string.booking_header_selected),
                    tint = Primary
                )
                CheckRow(
                    label = stringResource(R.string.booking_row_reason),
                    value = consultationReason.ifEmpty { stringResource(R.string.booking_reason_placeholder) }
                )
                CheckRow(
                    label = stringResource(R.string.booking_row_datetime),
                    value = slotSummary
                )
                CheckRow(
                    label = stringResource(R.string.booking_row_institute),
                    value = practitioner?.clinicName ?: "",
                    isLast = true
                )
            }
        }

        if (isChange) {
            VSpace(Dimens.paddingM)

            // ── Card 3: Old appointment ───────────────────────────────
            AppCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimens.paddingL)
            ) {
                Column {
                    BookingCardHeader(
                        icon = Icons.Filled.History,
                        label = stringResource(R.string.booking_header_old_selected),
                        tint = OldApptAmber
                    )
                    CheckRow(
                        label = stringResource(R.string.booking_row_reason),
                        value = bookingUiState.previousAppointment?.consultationReason ?: "",
                        accentColor = OldApptAmberIcon
                    )
                    CheckRow(
                        label = stringResource(R.string.booking_row_datetime),
                        value = bookingUiState.previousAppointment?.let { DateUtil.formatSlotSummary(it.dateTime) } ?: "",
                        accentColor = OldApptAmberIcon
                    )
                    CheckRow(
                        label = stringResource(R.string.booking_row_institute),
                        value = bookingUiState.previousAppointment?.clinicName ?: "",
                        accentColor = OldApptAmberIcon,
                        isLast = true
                    )
                }
            }
        }

        VSpace(Dimens.paddingM)

        // ── Policy warning ────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimens.paddingL)
                .clip(RoundedCornerShape(Dimens.radiusCard))
                .background(WarningBg)
                .padding(Dimens.paddingM),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AppIcon(
                imageVector = Icons.Filled.Info,
                contentDescription = null, // a11y: decorative — labelled by adjacent Text
                tint = Warning,
                size = Dimens.iconSizeSm
            )
            HSpace(Dimens.paddingS)
            AppCaptionText(
                text = stringResource(R.string.booking_policy),
                color = Warning
            )
        }

        VSpace(Dimens.paddingM)

        // ── Card 4: Confirmation ──────────────────────────────────────
        AppCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimens.paddingL)
        ) {
            Column {
                BookingCardHeader(
                    icon = Icons.Filled.Info,
                    label = stringResource(R.string.booking_header_confirmation),
                    tint = Primary
                )
                Column(modifier = Modifier.padding(Dimens.paddingPlus)) {
                    AppCaptionText(
                        text = stringResource(R.string.booking_confirmation_hint),
                        color = TextSecondary
                    )
                    VSpace(Dimens.paddingM)
                    TextAreaField(
                        label = stringResource(R.string.booking_reason),
                        value = consultationReason,
                        onValueChange = { consultationReason = it },
                        placeholder = stringResource(R.string.booking_reason_placeholder),
                        minLines = 2
                    )
                    VSpace(Dimens.paddingM)
                    TextAreaField(
                        label = stringResource(R.string.booking_message_label),
                        value = reason,
                        onValueChange = { reason = it },
                        placeholder = stringResource(R.string.booking_message_commentary),
                        minLines = 4
                    )
                }
            }
        }

        if (!isAuthenticated) {
            VSpace(Dimens.paddingM)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimens.paddingL)
                    .clip(RoundedCornerShape(Dimens.radiusCard))
                    .background(PrimaryLight)
                    .padding(Dimens.paddingM),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AppIcon(
                    imageVector = Icons.Filled.Lock,
                    contentDescription = null, // a11y: decorative — labelled by adjacent Text
                    tint = Primary,
                    size = Dimens.iconSizeSm
                )
                HSpace(Dimens.paddingS)
                Column {
                    AppLabelText(
                        text = stringResource(R.string.booking_auth_required),
                        color = Primary
                    )
                    AppCaptionText(
                        text = stringResource(R.string.booking_auth_subtitle),
                        color = TextSecondary
                    )
                }
            }
        }

        VSpace(Dimens.paddingL)
    }
}

@Composable
private fun BookingCardHeader(icon: ImageVector, label: String, tint: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.paddingM, vertical = Dimens.paddingS),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AppIcon(
            imageVector = icon,
            contentDescription = null, // a11y: decorative — labelled by adjacent Text
            tint = tint,
            size = Dimens.iconSizeMicro,
        )
        HSpace(Dimens.paddingTiny)
        AppSectionHeaderText(text = label, color = tint)
    }
    HorizontalDivider(thickness = Dimens.borderHairline, color = BorderLight)
}