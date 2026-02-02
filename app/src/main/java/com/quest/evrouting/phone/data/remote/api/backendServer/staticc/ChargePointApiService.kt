package com.quest.evrouting.phone.data.remote.api.backendServer.staticc

import com.quest.evrouting.phone.data.remote.api.backendServer.staticc.dto.response.AllChargePointsApiResponse
import retrofit2.http.GET

interface ChargePointApiService {
    @GET("data/poi")
    suspend fun getAllChargePointInfo(): AllChargePointsApiResponse
}