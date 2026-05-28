package lu.esklepios.app.data.preferences

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.alloc
import kotlinx.cinterop.interpretObjCPointer
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import lu.esklepios.app.data.network.TokenStorage
import platform.CoreFoundation.CFBridgingRelease
import platform.CoreFoundation.CFDictionaryRef
import platform.CoreFoundation.CFStringRef
import platform.CoreFoundation.CFTypeRefVar
import platform.Foundation.NSData
import platform.Foundation.NSMutableDictionary
import platform.Foundation.NSNumber
import platform.Foundation.NSString
import platform.Foundation.NSUTF8StringEncoding
import platform.Security.SecItemAdd
import platform.Security.SecItemCopyMatching
import platform.Security.SecItemDelete
import platform.Security.SecItemUpdate
import platform.Security.errSecItemNotFound
import platform.Security.errSecSuccess
import platform.Security.kSecAttrAccount
import platform.Security.kSecAttrService
import platform.Security.kSecClass
import platform.Security.kSecClassGenericPassword
import platform.Security.kSecMatchLimit
import platform.Security.kSecMatchLimitOne
import platform.Security.kSecReturnData
import platform.Security.kSecValueData

@OptIn(ExperimentalForeignApi::class)
@Suppress("UNCHECKED_CAST")
class IosTokenStorage : TokenStorage {

    private val service = "lu.esklepios.app"

    private fun baseQuery(account: String): NSMutableDictionary =
        NSMutableDictionary().apply {
            setObject(kSecClassGenericPassword.ns!!, forKey = kSecClass.ns!!)
            setObject(service, forKey = kSecAttrService.ns!!)
            setObject(account, forKey = kSecAttrAccount.ns!!)
        }

    private fun secureGet(account: String): String? = memScoped {
        val query = baseQuery(account).apply {
            setObject(NSNumber.numberWithBool(true), forKey = kSecReturnData.ns!!)
            setObject(kSecMatchLimitOne.ns!!, forKey = kSecMatchLimit.ns!!)
        }
        val result = alloc<CFTypeRefVar>()
        val status = SecItemCopyMatching(query as CFDictionaryRef, result.ptr)
        if (status == errSecSuccess) {
            (CFBridgingRelease(result.value) as? NSData)
                ?.let { NSString(data = it, encoding = NSUTF8StringEncoding) as? String }
        } else null
    }

    private fun secureSet(account: String, value: String) {
        val data = NSString(string = value).dataUsingEncoding(NSUTF8StringEncoding) ?: return
        val query = baseQuery(account)
        val attrs = NSMutableDictionary().apply { setObject(data, forKey = kSecValueData.ns!!) }
        val status = SecItemUpdate(query as CFDictionaryRef, attrs as CFDictionaryRef)
        if (status == errSecItemNotFound) {
            query.setObject(data, forKey = kSecValueData.ns!!)
            SecItemAdd(query as CFDictionaryRef, null)
        }
    }

    private fun secureRemove(account: String) {
        SecItemDelete(baseQuery(account) as CFDictionaryRef)
    }

    override fun getToken(): String? = secureGet(KEY_TOKEN)
    override fun setToken(token: String) = secureSet(KEY_TOKEN, token)
    override fun getRefreshToken(): String? = secureGet(KEY_REFRESH_TOKEN)
    override fun setRefreshToken(token: String) = secureSet(KEY_REFRESH_TOKEN, token)
    override fun getSavedEmail(): String? = secureGet(KEY_SAVED_EMAIL)
    override fun setSavedEmail(email: String) = secureSet(KEY_SAVED_EMAIL, email)
    override fun getSavedPassword(): String? = secureGet(KEY_SAVED_PASSWORD)
    override fun setSavedPassword(password: String) = secureSet(KEY_SAVED_PASSWORD, password)
    override fun clearSavedCredentials() {
        secureRemove(KEY_SAVED_EMAIL)
        secureRemove(KEY_SAVED_PASSWORD)
    }
    override fun clear() {
        secureRemove(KEY_TOKEN)
        secureRemove(KEY_REFRESH_TOKEN)
        clearSavedCredentials()
    }

    companion object {
        private const val KEY_TOKEN = "auth_token"
        private const val KEY_REFRESH_TOKEN = "auth_refresh_token"
        private const val KEY_SAVED_EMAIL = "saved_email"
        private const val KEY_SAVED_PASSWORD = "saved_password"
    }
}

// Toll-free bridges a CFStringRef constant to NSString for use as an NSDictionary key/value.
@OptIn(ExperimentalForeignApi::class)
private val CFStringRef?.ns: NSString?
    get() = this?.rawValue?.let { interpretObjCPointer(it) }
