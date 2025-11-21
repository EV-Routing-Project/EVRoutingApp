package com.quest.evrounting.algorithm.spatial.routing

import com.quest.evrounting.algorithm.domain.model.LineString
import com.quest.evrounting.algorithm.domain.model.Point

interface IRoutingManager {
    fun findShortestPath(start: Point, end: Point): LineString?
}