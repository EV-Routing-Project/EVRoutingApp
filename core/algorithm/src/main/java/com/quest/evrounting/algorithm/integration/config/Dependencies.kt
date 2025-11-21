package com.quest.evrounting.algorithm.integration.config

import com.quest.evrounting.algorithm.domain.port.GeohashPort
import com.quest.evrounting.algorithm.domain.port.GeometryPort
import com.quest.evrounting.algorithm.domain.port.PolylinePort
import com.quest.evrounting.algorithm.domain.port.RoutingPort
import com.quest.evrounting.algorithm.integration.adapter.GeohashAdapter
import com.quest.evrounting.algorithm.integration.adapter.GeometryAdapter
import com.quest.evrounting.algorithm.integration.adapter.PolylineAdapter
import com.quest.evrounting.algorithm.integration.adapter.RoutingAdapter


object Dependencies {
    val geohashTool: GeohashPort by lazy {
        GeohashAdapter()
    }

    val geometryTool: GeometryPort by lazy {
        GeometryAdapter()
    }

    val polylineTool: PolylinePort by lazy {
        PolylineAdapter()
    }

    val routingTool: RoutingPort by lazy {
        RoutingAdapter()
    }
}