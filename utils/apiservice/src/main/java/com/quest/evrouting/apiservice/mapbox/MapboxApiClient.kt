package com.quest.evrouting.apiservice.mapbox

import retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.quest.evrouting.apiservice.mapbox.directions.DirectionsApiService
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
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
}