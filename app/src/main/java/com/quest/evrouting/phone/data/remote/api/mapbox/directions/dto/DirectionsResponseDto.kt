package com.quest.evrouting.phone.data.remote.api.mapbox.directions.dto


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Data class đại diện cho cấu trúc JSON trả về từ Directions API.
 * Chỉ định nghĩa những trường chúng ta cần.
 */
@Serializable
data class DirectionsResponseDto(
    @SerialName("routes") val routes: List<RouteDto>,
    @SerialName("code") val code: String
)