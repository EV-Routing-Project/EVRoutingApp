package com.quest.evrounting.algorithm.spatial.config

import com.quest.evrounting.algorithm.spatial.SpatialManager
import com.quest.evrounting.algorithm.spatial.cache.ICacheManager
import com.quest.evrounting.algorithm.spatial.cache.LineStringGraph
import com.quest.evrounting.algorithm.spatial.entity.BaseEntity
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
        return LineStringGraph()
    }
    fun createRoutingManager(): IRoutingManager {
        return MapboxDirectionsRouting(
            IntegrationDependencies.routingTool,
            IntegrationDependencies.geometryTool
        )
    }

    fun createSpatialManager(entities: List<BaseEntity>): SpatialManager {
        return SpatialManager(
            createEntityManager(),
            createCacheManager(),
            createRoutingManager(),
            entities
        )
    }
}