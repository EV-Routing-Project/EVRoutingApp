package com.quest.evrounting.libservice.geometry.domain.port

import com.quest.evrounting.libservice.geometry.domain.model.Geohash
import com.quest.evrounting.libservice.geometry.domain.model.Point
import com.quest.evrounting.libservice.geometry.utils.GeometryUnit

interface GeohashPort {
    fun getAdjacent(geohash: Geohash): List<Geohash>
    fun encode(point: Point, significantBits: Int): Geohash
}