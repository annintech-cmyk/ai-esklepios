package lu.esklepios.app.domain.model

data class User(
    val id: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phone: String,
    val gender: String,
    val dateOfBirth: String,
    val cnsNumber: String,
    val profileType: ProfileType,
    val language: String,
) {
    val fullName: String get() = "$firstName $lastName"
    val initials: String get() = "${firstName.firstOrNull() ?: ""}${lastName.firstOrNull() ?: ""}".uppercase()
}

enum class ProfileType {
    PATIENT,
    PRACTITIONER,
    ;

    companion object {
        fun fromString(value: String): ProfileType =
            entries.find {
                it.name.equals(value, ignoreCase = true)
            } ?: PATIENT
    }
}
