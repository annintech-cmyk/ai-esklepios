package lu.esklepios.app.di

import kotlinx.datetime.Clock
import lu.esklepios.app.BuildKonfig
import lu.esklepios.app.data.db.DatabaseDriverFactory
import lu.esklepios.app.data.network.ApiService
import lu.esklepios.app.data.network.HttpClientFactory
import lu.esklepios.app.data.network.KtorApiService
import lu.esklepios.app.data.repository.AppointmentRepositoryImpl
import lu.esklepios.app.data.repository.AuthRepositoryImpl
import lu.esklepios.app.data.repository.PractitionerRepositoryImpl
import lu.esklepios.app.data.repository.UserRepositoryImpl
import lu.esklepios.app.db.ESklepiosDatabase
import lu.esklepios.app.domain.repository.AppointmentRepository
import lu.esklepios.app.domain.repository.AuthRepository
import lu.esklepios.app.domain.repository.PractitionerRepository
import lu.esklepios.app.domain.repository.UserRepository
import lu.esklepios.app.domain.usecase.CancelAppointmentUseCase
import lu.esklepios.app.domain.usecase.ChangeEmailUseCase
import lu.esklepios.app.domain.usecase.ChangePasswordUseCase
import lu.esklepios.app.domain.usecase.CreateAppointmentUseCase
import lu.esklepios.app.domain.usecase.ForgotPasswordUseCase
import lu.esklepios.app.domain.usecase.GetPastAppointmentsUseCase
import lu.esklepios.app.domain.usecase.GetPractitionerDetailUseCase
import lu.esklepios.app.domain.usecase.GetProfileUseCase
import lu.esklepios.app.domain.usecase.GetUpcomingAppointmentsUseCase
import lu.esklepios.app.domain.usecase.LoginUseCase
import lu.esklepios.app.domain.usecase.LogoutUseCase
import lu.esklepios.app.domain.usecase.ModifyAppointmentUseCase
import lu.esklepios.app.domain.usecase.RegisterUseCase
import lu.esklepios.app.domain.usecase.SearchPractitionersUseCase
import lu.esklepios.app.domain.usecase.ToggleFavoriteUseCase
import lu.esklepios.app.domain.usecase.UpdateProfileUseCase
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
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

fun sharedModule() =
    module {
        // Utilities
        single<Clock> { Clock.System }

        // Database
        single { get<DatabaseDriverFactory>().createDriver() }
        single { ESklepiosDatabase(get()) }

        // Network
        single { HttpClientFactory(get(), BuildKonfig.ENABLE_LOGGING).create() }
        single<ApiService> { KtorApiService(get(), BuildKonfig.BASE_API_URL) }

        // Repositories
        single<AuthRepository> { AuthRepositoryImpl(get(), get(), get()) }
        single<PractitionerRepository> { PractitionerRepositoryImpl(get(), get()) }
        single<AppointmentRepository> { AppointmentRepositoryImpl(get(), get()) }
        single<UserRepository> { UserRepositoryImpl(get()) }

        // Use cases
        factoryOf(::LoginUseCase)
        factoryOf(::RegisterUseCase)
        factoryOf(::ForgotPasswordUseCase)
        factoryOf(::LogoutUseCase)
        factoryOf(::SearchPractitionersUseCase)
        factoryOf(::GetPractitionerDetailUseCase)
        factoryOf(::ToggleFavoriteUseCase)
        factoryOf(::CreateAppointmentUseCase)
        factoryOf(::GetUpcomingAppointmentsUseCase)
        factoryOf(::GetPastAppointmentsUseCase)
        factoryOf(::CancelAppointmentUseCase)
        factoryOf(::ModifyAppointmentUseCase)
        factoryOf(::GetProfileUseCase)
        factoryOf(::UpdateProfileUseCase)
        factoryOf(::ChangeEmailUseCase)
        factoryOf(::ChangePasswordUseCase)

        // ViewModels
        factoryOf(::SplashViewModel)
        factoryOf(::AuthViewModel)
        factoryOf(::HomeViewModel)
        factoryOf(::PractitionerDetailViewModel)
        factoryOf(::BookAppointmentViewModel)
        factoryOf(::AppointmentSuccessViewModel)
        factoryOf(::MyAppointmentsViewModel)
        factoryOf(::ProfileViewModel)
        factoryOf(::EditProfileViewModel)
        factoryOf(::ChangeEmailViewModel)
        factoryOf(::ChangePasswordViewModel)
    }
