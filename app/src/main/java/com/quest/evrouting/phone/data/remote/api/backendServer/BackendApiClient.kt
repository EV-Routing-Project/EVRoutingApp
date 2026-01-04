package com.quest.evrouting.phone.data.remote.api.backendServer

import com.quest.evrouting.phone.data.remote.api.backendServer.directions.DirectionsApiService
import com.quest.evrouting.phone.data.remote.api.backendServer.staticc.ChargePointApiService
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

object BackendApiClient {

    // Url vd: http://10.0.2.2:8080/api/directions
//    private const val BASE_URL = "https://your-backend-url/" // <<< THAY ĐỔI URL NÀY
    private const val BASE_URL = "https://mocki.io/"

    // 2. Cấu hình Json giống như MapboxApiClient
    private val json = Json {
        ignoreUnknownKeys = true // Bỏ qua các trường không xác định trong JSON response
    }

    // 5. Tạo Retrofit instance cho backend
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    val directionsService: DirectionsApiService by lazy {
        retrofit.create(DirectionsApiService::class.java)
    }

    val chargePointsService: ChargePointApiService by lazy {
        retrofit.create(ChargePointApiService::class.java)
    }
}