# kotlinx.serialization — keep all @Serializable classes
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}
-keep,includedescriptorclasses class lu.esklepios.app.**$$serializer { *; }
-keepclassmembers class lu.esklepios.app.** {
    *** Companion;
}
-keepclasseswithmembers class lu.esklepios.app.** {
    kotlinx.serialization.KSerializer serializer(...);
}
-keep @kotlinx.serialization.Serializable class lu.esklepios.app.** { *; }

# Koin — dependency injection
-keep class org.koin.** { *; }
-keep class org.koin.core.** { *; }
-keepnames class org.koin.** { *; }

# SQLDelight — generated database code
-keep class lu.esklepios.app.db.** { *; }
-keep class lu.esklepios.app.db.*Queries { *; }

# Ktor — HTTP client
-keep class io.ktor.** { *; }
-keep class io.ktor.client.** { *; }
-keep class io.ktor.serialization.** { *; }
-dontwarn io.ktor.**

# Kotlin Coroutines
-keep class kotlinx.coroutines.** { *; }
-dontwarn kotlinx.coroutines.**

# Kotlin reflect
-keep class kotlin.Metadata { *; }
-dontwarn kotlin.**
-dontwarn kotlinx.**

# Keep KMM shared framework entry points
-keep class lu.esklepios.app.** { *; }

# AndroidX Security Crypto (EncryptedSharedPreferences)
-keep class androidx.security.crypto.** { *; }

# OkHttp (used by Ktor OkHttp engine)
-dontwarn okhttp3.**
-dontwarn okio.**
