package com.quest.evrounting.apiservice.mapbox.directions
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import com.quest.evrounting.apiservice.mapbox.model.Route
import com.quest.evrounting.apiservice.mapbox.model.Waypoint
@Serializable
data class DirectionsResponse(
    @SerialName("routes") val routes: List<Route>,
    @SerialName("waypoints") val waypoints: List<Waypoint>,
    @SerialName("code") val code: String,
    @SerialName("uuid") val uuid: String
)
