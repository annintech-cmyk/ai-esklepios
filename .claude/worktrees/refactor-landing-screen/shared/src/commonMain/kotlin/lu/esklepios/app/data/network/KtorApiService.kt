package lu.esklepios.app.data.network

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody

class KtorApiService(
    private val client: HttpClient,
    private val baseUrl: String
) : ApiService {

    override suspend fun login(email: String, password: String): Result<LoginResponse> =
        safeCall {
            client.post("$baseUrl/auth/login") {
                setBody(LoginRequest(email = email, password = password))
            }.body()
        }

    override suspend fun register(request: RegisterRequest): Result<RegisterResponse> =
        safeCall {
            client.post("$baseUrl/auth/register") {
                setBody(request)
            }.body()
        }

    override suspend fun forgotPassword(email: String): Result<ForgotPasswordResponse> =
        safeCall {
            client.post("$baseUrl/auth/forgot-password") {
                setBody(ForgotPasswordRequest(email = email))
            }.body()
        }

    override suspend fun refreshToken(refreshToken: String): Result<RefreshTokenResponse> =
        safeCall {
            client.post("$baseUrl/auth/refresh") {
                setBody(RefreshTokenRequest(refreshToken = refreshToken))
            }.body()
        }

    override suspend fun searchPractitioners(
        location: String,
        specialty: String,
        page: Int,
        limit: Int
    ): Result<List<PractitionerDto>> =
        safeCall {
            client.get("$baseUrl/practitioners") {
                parameter("location", location)
                parameter("specialty", specialty)
                parameter("page", page)
                parameter("limit", limit)
            }.body()
        }

    override suspend fun getPractitioner(id: String): Result<PractitionerDto> =
        safeCall {
            client.get("$baseUrl/practitioners/$id").body()
        }

    override suspend fun createAppointment(request: CreateAppointmentRequest): Result<AppointmentDto> =
        safeCall {
            client.post("$baseUrl/appointments") {
                setBody(request)
            }.body()
        }

    override suspend fun getAppointments(userId: String): Result<List<AppointmentDto>> =
        safeCall {
            client.get("$baseUrl/appointments") {
                parameter("user_id", userId)
            }.body()
        }

    override suspend fun modifyAppointment(
        id: String,
        request: ModifyAppointmentRequest
    ): Result<AppointmentDto> =
        safeCall {
            client.put("$baseUrl/appointments/$id") {
                setBody(request)
            }.body()
        }

    override suspend fun cancelAppointment(id: String): Result<Unit> =
        safeCall {
            client.delete("$baseUrl/appointments/$id")
            Unit
        }

    override suspend fun getProfile(): Result<UserDto> =
        safeCall {
            client.get("$baseUrl/profile").body()
        }

    override suspend fun updateProfile(request: UpdateProfileRequest): Result<UserDto> =
        safeCall {
            client.put("$baseUrl/profile") {
                setBody(request)
            }.body()
        }

    override suspend fun changeEmail(newEmail: String, password: String): Result<Unit> =
        safeCall {
            client.put("$baseUrl/profile/email") {
                setBody(ChangeEmailRequest(newEmail = newEmail, password = password))
            }
            Unit
        }

    override suspend fun changePassword(oldPassword: String, newPassword: String): Result<Unit> =
        safeCall {
            client.put("$baseUrl/profile/password") {
                setBody(ChangePasswordRequest(oldPassword = oldPassword, newPassword = newPassword))
            }
            Unit
        }
}

suspend fun <T> safeCall(block: suspend () -> T): Result<T> {
    return try {
        Result.success(block())
    } catch (e: io.ktor.client.plugins.ClientRequestException) {
        Result.failure(ApiException(e.response.status.value, e.message ?: "Client request error"))
    } catch (e: io.ktor.client.plugins.ServerResponseException) {
        Result.failure(ApiException(e.response.status.value, e.message ?: "Server error"))
    } catch (e: io.ktor.client.network.sockets.ConnectTimeoutException) {
        Result.failure(NetworkException("Connection timed out"))
    } catch (e: io.ktor.client.network.sockets.SocketTimeoutException) {
        Result.failure(NetworkException("Socket timed out"))
    } catch (e: Exception) {
        Result.failure(e)
    }
}

class ApiException(val statusCode: Int, message: String) : Exception(message)
class NetworkException(message: String) : Exception(message)
