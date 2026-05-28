package lu.esklepios.app.storage

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import lu.esklepios.app.data.network.TokenStorage

class SecureStorage(context: Context) : TokenStorage {
    private val masterKey =
        MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

    private val prefs =
        EncryptedSharedPreferences.create(
            context,
            "esklepios_secure_prefs",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
        )

    override fun getToken(): String? = prefs.getString(KEY_TOKEN, null)

    override fun setToken(token: String) = prefs.edit().putString(KEY_TOKEN, token).apply()

    override fun getRefreshToken(): String? = prefs.getString(KEY_REFRESH_TOKEN, null)

    override fun setRefreshToken(token: String) = prefs.edit().putString(KEY_REFRESH_TOKEN, token).apply()

    override fun getSavedEmail(): String? = prefs.getString(KEY_SAVED_EMAIL, null)

    override fun setSavedEmail(email: String) = prefs.edit().putString(KEY_SAVED_EMAIL, email).apply()

    override fun getSavedPassword(): String? = prefs.getString(KEY_SAVED_PASSWORD, null)

    override fun setSavedPassword(password: String) = prefs.edit().putString(KEY_SAVED_PASSWORD, password).apply()

    override fun clearSavedCredentials() {
        prefs.edit().remove(KEY_SAVED_EMAIL).remove(KEY_SAVED_PASSWORD).apply()
    }

    override fun clear() = prefs.edit().clear().apply()

    companion object {
        private const val KEY_TOKEN = "auth_token"
        private const val KEY_REFRESH_TOKEN = "auth_refresh_token"
        private const val KEY_SAVED_EMAIL = "saved_email"
        private const val KEY_SAVED_PASSWORD = "saved_password"
    }
}
