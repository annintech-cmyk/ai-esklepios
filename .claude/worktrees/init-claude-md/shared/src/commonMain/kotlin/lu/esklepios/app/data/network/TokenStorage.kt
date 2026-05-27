package lu.esklepios.app.data.network

interface TokenStorage {
    fun getToken(): String?
    fun setToken(token: String)
    fun getRefreshToken(): String?
    fun setRefreshToken(token: String)
    fun clear()
}
