package com.quest.evrouting.phone.data.remote.api.backendServer.directions.dto.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DirectionsRequest(
    @SerialName("current") val current: Location,
    @SerialName("target") val target: Location,
    @SerialName("top_speed") val topSpeed: Int = 120,
    @SerialName("alternates") val alternates: Int = 0
)