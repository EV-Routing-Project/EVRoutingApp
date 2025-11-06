package com.quest.evrounting.libservice.geometry.utils

import de.brudaswen.kotlin.polyline.Coordinate as KotlinCoordinate
import com.quest.evrounting.libservice.geometry.model.Position as DomainCoordinate
import de.brudaswen.kotlin.polyline.Polyline as KotlinPolyline
import com.quest.evrounting.libservice.geometry.model.LineString as DomainPolyline
import com.quest.evrounting.libservice.geometry.model.Geometry as DomainGeometry


//Coordinate
fun DomainCoordinate.toKotlin() : KotlinCoordinate {
    return KotlinCoordinate(this.latitude, this.longitude)
}
fun KotlinCoordinate.toDomain() : DomainCoordinate {
    return DomainCoordinate(this.lon, this.lat)
}

//Polyline
fun DomainPolyline.toKotlin() : KotlinPolyline {
    val kotlinCoordinates: List<KotlinCoordinate> = this.coordinates.map{
        it.coordinates.toKotlin()
    }
    return KotlinPolyline(kotlinCoordinates)
}
fun KotlinPolyline.toDomain() : DomainPolyline {
    val domainCoordinates: List<DomainCoordinate> = this.coordinates.map{
        it.toDomain()
    }
    return DomainPolyline(domainCoordinates)
}