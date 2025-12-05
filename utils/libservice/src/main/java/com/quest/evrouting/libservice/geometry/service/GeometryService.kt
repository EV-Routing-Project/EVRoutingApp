package com.quest.evrouting.libservice.geometry.service

import com.quest.evrouting.libservice.geometry.domain.manager.GeometryManager
import com.quest.evrouting.libservice.geometry.utils.GeometryUnit
import com.quest.evrouting.libservice.geometry.domain.model.Point
import com.quest.evrouting.libservice.geometry.infrastructure.config.Dependencies
import com.quest.evrouting.libservice.geometry.domain.manager.MeasurementManager
import com.quest.evrouting.libservice.geometry.domain.model.LineString
import com.quest.evrouting.libservice.geometry.domain.model.Polygon

object GeometryService {
    private val measurementManager: MeasurementManager = Dependencies.measurementManager
    private val geometryManager: GeometryManager = Dependencies.geometryManager

    fun createPoint(lon: Double, lat: Double, alt: Double = 0.0): Point? {
        return geometryManager.createPoint(lon, lat, alt)
    }

    fun createLineString(points: List<Point>) : LineString? {
        return geometryManager.createLineString(points)
    }

    fun createPolygon(points: List<List<Point>>) : Polygon? {
        return geometryManager.createPolygon(points)
    }

    fun distance(startPoint: Point, endPoint: Point, units: GeometryUnit = GeometryUnit.UNIT_METERS): Double {
        return measurementManager.getHaversineDistance(startPoint, endPoint, units)
    }

    fun findPointsInsideCircle(
        points: List<Point>,
        radius: Double,
        units: GeometryUnit = GeometryUnit.UNIT_METERS,
        center: Point
    ) : MutableList<Point>{
        return measurementManager.findPointsInsideCircle(points, radius, units, center)
    }

    fun getLengthOfPath(path: LineString, units: GeometryUnit = GeometryUnit.UNIT_METERS): Double {
        return measurementManager.getLengthOfLineString(path, units)
    }

    fun findPathAlongPath (
        path: LineString,
        distanceAlong: Double,
        units: GeometryUnit = GeometryUnit.UNIT_METERS
    ): LineString? {
        return measurementManager.findPathAlongPath(path, distanceAlong, units)
    }

    fun findPointAlongPath (
        path: LineString,
        distanceAlong: Double,
        units: GeometryUnit = GeometryUnit.UNIT_METERS
    ): Point? {
        return measurementManager.findPointAlongPath(path, distanceAlong, units)
    }
}