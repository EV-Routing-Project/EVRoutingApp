import org.gradle.kotlin.dsl.accessors.runtime.maybeRegister

plugins {
    alias(libs.plugins.jetbrains.kotlin.jvm)}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

kotlin {
    compilerOptions {
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11
    }
}

dependencies {
    implementation(project(":utils:apiservice"))
    // --- THƯ VIỆN EXPOSED ---
    implementation(libs.exposed.core)
    implementation(libs.exposed.dao)
    implementation(libs.exposed.jdbc)
    implementation(libs.exposed.java.time)

    // --- CÁC THƯ VIỆN HỖ TRỢ CHO SERVER ---
    // JDBC Driver cho MySQL
    implementation(libs.mysql.connector.j)

    // Coroutines
    implementation(libs.kotlinx.coroutines.core)

    // Logging
    implementation(libs.slf4j.simple)

    // Thư viện cho Retrofit và OkHttp
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.kotlinx.serialization)
    implementation(libs.okhttp)
}
