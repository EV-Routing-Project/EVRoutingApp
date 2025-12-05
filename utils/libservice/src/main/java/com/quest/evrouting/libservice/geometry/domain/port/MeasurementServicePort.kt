package com.quest.evrouting.libservice.geometry.domain.port

import com.quest.evrouting.libservice.geometry.domain.model.LineString
import com.quest.evrouting.libservice.geometry.utils.GeometryUnit
import com.quest.evrouting.libservice.geometry.domain.model.Point
import com.quest.evrouting.libservice.geometry.service.GeometryService.distance
import com.quest.evrouting.libservice.geometry.service.GeometryService.getLengthOfPath

interface MeasurementServicePort {
    fun getHaversineDistance(startPoint: Point, endPoint: Point, units: GeometryUnit = GeometryUnit.UNIT_METERS): Double
    fun getLengthOfLineString(path: LineString, units: GeometryUnit = GeometryUnit.UNIT_METERS): Double

    fun findPointsInsideCircle(
        points: List<Point>,
        radius: Double,
        units: GeometryUnit = GeometryUnit.UNIT_METERS,
        center: Point
    ) : MutableList<Point>

    fun findPathAlongPath(
        path: LineString,
        distanceAlong: Double,
        units: GeometryUnit = GeometryUnit.UNIT_METERS
    ): LineString?

    fun findPointAlongPath (
        path: LineString,
        distanceAlong: Double,
        units: GeometryUnit = GeometryUnit.UNIT_METERS
    ): Point?
}