package lu.esklepios.app.di

import lu.esklepios.app.data.db.DatabaseDriverFactory
import lu.esklepios.app.data.network.TokenStorage
import lu.esklepios.app.storage.SecureStorage
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

fun androidModule() = module {
    single<TokenStorage> { SecureStorage(androidContext()) }
    single { DatabaseDriverFactory(androidContext()) }
}
