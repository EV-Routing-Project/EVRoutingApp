package com.quest.evrounting.algorithm.domain.port

import com.quest.evrounting.algorithm.domain.model.LineString
import com.quest.evrounting.algorithm.domain.model.Point

interface GeometryPort {
    fun createPoint(lon: Double, lat: Double, alt: Double = 0.0): Point?
    fun createLineString(points: List<Point>) : LineString?
    fun findPointsInsideCircle(points: List<Point>, radius: Double, center: Point) : List<Point>
    fun getLengthOfPath(path: LineString): Double
    fun findPathAlongPath(path: LineString ,distanceAlong: Double): LineString?
    fun findPointAlongPath (path: LineString, distanceAlong: Double): Point?
}