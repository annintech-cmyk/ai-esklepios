package lu.esklepios.app.di

import lu.esklepios.app.presentation.viewmodel.AppointmentSuccessViewModel
import lu.esklepios.app.presentation.viewmodel.AuthViewModel
import lu.esklepios.app.presentation.viewmodel.BookAppointmentViewModel
import lu.esklepios.app.presentation.viewmodel.ChangeEmailViewModel
import lu.esklepios.app.presentation.viewmodel.ChangePasswordViewModel
import lu.esklepios.app.presentation.viewmodel.EditProfileViewModel
import lu.esklepios.app.presentation.viewmodel.HomeViewModel
import lu.esklepios.app.presentation.viewmodel.MyAppointmentsViewModel
import lu.esklepios.app.presentation.viewmodel.PractitionerDetailViewModel
import lu.esklepios.app.presentation.viewmodel.ProfileViewModel
import lu.esklepios.app.presentation.viewmodel.SplashViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.startKoin

fun doInitKoin(enableLogging: Boolean = false) {
    startKoin {
        modules(
            iosModule(),
            sharedModule(),
        )
    }
}

class IOSViewModelFactory : KoinComponent {
    val splashViewModel: SplashViewModel by inject()
    val authViewModel: AuthViewModel by inject()
    val homeViewModel: HomeViewModel by inject()
    val practitionerDetailViewModel: PractitionerDetailViewModel by inject()
    val bookAppointmentViewModel: BookAppointmentViewModel by inject()
    val appointmentSuccessViewModel: AppointmentSuccessViewModel by inject()
    val myAppointmentsViewModel: MyAppointmentsViewModel by inject()
    val profileViewModel: ProfileViewModel by inject()
    val editProfileViewModel: EditProfileViewModel by inject()
    val changeEmailViewModel: ChangeEmailViewModel by inject()
    val changePasswordViewModel: ChangePasswordViewModel by inject()
}
