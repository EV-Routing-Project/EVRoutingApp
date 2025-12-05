package com.quest.evrouting.libservice.geometry.domain.manager

import com.quest.evrouting.libservice.geometry.domain.model.Geohash
import com.quest.evrouting.libservice.geometry.domain.model.Point
import com.quest.evrouting.libservice.geometry.domain.port.GeohashServicePort
import com.quest.evrouting.libservice.geometry.utils.GeometryConstants
import kotlin.collections.plus

class GeohashManager(val geohashAdapter: GeohashServicePort){

    fun getGeohashGridForPoint(point: Point, significantBits: Int): List<Geohash> {
        return geohashAdapter.getGeohashGridForPoint(point,significantBits)
    }

    fun encode(point: Point, significantBits: Int): Geohash {
        return geohashAdapter.encode(point, significantBits)
    }
    fun createGeohashFromLongValue(value: Long, significantBits: Int): Geohash {
        return geohashAdapter.createGeohashFromLongValue(value, significantBits)
    }
    fun getLonDegree(bits: Int): Double {
        return 360.0 / Math.pow(2.0, ((bits + 1) / 2).toDouble())
    }
    fun getLatDegree(bits: Int): Double {
        return 180.0 / Math.pow(2.0, (bits/ 2).toDouble())
    }

    fun convertLonDegreeToMeters(lonDegree: Double, point: Point): Double {
        val metersPerDegree = GeometryConstants.METER_PER_DEGREE_LONGITUDE_AT_EQUATOR * Math.cos(Math.toRadians(point.lat))
        return lonDegree * metersPerDegree
    }

    fun convertLatDegreeToMeters(latDegree: Double): Double {
        return latDegree * GeometryConstants.METER_PER_DEGREE_LATITUDE
    }

    fun getLonSize(bits: Int, point: Point): Double{
        return convertLonDegreeToMeters(getLonDegree(bits), point)
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