package com.quest.evrouting.phone.data.remote.api.backendServer.staticc.dto.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChargePointResponseDto(
    @SerialName("type") val type: String,
    @SerialName("properties") val properties: PropertiesDto,
    @SerialName("geometry") val geometry: GeometryDto
)