// file: frontend/src/main/java/com/example/frontend/data/DirectionsRepository.kt
package com.example.frontend.repository

import com.mapbox.geojson.LineString

// Interface định nghĩa "hợp đồng": phải có khả năng tìm đường
interface DirectionsRepository {
    // Trả về một LineString (để vẽ lên bản đồ) hoặc null nếu có lỗi
    suspend fun getDirectionsRoute(origin: com.mapbox.geojson.Point, destination: com.mapbox.geojson.Point): LineString?
}
    