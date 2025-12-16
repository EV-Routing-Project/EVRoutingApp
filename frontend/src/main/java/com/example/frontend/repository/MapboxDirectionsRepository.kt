//// file: frontend/src/main/java/com/example/frontend/data/MapboxDirectionsRepository.kt
//package com.example.frontend.repository
//
//import com.mapbox.geojson.LineString
//import com.mapbox.geojson.Point
//import com.quest.evrounting.apiservice.mapbox.MapboxApiClient
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.withContext
//
//class MapboxDirectionsRepository : DirectionsRepository {
//
//    private val directionsService = MapboxApiClient.directionsService
//
//    override suspend fun getDirectionsRoute(origin: Point, destination: Point): LineString? {
//        // Sử dụng withContext để chuyển sang luồng I/O khi gọi mạng
//        return withContext(Dispatchers.IO) {
//            try {
//                val response = directionsService.getDirections(
//                    profile = "driving-traffic", // hoặc "driving", "walking", "cycling"
//                    coordinates = "${origin.longitude()},${origin.latitude()};${destination.longitude()},${destination.latitude()}"
//                    // Access token sẽ được thêm tự động bởi Interceptor trong module apiservice
//                )
//
//                if (response.isSuccessful && response.body() != null) {
//                    val route = response.body()!!.routes.firstOrNull()
//                    // Giải mã chuỗi geometry để vẽ lên bản đồ
//                    route?.geometry?.let { LineString.fromPolyline(it, 6) }
//                } else {
//                    null // Trả về null nếu gọi API thất bại
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//                null // Trả về null nếu có lỗi mạng hoặc lỗi khác
//            }
//        }
//    }
//}
