package lu.esklepios.app.data.db

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import lu.esklepios.app.db.ESklepiosDatabase

actual class DatabaseDriverFactory(private val context: Context) {
    actual fun createDriver(): SqlDriver =
        AndroidSqliteDriver(ESklepiosDatabase.Schema, context, "esklepios.db")
}
