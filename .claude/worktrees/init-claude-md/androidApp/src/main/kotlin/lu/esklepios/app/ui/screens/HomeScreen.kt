package lu.esklepios.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import lu.esklepios.app.domain.model.Practitioner
import lu.esklepios.app.presentation.viewmodel.HomeViewModel
import lu.esklepios.app.ui.components.*
import lu.esklepios.app.ui.navigation.NavDestination
import lu.esklepios.app.ui.theme.*
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var localSearch by remember { mutableStateOf("") }

    val filters = listOf("All", "Today", "Within 3 days", "Open to new patients", "Cardiology", "General Medicine", "Dermatology")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        // Gradient header with search
        GradientHeader(roundedBottom = true) {
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Hello",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 14.sp
            )
            Text(
                text = "Find a practitioner",
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
                placeholder = { Text("Search specialty, doctor…", color = Color.White.copy(alpha = 0.6f)) },
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

        // Filter chips
        LazyRow(
            modifier = Modifier.padding(vertical = 12.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filters) { filter ->
                val isSelected = uiState.selectedFilters.contains(filter) ||
                        (filter == "All" && uiState.selectedFilters.isEmpty())
                AppFilterChip(
                    label = filter,
                    isSelected = isSelected,
                    onClick = {
                        if (filter == "All") {
                            filters.forEach { viewModel.toggleFilter(it) }
                        } else {
                            viewModel.toggleFilter(filter)
                        }
                    }
                )
            }
        }

        // Practitioners section
        when {
            uiState.isLoading -> LoadingIndicator(message = "Finding practitioners…")
            uiState.error != null -> ErrorView(
                message = uiState.error ?: "Something went wrong",
                onRetry = { viewModel.search() }
            )
            uiState.practitioners.isEmpty() -> EmptyStateView(
                icon = Icons.Filled.DateRange,
                title = "No practitioners found",
                subtitle = "Try adjusting your search or filters",
                actionLabel = "Clear Filters",
                onAction = { viewModel.search() }
            )
            else -> {
                Text(
                    text = "Practitioners (${uiState.practitioners.size})",
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
