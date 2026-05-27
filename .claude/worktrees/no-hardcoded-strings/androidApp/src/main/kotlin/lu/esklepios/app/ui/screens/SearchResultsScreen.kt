package lu.esklepios.app.ui.screens

import androidx.compose.runtime.*
import androidx.navigation.NavController
import lu.esklepios.app.presentation.viewmodel.HomeViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun SearchResultsScreen(
    navController: NavController,
    query: String = "",
    viewModel: HomeViewModel = koinViewModel()
) {
    LaunchedEffect(query) {
        viewModel.updateSearchQuery(query)
        viewModel.search()
    }
    HomeScreen(navController = navController, viewModel = viewModel)
}
