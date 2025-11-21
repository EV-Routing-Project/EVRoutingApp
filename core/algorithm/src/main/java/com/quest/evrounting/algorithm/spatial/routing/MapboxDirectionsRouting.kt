package com.quest.evrounting.algorithm.spatial.routing

import com.quest.evrounting.algorithm.domain.model.LineString
import com.quest.evrounting.algorithm.domain.model.Point
import com.quest.evrounting.algorithm.domain.port.RoutingPort

class MapboxDirectionsRouting(
    val routingTool: RoutingPort
) : IRoutingManager{
    override fun findShortestPath(
        start: Point,
        end: Point
    ): LineString? {
        TODO("Not yet implemented")
    }

}