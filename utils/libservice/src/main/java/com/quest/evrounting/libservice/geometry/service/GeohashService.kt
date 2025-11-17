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

    fun encode(lon: Double, lat: Double, significantBits: Int): Geohash {
        return geohashManager.encode(lon, lat, significantBits)
    }

//    fun encodeToLong(lon: Double, lat: Double, significantBits: Int): Long {
//        return geohashManager.encodeToLong(lon, lat, significantBits)
//    }

    fun getGeohashGridForPoint(lon: Double, lat: Double, significantBits: Int): List<Geohash> {
        return geohashManager.getGeohashGridForPoint(lon, lat, significantBits)
    }

    fun getLonSize(bits: Int, latitude: Double): Double {
        return geohashManager.getLonSize(bits, latitude)
    }

    fun getLatSize(bits: Int): Double {
        return geohashManager.getLatSize(bits)
    }

    fun adjustGeohashPrecision(geohash: Geohash, significantOfSystem: Int) : Geohash {
        return geohashManager.adjustGeohashPrecision(geohash, significantOfSystem)
    }
}