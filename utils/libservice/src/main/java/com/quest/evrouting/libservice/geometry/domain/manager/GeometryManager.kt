package com.quest.evrouting.libservice.geometry.domain.manager

import com.quest.evrouting.libservice.geometry.domain.model.LineString
import com.quest.evrouting.libservice.geometry.domain.model.Point
import com.quest.evrouting.libservice.geometry.domain.model.Polygon

class GeometryManager {
    fun createPoint(lon: Double, lat: Double, alt: Double = 0.0): Point? {
        return try {
            Point(lon, lat, alt)
        } catch(e: IllegalArgumentException){
            null
        }
    }

    fun createLineString(points: List<Point>) : LineString? {
        return try {
            LineString(points)
        } catch(e: IllegalArgumentException){
            null
        }
    }

    fun createPolygon(points: List<List<Point>>) : Polygon? {
        return try {
            Polygon(points)
        } catch(e: IllegalArgumentException){
            null
        }
    }
}