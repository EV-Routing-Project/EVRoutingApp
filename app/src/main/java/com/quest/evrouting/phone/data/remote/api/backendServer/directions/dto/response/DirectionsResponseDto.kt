package com.quest.evrouting.phone.data.remote.api.backendServer.directions.dto.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Data class đại diện cho cấu trúc JSON trả về từ Directions API.
 * Chỉ định nghĩa những trường chúng ta cần.
 */
@Serializable
data class DirectionsResponseDto(
    @SerialName("paths") val paths: List<Path>
)