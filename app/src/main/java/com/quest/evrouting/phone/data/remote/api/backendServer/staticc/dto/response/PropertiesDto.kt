package com.quest.evrouting.phone.data.remote.api.backendServer.staticc.dto.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PropertiesDto(
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String,
    @SerialName("addressName") val address: String,
    @SerialName("town") val town: String,
    @SerialName("connections") val connections: List<ConnectionDto>
)