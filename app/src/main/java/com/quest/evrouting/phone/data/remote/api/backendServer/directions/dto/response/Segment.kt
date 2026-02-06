package com.quest.evrouting.phone.data.remote.api.backendServer.directions.dto.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Segment(
    @SerialName("path") val path: Path,
    @SerialName("energy") val energy: Int
)
