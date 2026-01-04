package com.quest.evrouting.phone.data.repository

import android.util.Log
import com.mapbox.geojson.Point
import com.quest.evrouting.phone.data.remote.api.backendServer.BackendApiClient
import com.quest.evrouting.phone.data.remote.api.backendServer.directions.dto.request.DirectionsRequest
import com.quest.evrouting.phone.data.remote.api.backendServer.directions.toRoute
import com.quest.evrouting.phone.domain.model.Route
import com.quest.evrouting.phone.domain.repository.DirectionsRepository
import com.quest.evrouting.phone.util.Constants
import java.io.IOException


// Logic vẽ đường đi
//class DirectionsRepositoryImpl : DirectionsRepository {
//
//    // Sử dụng service từ BackendApiClient để gọi server của bạn
//    private val backendApiService = BackendApiClient.directionsService
//
//    // Hàm getDirections này sẽ lấy lộ trình giữa điểm ĐẦU TIÊN và CUỐI CÙNG trong danh sách
//    override suspend fun getDirections(points: List<Point>, profile: String): Route? {
//        if (points.size < 2) {
//            Log.w("DirectionsRepoImpl", "Cần ít nhất 2 điểm để tìm đường.")
//            return null
//        }
//
//        val originPoint = points.first()
//        val destinationPoint = points.last()
//
//        // Tạo đối tượng request body với 4 thuộc tính Double
//        val requestBody = DirectionsRequest(
//            originLon = originPoint.longitude(),
//            originLat = originPoint.latitude(),
//            destinationLon = destinationPoint.longitude(),
//            destinationLat = destinationPoint.latitude()
//        )
//
//        Log.d("DirectionsRepoImpl", "Đang gọi Backend Server với request: $requestBody")
//
//        try {
//            // SỬA LỖI: Truyền thẳng requestBody vào hàm, Retrofit sẽ tự xử lý @Body
//            val responseDto = backendApiService.getDirections(requestBody)
//
//            if (responseDto.routes.isEmpty()) {
//                throw Exception("Backend API không trả về lộ trình nào.")
//            }
//
//            // Chuyển đổi DTO sang Domain Model
//            return responseDto.routes.first().toRoute()
//
//        } catch (e: IOException) {
//            Log.e("DirectionsRepoImpl", "Lỗi mạng khi gọi Backend Server", e)
//            throw e // Ném lại lỗi để ViewModel xử lý
//        } catch (e: Exception) {
//            Log.e("DirectionsRepoImpl", "Lỗi không xác định khi gọi Backend Server", e)
//            throw e // Ném lại lỗi để ViewModel xử lý
//        }
//    }
//}
