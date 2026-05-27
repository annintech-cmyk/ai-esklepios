package lu.esklepios.app.core.navigation

import android.content.Intent
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import kotlinx.coroutines.launch
import lu.esklepios.app.core.ui.LocalNavAnimatedVisibilityScope
import lu.esklepios.app.core.ui.LocalSharedTransitionScope
import lu.esklepios.app.core.ui.components.AppCaptionText
import lu.esklepios.app.core.ui.components.AppDrawerContent
import lu.esklepios.app.core.ui.theme.GradientStart
import lu.esklepios.app.core.ui.theme.Primary
import lu.esklepios.app.core.ui.theme.Surface
import lu.esklepios.app.presentation.viewmodel.HomeViewModel
import lu.esklepios.app.util.AppUrls
import lu.esklepios.app.view.auth.forgotpassword.ForgotPasswordScreen
import lu.esklepios.app.view.auth.login.LoginScreen
import lu.esklepios.app.view.auth.register.RegisterScreen
import lu.esklepios.app.view.dashboard.appointments.MyAppointmentsScreen
import lu.esklepios.app.view.dashboard.appointments.booking.AppointmentSuccessScreen
import lu.esklepios.app.view.dashboard.appointments.booking.BookingScreen
import lu.esklepios.app.view.dashboard.home.HomeScreen
import lu.esklepios.app.view.dashboard.home.practitioner_data.PractitionerDetailScreen
import lu.esklepios.app.view.dashboard.home.practitioners.PractitionerListScreen
import lu.esklepios.app.view.dashboard.profile.ProfileScreen
import lu.esklepios.app.view.dashboard.profile.profile_edit.ChangeEmailScreen
import lu.esklepios.app.view.dashboard.profile.profile_edit.ChangePasswordScreen
import lu.esklepios.app.view.dashboard.profile.profile_edit.EditProfileScreen
import lu.esklepios.app.view.landing.LandingScreen
import lu.esklepios.app.view.splash.SplashScreen
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun AppNavGraph() {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    // Activity-scoped so LandingScreen and HomeScreen share the same instance,
    // allowing search queries typed on Landing to pre-fill HomeScreen.
    val homeViewModel: HomeViewModel = koinViewModel()
    val currentBackStack by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStack?.destination?.route

    val bottomNavRoutes =
        setOf(
            NavDestination.Home.route,
            NavDestination.MyAppointments.route,
            NavDestination.Profile.route,
        )

    val showDrawer = currentRoute in bottomNavRoutes
    val onMenuClick: () -> Unit = { scope.launch { drawerState.open() } }

    // ── Global: keep status bar icons white on the primary-blue background ──
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as ComponentActivity).window
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = false // white icons on blue bg
                isAppearanceLightNavigationBars = false
            }
        }
    }

    // ── Primary-blue layer sits behind the transparent status bar for ALL screens ──
    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(GradientStart),
    ) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            gesturesEnabled = showDrawer,
            drawerContent = {
                // windowInsets = WindowInsets(0) prevents ModalDrawerSheet from adding its
                // own status-bar padding on top of the padding AppDrawerContent already applies.
                ModalDrawerSheet(
                    drawerContainerColor = Surface,
                    windowInsets = WindowInsets(0),
                ) {
                    val context = LocalContext.current
                    AppDrawerContent(
                        onLogout = {
                            navController.navigate(NavDestination.Landing.route) {
                                popUpTo(0) { inclusive = true }
                            }
                        },
                        onCloseDrawer = { scope.launch { drawerState.close() } },
                        onOpenUrl = { url ->
                            runCatching {
                                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                            }
                        },
                        onOpenEmail = {
                            runCatching {
                                context.startActivity(
                                    Intent(Intent.ACTION_SENDTO).apply {
                                        data = Uri.parse(AppUrls.CONTACT_EMAIL)
                                    },
                                )
                            }
                        },
                    )
                }
            },
        ) {
            // Scaffold is transparent so the GradientStart box shows through the
            // transparent status bar on every screen.
            Scaffold(
                containerColor = Color.Transparent,
                bottomBar = {
                    if (currentRoute in bottomNavRoutes) {
                        AppBottomNavBar(
                            navController = navController,
                            currentRoute = currentRoute,
                        )
                    }
                },
            ) { innerPadding ->
                SharedTransitionLayout {
                    CompositionLocalProvider(LocalSharedTransitionScope provides this) {
                        NavHost(
                            navController = navController,
                            startDestination = NavDestination.Splash.route,
                            modifier = Modifier.padding(innerPadding),
                        ) {
                            composable(NavDestination.Splash.route) { SplashScreen(navController) }
                            composable(NavDestination.Landing.route) { LandingScreen(navController, homeViewModel) }
                            composable(NavDestination.Login.route) { LoginScreen(navController) }
                            composable(NavDestination.Register.route) { RegisterScreen(navController) }
                            composable(NavDestination.ForgotPassword.route) { ForgotPasswordScreen(navController) }
                            composable(NavDestination.Home.route) {
                                CompositionLocalProvider(LocalNavAnimatedVisibilityScope provides this) {
                                    HomeScreen(navController, onMenuClick, homeViewModel)
                                }
                            }
                            composable(NavDestination.PractitionerList.route) {
                                CompositionLocalProvider(LocalNavAnimatedVisibilityScope provides this) {
                                    PractitionerListScreen(navController, homeViewModel)
                                }
                            }
                            composable(
                                route = NavDestination.PractitionerDetail.route,
                                arguments = listOf(navArgument("practitionerId") { type = NavType.StringType }),
                            ) { backStackEntry ->
                                val practitionerId =
                                    backStackEntry.arguments?.getString("practitionerId") ?: ""
                                CompositionLocalProvider(LocalNavAnimatedVisibilityScope provides this) {
                                    PractitionerDetailScreen(navController, practitionerId)
                                }
                            }
                            composable(
                                route = NavDestination.BookAppointment.route,
                                arguments =
                                    listOf(
                                        navArgument("practitionerId") { type = NavType.StringType },
                                        navArgument("slotId") { type = NavType.StringType },
                                    ),
                            ) { backStackEntry ->
                                val practitionerId =
                                    backStackEntry.arguments?.getString("practitionerId") ?: ""
                                val slotId = backStackEntry.arguments?.getString("slotId") ?: ""
                                BookingScreen(navController, practitionerId, slotId, isChange = false)
                            }
                            composable(
                                route = NavDestination.AppointmentSuccess.route,
                                arguments =
                                    listOf(
                                        navArgument("appointmentId") { type = NavType.StringType },
                                    ),
                            ) { backStackEntry ->
                                val appointmentId =
                                    backStackEntry.arguments?.getString("appointmentId") ?: ""
                                AppointmentSuccessScreen(navController, appointmentId)
                            }
                            composable(NavDestination.MyAppointments.route) {
                                MyAppointmentsScreen(navController, onMenuClick)
                            }
                            composable(NavDestination.Profile.route) {
                                ProfileScreen(navController, onMenuClick)
                            }
                            composable(NavDestination.EditProfile.route) { EditProfileScreen(navController) }
                            composable(NavDestination.ChangeEmail.route) { ChangeEmailScreen(navController) }
                            composable(NavDestination.ChangePassword.route) {
                                ChangePasswordScreen(navController)
                            }
                            composable(
                                route = NavDestination.Booking.route,
                                arguments =
                                    listOf(
                                        navArgument("doctorId") { type = NavType.StringType },
                                        navArgument("slotId") { type = NavType.StringType },
                                        navArgument("isChange") {
                                            type = NavType.StringType
                                            defaultValue = "false"
                                        },
                                    ),
                            ) { backStackEntry ->
                                val doctorId = backStackEntry.arguments?.getString("doctorId") ?: ""
                                val slotId = backStackEntry.arguments?.getString("slotId") ?: ""
                                val isChange = backStackEntry.arguments?.getString("isChange")?.toBoolean() ?: false
                                BookingScreen(navController, doctorId, slotId, isChange)
                            }
                        }
                    }
                }
            }
        }
    }
}

data class BottomNavItem(val label: String, val icon: ImageVector, val route: String)

@Composable
fun AppBottomNavBar(
    navController: NavHostController,
    currentRoute: String?,
) {
    val items =
        listOf(
            BottomNavItem("Home", Icons.Filled.Home, NavDestination.Home.route),
            BottomNavItem("Appointments", Icons.Filled.CalendarMonth, NavDestination.MyAppointments.route),
            BottomNavItem("Profile", Icons.Filled.Person, NavDestination.Profile.route),
        )
    NavigationBar(containerColor = Surface) {
        items.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(NavDestination.Home.route) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { AppCaptionText(text = item.label) },
                colors =
                    NavigationBarItemDefaults.colors(
                        selectedIconColor = Primary,
                        selectedTextColor = Primary,
                        indicatorColor = Primary.copy(alpha = 0.12f),
                    ),
            )
        }
    }
}
