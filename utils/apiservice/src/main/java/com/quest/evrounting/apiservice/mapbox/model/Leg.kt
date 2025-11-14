package com.quest.evrounting.apiservice.mapbox.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class Leg(
    @SerialName("via_waypoints") val viaWaypoints: List<JsonElement>,
    @SerialName("admins") val admins: List<JsonElement>,
    @SerialName("weight") val weight: Double,
    @SerialName("duration") val duration: Double,
    @SerialName("steps") val steps: List<JsonElement>,
    @SerialName("distance") val distance: Double,
    @SerialName("summary") val summary: String
)
