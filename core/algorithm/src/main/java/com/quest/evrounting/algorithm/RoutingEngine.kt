package com.quest.evrounting.algorithm

import com.quest.evrounting.algorithm.domain.model.LineString
import com.quest.evrounting.algorithm.domain.model.Point
import com.quest.evrounting.algorithm.integration.adapter.GeohashAdapter
import com.quest.evrounting.algorithm.spatial.SpatialManager
import com.quest.evrounting.algorithm.spatial.config.Dependencies
import com.quest.evrounting.algorithm.spatial.index.GeoBitTrie

class RoutingEngine {
    val spatialManager = Dependencies.createSpatialManager(emptyList())
    fun findOptimalPath(
        start: Point,
        end: Point,
        batteryConsumptionRate: Double,
        batteryCapacity: Double,
        minBatteryPercent: Int,
        maxBatteryPercent: Int
    ): LineString? {
        return null
    }
    fun findByBacktracking(
        start: Point,
        end: Point,
        batteryConsumptionRate: Double,
        batteryCapacity: Double,
        minBatteryPercent: Int,
        maxBatteryPercent: Int
    ): LineString? {
        val shortestPath = spatialManager.findShortestPath(start, end)
        if (shortestPath != null){
            for (i in (minBatteryPercent until maxBatteryPercent)) {
                val dis = (batteryCapacity - i) * batteryConsumptionRate
                if (dis <= 0) continue
                val point = spatialManager.findPointAlongPath(shortestPath, dis)
                if(point != null){
                    val entities = spatialManager.findEntityInsideCircle((i * batteryConsumptionRate), point)
                    if(!entities.isEmpty()){

                    }
                }
            }
        }
        return null
    }
}