package com.quest.evrouting.phone.data.remote.api.backendServer.directions.dto.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Node(
    @SerialName("id") val id: String,
    @SerialName("groupId") val groupId: String,
    @SerialName("time") val time: Int,
    @SerialName("preBatteryPercent") val preBatteryPercent: Double,
    @SerialName("batteryPercent") val batteryPercent: Double
)
