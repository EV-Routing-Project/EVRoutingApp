package com.quest.evrouting.phone.data.remote.api.backendServer.directions.dto.response


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * DTO đại diện cho một tọa độ của trạm sạc được trả về bên trong một lộ trình (Route).
 * Cấu trúc JSON: { "lon": 10.123, "lat": 20.456 }
 */
@Serializable
data class ChargePointDto(
    @SerialName("id") val id: Int
)