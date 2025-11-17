package com.quest.evrounting.algorithm.domain.port

import com.quest.evrounting.algorithm.domain.model.LineString
import com.quest.evrounting.algorithm.domain.model.Point
import com.quest.evrounting.algorithm.utils.RoutingProfile

interface RoutingPort {
    fun findRoute(start: Point, end: Point, profile: RoutingProfile): LineString?
}