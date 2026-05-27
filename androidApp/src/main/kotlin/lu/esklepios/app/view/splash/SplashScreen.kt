package lu.esklepios.app.view.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import lu.esklepios.app.R
import lu.esklepios.app.core.navigation.NavDestination
import lu.esklepios.app.core.ui.components.AppBodyText
import lu.esklepios.app.core.ui.components.AppTitleText
import lu.esklepios.app.core.ui.theme.Dimens
import lu.esklepios.app.core.ui.theme.Gradients
import lu.esklepios.app.presentation.viewmodel.SplashViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun SplashScreen(
    navController: NavController,
    viewModel: SplashViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) { viewModel.checkAuth() }

    LaunchedEffect(uiState.isLoading) {
        if (!uiState.isLoading) {
            if (uiState.isAuthenticated) {
                navController.navigate(NavDestination.Home.route) {
                    popUpTo(0) { inclusive = true }
                }
            } else {
                navController.navigate(NavDestination.Landing.route) {
                    popUpTo(0) { inclusive = true }
                }
            }
        }
    }

    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(Gradients.primaryBrush),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .safeDrawingPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            AppTitleText(
                text = stringResource(R.string.app_name),
                color = Color.White,
            )
            Spacer(Modifier.height(Dimens.paddingS))
            AppBodyText(
                text = stringResource(R.string.landing_tagline),
                color = Color.White.copy(alpha = 0.7f),
            )
            Spacer(Modifier.height(Dimens.paddingXXXL + Dimens.paddingL))
            CircularProgressIndicator(
                color = Color.White,
                modifier = Modifier.size(Dimens.paddingXXXL),
            )
        }
    }
}
