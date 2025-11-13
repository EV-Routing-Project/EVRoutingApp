package com.quest.evrounting.libservice.geometry.domain.manager

import com.quest.evrounting.libservice.geometry.domain.model.Geohash
import com.quest.evrounting.libservice.geometry.domain.model.Point
import com.quest.evrounting.libservice.geometry.domain.port.GeohashPort
import com.quest.evrounting.libservice.geometry.service.GeohashService
import com.quest.evrounting.libservice.geometry.utils.GeometryConstants
import kotlin.collections.plus

class GeohashManager(val geohashAdapter: GeohashPort){
    fun getAdjacent(geohash: Geohash): List<Geohash> {
        return geohashAdapter.getAdjacent(geohash)
    }

    fun getGeohashGridForPoint(lon: Double, lat: Double, significantBits: Int): List<Geohash> {
        val geohash = GeohashService.encode(lon, lat, significantBits)
        return getAdjacent(geohash) + geohash
    }

    fun encode(lon: Double, lat: Double, significantBits: Int): Geohash {
        val point = Point(lon, lat)
        return geohashAdapter.encode(point, significantBits)
    }
//
//    fun encodeToLong(lon: Double, lat: Double, significantBits: Int): Long {
//        val geohash = encode(lon, lat, significantBits)
//        return geohash.value
//    }
//
    fun createGeohashFromLongValue(value: Long, significantBits: Int): Geohash {
        return Geohash(value, significantBits)
    }

    fun getLonDegree(bits: Int): Double {
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

    fun adjustGeohashPrecision(geohash: Geohash, significantOfSystem: Int) : Geohash {
        val bitOffset = geohash.significantBits - significantOfSystem
        if(bitOffset > 0){
            return createGeohashFromLongValue(geohash.value shr bitOffset, significantOfSystem)
        } else {
            return createGeohashFromLongValue(geohash.value shl (-bitOffset), significantOfSystem)
        }
    }
}