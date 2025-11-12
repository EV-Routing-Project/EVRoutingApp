package com.quest.evrounting.libservice.geometry.service

import com.quest.evrounting.libservice.geometry.domain.manager.GeohashManager
import com.quest.evrounting.libservice.geometry.domain.model.Geohash
import com.quest.evrounting.libservice.geometry.domain.model.Point
import com.quest.evrounting.libservice.geometry.external.di.ServiceLocator

object GeohashService {

    fun createGeohashFromLongValue(value: Long, significantBits: Int): Geohash {
        return Geohash(value, significantBits)
    }

    fun encode(point: Point, significantBits: Int): Geohash {
        return geohashManager.encode(point, significantBits)
    }

    private val geohashManager: GeohashManager = ServiceLocator.geohashManager

    fun getAdjacent(geohash: Geohash): List<Geohash> {
        return geohashManager.getAdjacent(geohash)
    }

    fun getGeohashGridForPoint(geohash: Geohash): List<Geohash> {
        return geohashManager.getAdjacent(geohash) + geohash
    }
}