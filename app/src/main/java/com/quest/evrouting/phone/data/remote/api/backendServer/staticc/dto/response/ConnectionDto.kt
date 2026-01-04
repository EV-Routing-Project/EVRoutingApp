package com.quest.evrouting.phone.data.remote.api.backendServer.staticc.dto.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ConnectionDto(
    @SerialName("connectionTypeName") val connectionTypeName: String,
    @SerialName("currentTypeName") val currentTypeName: String,
    @SerialName("powerKw") val powerKw: Double,
    @SerialName("quantity") val quantity: Int
)