import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("org.jetbrains.kotlin.plugin.serialization")
    id("kotlin-parcelize")
}

android {
    namespace = "com.quest.evrouting.phone"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.quest.evrouting.phone"
        minSdk = 28
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

            // Đọc token từ local.properties
            val localProperties = Properties()
            val localPropertiesFile = rootProject.file("local.properties")
            if (localPropertiesFile.exists()) {
                localProperties.load(localPropertiesFile.inputStream())
            }
            val mapboxAccessToken = localProperties.getProperty("MAPBOX_ACCESS_TOKEN", "")

            // Cung cấp token cho Manifest thông qua manifestPlaceholders
            manifestPlaceholders["MAPBOX_ACCESS_TOKEN"] = mapboxAccessToken
            buildConfigField("String", "MAPBOX_ACCESS_TOKEN", "\"$mapboxAccessToken\"")
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.kotlinx.serialization)
    implementation(libs.okhttp)
    implementation(libs.kotlinx.serialization.json)

    // Test gọi API
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0") // Đảm bảo phiên bản khớp với okhttp

    implementation("com.mapbox.maps:android-ndk27:11.17.0")
    implementation("com.mapbox.extension:maps-compose-ndk27:11.17.0")

    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.9.4")

    // Chuyển trang
    implementation("androidx.navigation:navigation-compose:2.7.7")

    implementation("androidx.compose.material:material-icons-extended-android:1.6.8")


}
