package lu.esklepios.app.data.network

import io.ktor.client.HttpClient
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

expect fun createHttpEngine(): io.ktor.client.engine.HttpClientEngine

class HttpClientFactory(
    private val tokenStorage: TokenStorage,
    private val enableLogging: Boolean
) {
    fun create(): HttpClient {
        return HttpClient(createHttpEngine()) {
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = false
                    isLenient = true
                    ignoreUnknownKeys = true
                    encodeDefaults = true
                })
            }

            install(io.ktor.client.plugins.HttpTimeout) {
                connectTimeoutMillis = 30_000L
                requestTimeoutMillis = 30_000L
                socketTimeoutMillis = 30_000L
            }

            if (enableLogging) {
                install(Logging) {
                    logger = Logger.SIMPLE
                    level = LogLevel.BODY
                }
            }

            install(Auth) {
                bearer {
                    loadTokens {
                        val token = tokenStorage.getToken()
                        val refreshToken = tokenStorage.getRefreshToken()
                        if (token != null && refreshToken != null) {
                            BearerTokens(token, refreshToken)
                        } else {
                            null
                        }
                    }
                    refreshTokens {
                        val refreshToken = tokenStorage.getRefreshToken()
                        if (refreshToken != null) {
                            BearerTokens(
                                accessToken = tokenStorage.getToken() ?: "",
                                refreshToken = refreshToken
                            )
                        } else {
                            null
                        }
                    }
                }
            }

            defaultRequest {
                contentType(ContentType.Application.Json)
                headers.append("Accept", "application/json")
                headers.append("X-App-Platform", "mobile")
            }
        }
    }
}
