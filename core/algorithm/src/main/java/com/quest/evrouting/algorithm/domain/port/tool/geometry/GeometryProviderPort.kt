package com.quest.evrouting.algorithm.domain.port.tool.geometry

import com.quest.evrouting.algorithm.domain.model.LineString
import com.quest.evrouting.algorithm.domain.model.Point

interface GeometryProviderPort {
    fun createPoint(lon: Double, lat: Double, alt: Double = 0.0): Point?
    fun createLineString(points: List<Point>) : LineString?
    fun getHaversineDistance(start: Point, end: Point): Double
    fun findPointsInsideCircle(points: List<Point>, radius: Double, center: Point) : List<Point>
    fun getLengthOfPath(path: LineString): Double
    fun findPathAlongPath(path: LineString ,distanceAlong: Double): LineString?
    fun findPointAlongPath (path: LineString, distanceAlong: Double): Point?
}