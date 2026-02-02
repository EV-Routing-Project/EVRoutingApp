package com.quest.evrouting.phone.data.remote.api.backendServer.directions

import com.quest.evrouting.phone.data.remote.api.backendServer.directions.dto.request.DirectionsRequest
import com.quest.evrouting.phone.data.remote.api.backendServer.directions.dto.response.DirectionsResponseDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface DirectionsApiService {
    @POST("route/base/directions")
    suspend fun getDirections(@Body requestBody: DirectionsRequest): DirectionsResponseDto
}