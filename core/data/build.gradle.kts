import org.gradle.api.JavaVersion
plugins {
    // 1. Khai báo đây là một module Android Library
    id("com.android.library")
    // 2. Sử dụng Kotlin cho Android
    id("org.jetbrains.kotlin.android")
    // 3. Thêm plugin KSP để Room hoạt động
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.quest.evrounting.data"
    // Giữ compileSdk = 36 đồng bộ với module :app
    compileSdk = 36

    defaultConfig {
        // Giữ minSdk = 28 đồng bộ với module :app
        minSdk = 28

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    // Các thư viện cần thiết cho một module Android
    implementation("androidx.core:core-ktx:1.13.1")

    // --- Khai báo thư viện Room ---
    val roomVersion = "2.6.1"
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion") // Hỗ trợ Coroutines và Flow
    ksp("androidx.room:room-compiler:$roomVersion") // Bộ xử lý annotation của Room
}

