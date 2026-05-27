package lu.esklepios.app.di

import lu.esklepios.app.data.db.DatabaseDriverFactory
import lu.esklepios.app.data.network.TokenStorage
import lu.esklepios.app.debug.FakePractitionerRepository
import lu.esklepios.app.domain.repository.PractitionerRepository
import lu.esklepios.app.storage.SecureStorage
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

fun androidModule() =
    module {
        single<TokenStorage> { SecureStorage(androidContext()) }
        single { DatabaseDriverFactory(androidContext()) }

        // Overrides shared module's real PractitionerRepository with the fake for development.
        // Remove this line and delete FakePractitionerRepository when the API is ready.
        // AndroidModule is loaded last in ESklepiosApp so it wins (allowOverride = true by default).
        single<PractitionerRepository> { FakePractitionerRepository() }
    }
