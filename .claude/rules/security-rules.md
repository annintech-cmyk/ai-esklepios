# Security Rules

## Rule SEC-1: TokenStorage Is the Sole Auth Source of Truth
`TokenStorage` (via `SecureStorage` on Android, `KeychainStorage` on iOS) is the **only** place auth state is stored. Do not add a secondary boolean flag (SharedPreferences, UserDefaults, in-memory field) alongside it.

- Android keys: `auth_token`, `auth_refresh_token` in `EncryptedSharedPreferences`
- iOS service: `lu.esklepios.app` in the system Keychain
- Check: `tokenStorage.getToken() != null`
- `SessionManager` has been deleted and must not be reintroduced.

## Rule SEC-2: Never Hardcode Secrets in Source Code
API keys, client secrets, and environment-specific URLs live in:
- `dev.properties` (NOT committed — in `.gitignore`)
- `prod.properties` (committed, but must contain only non-sensitive config)

Access via `BuildKonfig.FIELD_NAME`. If a secret must be fetched at runtime, fetch it from the API — never embed it in the binary.

## Rule SEC-3: Token Refresh Is Ktor-Managed
The Ktor `BearerTokenPlugin` handles token refresh automatically. Repositories must never implement retry-on-401 logic. Adding manual refresh in a repository violates Rule API-5.

## Rule SEC-4: No Sensitive Data in Logs
`BuildKonfig.ENABLE_LOGGING` gates Ktor body logging. Even when logging is enabled (dev builds), avoid logging:
- Passwords or password hashes
- Auth tokens or refresh tokens
- CNS numbers or other personal health identifiers

## Rule SEC-5: Logout Clears All Local Data Atomically
`AuthRepositoryImpl.logout()` uses a single `database.transaction { }` block to clear all three tables (`usersQueries`, `practitionersQueries`, `appointmentsQueries`) after clearing the token. Partial clears leave stale data visible between sessions.

## Rule SEC-6: Certificate Pinning (Future)
When the prod API certificate is finalized, add a `CertificatePinner` to the OkHttp client (Android) and `URLSession` configuration (iOS). Do not implement self-signed cert acceptance in production code.
