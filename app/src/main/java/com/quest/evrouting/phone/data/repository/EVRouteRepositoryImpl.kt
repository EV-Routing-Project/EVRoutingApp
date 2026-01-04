package com.quest.evrouting.phone.data.repository

import android.util.Log
import com.quest.evrouting.phone.data.remote.api.backendServer.BackendApiClient
import com.quest.evrouting.phone.data.remote.api.backendServer.directions.dto.request.DirectionsRequest
import com.quest.evrouting.phone.data.remote.api.backendServer.directions.dto.request.VehicleRequest
import com.quest.evrouting.phone.data.remote.api.backendServer.directions.toRoute
import com.quest.evrouting.phone.domain.model.Route
import com.quest.evrouting.phone.domain.repository.EVRouteRepository

/**
 * Lớp triển khai cụ thể cho EVRouteRepository.
 * Nhiệm vụ của nó là gọi đến Backend API để lấy dữ liệu lộ trình khi người dùng tìm kiếm.
 */
class EVRouteRepositoryImpl : EVRouteRepository {

    private val directionsApiService = BackendApiClient.directionsService

    // --- BƯỚC 1: THAY ĐỔI CHỮ KÝ HÀM ---
    // Nhận 4 tham số Double thay vì 2 chuỗi String.
    override suspend fun getRoute(
        originLon: Double,
        originLat: Double,
        destinationLon: Double,
        destinationLat: Double,
        powerKwh: Double,
        currentPower: Double
    ): Route? {
        val vehicleInfo = VehicleRequest(powerKwh = powerKwh, currentPower =  currentPower)

        val requestBody = DirectionsRequest(
            originLon = originLon,
            originLat = originLat,
            destinationLon = destinationLon,
            destinationLat = destinationLat,
            vehicle = vehicleInfo
        )

        Log.d("DEBUG_ROUTE", "[Repository] Chuẩn bị gọi API Backend với Request Body: $requestBody")

        // Gọi hàm getDirections từ service, Retrofit sẽ tự động chuyển đổi requestBody thành JSON.

//        val responseDto = directionsApiService.getDirections(requestBody)


        val responseDto = directionsApiService.getDirections()




        // Ánh xạ kết quả từ DTO sang Domain Model (không thay đổi)
        return responseDto.routes.firstOrNull()?.toRoute()
    }
}
