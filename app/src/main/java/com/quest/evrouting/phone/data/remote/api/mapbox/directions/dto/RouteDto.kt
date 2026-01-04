package com.quest.evrouting.phone.data.remote.api.mapbox.directions.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RouteDto(
    @SerialName("geometry") val geometry: String,
    @SerialName("distance") val distance: Double,
    @SerialName("duration") val duration: Double
)