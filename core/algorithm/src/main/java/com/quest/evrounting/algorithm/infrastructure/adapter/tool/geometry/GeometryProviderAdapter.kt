package com.quest.evrounting.algorithm.infrastructure.adapter.tool.geometry

import com.quest.evrounting.algorithm.domain.model.LineString
import com.quest.evrounting.algorithm.domain.model.Point
import com.quest.evrounting.algorithm.domain.port.tool.geometry.GeometryProviderPort
import com.quest.evrounting.algorithm.infrastructure.mapper.toDomain
import com.quest.evrounting.algorithm.infrastructure.mapper.toExternal
import com.quest.evrounting.libservice.geometry.ServiceKit

class GeometryProviderAdapter : GeometryProviderPort {
    private val geometryService = ServiceKit.geometryService
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
        return geometryService.distance(start.toExternal(), end.toExternal())
    }

    override fun findPointsInsideCircle(
        points: List<Point>,
        radius: Double,
        center: Point
    ): List<Point> {
        val listPoints = points.map {
            it.toExternal()
        }
        return geometryService.findPointsInsideCircle(
            points = listPoints,
            radius = radius,
            center = center.toExternal()).map {
            it.toDomain()
        }
    }

    override fun getLengthOfPath(path: LineString): Double {
        return geometryService.getLengthOfPath(path.toExternal())
    }

    override fun findPathAlongPath(
        path: LineString,
        distanceAlong: Double
    ): LineString? {
        return geometryService.findPathAlongPath(
            path.toExternal(),
            distanceAlong)?.toDomain()
    }

    override fun findPointAlongPath(
        path: LineString,
        distanceAlong: Double
    ): Point? {
        return geometryService.findPointAlongPath(
            path.toExternal(),
            distanceAlong)?.toDomain()
    }
}