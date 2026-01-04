package com.quest.evrouting.phone.domain.repository

import com.mapbox.geojson.Point
import com.quest.evrouting.phone.domain.model.Route // << IMPORT LỚP MỚI

/**
 * Interface (hợp đồng) cho một repository có chức năng tìm đường.
 */
interface DirectionsRepository {
    /**
     * Tìm lộ trình đi qua một danh sách các điểm.
     * @return Một đối tượng Route hoặc null nếu thất bại.
     */
    suspend fun getDirections(points: List<Point>, profile: String): Route?
}
