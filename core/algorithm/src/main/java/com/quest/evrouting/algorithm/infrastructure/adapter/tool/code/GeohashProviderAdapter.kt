package com.quest.evrouting.algorithm.infrastructure.adapter.tool.code

import com.quest.evrouting.algorithm.domain.model.Geohash
import com.quest.evrouting.algorithm.domain.model.Point
import com.quest.evrouting.algorithm.domain.port.tool.code.GeohashProviderPort
import com.quest.evrouting.algorithm.infrastructure.mapper.toDomain
import com.quest.evrouting.algorithm.infrastructure.mapper.toExternal
import com.quest.evrouting.libservice.geometry.ServiceKit
import com.quest.evrouting.libservice.geometry.domain.port.GeohashServicePort

class GeohashProviderAdapter(
    private val geohashService: GeohashServicePort
) : GeohashProviderPort {
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