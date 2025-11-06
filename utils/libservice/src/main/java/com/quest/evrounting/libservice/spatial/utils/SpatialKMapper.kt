package com.quest.evrounting.libservice.spatial.utils

import org.maplibre.spatialk.geojson.Geometry as SpatialKGeometry
import com.quest.evrounting.libservice.spatial.model.Geometry as DomainGeometry
import org.maplibre.spatialk.geojson.Position as SpatialKPosition
import com.quest.evrounting.libservice.spatial.model.Position as DomainPosition
import org.maplibre.spatialk.geojson.Point as SpatialKPoint
import com.quest.evrounting.libservice.spatial.model.Point as DomainPoint
import org.maplibre.spatialk.geojson.LineString as SpatialKLineString
import com.quest.evrounting.libservice.spatial.model.LineString as DomainLineString
import org.maplibre.spatialk.geojson.Polygon as SpatialKPolygon
import com.quest.evrounting.libservice.spatial.model.Polygon as DomainPolygon

//Geometry
fun DomainGeometry.toSpatialK() : SpatialKGeometry {
    return when (this) {
        is DomainPoint -> this.toSpatialK()
        is DomainLineString -> this.toSpatialK()
        is DomainPolygon -> this.toSpatialK()
        else -> throw IllegalArgumentException("Unsupported geometry type: ${this::class.simpleName}")
    }
}
fun SpatialKGeometry.toDomain() : DomainGeometry{
    return when (this) {
        is SpatialKPoint -> this.toDomain()
        is SpatialKLineString -> this.toDomain()
        is SpatialKPolygon -> this.toDomain()
        else -> throw IllegalArgumentException("Unsupported geometry type: ${this::class.simpleName}")
    }
}

//Position
fun DomainPosition.toSpatialK() : SpatialKPosition {
    return SpatialKPosition(this.longitude, this.latitude)
}
fun  SpatialKPosition.toDomain() : DomainPosition {
    return DomainPosition(this.longitude, this.latitude)
}

//Point
fun DomainPoint.toSpatialK() : SpatialKPoint {
    return SpatialKPoint(this.coordinates.toSpatialK())
}
fun  SpatialKPoint.toDomain() : DomainPoint {
    return DomainPoint(this.coordinates.toDomain())
}

//LineString
fun DomainLineString.toSpatialK(): SpatialKLineString {
    val spatialKCoordinates: List<SpatialKPosition> = this.coordinates.map {
        it.coordinates.toSpatialK()
    }
    return SpatialKLineString(spatialKCoordinates)
}
fun SpatialKLineString.toDomain() : DomainLineString {
    val domainCoordinates: List<DomainPosition> = this.coordinates.map {
        it.toDomain()
    }
    return DomainLineString(domainCoordinates)
}

//Polygon
fun DomainPolygon.toSpatialK(): SpatialKPolygon {
    val spatialKCoordinates: List<List<SpatialKPosition>> = this.edges.map{
        it.coordinates.map {
            it.coordinates.toSpatialK()
        }
    }
    return SpatialKPolygon(spatialKCoordinates)
}
fun SpatialKPolygon.toDomain(): DomainPolygon {
    val domainCoordinates: List<List<DomainPosition>> = this.coordinates.map {
        it.map {
            it.toDomain()
        }
    }
    return DomainPolygon(domainCoordinates)
}

