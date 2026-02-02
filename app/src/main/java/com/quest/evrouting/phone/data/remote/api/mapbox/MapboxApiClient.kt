package com.quest.evrouting.phone.data.remote.api.mapbox

import retrofit2.converter.kotlinx.serialization.asConverterFactory
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

    val geocodingService: GeocodingApiService by lazy {
        retrofit.create(GeocodingApiService::class.java)
    }


    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    val instance: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }
}
