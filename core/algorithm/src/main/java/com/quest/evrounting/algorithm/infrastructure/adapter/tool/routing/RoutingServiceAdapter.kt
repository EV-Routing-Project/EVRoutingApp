package com.quest.evrounting.algorithm.infrastructure.adapter.tool.routing

import com.quest.evrounting.algorithm.infrastructure.adapter.model.DirectPathAdapter
import com.quest.evrounting.algorithm.domain.model.Point
import com.quest.evrounting.algorithm.domain.port.model.PathPort
import com.quest.evrounting.algorithm.domain.port.tool.geometry.GeometryProviderPort
import com.quest.evrounting.algorithm.domain.port.tool.routing.RoutingProviderPort
import com.quest.evrounting.algorithm.domain.port.tool.routing.RoutingServicePort
import com.quest.evrounting.algorithm.config.utils.RoutingProfile

class RoutingServiceAdapter(
    val routingTool: RoutingProviderPort,
    val geometryTool: GeometryProviderPort
) : RoutingServicePort {
    override suspend fun findShortestPath(
        start: Point,
        end: Point,
        profile: RoutingProfile
    ): PathPort? {
        val path = routingTool.findRoute(start, end, profile)
        if(path != null) {
            return DirectPathAdapter(path)
        }
        return null
    }

    override fun getHaversineDistance(
        start: Point,
        end: Point
    ): Double {
        return geometryTool.getHaversineDistance(start, end)
    }

    override fun findPathAlongPath(
        path: PathPort,
        distanceAlong: Double
    ): PathPort? {
        val lineString = path.toLineString()
        if(lineString != null){
            val resPath = geometryTool.findPathAlongPath(lineString, distanceAlong)
            if(resPath != null) {
                return DirectPathAdapter(resPath)
            }
        }
        return null
    }

    override fun findPointAlongPath(
        path: PathPort,
        distanceAlong: Double
    ): Point? {
        val lineString = path.toLineString()
        if(lineString != null) {
            return geometryTool.findPointAlongPath(lineString, distanceAlong)
        }
        return null
    }
}