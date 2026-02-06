package com.quest.evrouting.phone.data.remote.api.backendServer.directions.dto.response


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Route(
    @SerialName("segment") val segment: List<Segment>,
    @SerialName("nodes") val nodes: List<Node>,
    @SerialName("energy") val energy: Int,
    @SerialName("time") val time: Int,
    @SerialName("length") val length: Int
)
