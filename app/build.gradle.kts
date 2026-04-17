plugins {
    id("com.android.application")
    kotlin("android")
}

android {

    testOptions {
        unitTests.isReturnDefaultValues = true
    }


    namespace = "com.reconmaps.app"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.reconmaps.app"
        minSdk = 24
        targetSdk = 34

        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

    // 🔥 FIXES YOUR JVM ERROR
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("org.maplibre.gl:android-sdk:13.0.1")
}