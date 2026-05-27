package lu.esklepios.app.view.landing

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import lu.esklepios.app.R
import lu.esklepios.app.core.navigation.NavDestination
import lu.esklepios.app.core.ui.components.*
import lu.esklepios.app.core.ui.theme.*
import lu.esklepios.app.domain.repository.AuthRepository
import lu.esklepios.app.presentation.viewmodel.HomeViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
fun LandingScreen(
    navController: NavController,
    viewModel: HomeViewModel = koinViewModel(),
    authRepository: AuthRepository = koinInject(),
) {
    var searchQuery by remember { mutableStateOf("") }
    var locationQuery by remember { mutableStateOf("") }

    val canSearch by remember { derivedStateOf { searchQuery.isNotBlank() || locationQuery.isNotBlank() } }

    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val heroHeight = screenHeight * 0.65f
    val overlapHeight = screenHeight * 0.30f

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(PrimaryMid)
                .verticalScroll(rememberScrollState()),
    ) {
        LandingHeroSection(
            heroHeight = heroHeight,
            onSignInClick = { navController.navigate(NavDestination.Login.route) },
        )
        LandingSearchCard(
            searchQuery = searchQuery,
            onSearchQueryChange = {
                searchQuery = it
                viewModel.onSpecialtyQueryChange(it)
            },
            locationQuery = locationQuery,
            onLocationQueryChange = {
                locationQuery = it
                viewModel.onLocationQueryChange(it)
            },
            enabled = canSearch,
            onSearchClick = {
                viewModel.onSearch()
                if (authRepository.isLoggedIn()) {
                    navController.navigate(NavDestination.Home.route)
                } else {
                    navController.navigate(NavDestination.PractitionerList.route)
                }
            },
            showPoints = true,
            modifier = Modifier.offset(y = -overlapHeight).zIndex(1f).padding(Dimens.paddingL),
        )
    }
}

@Composable
private fun LandingHeroSection(
    heroHeight: Dp,
    onSignInClick: () -> Unit,
) {
    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(heroHeight)
                .clip(
                    RoundedCornerShape(
                        bottomStart = Dimens.radiusPill,
                        bottomEnd = Dimens.radiusPill,
                    ),
                )
                .background(Gradients.verticalBrush)
                .padding(horizontal = Dimens.paddingXXL),
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(bottom = Dimens.paddingXXXL),
                horizontalArrangement = Arrangement.End,
            ) {
                GlassButton(
                    text = stringResource(R.string.action_sign_in),
                    onClick = onSignInClick,
                )
            }

            AppTitleText(
                text = stringResource(R.string.app_name),
                color = Color.White,
            )

            VSpace(Dimens.paddingXL)

            // AnnotatedString usage — cannot be wrapped in App*Text: intentional exception
            Text(
                text =
                    buildAnnotatedString {
                        withStyle(SpanStyle(color = Color.White, fontWeight = FontWeight.ExtraBold)) {
                            append(stringResource(R.string.landing_hero_prefix))
                        }
                        withStyle(SpanStyle(color = TealAccent, fontWeight = FontWeight.ExtraBold)) {
                            append(stringResource(R.string.landing_hero_accent))
                        }
                    },
                style = MaterialTheme.typography.displaySmall,
            )
        }
    }
}
