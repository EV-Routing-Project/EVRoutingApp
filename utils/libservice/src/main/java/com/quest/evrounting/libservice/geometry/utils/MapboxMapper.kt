package com.quest.evrounting.libservice.geometry.utils

//DOMAIN GEOMETRY JSON
import com.quest.evrounting.libservice.geometry.model.Geometry as DomainGeometry
import com.quest.evrounting.libservice.geometry.model.Point as DomainPoint
import com.quest.evrounting.libservice.geometry.model.LineString as DomainLineString
import com.quest.evrounting.libservice.geometry.model.Polygon as DomainPolygon

//MAPBOX GEOMETRY JSON
import com.mapbox.geojson.Geometry as MapboxGeometry
import com.mapbox.geojson.Point as MapboxPoint
import com.mapbox.geojson.LineString as MapboxLineString
import com.mapbox.geojson.Polygon as MapboxPolygon


//Geometry
fun DomainGeometry.toMapboxGeo(): MapboxGeometry {
    return when(this) {
        is DomainPoint -> this.toMapbox()
        is DomainLineString -> this.toMapbox()
        is DomainPolygon -> this.toMapbox()
        else -> throw IllegalArgumentException("Unsupported geometry type: $this")
    }
}
fun MapboxGeometry.toDomainGeo(): DomainGeometry {
    return when(this) {
        is MapboxPoint -> this.toDomain()
        is MapboxLineString -> this.toDomain()
        is MapboxPolygon -> this.toDomain()
        else -> throw IllegalArgumentException("Unsupported geometry type: $this")
    }
}


//Point
fun DomainPoint.toMapbox(): MapboxPoint {
    return MapboxPoint.fromLngLat(lon, lat, alt)
}
fun MapboxPoint.toDomain(): DomainPoint {
    if(this.hasAltitude()){
        return DomainPoint(this.longitude(), this.latitude(), this.altitude())
    } else {
        return DomainPoint(this.longitude(),this.latitude())
    }
}

//LineString
fun DomainLineString.toMapbox(): MapboxLineString {
    val mapboxPoints: List<MapboxPoint> = this.coordinates.map {
        it.toMapbox()
    }
    return MapboxLineString.fromLngLats(mapboxPoints)
}
fun MapboxLineString.toDomain(): DomainLineString {
    val domainPoints: List<DomainPoint> = this.coordinates().map {
        it.toDomain()
    }
    return DomainLineString(domainPoints)
}

//Polygon
fun DomainPolygon.toMapbox(): MapboxPolygon {
    val mapboxCoordinates: List<List<MapboxPoint>> = this.rings.map {
        it.map {
            it.toMapbox()
        }
    }
    return MapboxPolygon.fromLngLats(mapboxCoordinates)
}
fun MapboxPolygon.toDomain(): DomainPolygon {
    val domainEdges: List<List<DomainPoint>> = this.coordinates().map {
        it.map {
            it.toDomain()
        }
    }
    return DomainPolygon(domainEdges)
}