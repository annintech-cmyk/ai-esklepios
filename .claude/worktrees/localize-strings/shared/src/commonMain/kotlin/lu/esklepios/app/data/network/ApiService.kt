package lu.esklepios.app.data.network

interface ApiService {
    suspend fun login(email: String, password: String): Result<LoginResponse>
    suspend fun register(request: RegisterRequest): Result<RegisterResponse>
    suspend fun forgotPassword(email: String): Result<ForgotPasswordResponse>
    suspend fun refreshToken(refreshToken: String): Result<RefreshTokenResponse>
    suspend fun searchPractitioners(
        location: String,
        specialty: String,
        page: Int,
        limit: Int
    ): Result<List<PractitionerDto>>
    suspend fun getPractitioner(id: String): Result<PractitionerDto>
    suspend fun createAppointment(request: CreateAppointmentRequest): Result<AppointmentDto>
    suspend fun getAppointments(userId: String): Result<List<AppointmentDto>>
    suspend fun modifyAppointment(id: String, request: ModifyAppointmentRequest): Result<AppointmentDto>
    suspend fun cancelAppointment(id: String): Result<Unit>
    suspend fun getProfile(): Result<UserDto>
    suspend fun updateProfile(request: UpdateProfileRequest): Result<UserDto>
    suspend fun changeEmail(newEmail: String, password: String): Result<Unit>
    suspend fun changePassword(oldPassword: String, newPassword: String): Result<Unit>
}
