package com.quest.evrounting.algorithm.domain.port.tool.routing

import com.quest.evrounting.algorithm.domain.model.Point
import com.quest.evrounting.algorithm.domain.port.model.PathPort
import com.quest.evrounting.algorithm.config.utils.RoutingProfile

interface RoutingServicePort {
    suspend fun findShortestPath(start: Point, end: Point, profile: RoutingProfile = RoutingProfile.DRIVING): PathPort?

    fun getHaversineDistance(start: Point, end: Point): Double

    fun findPathAlongPath(path: PathPort, distanceAlong: Double) : PathPort?

    fun findPointAlongPath(path: PathPort, distanceAlong: Double) : Point?
}