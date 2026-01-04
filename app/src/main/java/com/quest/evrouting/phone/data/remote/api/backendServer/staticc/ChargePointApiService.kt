package com.quest.evrouting.phone.data.remote.api.backendServer.staticc

import com.quest.evrouting.phone.data.remote.api.backendServer.staticc.dto.response.ChargePointResponseDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ChargePointApiService {
//    @GET("/api/pois")
    @GET("v1/543748aa-acc6-4d7a-b084-864e515bc342")
    suspend fun getAllChargePointInfo(): List<ChargePointResponseDto>
}                                                                                           