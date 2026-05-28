package lu.esklepios.app.view.dashboard.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import lu.esklepios.app.R
import lu.esklepios.app.core.navigation.NavDestination
import lu.esklepios.app.core.ui.components.AppBodyText
import lu.esklepios.app.core.ui.components.AppCaptionText
import lu.esklepios.app.core.ui.components.AppGradientHeader
import lu.esklepios.app.core.ui.components.AppIcon
import lu.esklepios.app.core.ui.components.AppSubtitleText
import lu.esklepios.app.core.ui.components.AppTextLink
import lu.esklepios.app.core.ui.components.EmptyStateView
import lu.esklepios.app.core.ui.components.ErrorView
import lu.esklepios.app.core.ui.components.HeaderAction
import lu.esklepios.app.core.ui.components.HeaderSearch
import lu.esklepios.app.core.ui.components.LoadingIndicator
import lu.esklepios.app.core.ui.theme.Background
import lu.esklepios.app.core.ui.theme.BorderColor
import lu.esklepios.app.core.ui.theme.Dimens
import lu.esklepios.app.core.ui.theme.Primary
import lu.esklepios.app.core.ui.theme.Surface
import lu.esklepios.app.core.ui.theme.TextHint
import lu.esklepios.app.core.ui.theme.TextPrimary
import lu.esklepios.app.core.ui.theme.TextSecondary
import lu.esklepios.app.presentation.viewmodel.HomeViewModel
import lu.esklepios.app.util.DateFilter
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeScreen(
    navController: NavController,
    onMenuClick: () -> Unit = {},
    viewModel: HomeViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val filteredPractitioners = viewModel.filteredPractitioners

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
        AppGradientHeader(
            roundedBottom = true,
            leadingAction =
                HeaderAction.IconButtonAction(
                    icon = Icons.Filled.Menu,
                    contentDescription = stringResource(R.string.cd_open_menu),
                    onClick = onMenuClick,
                ),
            centerAction =
                HeaderAction.TitleAction(
                    text = stringResource(R.string.app_name),
                    style = MaterialTheme.typography.headlineMedium,
                ),
            search =
                HeaderSearch(
                    searchQuery = uiState.specialtyQuery,
                    onSearchQueryChange = { viewModel.onSpecialtyQueryChange(it) },
                    locationQuery = uiState.locationQuery,
                    onLocationQueryChange = { viewModel.onLocationQueryChange(it) },
                    onSearchClick = { viewModel.onSearch() },
                ),
        )

        // Date filter segmented control
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
                        AppCaptionText(text = label)
                    }
                }
            }
        }

        // "Open to New Patients" toggle + "See all" shortcut
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .background(Surface)
                    .padding(horizontal = Dimens.paddingL)
                    .padding(bottom = Dimens.paddingM),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            NewPatientsToggle(
                checked = uiState.openToNewPatients,
                label = stringResource(R.string.home_filter_new_patients),
                onToggle = { viewModel.toggleNewPatientsFilter() },
            )
            Spacer(Modifier.weight(1f))
            AppTextLink(
                text = stringResource(R.string.home_see_all_short),
                onClick = { navController.navigate(NavDestination.PractitionerList.route) },
            )
        }

        // Section header
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimens.paddingL, vertical = Dimens.paddingXS),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AppSubtitleText(text = stringResource(R.string.home_nearby_label))
            AppCaptionText(
                text =
                    stringResource(
                        R.string.home_results_count,
                        if (uiState.hasSearched) filteredPractitioners.size else 0,
                    ),
            )
        }

        // Content area
        when {
            !uiState.hasSearched -> {
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .weight(1f),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(horizontal = Dimens.paddingXXXL),
                    ) {
                        AppIcon(
                            imageVector = Icons.Filled.Search,
                            // a11y: decorative — labelled by adjacent Text
                            contentDescription = null,
                            tint = TextHint,
                            size = Dimens.emptyIconSmSize,
                        )
                        Spacer(Modifier.height(Dimens.paddingM))
                        AppBodyText(
                            text = stringResource(R.string.home_not_searched_title),
                            color = TextSecondary,
                            textAlign = TextAlign.Center,
                        )
                        Spacer(Modifier.height(Dimens.paddingXXS))
                        AppCaptionText(
                            text = stringResource(R.string.home_not_searched_subtitle),
                            color = TextHint,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }
            uiState.isLoading -> LoadingIndicator()
            uiState.error != null ->
                ErrorView(
                    message = stringResource(R.string.error_generic),
                    onRetry = { viewModel.onSearch() },
                )
            filteredPractitioners.isEmpty() ->
                EmptyStateView(
                    icon = Icons.Filled.Search,
                    title = stringResource(R.string.home_no_results_title),
                    subtitle = stringResource(R.string.home_no_results_subtitle),
                    actionLabel = stringResource(R.string.home_clear_filters),
                    onAction = { viewModel.setDateFilter(DateFilter.ALL.apiKey) },
                )
            else -> {
                LazyColumn(
                    contentPadding = PaddingValues(vertical = Dimens.paddingS),
                    verticalArrangement = Arrangement.spacedBy(Dimens.paddingM),
                ) {
                    items(
                        filteredPractitioners.take(2),
                        key = { it.id },
                    ) { practitioner ->
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

                    if (filteredPractitioners.size > 2) {
                        item {
                            Box(
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = Dimens.paddingL),
                                contentAlignment = Alignment.Center,
                            ) {
                                AppTextLink(
                                    text = stringResource(R.string.home_see_all),
                                    onClick = { navController.navigate(NavDestination.PractitionerList.route) },
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
private fun NewPatientsToggle(
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
