package lu.esklepios.app.data.network

interface TokenStorage {
    fun getToken(): String?

    fun setToken(token: String)

    fun getRefreshToken(): String?

    fun setRefreshToken(token: String)

    fun getSavedEmail(): String?

    fun setSavedEmail(email: String)

    fun getSavedPassword(): String?

    fun setSavedPassword(password: String)

    fun clearSavedCredentials()

    fun clear()
}
