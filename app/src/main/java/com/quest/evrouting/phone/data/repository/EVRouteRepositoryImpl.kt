package com.quest.evrouting.phone.data.repository

import android.util.Log
import com.quest.evrouting.phone.data.remote.api.backendServer.BackendApiClient
import com.quest.evrouting.phone.data.remote.api.backendServer.directions.dto.request.DirectionsRequest
import com.quest.evrouting.phone.data.remote.api.backendServer.directions.toLocationDto
import com.quest.evrouting.phone.data.remote.api.backendServer.directions.toPath
import com.quest.evrouting.phone.domain.model.Location
import com.quest.evrouting.phone.domain.model.Path
import com.quest.evrouting.phone.domain.repository.EVRouteRepository

class EVRouteRepositoryImpl : EVRouteRepository {

    private val directionsApiService = BackendApiClient.directionsService

    override suspend fun getRoute(
        current: Location,
        target: Location
    ): Path? {

        val requestBody = DirectionsRequest(
            current = current.toLocationDto(),
            target = target.toLocationDto()
        )

        Log.d("DEBUG_ROUTE", "[Repository] Chuẩn bị gọi API Backend với Request Body: $requestBody")

        return try {
            val responseDto = directionsApiService.getDirections(requestBody)
            Log.d("DEBUG_ROUTE", "[Repository] Nhận được phản hồi từ API: $responseDto")

            val path = responseDto.paths.firstOrNull()?.toPath()
            if (path == null) {
                Log.w("DEBUG_ROUTE", "[Repository] Phản hồi không chứa lộ trình nào.")
            }
            path
        } catch (e: Exception) {
            Log.e("DEBUG_ROUTE", "[Repository] Lỗi khi gọi API hoặc xử lý dữ liệu: ${e.message}", e)
            null
        }
    }
}
