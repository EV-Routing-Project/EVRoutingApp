package com.quest.evrouting.phone.data.remote.api.backendServer.directions.dto.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Path(
    @SerialName("encoded_polyline") val polyline: String,
    @SerialName("length") val length: Double,
    @SerialName("time") val time: Double
)