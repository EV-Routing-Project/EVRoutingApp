package com.quest.evrouting.phone.data.remote.api.backendServer.staticc.dto.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Location(
    @SerialName("lat") val lat: Double,
    @SerialName("lon") val lon: Double
)