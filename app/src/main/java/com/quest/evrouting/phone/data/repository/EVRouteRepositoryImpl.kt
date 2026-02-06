package com.quest.evrouting.phone.data.repository

import android.content.Context
import android.util.Log
import com.quest.evrouting.phone.data.remote.api.backendServer.BackendApiClient
import com.quest.evrouting.phone.data.remote.api.backendServer.directions.dto.request.DirectionsRequest
import com.quest.evrouting.phone.data.remote.api.backendServer.directions.toLocationDto
import com.quest.evrouting.phone.data.remote.api.backendServer.directions.toPath
import com.quest.evrouting.phone.domain.model.Location
import com.quest.evrouting.phone.domain.model.Path
import com.quest.evrouting.phone.domain.repository.EVRouteRepository

class EVRouteRepositoryImpl(context: Context) : EVRouteRepository {

    private val directionsApiService = BackendApiClient.getDirectionsService(context)

    override suspend fun getRoute(
        start: Location,
        end: Location
    ): Pair<Path?, List<String>> {

        val requestBody = DirectionsRequest(
            start = start.toLocationDto(),
            end = end.toLocationDto()
        )

        Log.d("DEBUG_ROUTE", "[Repository] Chuẩn bị gọi service với Request Body: $requestBody")

        return try {
            val responseDto = directionsApiService.getDirections(requestBody)
            Log.d("DEBUG_ROUTE", "[Repository] Nhận được phản hồi từ Mock Service: $responseDto")


            val pathDtos = responseDto.route.segment.map { it.path }
            val path = if (pathDtos.isNotEmpty()) {
                val combinedDecodedPolyline = pathDtos.flatMap { it.toPath().decodedPolyline }
                val totalDistance = pathDtos.sumOf { it.toPath().length }
                val totalDuration = pathDtos.sumOf { it.toPath().time }
                Path(
                    decodedPolyline = combinedDecodedPolyline,
                    length = totalDistance,
                    time = totalDuration
                )
            } else {
                null
            }
            val recommendedNodeIds = responseDto.route.nodes.map { it.id }
            Log.d("DEBUG_ROUTE", "[Repository] Trạm sạc đề xuất: $recommendedNodeIds")

            Pair(path, recommendedNodeIds)

        } catch (e: Exception) {
            Log.e("DEBUG_ROUTE", "[Repository] Lỗi khi gọi service hoặc xử lý dữ liệu: ${e.message}", e)
            Pair(null, emptyList())
        }
    }
}



//class EVRouteRepositoryImpl : EVRouteRepository {
//
//    private val directionsApiService = BackendApiClient.directionsService
//
//    override suspend fun getRoute(
//        current: Location,
//        target: Location
//    ): Path? {
//
//        val requestBody = DirectionsRequest(
//            current = current.toLocationDto(),
//            target = target.toLocationDto()
//        )
//
//        Log.d("DEBUG_ROUTE", "[Repository] Chuẩn bị gọi API Backend với Request Body: $requestBody")
//
//        return try {
//            val responseDto = directionsApiService.getDirections(requestBody)
//            Log.d("DEBUG_ROUTE", "[Repository] Nhận được phản hồi từ API: $responseDto")
//
//            val path = responseDto.paths.firstOrNull()?.toPath()
//            if (path == null) {
//                Log.w("DEBUG_ROUTE", "[Repository] Phản hồi không chứa lộ trình nào.")
//            }
//            path
//        } catch (e: Exception) {
//            Log.e("DEBUG_ROUTE", "[Repository] Lỗi khi gọi API hoặc xử lý dữ liệu: ${e.message}", e)
//            null
//        }
//    }
//}