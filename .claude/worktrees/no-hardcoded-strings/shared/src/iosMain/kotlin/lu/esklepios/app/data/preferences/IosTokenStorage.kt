package lu.esklepios.app.data.preferences

import com.russhwolf.settings.NSUserDefaultsSettings
import com.russhwolf.settings.Settings
import lu.esklepios.app.data.network.TokenStorage

class IosTokenStorage : TokenStorage {
    private val settings: Settings = NSUserDefaultsSettings.Factory().create("esklepios_auth")

    override fun getToken(): String? = settings.getStringOrNull(KEY_TOKEN)

    override fun setToken(token: String) {
        settings.putString(KEY_TOKEN, token)
    }

    override fun getRefreshToken(): String? = settings.getStringOrNull(KEY_REFRESH_TOKEN)

    override fun setRefreshToken(token: String) {
        settings.putString(KEY_REFRESH_TOKEN, token)
    }

    override fun clear() {
        settings.remove(KEY_TOKEN)
        settings.remove(KEY_REFRESH_TOKEN)
    }

    companion object {
        private const val KEY_TOKEN = "auth_token"
        private const val KEY_REFRESH_TOKEN = "auth_refresh_token"
    }
}
