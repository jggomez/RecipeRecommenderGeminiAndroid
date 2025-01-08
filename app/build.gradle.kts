plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    kotlin("plugin.serialization") version "1.9.22"
    id("com.google.gms.google-services")
}

android {
    namespace = "co.devhack.RecipeRecommenderGemini"
    compileSdk = 35

    defaultConfig {
        applicationId = "co.devhack.RecipeRecommenderGemini"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        debug {
            buildConfigField("String", "API_KEY", "\"API_KEY\"")
            buildConfigField("String", "TEXT_MODEL_NAME", "\"gemini-1.5-flash\"")
            buildConfigField("String", "IMAGE_MODEL_NAME", "\"gemini-1.5-flash\"")
            buildConfigField("String", "API_URL_GET_FOOD_MEXICAN", "\"https://themealdb.com/api/json/v1/1/filter.php?a=\"")
        }

        release {
            buildConfigField("String", "API_KEY", "API_KEY")
            buildConfigField("String", "TEXT_MODEL_NAME", "\"gemini-1.5-flash\"")
            buildConfigField("String", "IMAGE_MODEL_NAME", "\"gemini-1.5-flash\"")
            buildConfigField("String", "API_URL_GET_FOOD_MEXICAN", "\"https://themealdb.com/api/json/v1/1/filter.php?a=\"")
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
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.9"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    // MediaPipe
    implementation("com.google.mediapipe:tasks-genai:0.10.20")

    // Vertex AI for Firebase
    implementation("com.google.firebase:firebase-vertexai:16.0.2")

    // Exoplayer
    implementation("androidx.media3:media3-exoplayer:1.5.1")
    implementation("androidx.media3:media3-ui:1.5.1")

    // HTTP
    implementation("io.ktor:ktor-client-core:2.3.11")
    implementation("io.ktor:ktor-client-android:2.3.11")
    implementation("io.ktor:ktor-client-logging:2.3.11")
    implementation("io.ktor:ktor-client-content-negotiation:2.3.11")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.11")

    // Compose
    implementation(platform("androidx.compose:compose-bom:2024.12.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("io.coil-kt:coil-compose:2.6.0")

    // Core
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.3")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.3")
    implementation("androidx.activity:activity-compose:1.9.0")
    implementation("androidx.navigation:navigation-compose:2.7.7")

    // Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
    implementation("com.google.code.gson:gson:2.10.1")

    // Logs
    implementation("com.jakewharton.timber:timber:5.0.1")
    implementation("androidx.constraintlayout:constraintlayout:2.2.0")

    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.12.01"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")

    // Debug
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}
