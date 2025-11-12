package com.quest.evrounting.libservice.geometry.service

import com.quest.evrounting.libservice.geometry.domain.manager.GeohashManager
import com.quest.evrounting.libservice.geometry.domain.model.Geohash
import com.quest.evrounting.libservice.geometry.domain.model.Point
import com.quest.evrounting.libservice.geometry.external.di.ServiceLocator
import com.quest.evrounting.libservice.geometry.utils.GeometryConstants

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

    fun getLonDegree(bits: Int): Double{
        return 360.0 / Math.pow(2.0, ((bits + 1) / 2).toDouble())
    }

    fun getLatDegree(bits: Int): Double {
        return 180.0 / Math.pow(2.0, (bits/ 2).toDouble())
    }

    fun convertLonDegreeToMeters(lonDegree: Double, latitude: Double): Double {
        val metersPerDegree = GeometryConstants.METER_PER_DEGREE_LONGITUDE_AT_EQUATOR * Math.cos(Math.toRadians(latitude))
        return lonDegree * metersPerDegree
    }

    fun convertLatDegreeToMeters(latDegree: Double): Double {
        return latDegree * GeometryConstants.METER_PER_DEGREE_LATITUDE
    }

    fun getLonSize(bits: Int, latitude: Double): Double{
        return convertLonDegreeToMeters(getLonDegree(bits), latitude)
    }

    fun getLatSize(bits: Int): Double {
        return convertLatDegreeToMeters(getLatDegree(bits))
    }
}