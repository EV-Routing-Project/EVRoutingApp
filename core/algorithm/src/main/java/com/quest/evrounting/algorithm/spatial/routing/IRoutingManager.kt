package com.quest.evrounting.algorithm.spatial.routing

import com.quest.evrounting.algorithm.domain.model.LineString
import com.quest.evrounting.algorithm.domain.model.Point
import okhttp3.Dispatcher

interface IRoutingManager {
    fun findShortestPath(start: Point, end: Point): LineString?

    fun findPathAlongPath(path: LineString, distanceAlong: Double) : LineString?

    fun findPointAlongPath(path: LineString, distanceAlong: Double) : Point?
}