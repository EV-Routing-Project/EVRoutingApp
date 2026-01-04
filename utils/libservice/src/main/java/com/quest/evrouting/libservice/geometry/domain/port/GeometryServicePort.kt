package com.quest.evrouting.libservice.geometry.domain.port

import com.quest.evrouting.libservice.geometry.domain.model.LineString
import com.quest.evrouting.libservice.geometry.domain.model.Point
import com.quest.evrouting.libservice.geometry.domain.model.Polygon

interface GeometryServicePort {

    fun createPoint(lon: Double, lat: Double, alt: Double = 0.0): Point?

    fun createLineString(points: List<Point>) : LineString?

    fun createPolygon(points: List<List<Point>>) : Polygon?
}