package com.quest.evrouting.libservice.geometry.infrastructure.adapter

import com.quest.evrouting.libservice.geometry.domain.model.Geohash
import com.quest.evrouting.libservice.geometry.domain.model.Point
import com.quest.evrouting.libservice.geometry.domain.model.Geohash as DomainGeohash
import com.quest.evrouting.libservice.geometry.domain.port.GeohashServicePort
import com.quest.evrouting.libservice.geometry.infrastructure.mapper.toDomain
import com.quest.evrouting.libservice.geometry.infrastructure.mapper.toExternal
import com.quest.evrouting.libservice.geometry.utils.GeometryConstants
import ch.hsr.geohash.GeoHash as ExternalGeohash



class GeohashServiceAdapter: GeohashServicePort {
    fun getAdjacent(geohash: DomainGeohash): List<DomainGeohash> {
        val externalGeohash: ExternalGeohash = geohash.toExternal()
        val adj: Array<ExternalGeohash> = externalGeohash.adjacent
        return adj.map {
            it.toDomain()
        }
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

    override fun getLonSize(bits: Int, point: Point): Double{
        return convertLonDegreeToMeters(getLonDegree(bits), point)
    }

    override fun encode(point: Point, significantBits: Int): DomainGeohash {
        return ExternalGeohash.withBitPrecision(point.lat, point.lon, significantBits).toDomain()
    }

    override fun getGeohashGridForPoint(
        point: Point,
        significantBits: Int
    ): List<DomainGeohash> {
        val geohash = encode(point, significantBits)
        return getAdjacent(geohash) + geohash
    }

    override fun createGeohashFromLongValue(value: Long, significantBits: Int): Geohash {
        return Geohash(value, significantBits)
    }

    override fun getLatSize(bits: Int): Double {
        return convertLatDegreeToMeters(getLatDegree(bits))
    }
    override fun adjustGeohashPrecision(geohash: Geohash, significantOfSystem: Int) : Geohash {
        val bitOffset = geohash.significantBits - significantOfSystem
        if(bitOffset > 0){
            return createGeohashFromLongValue(geohash.value shr bitOffset, significantOfSystem)
        } else {
            return createGeohashFromLongValue(geohash.value shl (-bitOffset), significantOfSystem)
        }
    }
}