package com.quest.evrouting.phone.data.remote.api.mapbox.geocoding

import com.quest.evrouting.phone.data.remote.api.mapbox.geocoding.dto.GeocodingResponseDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GeocodingApiService {
    @GET("/geocoding/v5/mapbox.places/{searchText}.json")
    suspend fun getCoordinates(
        @Path("searchText") searchText: String,
        @Query("access_token") accessToken: String,
        @Query("limit") limit: Int = 1 // Chỉ cần kết quả chính xác nhất
    ): GeocodingResponseDto
}
