package com.quest.evrouting.apiservice.ocm

import com.quest.evrouting.apiservice.ocm.pois.PoisApiService
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

/**
 * Singleton object để tạo và cấu hình Retrofit.
 */
object OcmApiClient {
    private const val BASE_URL = "https://api.openchargemap.io/v3/"

    private val json = Json {
        ignoreUnknownKeys = true
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    val poisServices: PoisApiService by lazy {
        retrofit.create(PoisApiService::class.java)
    }
}