package com.quest.evrounting.algorithm.integration.config

import com.quest.evrounting.algorithm.domain.port.GeohashPort
import com.quest.evrounting.algorithm.domain.port.GeometryPort
import com.quest.evrounting.algorithm.domain.port.RoutingPort
import com.quest.evrounting.algorithm.integration.adapter.GeohashAdapter
import com.quest.evrounting.algorithm.integration.adapter.GeometryAdapter


object Dependencies {
    val geohashTool: GeohashPort by lazy {
        GeohashAdapter()
    }

    val geometryTool: GeometryPort by lazy {
        GeometryAdapter()
    }
}