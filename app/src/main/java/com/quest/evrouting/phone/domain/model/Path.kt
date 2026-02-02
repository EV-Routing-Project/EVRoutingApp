package com.quest.evrouting.phone.domain.model

import com.mapbox.geojson.Point

data class Path(
    val decodedPolyline: List<Point>,
    val length: Double,
    val time: Double
)
