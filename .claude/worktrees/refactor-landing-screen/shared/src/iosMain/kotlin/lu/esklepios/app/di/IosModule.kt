package lu.esklepios.app.di

import lu.esklepios.app.data.db.DatabaseDriverFactory
import lu.esklepios.app.data.network.TokenStorage
import lu.esklepios.app.data.preferences.IosTokenStorage
import org.koin.dsl.module

fun iosModule() = module {
    single<TokenStorage> { IosTokenStorage() }
    single { DatabaseDriverFactory() }
}
