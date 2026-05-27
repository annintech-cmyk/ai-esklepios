package lu.esklepios.app.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import lu.esklepios.app.R
import lu.esklepios.app.ui.screens.*

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()
    val currentBackStack by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStack?.destination?.route

    val bottomNavRoutes = setOf(
        NavDestination.Home.route,
        NavDestination.MyAppointments.route,
        NavDestination.Profile.route
    )

    Scaffold(
        bottomBar = {
            if (currentRoute in bottomNavRoutes) {
                AppBottomNavBar(navController = navController, currentRoute = currentRoute)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = NavDestination.Splash.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(NavDestination.Splash.route) { SplashScreen(navController) }
            composable(NavDestination.Landing.route) { LandingScreen(navController) }
            composable(NavDestination.Login.route) { LoginScreen(navController) }
            composable(NavDestination.Register.route) { RegisterScreen(navController) }
            composable(NavDestination.ForgotPassword.route) { ForgotPasswordScreen(navController) }
            composable(NavDestination.Home.route) { HomeScreen(navController) }
            composable(
                route = NavDestination.PractitionerDetail.route,
                arguments = listOf(navArgument("practitionerId") { type = NavType.StringType })
            ) { backStackEntry ->
                val practitionerId = backStackEntry.arguments?.getString("practitionerId") ?: ""
                PractitionerDetailScreen(navController, practitionerId)
            }
            composable(
                route = NavDestination.BookAppointment.route,
                arguments = listOf(
                    navArgument("practitionerId") { type = NavType.StringType },
                    navArgument("slotId") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val practitionerId = backStackEntry.arguments?.getString("practitionerId") ?: ""
                val slotId = backStackEntry.arguments?.getString("slotId") ?: ""
                BookAppointmentScreen(navController, practitionerId, slotId)
            }
            composable(
                route = NavDestination.AppointmentSuccess.route,
                arguments = listOf(navArgument("appointmentId") { type = NavType.StringType })
            ) { backStackEntry ->
                val appointmentId = backStackEntry.arguments?.getString("appointmentId") ?: ""
                AppointmentSuccessScreen(navController, appointmentId)
            }
            composable(NavDestination.MyAppointments.route) { MyAppointmentsScreen(navController) }
            composable(NavDestination.Profile.route) { ProfileScreen(navController) }
            composable(NavDestination.EditProfile.route) { EditProfileScreen(navController) }
            composable(NavDestination.ChangeEmail.route) { ChangeEmailScreen(navController) }
            composable(NavDestination.ChangePassword.route) { ChangePasswordScreen(navController) }
            composable(
                route = NavDestination.SearchResults.route,
                arguments = listOf(
                    navArgument("query") {
                        type = NavType.StringType
                        defaultValue = ""
                    }
                )
            ) { backStackEntry ->
                val query = backStackEntry.arguments?.getString("query") ?: ""
                SearchResultsScreen(navController, query)
            }
        }
    }
}

data class BottomNavItem(val label: String, val icon: ImageVector, val route: String)

@Composable
fun AppBottomNavBar(navController: NavHostController, currentRoute: String?) {
    val items = listOf(
        BottomNavItem(stringResource(R.string.nav_home), Icons.Filled.Home, NavDestination.Home.route),
        BottomNavItem(stringResource(R.string.nav_appointments), Icons.Filled.CalendarMonth, NavDestination.MyAppointments.route),
        BottomNavItem(stringResource(R.string.nav_profile), Icons.Filled.Person, NavDestination.Profile.route)
    )
    NavigationBar {
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
                label = { Text(item.label) }
            )
        }
    }
}
