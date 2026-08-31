import com.android.build.api.dsl.ApplicationExtension

plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "org.routine_work.notepad"

    compileSdk {
        version = release(37) {
            minorApiLevel = 1
        }
    }
    buildToolsVersion = "36.0.0"

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        applicationId = "org.routine_work.notepad"
        minSdk = 24
        targetSdk = 37
        versionCode = 60
        versionName = "1.1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField("long", "BUILD_TIMESTAMP", "${System.currentTimeMillis()}L")
        }
        debug {
            buildConfigField("long", "BUILD_TIMESTAMP", "${System.currentTimeMillis()}L")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.preference)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}