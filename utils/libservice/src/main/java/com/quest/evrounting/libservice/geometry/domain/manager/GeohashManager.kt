package com.quest.evrounting.libservice.geometry.domain.manager

import com.quest.evrounting.libservice.geometry.domain.model.Geohash
import com.quest.evrounting.libservice.geometry.domain.model.Point
import com.quest.evrounting.libservice.geometry.domain.port.GeohashPort

class GeohashManager(val adapter: GeohashPort){
    fun getAdjacent(geohash: Geohash): List<Geohash> {
        return adapter.getAdjacent(geohash)
    }

    fun encode(point: Point, significantBits: Int): Geohash {
        return adapter.encode(point, significantBits)
    }
}