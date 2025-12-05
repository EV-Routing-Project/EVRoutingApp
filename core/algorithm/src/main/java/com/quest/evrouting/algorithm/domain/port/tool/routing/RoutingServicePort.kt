package com.quest.evrouting.algorithm.domain.port.tool.routing

import com.quest.evrouting.algorithm.domain.model.Point
import com.quest.evrouting.algorithm.domain.port.model.PathPort
import com.quest.evrouting.algorithm.config.utils.RoutingProfile

interface RoutingServicePort {
    suspend fun findShortestPath(start: Point, end: Point, profile: RoutingProfile = RoutingProfile.DRIVING): PathPort?

    fun getHaversineDistance(start: Point, end: Point): Double

    fun findPathAlongPath(path: PathPort, distanceAlong: Double) : PathPort?

    fun findPointAlongPath(path: PathPort, distanceAlong: Double) : Point?
}