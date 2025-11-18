package com.quest.evrounting.algorithm.integration.adapter

import com.quest.evrounting.algorithm.domain.model.LineString
import com.quest.evrounting.algorithm.domain.model.Point
import com.quest.evrounting.algorithm.domain.port.GeometryPort
import com.quest.evrounting.algorithm.integration.mapper.toDomain
import com.quest.evrounting.algorithm.integration.mapper.toExternal
import com.quest.evrounting.libservice.geometry.ServiceKit

class GeometryAdapter : GeometryPort {
    private val geometryService = ServiceKit.geometryService
    override fun createPoint(
        lon: Double,
        lat: Double,
        alt: Double
    ): Point? {
        TODO("Not yet implemented")
    }

    override fun createLineString(points: List<Point>): LineString? {
        TODO("Not yet implemented")
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
        TODO("Not yet implemented")
    }

    override fun findPathAlongPath(
        path: LineString,
        distanceAlong: Double
    ): LineString? {
        TODO("Not yet implemented")
    }

    override fun findPointAlongPath(
        path: LineString,
        distanceAlong: Double
    ): Point? {
        TODO("Not yet implemented")
    }

}