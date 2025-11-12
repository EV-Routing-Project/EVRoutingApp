package com.quest.evrounting.libservice.geometry.external.mapper

//DOMAIN GEOMETRY JSON
import com.quest.evrounting.libservice.geometry.domain.model.Geometry as DomainGeometry
import com.quest.evrounting.libservice.geometry.domain.model.Point as DomainPoint
import com.quest.evrounting.libservice.geometry.domain.model.LineString as DomainLineString
import com.quest.evrounting.libservice.geometry.domain.model.Polygon as DomainPolygon
import com.quest.evrounting.libservice.geometry.domain.model.Geohash as DomainGeohash

//EXTERNAL GEOMETRY JSON
import com.mapbox.geojson.Geometry as ExternalGeometry
import com.mapbox.geojson.Point as ExternalPoint
import com.mapbox.geojson.LineString as ExternalLineString
import com.mapbox.geojson.Polygon as ExternalPolygon
import ch.hsr.geohash.GeoHash as ExternalGeohash


//Geometry
fun DomainGeometry.toExternal(): ExternalGeometry {
    return when(this) {
        is DomainPoint -> this.toExternal()
        is DomainLineString -> this.toExternal()
        is DomainPolygon -> this.toExternal()
        else -> throw IllegalArgumentException("Unsupported geometry type: $this")
    }
}
fun ExternalGeometry.toDomain(): DomainGeometry {
    return when(this) {
        is ExternalPoint -> this.toDomain()
        is ExternalLineString -> this.toDomain()
        is ExternalPolygon -> this.toDomain()
        else -> throw IllegalArgumentException("Unsupported geometry type: $this")
    }
}


//Point
fun DomainPoint.toExternal(): ExternalPoint {
    return ExternalPoint.fromLngLat(lon, lat, alt)
}
fun ExternalPoint.toDomain(): DomainPoint {
    if(this.hasAltitude()){
        return DomainPoint(this.longitude(), this.latitude(), this.altitude())
    } else {
        return DomainPoint(this.longitude(),this.latitude())
    }
}

//LineString
fun DomainLineString.toExternal(): ExternalLineString {
    val mapboxPoints: List<ExternalPoint> = this.coordinates.map {
        it.toExternal()
    }
    return ExternalLineString.fromLngLats(mapboxPoints)
}
fun ExternalLineString.toDomain(): DomainLineString {
    val domainPoints: List<DomainPoint> = this.coordinates().map {
        it.toDomain()
    }
    return DomainLineString(domainPoints)
}

//Polygon
fun DomainPolygon.toExternal(): ExternalPolygon {
    val mapboxCoordinates: List<List<ExternalPoint>> = this.rings.map {
        it.map {
            it.toExternal()
        }
    }
    return ExternalPolygon.fromLngLats(mapboxCoordinates)
}
fun ExternalPolygon.toDomain(): DomainPolygon {
    val domainEdges: List<List<DomainPoint>> = this.coordinates().map {
        it.map {
            it.toDomain()
        }
    }
    return DomainPolygon(domainEdges)
}

//Geohash
fun DomainGeohash.toExternal(): ExternalGeohash {
    return ExternalGeohash.fromLongValue(this.value, this.significantBits)
}

fun ExternalGeohash.toDomain(): DomainGeohash {
    return DomainGeohash(this.longValue(), this.significantBits())
}