package com.quest.evrounting.algorithm.application.routing

import com.quest.evrounting.algorithm.application.entity.DirectPath
import com.quest.evrounting.algorithm.domain.modelport.IPath
import com.quest.evrounting.algorithm.domain.model.Point
import com.quest.evrounting.algorithm.domain.port.GeometryPort
import com.quest.evrounting.algorithm.domain.port.RoutingPort
import com.quest.evrounting.algorithm.utils.RoutingProfile

class DirectionsRouting(
    val routingTool: RoutingPort,
    val geometryTool: GeometryPort
) : IRoutingManager{
    override suspend fun findShortestPath(
        start: Point,
        end: Point,
        profile: RoutingProfile
    ): IPath? {
        val path = routingTool.findRoute(start, end, profile)
        if(path != null) {
            return DirectPath(path)
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
        path: IPath,
        distanceAlong: Double
    ): IPath? {
        val lineString = path.toLineString()
        if(lineString != null){
            val resPath = geometryTool.findPathAlongPath(lineString, distanceAlong)
            if(resPath != null) {
                return DirectPath(resPath)
            }
        }
        return null
    }

    override fun findPointAlongPath(
        path: IPath,
        distanceAlong: Double
    ): Point? {
        val lineString = path.toLineString()
        if(lineString != null) {
            return geometryTool.findPointAlongPath(lineString, distanceAlong)
        }
        return null
    }
}