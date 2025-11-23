plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "org.routine_work.notepad"
    compileSdk = 36

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        applicationId = "org.routine_work.notepad"
        minSdk = 24
        targetSdk = 35
        versionCode = 49
        versionName = "1.0.20"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
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

//    implementation(libs.appcompat)
//    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}