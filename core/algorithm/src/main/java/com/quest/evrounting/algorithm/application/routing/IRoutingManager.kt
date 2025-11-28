package com.quest.evrounting.algorithm.application.routing

import com.quest.evrounting.algorithm.domain.modelport.IPath
import com.quest.evrounting.algorithm.domain.model.Point
import com.quest.evrounting.algorithm.utils.RoutingProfile

interface IRoutingManager {
    suspend fun findShortestPath(start: Point, end: Point, profile: RoutingProfile  = RoutingProfile.DRIVING): IPath?

    fun getHaversineDistance(start: Point, end: Point): Double

    fun findPathAlongPath(path: IPath, distanceAlong: Double) : IPath?

    fun findPointAlongPath(path: IPath, distanceAlong: Double) : Point?
}