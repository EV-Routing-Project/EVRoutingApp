package com.quest.evrounting.algorithm.domain.port.tool.routing

import com.quest.evrounting.algorithm.domain.model.LineString
import com.quest.evrounting.algorithm.domain.model.Point
import com.quest.evrounting.algorithm.config.utils.RoutingProfile

interface RoutingProviderPort {
    suspend fun findRoute(start: Point, end: Point, profile: RoutingProfile): LineString?
}