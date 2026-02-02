package com.quest.evrouting.phone.data.remote.api.backendServer.staticc.dto.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class POI(
    @SerialName("id") val id: String,
    @SerialName("location_id") val locationId: String,
    @SerialName("location") val location: Location,
    @SerialName("information") val info: Map<String, String>,
    @SerialName("status") val status: String,
    @SerialName("connectors") val connectors: List<Connector>
)
