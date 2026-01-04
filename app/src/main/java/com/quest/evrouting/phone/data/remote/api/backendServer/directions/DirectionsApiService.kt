package com.quest.evrouting.phone.data.remote.api.backendServer.directions

import com.quest.evrouting.phone.data.remote.api.backendServer.directions.dto.request.DirectionsRequest
import com.quest.evrouting.phone.data.remote.api.backendServer.directions.dto.response.DirectionsResponseDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface DirectionsApiService {

    /**
     * Gửi yêu cầu tìm đường đến backend server.
     *
     * @param requestBody Đối tượng chứa thông tin về profile, tọa độ...
     * @return Dữ liệu phản hồi từ server, được phân tích thành DirectionsResponseDto.
     */
//    @POST("/api/directions") // <-- 1. Dùng @POST và endpoint của backend
//    suspend fun getDirections(
//        @Body requestBody: DirectionsRequest, // <-- 2. Gửi toàn bộ dữ liệu qua @Body
//    ): DirectionsResponseDto

    @GET("/v1/1103e13f-d88b-4f31-b688-de6f9062e2b5")
    suspend fun getDirections(): DirectionsResponseDto

}