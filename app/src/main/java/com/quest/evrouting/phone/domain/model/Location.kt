package com.quest.evrouting.phone.domain.model

import com.mapbox.geojson.Point

data class Location(
    val latitude: Double,
    val longitude: Double
) {
    fun toPoint(): Point {
        return Point.fromLngLat(longitude, latitude)
    }
}


