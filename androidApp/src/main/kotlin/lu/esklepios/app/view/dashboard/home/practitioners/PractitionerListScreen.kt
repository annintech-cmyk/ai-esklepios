package lu.esklepios.app.view.dashboard.home.practitioners

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import lu.esklepios.app.R
import lu.esklepios.app.core.navigation.NavDestination
import lu.esklepios.app.core.ui.components.*
import lu.esklepios.app.core.ui.theme.*
import lu.esklepios.app.presentation.viewmodel.HomeViewModel
import lu.esklepios.app.util.DateFilter
import lu.esklepios.app.view.dashboard.home.HomePractitionerCard

@Composable
fun PractitionerListScreen(
    navController: NavController,
    viewModel: HomeViewModel,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val dateFilters =
        DateFilter.entries.map { filter ->
            val resId =
                when (filter) {
                    DateFilter.ALL -> R.string.home_filter_all
                    DateFilter.TODAY -> R.string.home_filter_today
                    DateFilter.WITHIN_3_DAYS -> R.string.home_filter_3days
                }
            filter.apiKey to stringResource(resId)
        }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(Background),
    ) {
        AppToolbar(
            title = stringResource(R.string.screen_practitioner_list),
            onNavigateBack = { navController.popBackStack() },
        )

        // ── Date filter segmented control ────────────────────────────────
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .background(Surface)
                    .padding(horizontal = Dimens.paddingL, vertical = Dimens.paddingM),
        ) {
            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                dateFilters.forEachIndexed { index, (key, label) ->
                    SegmentedButton(
                        selected = uiState.selectedDateFilter == key,
                        onClick = { viewModel.setDateFilter(key) },
                        shape = SegmentedButtonDefaults.itemShape(index = index, count = dateFilters.size),
                        colors =
                            SegmentedButtonDefaults.colors(
                                activeContainerColor = Primary,
                                activeContentColor = Color.White,
                                activeBorderColor = Primary,
                                inactiveContainerColor = Surface,
                                inactiveContentColor = TextSecondary,
                                inactiveBorderColor = BorderColor,
                            ),
                        icon = {},
                    ) {
                        AppLabelText(text = label, color = Color.Unspecified)
                    }
                }
            }
        }

        // ── "Open to New Patients" toggle ────────────────────────────────
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .background(Surface)
                    .padding(horizontal = Dimens.paddingL)
                    .padding(bottom = Dimens.paddingM),
        ) {
            PractitionerListNewPatientsToggle(
                checked = uiState.openToNewPatients,
                label = stringResource(R.string.home_filter_new_patients),
                onToggle = { viewModel.toggleNewPatientsFilter() },
            )
        }

        // ── Section header ───────────────────────────────────────────────
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimens.paddingL, vertical = Dimens.paddingXS),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AppSubtitleText(
                text = stringResource(R.string.home_nearby_label),
            )
            AppCaptionText(
                text = stringResource(R.string.home_results_count, uiState.practitioners.size),
            )
        }

        // ── Content ──────────────────────────────────────────────────────
        when {
            uiState.isLoading -> LoadingIndicator()
            uiState.error != null ->
                ErrorView(
                    message = stringResource(R.string.error_generic),
                    onRetry = { viewModel.refresh() },
                )
            uiState.practitioners.isEmpty() -> {
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .weight(1f),
                    contentAlignment = Alignment.Center,
                ) {
                    EmptyStateView(
                        icon = Icons.Filled.Search,
                        title = stringResource(R.string.home_no_results_title),
                        subtitle = stringResource(R.string.home_no_results_subtitle),
                        actionLabel = stringResource(R.string.home_clear_filters),
                        onAction = { viewModel.setDateFilter(DateFilter.ALL.apiKey) },
                    )
                }
            }
            else -> {
                LazyColumn(
                    contentPadding = PaddingValues(vertical = Dimens.paddingS),
                    verticalArrangement = Arrangement.spacedBy(Dimens.paddingM),
                ) {
                    items(uiState.practitioners, key = { it.id }) { practitioner ->
                        HomePractitionerCard(
                            practitioner = practitioner,
                            onBook = { slotId ->
                                navController.navigate(
                                    NavDestination.Booking.createRoute(practitioner.id, slotId),
                                )
                            },
                            onSeeProfile = {
                                navController.navigate(
                                    NavDestination.PractitionerDetail.createRoute(practitioner.id),
                                )
                            },
                            modifier = Modifier.padding(horizontal = Dimens.paddingL),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PractitionerListNewPatientsToggle(
    checked: Boolean,
    label: String,
    onToggle: () -> Unit,
) {
    Row(
        modifier = Modifier.clickable(onClick = onToggle),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = { onToggle() },
            colors =
                CheckboxDefaults.colors(
                    checkedColor = Primary,
                    uncheckedColor = TextHint,
                    checkmarkColor = Color.White,
                ),
        )
        AppCaptionText(
            text = label,
            color = if (checked) TextPrimary else TextSecondary,
        )
    }
}
