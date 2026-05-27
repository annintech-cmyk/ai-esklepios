import Foundation
import shared

class KoinHelper {
    static let shared = KoinHelper()
    private var factory: IOSViewModelFactory?

    private init() {}

    func start(enableLogging: Bool = false) {
        IosKoinInitKt.doInitKoin(enableLogging: enableLogging)
        factory = IOSViewModelFactory()
    }

    func splashViewModel() -> SplashViewModel {
        factory!.splashViewModel
    }

    func authViewModel() -> AuthViewModel {
        factory!.authViewModel
    }

    func homeViewModel() -> HomeViewModel {
        factory!.homeViewModel
    }

    func practitionerDetailViewModel() -> PractitionerDetailViewModel {
        factory!.practitionerDetailViewModel
    }

    func bookAppointmentViewModel() -> BookAppointmentViewModel {
        factory!.bookAppointmentViewModel
    }

    func appointmentSuccessViewModel() -> AppointmentSuccessViewModel {
        factory!.appointmentSuccessViewModel
    }

    func myAppointmentsViewModel() -> MyAppointmentsViewModel {
        factory!.myAppointmentsViewModel
    }

    func profileViewModel() -> ProfileViewModel {
        factory!.profileViewModel
    }

    func editProfileViewModel() -> EditProfileViewModel {
        factory!.editProfileViewModel
    }

    func changeEmailViewModel() -> ChangeEmailViewModel {
        factory!.changeEmailViewModel
    }

    func changePasswordViewModel() -> ChangePasswordViewModel {
        factory!.changePasswordViewModel
    }
}
