package com.quest.evrounting.libservice.geometry.service

import com.quest.evrounting.libservice.geometry.domain.manager.GeohashManager
import com.quest.evrounting.libservice.geometry.domain.model.Geohash
import com.quest.evrounting.libservice.geometry.domain.model.Point
import com.quest.evrounting.libservice.geometry.infrastructure.di.ServiceLocator
import com.quest.evrounting.libservice.geometry.utils.GeometryConstants

object GeohashService {
    private val geohashManager: GeohashManager = ServiceLocator.geohashManager

//    fun createGeohashFromLongValue(value: Long, significantBits: Int): Geohash {
//        return geohashManager.createGeohashFromLongValue(value, significantBits)
//    }

    fun encode(point: Point, significantBits: Int): Geohash {
        return geohashManager.encode(point, significantBits)
    }

//    fun encodeToLong(lon: Double, lat: Double, significantBits: Int): Long {
//        return geohashManager.encodeToLong(lon, lat, significantBits)
//    }

    fun getGeohashGridForPoint(point: Point, significantBits: Int): List<Geohash> {
        return geohashManager.getGeohashGridForPoint(point, significantBits)
    }

    fun getLonSize(bits: Int, point: Point): Double {
        return geohashManager.getLonSize(bits, point)
    }

    fun getLatSize(bits: Int): Double {
        return geohashManager.getLatSize(bits)
    }

    fun adjustGeohashPrecision(geohash: Geohash, significantOfSystem: Int) : Geohash {
        return geohashManager.adjustGeohashPrecision(geohash, significantOfSystem)
    }
}