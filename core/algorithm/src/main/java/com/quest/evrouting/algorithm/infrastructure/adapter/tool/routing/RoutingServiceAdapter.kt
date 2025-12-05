package com.quest.evrouting.algorithm.infrastructure.adapter.tool.routing

import com.quest.evrouting.algorithm.infrastructure.adapter.model.DirectPathAdapter
import com.quest.evrouting.algorithm.domain.model.Point
import com.quest.evrouting.algorithm.domain.port.model.PathPort
import com.quest.evrouting.algorithm.domain.port.tool.geometry.GeometryProviderPort
import com.quest.evrouting.algorithm.domain.port.tool.routing.RoutingProviderPort
import com.quest.evrouting.algorithm.domain.port.tool.routing.RoutingServicePort
import com.quest.evrouting.algorithm.config.utils.RoutingProfile

class RoutingServiceAdapter(
    val routingProvider: RoutingProviderPort,
    val geometryProvider: GeometryProviderPort
) : RoutingServicePort {
    override suspend fun findShortestPath(
        start: Point,
        end: Point,
        profile: RoutingProfile
    ): PathPort? {
        val path = routingProvider.findRoute(start, end, profile)
        if(path != null) {
            return DirectPathAdapter(path)
        }
        return null
    }

    override fun getHaversineDistance(
        start: Point,
        end: Point
    ): Double {
        return geometryProvider.getHaversineDistance(start, end)
    }

    override fun findPathAlongPath(
        path: PathPort,
        distanceAlong: Double
    ): PathPort? {
        val lineString = path.toLineString()
        if(lineString != null){
            val resPath = geometryProvider.findPathAlongPath(lineString, distanceAlong)
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
            return geometryProvider.findPointAlongPath(lineString, distanceAlong)
        }
        return null
    }
}