package com.quest.evrounting.algorithm.integration.adapter

import com.quest.evrounting.algorithm.domain.model.Geohash
import com.quest.evrounting.algorithm.domain.model.Point
import com.quest.evrounting.algorithm.domain.port.GeohashPort
import com.quest.evrounting.algorithm.integration.mapper.toDomain
import com.quest.evrounting.algorithm.integration.mapper.toExternal
import com.quest.evrounting.libservice.geometry.ServiceKit

class GeohashAdapter : GeohashPort {
    private val geohashService = ServiceKit.geohashService
    override fun encode(
        point: Point,
        significantBits: Int
    ): Geohash {
        return geohashService.encode(point.toExternal(),significantBits).toDomain()
    }

    override fun getGeohashGridForPoint(
        point: Point,
        significantBits: Int
    ): List<Geohash> {
        val listGeohash = geohashService.getGeohashGridForPoint(point.toExternal(),significantBits).map {
            it.toDomain()
        }
        return listGeohash
    }

    override fun getLonSize(bits: Int, point: Point): Double {
        return geohashService.getLonSize(bits,point.toExternal())
    }

    override fun getLatSize(bits: Int): Double {
        return geohashService.getLatSize(bits)
    }

    override fun adjustGeohashPrecision (
        geohash: Geohash,
        significantOfSystem: Int
    ): Geohash {
        return geohashService.adjustGeohashPrecision(geohash.toExternal(),significantOfSystem).toDomain()
    }
}