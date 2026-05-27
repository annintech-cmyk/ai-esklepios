package lu.esklepios.app

import android.app.Application
import lu.esklepios.app.di.androidModule
import lu.esklepios.app.di.sharedModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class ESklepiosApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@ESklepiosApp)
            modules(androidModule(), sharedModule())
        }
    }
}
