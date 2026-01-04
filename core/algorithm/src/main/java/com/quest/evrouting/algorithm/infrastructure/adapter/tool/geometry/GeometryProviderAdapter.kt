package com.quest.evrouting.algorithm.infrastructure.adapter.tool.geometry

import com.quest.evrouting.algorithm.domain.model.LineString
import com.quest.evrouting.algorithm.domain.model.Point
import com.quest.evrouting.algorithm.domain.port.tool.geometry.GeometryProviderPort
import com.quest.evrouting.algorithm.infrastructure.mapper.toDomain
import com.quest.evrouting.algorithm.infrastructure.mapper.toExternal
import com.quest.evrouting.libservice.geometry.domain.port.GeometryServicePort
import com.quest.evrouting.libservice.geometry.domain.port.MeasurementServicePort

class GeometryProviderAdapter(
    private val measurementService: MeasurementServicePort,
    private val geometryService: GeometryServicePort
) : GeometryProviderPort {
    override fun createPoint(
        lon: Double,
        lat: Double,
        alt: Double
    ): Point? {
        return geometryService.createPoint(lon, lat, alt)?.toDomain()
    }

    override fun createLineString(points: List<Point>): LineString? {
        return geometryService.createLineString(points.map {
            it.toExternal()
        })?.toDomain()
    }

    override fun getHaversineDistance(
        start: Point,
        end: Point
    ): Double {
        return measurementService.getHaversineDistance(start.toExternal(), end.toExternal())
    }

    override fun findPointsInsideCircle(
        points: List<Point>,
        radius: Double,
        center: Point
    ): List<Point> {
        val listPoints = points.map {
            it.toExternal()
        }
        return measurementService.findPointsInsideCircle(
            points = listPoints,
            radius = radius,
            center = center.toExternal()).map {
            it.toDomain()
        }
    }

    override fun getLengthOfLineString(path: LineString): Double {
        return measurementService.getLengthOfLineString(path.toExternal())
    }

    override fun findPathAlongPath(
        path: LineString,
        distanceAlong: Double
    ): LineString? {
        return measurementService.findPathAlongPath(
            path.toExternal(),
            distanceAlong)?.toDomain()
    }

    override fun findPointAlongPath(
        path: LineString,
        distanceAlong: Double
    ): Point? {
        return measurementService.findPointAlongPath(
            path.toExternal(),
            distanceAlong)?.toDomain()
    }
}