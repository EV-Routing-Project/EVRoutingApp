package com.quest.evrouting.phone.data.remote.api.mapbox.geocoding.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FeatureDto(
    @SerialName("center") val center: List<Double> // Mapbox trả về [longitude, latitude]
)