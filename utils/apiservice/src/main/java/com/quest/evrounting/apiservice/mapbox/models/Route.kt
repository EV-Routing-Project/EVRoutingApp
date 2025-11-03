package com.quest.evrounting.apiservice.mapbox.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Route(
    @SerialName("weight_name") val weightName: String,
    @SerialName("weight") val weight: Double,
    @SerialName("duration") val duration: Double,
    @SerialName("distance") val distance: Double,
    @SerialName("legs") val legs: List<Leg>,
    @SerialName("geometry") val geometry: Geometry
)