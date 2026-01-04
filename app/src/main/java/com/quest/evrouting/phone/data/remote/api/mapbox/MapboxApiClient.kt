package com.quest.evrouting.phone.data.remote.api.mapbox

import retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.quest.evrouting.phone.data.remote.api.mapbox.directions.DirectionsApiService
import com.quest.evrouting.phone.data.remote.api.mapbox.geocoding.GeocodingApiService
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit

object MapboxApiClient {
    private const val  BASE_URL = "https://api.mapbox.com"
    private val json = Json {
        ignoreUnknownKeys = true
    }
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }
    val directionsService: DirectionsApiService by lazy {
        retrofit.create(DirectionsApiService::class.java)
    }

    val geocodingService: GeocodingApiService by lazy {
        retrofit.create(GeocodingApiService::class.java)
    }


    // >>> BẮT ĐẦU SỬA Ở ĐÂY <<<

    // 1. Tạo một Logging Interceptor
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY // BODY sẽ hiển thị tất cả chi tiết
    }

    // 2. Tạo OkHttpClient và thêm interceptor vào
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor) // Thêm interceptor vào client
        .build()

    // 3. Xây dựng Retrofit với OkHttpClient đã được tùy chỉnh
    val instance: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient) // <-- Sử dụng OkHttpClient tùy chỉnh ở đây
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }
    // >>> KẾT THÚC PHẦN SỬA <<<

}
