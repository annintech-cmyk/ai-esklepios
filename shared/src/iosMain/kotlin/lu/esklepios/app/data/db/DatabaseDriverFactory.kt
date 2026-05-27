package lu.esklepios.app.data.db

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import lu.esklepios.app.db.ESklepiosDatabase

actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver = NativeSqliteDriver(ESklepiosDatabase.Schema, "esklepios.db")
}
