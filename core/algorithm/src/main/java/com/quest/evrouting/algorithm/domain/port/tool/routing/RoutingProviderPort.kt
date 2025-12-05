package com.quest.evrouting.algorithm.domain.port.tool.routing

import com.quest.evrouting.algorithm.domain.model.LineString
import com.quest.evrouting.algorithm.domain.model.Point
import com.quest.evrouting.algorithm.config.utils.RoutingProfile

interface RoutingProviderPort {
    suspend fun findRoute(start: Point, end: Point, profile: RoutingProfile): LineString?
}