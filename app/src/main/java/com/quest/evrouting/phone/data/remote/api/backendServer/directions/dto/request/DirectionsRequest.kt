package com.quest.evrouting.phone.data.remote.api.backendServer.directions.dto.request

import kotlinx.serialization.Serializable

/**
 * Data class đại diện cho JSON body được gửi đến server khi yêu cầu tìm lộ trình.
 * Annotation @Serializable là bắt buộc để kotlinx.serialization có thể chuyển đổi object này thành JSON.
 */
@Serializable
data class DirectionsRequest(
    val originLon: Double,
    val originLat: Double,
    val destinationLon: Double,
    val destinationLat: Double,
    val vehicle: VehicleRequest
    // val vehicleType: String = "electric_car",
    // val avoidTolls: Boolean = false
)