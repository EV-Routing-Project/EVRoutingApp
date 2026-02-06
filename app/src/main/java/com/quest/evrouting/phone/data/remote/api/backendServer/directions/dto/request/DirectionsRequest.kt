package com.quest.evrouting.phone.data.remote.api.backendServer.directions.dto.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DirectionsRequest(
    @SerialName("start") val start: Location,
    @SerialName("end") val end: Location,
)