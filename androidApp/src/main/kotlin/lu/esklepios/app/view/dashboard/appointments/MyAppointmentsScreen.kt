package lu.esklepios.app.view.dashboard.appointments

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import lu.esklepios.app.R
import lu.esklepios.app.domain.model.Appointment
import lu.esklepios.app.domain.model.AppointmentStatus
import lu.esklepios.app.presentation.viewmodel.MyAppointmentsViewModel
import lu.esklepios.app.core.navigation.NavDestination
import lu.esklepios.app.core.ui.components.*
import lu.esklepios.app.core.ui.theme.*
import lu.esklepios.app.utils.DateUtil
import org.koin.androidx.compose.koinViewModel

@Composable
fun MyAppointmentsScreen(
    navController: NavController,
    onMenuClick: () -> Unit = {},
    viewModel: MyAppointmentsViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var cancelTargetId by remember { mutableStateOf<String?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) viewModel.loadAppointments()
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let { error ->
            snackbarHostState.showSnackbar(error)
            viewModel.clearError()
        }
    }

    cancelTargetId?.let { id ->
        AlertDialog(
            onDismissRequest = { cancelTargetId = null },
            title = {
                AppTitleText(text = stringResource(R.string.appointments_cancel_title))
            },
            // AlertDialog text slot — using AppBodyText to replace raw Text
            text = {
                AppBodyText(text = stringResource(R.string.appointments_cancel_confirm))
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.cancelAppointment(id)
                        cancelTargetId = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Danger)
                ) {
                    AppButtonText(text = stringResource(R.string.appointments_cancel_yes))
                }
            },
            dismissButton = {
                TextButton(onClick = { cancelTargetId = null }) {
                    AppButtonText(
                        text = stringResource(R.string.appointments_keep),
                        color = Primary
                    )
                }
            }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Background
    ) { _ ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Background)
        ) {
            AppGradientHeader(
                leadingAction = HeaderAction.IconButtonAction(
                    icon = Icons.Filled.Menu,
                    contentDescription = stringResource(R.string.cd_open_menu),
                    onClick = onMenuClick
                ),
                centerAction = HeaderAction.TitleAction(
                    text = stringResource(R.string.appointments_title),
                    style = MaterialTheme.typography.headlineMedium
                )
            )

            AppTabRow(
                selectedIndex = uiState.selectedTab,
                tabs = listOf(
                    AppTabItem(stringResource(R.string.appointments_upcoming)) { viewModel.selectTab(0) },
                    AppTabItem(stringResource(R.string.appointments_past))     { viewModel.selectTab(1) },
                ),
            )

            when {
                uiState.isLoading -> LoadingIndicator(message = stringResource(R.string.appointments_loading))
                else -> {
                    val appointments = if (uiState.selectedTab == 0) uiState.upcomingAppointments else uiState.pastAppointments
                    if (appointments.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            EmptyStateView(
                                icon = Icons.Filled.CalendarMonth,
                                title = if (uiState.selectedTab == 0)
                                    stringResource(R.string.appointments_empty_upcoming_title)
                                else
                                    stringResource(R.string.appointments_empty_past_title),
                                subtitle = if (uiState.selectedTab == 0)
                                    stringResource(R.string.appointments_empty_upcoming_subtitle)
                                else
                                    stringResource(R.string.appointments_empty_past_subtitle),
                                actionLabel = if (uiState.selectedTab == 0)
                                    stringResource(R.string.appointments_find_practitioners)
                                else null,
                                onAction = if (uiState.selectedTab == 0) {
                                    { navController.navigate(NavDestination.Home.route) }
                                } else null
                            )
                        }
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(horizontal = Dimens.paddingL, vertical = Dimens.paddingM),
                            verticalArrangement = Arrangement.spacedBy(Dimens.paddingM)
                        ) {
                            items(appointments, key = { it.id }) { appointment ->
                                AppointmentItemCard(
                                    appointment = appointment,
                                    isUpcoming = uiState.selectedTab == 0,
                                    onCancel = { cancelTargetId = appointment.id },
                                    onModify = {}
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AppointmentItemCard(
    appointment: Appointment,
    isUpcoming: Boolean,
    onCancel: () -> Unit,
    onModify: () -> Unit
) {
    AppCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(Dimens.paddingL)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    AppSubtitleText(text = appointment.practitionerName)
                    AppCaptionText(text = appointment.specialty, color = Primary)
                    AppCaptionText(text = appointment.clinicName)
                }
                StatusBadge(status = appointment.status)
            }

            Spacer(Modifier.height(Dimens.paddingM))

            Row(verticalAlignment = Alignment.CenterVertically) {
                AppIcon(
                    Icons.Filled.CalendarMonth,
                    contentDescription = null, // a11y: decorative — labelled by adjacent Text
                    tint = TextSecondary,
                    size = Dimens.iconSizeSm
                )
                Spacer(Modifier.width(Dimens.paddingS))
                AppCaptionText(text = DateUtil.formatAppointmentDateTime(appointment.dateTime))
            }

            if (isUpcoming && appointment.status != AppointmentStatus.CANCELLED) {
                Spacer(Modifier.height(Dimens.paddingL))
                Row(horizontalArrangement = Arrangement.spacedBy(Dimens.paddingM)) {
                    SecondaryButton(
                        text = stringResource(R.string.appointments_modify),
                        onClick = onModify,
                        modifier = Modifier.weight(1f)
                    )
                    Button(
                        onClick = onCancel,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DangerBg,
                            contentColor = Danger
                        ),
                        shape = RoundedCornerShape(Dimens.radiusPill)
                    ) {
                        AppButtonText(
                            text = stringResource(R.string.action_cancel),
                            color = Danger
                        )
                    }
                }
            }
        }
    }
}

