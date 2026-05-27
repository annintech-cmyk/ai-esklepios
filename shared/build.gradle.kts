import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.BOOLEAN
import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.INT
import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING
import java.util.Properties

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.sqldelight)
    alias(libs.plugins.buildkonfig)
}

val devProperties =
    Properties().apply {
        val file = rootProject.file("dev.properties")
        if (file.exists()) load(file.inputStream())
    }

val prodProperties =
    Properties().apply {
        val file = rootProject.file("prod.properties")
        if (file.exists()) load(file.inputStream())
    }

kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }

    listOf(iosArm64(), iosX64(), iosSimulatorArm64()).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "shared"
            isStatic = false
        }
    }

    sourceSets {
        commonMain.dependencies {
            // Ktor
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.client.auth)
            implementation(libs.ktor.client.logging)
            implementation(libs.ktor.serialization.kotlinx.json)

            // KotlinX
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlinx.datetime)

            // Koin
            implementation(libs.koin.core)

            // SQLDelight
            implementation(libs.sqldelight.runtime)
            implementation(libs.sqldelight.coroutines.extensions)

            // Multiplatform Settings
            implementation(libs.multiplatform.settings)
            implementation(libs.multiplatform.settings.no.arg)

            // Lifecycle ViewModel (KMM multiplatform artifact)
            implementation(libs.androidx.lifecycle.viewmodel)
        }

        androidMain.dependencies {
            implementation(libs.ktor.client.android)
            implementation(libs.sqldelight.android.driver)
            implementation(libs.kotlinx.coroutines.android)
        }

        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
            implementation(libs.sqldelight.native.driver)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.turbine)
        }
    }
}

android {
    namespace = "lu.esklepios.app.shared"
    compileSdk = 35

    defaultConfig {
        minSdk = 26
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

sqldelight {
    databases {
        create("ESklepiosDatabase") {
            packageName.set("lu.esklepios.app.db")
        }
    }
}

buildkonfig {
    packageName = "lu.esklepios.app"
    objectName = "BuildKonfig"

    defaultConfigs {
        buildConfigField(
            STRING,
            "BASE_API_URL",
            devProperties.getProperty("BASE_API_URL", "https://dev-api.esklepios.lu/v1"),
        )
        buildConfigField(
            INT,
            "API_TIMEOUT_SECONDS",
            devProperties.getProperty("API_TIMEOUT_SECONDS", "30"),
        )
        buildConfigField(
            BOOLEAN,
            "ENABLE_LOGGING",
            devProperties.getProperty("ENABLE_LOGGING", "true"),
        )
        buildConfigField(
            STRING,
            "MAPS_API_KEY",
            devProperties.getProperty("MAPS_API_KEY", "DEV_MAPS_KEY_HERE"),
        )
    }
}
