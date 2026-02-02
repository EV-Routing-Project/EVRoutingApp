package com.quest.evrouting.phone.data.remote.api.backendServer.directions.dto.request

import com.mapbox.geojson.Point
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Location(
    @SerialName("lat") val lat: Double,
    @SerialName("lon") val lon: Double
){
    fun toPoint(): Point {
        return Point.fromLngLat(lon, lat)
    }
}