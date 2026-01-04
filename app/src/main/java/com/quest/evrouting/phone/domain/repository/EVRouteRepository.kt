package com.quest.evrouting.phone.domain.repository

import com.quest.evrouting.phone.domain.model.Route/**
 * Interface định nghĩa các phương thức để lấy dữ liệu lộ trình từ một nguồn nào đó (thường là backend).
 * Việc sử dụng interface giúp tách biệt logic của ViewModel khỏi việc triển khai chi tiết cách lấy dữ liệu.
 */
interface EVRouteRepository {
    suspend fun getRoute(
        originLon: Double,
        originLat: Double,
        destinationLon: Double,
        destinationLat: Double,
        powerKwh: Double,
        currentPower: Double
    ): Route?
}