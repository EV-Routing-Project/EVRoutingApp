package com.quest.evrounting.algorithm.application.config

import com.quest.evrounting.algorithm.application.cache.ICacheManager
import com.quest.evrounting.algorithm.application.cache.LineStringGraph
import com.quest.evrounting.algorithm.application.index.GeoBitTrie
import com.quest.evrounting.algorithm.application.index.IStationManager
import com.quest.evrounting.algorithm.application.routing.IRoutingManager
import com.quest.evrounting.algorithm.application.routing.DirectionsRouting
import com.quest.evrounting.algorithm.application.searching.AStarWithStationNode
import com.quest.evrounting.algorithm.application.searching.ISearchingManager
import com.quest.evrounting.algorithm.infrastructure.config.Dependencies as IntegrationDependencies

object Dependencies {
    fun createStationManagerGeoBitTrie(): IStationManager {
        return GeoBitTrie(
            IntegrationDependencies.geohashTool,
            IntegrationDependencies.geometryTool
            )
    }

    fun createCacheManagerLineStringGraph(): ICacheManager {
        return LineStringGraph()
    }

    fun createRoutingManagerDirectionsRouting(): IRoutingManager {
        return DirectionsRouting(
            IntegrationDependencies.routingTool,
            IntegrationDependencies.geometryTool
        )
    }

    fun createSearchingManagerAStarWithStationNode(
        stationManager: IStationManager,
        cacheManager: ICacheManager,
        routingManager: IRoutingManager
    ) : ISearchingManager {
        return AStarWithStationNode(
            stationManager,
            cacheManager,
            routingManager
        )
    }

    fun createBaseSearchingManagerAStarWithStationNode() : ISearchingManager {
        return createSearchingManagerAStarWithStationNode(
            createStationManagerGeoBitTrie(),
            createCacheManagerLineStringGraph(),
            createRoutingManagerDirectionsRouting()
        )
    }
}