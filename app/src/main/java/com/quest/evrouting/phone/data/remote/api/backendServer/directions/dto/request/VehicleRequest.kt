package com.quest.evrouting.phone.data.remote.api.backendServer.directions.dto.request

import kotlinx.serialization.Serializable

/**
 * Data class đại diện cho đối tượng "vehicle" lồng bên trong DirectionsRequest.
 * Chứa thông tin về tình trạng của xe điện.
 */
@Serializable
data class VehicleRequest(
    val powerKwh: Double,    // Tổng dung lượng pin của xe (đơn vị: kWh)
    val currentPower: Double // Mức pin hiện tại (đơn vị: kWh hoặc %)
)