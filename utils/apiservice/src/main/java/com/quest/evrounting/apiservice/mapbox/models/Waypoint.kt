package com.quest.evrounting.apiservice.mapbox.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Waypoint(
    @SerialName("distance") val distance: Double,
    @SerialName("name") val name: String,
    @SerialName("location") val location: List<Double>
)
