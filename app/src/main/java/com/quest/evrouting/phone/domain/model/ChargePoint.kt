package com.quest.evrouting.phone.domain.model

import com.mapbox.geojson.Point

data class ChargePoint(
    val id: Int,
    val name: String,
    val address: String,
    val town: String,
    val totalQuantity: Int,
    val point: Point,
    val connections: List<Connection>
)
