package com.quest.evrouting.libservice.geometry.infrastructure.adapter

import com.quest.evrouting.libservice.geometry.domain.model.Point
import com.quest.evrouting.libservice.geometry.domain.model.Geohash as DomainGeohash
import com.quest.evrouting.libservice.geometry.domain.port.GeohashPort
import com.quest.evrouting.libservice.geometry.infrastructure.mapper.toDomain
import com.quest.evrouting.libservice.geometry.infrastructure.mapper.toExternal
import ch.hsr.geohash.GeoHash as ExternalGeohash



class GeohashAdapter: GeohashPort {
    override fun getAdjacent(geohash: DomainGeohash): List<DomainGeohash> {
        val externalGeohash: ExternalGeohash = geohash.toExternal()
        val adj: Array<ExternalGeohash> = externalGeohash.adjacent
        return adj.map {
            it.toDomain()
        }
    }

    override fun encode(point: Point, significantBits: Int): DomainGeohash {
        return ExternalGeohash.withBitPrecision(point.lat, point.lon, significantBits).toDomain()
    }
}