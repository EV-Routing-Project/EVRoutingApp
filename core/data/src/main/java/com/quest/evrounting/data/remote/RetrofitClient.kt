package com.quest.evrounting.data.remote

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Singleton object để tạo và cấu hình Retrofit.
 */
object RetrofitClient {

    private const val BASE_URL = "https://api.openchargemap.io/v3/"
    private const val API_KEY = "YOUR_API_KEY"

    // Tạo một Interceptor để tự động thêm API Key Header
    private val apiKeyInterceptor = Interceptor { chain ->
        val originalRequest = chain.request()
        val newRequest = originalRequest.newBuilder()
            .header("X-API-Key", API_KEY)
            .build()
        chain.proceed(newRequest)
    }

    // Tạo client với CẢ HAI interceptor
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(apiKeyInterceptor)
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.HEADERS
        })
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient) // Sử dụng client đã được cấu hình
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService: OcmApiService by lazy {
        retrofit.create(OcmApiService::class.java)
    }
}