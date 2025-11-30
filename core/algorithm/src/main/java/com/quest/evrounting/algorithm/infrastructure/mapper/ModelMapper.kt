package com.quest.evrounting.algorithm.infrastructure.mapper

import com.quest.evrounting.libservice.geometry.domain.model.Geometry as ExternalGeometry
import com.quest.evrounting.libservice.geometry.domain.model.Point as ExternalPoint
import com.quest.evrounting.libservice.geometry.domain.model.LineString as ExternalLineString
import com.quest.evrounting.libservice.geometry.domain.model.Geohash as ExternalGeohash

import com.quest.evrounting.algorithm.domain.model.Geometry as DomainGeometry
import com.quest.evrounting.algorithm.domain.model.Point as DomainPoint
import com.quest.evrounting.algorithm.domain.model.LineString as DomainLineString
import com.quest.evrounting.algorithm.domain.model.Geohash as DomainGeohash


//Geometry
fun ExternalGeometry.toDomain(): DomainGeometry {
    return when (this) {
        is ExternalPoint -> this.toDomain()
        is ExternalLineString -> this.toDomain()
        else -> throw IllegalArgumentException("Unsupported geometry type")
    }
}

fun DomainGeometry.toExternal(): ExternalGeometry {
    return when(this){
        is DomainPoint -> this.toExternal()
        is DomainLineString -> this.toExternal()
        else -> throw IllegalArgumentException("Unsupported geometry type")
    }
}


//Point
fun ExternalPoint.toDomain(): DomainPoint {
    return DomainPoint(this.lon, this.lat, this.alt)
}

fun DomainPoint.toExternal(): ExternalPoint {
    return ExternalPoint(this.lon, this.lat, this.alt)
}

//LineString
fun ExternalLineString.toDomain(): DomainLineString {
    return DomainLineString(this.coordinates.map { it.toDomain() })
}

fun DomainLineString.toExternal(): ExternalLineString {
    return ExternalLineString(this.coordinates.map { it.toExternal() })
}

//Geohash
fun ExternalGeohash.toDomain(): DomainGeohash {
    return DomainGeohash(this.value, this.significantBits)
}

fun DomainGeohash.toExternal(): ExternalGeohash {
    return ExternalGeohash(this.value, this.significantBits)
}