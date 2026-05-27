package lu.esklepios.app.ui.screens

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import lu.esklepios.app.R
import lu.esklepios.app.domain.model.Practitioner
import lu.esklepios.app.presentation.viewmodel.HomeViewModel
import lu.esklepios.app.ui.components.*
import lu.esklepios.app.ui.navigation.NavDestination
import lu.esklepios.app.ui.theme.*
import org.koin.androidx.compose.koinViewModel

private data class FilterItem(val key: String, @StringRes val labelRes: Int)

@Composable
fun HomeScreen(
    navController: NavController,
    onMenuClick: () -> Unit = {},
    viewModel: HomeViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var localSearch by remember { mutableStateOf("") }

    val filters = listOf(
        FilterItem("All",                  R.string.home_filter_all),
        FilterItem("Today",                R.string.home_filter_today),
        FilterItem("Within 3 days",        R.string.home_filter_3days),
        FilterItem("Open to new patients", R.string.home_filter_new_patients),
        FilterItem("Cardiology",           R.string.home_filter_cardiology),
        FilterItem("General Medicine",     R.string.home_filter_general_medicine),
        FilterItem("Dermatology",          R.string.home_filter_dermatology)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        GradientHeader(roundedBottom = true) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onMenuClick) {
                    Icon(Icons.Filled.Menu, contentDescription = stringResource(R.string.cd_open_menu), tint = Color.White)
                }
            }
            Text(
                text = stringResource(R.string.home_greeting),
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 14.sp
            )
            Text(
                text = stringResource(R.string.home_find_practitioner),
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = localSearch,
                onValueChange = {
                    localSearch = it
                    viewModel.updateSearchQuery(it)
                },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(stringResource(R.string.home_search_placeholder), color = Color.White.copy(alpha = 0.6f)) },
                leadingIcon = {
                    Icon(Icons.Filled.Search, contentDescription = null, tint = Color.White.copy(alpha = 0.8f))
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.White.copy(alpha = 0.5f),
                    unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                    focusedContainerColor = Color.White.copy(alpha = 0.15f),
                    unfocusedContainerColor = Color.White.copy(alpha = 0.1f),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )
        }

        LazyRow(
            modifier = Modifier.padding(vertical = 12.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filters) { filter ->
                val isSelected = uiState.selectedFilters.contains(filter.key) ||
                        (filter.key == "All" && uiState.selectedFilters.isEmpty())
                AppFilterChip(
                    label = stringResource(filter.labelRes),
                    isSelected = isSelected,
                    onClick = {
                        if (filter.key == "All") {
                            filters.filter { it.key != "All" }.forEach { viewModel.toggleFilter(it.key) }
                        } else {
                            viewModel.toggleFilter(filter.key)
                        }
                    }
                )
            }
        }

        when {
            uiState.isLoading -> LoadingIndicator(message = stringResource(R.string.home_loading))
            uiState.error != null -> ErrorView(
                message = uiState.error ?: stringResource(R.string.error_generic),
                onRetry = { viewModel.search() }
            )
            uiState.practitioners.isEmpty() -> EmptyStateView(
                icon = Icons.Filled.DateRange,
                title = stringResource(R.string.home_no_results_title),
                subtitle = stringResource(R.string.home_no_results_subtitle),
                actionLabel = stringResource(R.string.home_clear_filters),
                onAction = { viewModel.search() }
            )
            else -> {
                Text(
                    text = stringResource(R.string.home_practitioners_count, uiState.practitioners.size),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.practitioners, key = { it.id }) { practitioner ->
                        HomePractitionerCard(
                            practitioner = practitioner,
                            onSeeProfile = {
                                navController.navigate(NavDestination.PractitionerDetail.createRoute(practitioner.id))
                            },
                            onBook = { slotId ->
                                navController.navigate(NavDestination.BookAppointment.createRoute(practitioner.id, slotId))
                            },
                            onFavorite = { viewModel.toggleFavorite(practitioner.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HomePractitionerCard(
    practitioner: Practitioner,
    onSeeProfile: () -> Unit,
    onBook: (String) -> Unit,
    onFavorite: () -> Unit
) {
    val uiModel = PractitionerUiModel(
        id = practitioner.id,
        firstName = practitioner.firstName,
        lastName = practitioner.lastName,
        specialty = practitioner.specialty,
        clinic = practitioner.clinicName,
        address = "${practitioner.address}, ${practitioner.city}",
        isAcceptingNewPatients = practitioner.acceptingNewPatients,
        availableSlots = practitioner.availableSlots
            .filter { it.available }
            .groupBy { it.dateTime.take(10) }
            .map { (day, slots) ->
                SlotDayUiModel(
                    dayLabel = day,
                    slots = slots.map { it.id }
                )
            }
    )
    PractitionerCard(
        practitioner = uiModel,
        onSeeProfile = onSeeProfile,
        onBook = onBook
    )
}

@Composable
private fun AppFilterChip(label: String, isSelected: Boolean, onClick: () -> Unit) {
    FilterChip(
        selected = isSelected,
        onClick = onClick,
        label = { Text(label, fontSize = 12.sp) },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = Primary,
            selectedLabelColor = Color.White,
            containerColor = Surface,
            labelColor = TextSecondary
        ),
        border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = isSelected,
            borderColor = if (isSelected) Primary else BorderColor,
            selectedBorderColor = Primary
        )
    )
}
