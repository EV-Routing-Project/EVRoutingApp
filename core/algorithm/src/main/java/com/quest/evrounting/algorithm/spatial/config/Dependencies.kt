package com.quest.evrounting.algorithm.spatial.config

import com.quest.evrounting.algorithm.spatial.cache.ICacheManager
import com.quest.evrounting.algorithm.spatial.cache.LineStringTrie
import com.quest.evrounting.algorithm.spatial.index.GeoBitTrie
import com.quest.evrounting.algorithm.spatial.index.IEntityManager
import com.quest.evrounting.algorithm.spatial.routing.IRoutingManager
import com.quest.evrounting.algorithm.spatial.routing.MapboxDirectionsRouting
import com.quest.evrounting.algorithm.integration.config.Dependencies as IntegrationDependencies

object Dependencies {
    fun createEntityManager(): IEntityManager {
        return GeoBitTrie(
            IntegrationDependencies.geohashTool,
            IntegrationDependencies.geometryTool
            )
    }
    fun createCacheManager(): ICacheManager {
        return LineStringTrie(
            IntegrationDependencies.geometryTool
        )
    }
    fun createRoutingManager(): IRoutingManager {
        return MapboxDirectionsRouting(
            IntegrationDependencies.routingTool
        )
    }
}