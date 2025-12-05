package com.quest.evrouting.libservice.geometry.domain.port

import com.quest.evrouting.libservice.geometry.domain.model.Geohash
import com.quest.evrouting.libservice.geometry.domain.model.Point

interface GeohashPort {
    fun getAdjacent(geohash: Geohash): List<Geohash>
    fun encode(point: Point, significantBits: Int): Geohash
}