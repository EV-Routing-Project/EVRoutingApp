package com.quest.evrounting.libservice.geometry.service

import com.quest.evrounting.libservice.geometry.domain.manager.GeohashManager
import com.quest.evrounting.libservice.geometry.domain.model.Geohash
import com.quest.evrounting.libservice.geometry.domain.model.Point
import com.quest.evrounting.libservice.geometry.infrastructure.di.ServiceLocator
import com.quest.evrounting.libservice.geometry.utils.GeometryConstants

object GeohashService {
    private val geohashManager: GeohashManager = ServiceLocator.geohashManager

    fun createGeohashFromLongValue(value: Long, significantBits: Int): Geohash {
        return geohashManager.createGeohashFromLongValue(value, significantBits)
    }

    fun encode(point: Point, significantBits: Int): Geohash {
        return geohashManager.encode(point, significantBits)
    }

    fun getGeohashGridForPoint(geohash: Geohash): List<Geohash> {
        return geohashManager.getGeohashGridForPoint(geohash)
    }

    fun getLonSize(bits: Int, latitude: Double): Double{
        return geohashManager.getLonSize(bits, latitude)
    }

    fun getLatSize(bits: Int): Double {
        return geohashManager.getLatSize(bits)
    }
}