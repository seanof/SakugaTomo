plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id ("org.jetbrains.kotlin.plugin.serialization") version "1.9.0"
    id ("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.seanof.sakugatomo"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.seanof.sakugatomo"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.hilt.android)
    implementation(libs.androidx.media3.ui)
    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidx.navigation.compose)
    testImplementation(libs.core.testing)
    ksp(libs.hilt.android.compiler)
    implementation(libs.androidx.material3)
    implementation(libs.coil.compose)
    implementation(libs.ktor.client.android)
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.serialization)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.client.logging)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    testImplementation (libs.mockito.core)
    testImplementation (libs.inline)
    testImplementation (libs.mockito.android)
    testImplementation(libs.kotlinx.coroutines.test)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}