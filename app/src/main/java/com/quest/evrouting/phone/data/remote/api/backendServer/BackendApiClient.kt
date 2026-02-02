package com.quest.evrouting.phone.data.remote.api.backendServer

import com.quest.evrouting.phone.data.remote.api.backendServer.directions.DirectionsApiService
import com.quest.evrouting.phone.data.remote.api.backendServer.staticc.ChargePointApiService
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

object BackendApiClient {
    private const val BASE_URL = "http://10.0.2.2:8080/"

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

    val chargePointsService: ChargePointApiService by lazy {
        retrofit.create(ChargePointApiService::class.java)
    }
}