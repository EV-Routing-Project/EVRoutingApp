package com.quest.evrouting.phone.data.remote.api.mapbox.geocoding.dto


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GeocodingResponseDto(
    @SerialName("features")
    val features: List<FeatureDto>
)