package com.quest.evrouting.libservice.geometry.infrastructure.adapter

import com.quest.evrouting.libservice.geometry.domain.model.LineString
import com.quest.evrouting.libservice.geometry.domain.model.Point
import com.quest.evrouting.libservice.geometry.domain.model.Polygon
import com.quest.evrouting.libservice.geometry.domain.port.GeometryServicePort

class GeometryServiceAdapter : GeometryServicePort {
    override fun createPoint(lon: Double, lat: Double, alt: Double): Point? {
        return try {
            Point(lon, lat, alt)
        } catch(e: IllegalArgumentException){
            null
        }
    }

    override fun createLineString(points: List<Point>) : LineString? {
        return try {
            LineString(points)
        } catch(e: IllegalArgumentException){
            null
        }
    }

    override fun createPolygon(points: List<List<Point>>) : Polygon? {
        return try {
            Polygon(points)
        } catch(e: IllegalArgumentException){
            null
        }
    }
}