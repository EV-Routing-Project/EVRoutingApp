package com.quest.evrounting.algorithm.spatial.routing

import com.quest.evrounting.algorithm.domain.model.LineString
import com.quest.evrounting.algorithm.domain.model.Point
import com.quest.evrounting.algorithm.domain.port.GeometryPort
import com.quest.evrounting.algorithm.domain.port.RoutingPort

class MapboxDirectionsRouting(
    val routingTool: RoutingPort,
    val geometryTool: GeometryPort
) : IRoutingManager{
    override fun findShortestPath(
        start: Point,
        end: Point
    ): LineString? {
        TODO("Not yet implemented")
    }

    override fun findPathAlongPath(
        path: LineString,
        distanceAlong: Double
    ): LineString? {
        return geometryTool.findPathAlongPath(path, distanceAlong)
    }

    override fun findPointAlongPath(
        path: LineString,
        distanceAlong: Double
    ): Point? {
        return geometryTool.findPointAlongPath(path, distanceAlong)
    }

}