package lu.esklepios.app.ui.navigation

sealed class NavDestination(val route: String) {
    object Splash : NavDestination("splash")
    object Landing : NavDestination("landing")
    object Login : NavDestination("login")
    object Register : NavDestination("register")
    object ForgotPassword : NavDestination("forgot_password")
    object Home : NavDestination("home")
    object PractitionerDetail : NavDestination("practitioner_detail/{practitionerId}") {
        fun createRoute(practitionerId: String) = "practitioner_detail/$practitionerId"
    }
    object BookAppointment : NavDestination("book_appointment/{practitionerId}/{slotId}") {
        fun createRoute(practitionerId: String, slotId: String) = "book_appointment/$practitionerId/$slotId"
    }
    object AppointmentSuccess : NavDestination("appointment_success/{appointmentId}") {
        fun createRoute(appointmentId: String) = "appointment_success/$appointmentId"
    }
    object MyAppointments : NavDestination("my_appointments")
    object Profile : NavDestination("profile")
    object EditProfile : NavDestination("edit_profile")
    object ChangeEmail : NavDestination("change_email")
    object ChangePassword : NavDestination("change_password")
    object SearchResults : NavDestination("search_results?query={query}") {
        fun createRoute(query: String) = "search_results?query=${encodeQuery(query)}"
        private fun encodeQuery(q: String) = java.net.URLEncoder.encode(q, "UTF-8")
    }
}
