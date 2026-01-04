package com.quest.evrouting.phone.data.remote.api.backendServer.staticc.dto.response

import com.mapbox.geojson.Point
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GeometryDto(
    // Tọa độ trong GeoJSON luôn là [kinh độ, vĩ độ]
    @SerialName("coordinates") val coordinates: List<Double>,
    @SerialName("type") val type: String
) {
    /**
     * Hàm tiện ích để chuyển đổi từ DTO sang đối tượng `Point` của Mapbox.
     */
    fun toMapboxPoint(): Point {
        if (coordinates.size < 2) {
            throw IllegalArgumentException("Tọa độ không hợp lệ, cần ít nhất kinh độ và vĩ độ.")
        }
        return Point.fromLngLat(coordinates[0], coordinates[1])
    }
}