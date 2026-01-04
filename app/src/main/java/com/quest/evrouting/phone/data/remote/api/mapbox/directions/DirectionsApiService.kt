package com.quest.evrouting.phone.data.remote.api.mapbox.directions

import com.quest.evrouting.phone.data.remote.api.mapbox.directions.dto.DirectionsResponseDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface DirectionsApiService {
    @GET("/directions/v5/mapbox/{profile}/{coordinates}")
    suspend fun getDirections(
        @Path("profile") profile: String,
        @Path("coordinates") coordinates: String,
        @Query("access_token") accessToken: String,
        @Query("geometries") geometries: String = "polyline6",
        @Query("overview") overview: String = "full"
    ): DirectionsResponseDto
}
    