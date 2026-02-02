package com.quest.evrouting.phone.data.remote.api.backendServer.staticc.dto.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class Connector(
    @SerialName("connector_type") val connectorType: String,
    @SerialName("power_type") val powerType: String,
    @SerialName("connector_format") val connectorFormat: String,
    @SerialName("electric_power") val maxElectricPower: Int
)
