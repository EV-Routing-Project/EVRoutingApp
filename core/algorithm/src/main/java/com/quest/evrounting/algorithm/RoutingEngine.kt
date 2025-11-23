package com.quest.evrounting.algorithm

import com.quest.evrounting.algorithm.domain.model.LineString
import com.quest.evrounting.algorithm.domain.model.Point
import com.quest.evrounting.algorithm.integration.adapter.GeohashAdapter
import com.quest.evrounting.algorithm.spatial.SpatialManager
import com.quest.evrounting.algorithm.spatial.config.Dependencies
import com.quest.evrounting.algorithm.spatial.index.GeoBitTrie

class RoutingEngine {
    val spatialManager = Dependencies.createSpatialManager(emptyList())
    fun findOptimalPath(start: Point, end: Point): LineString? {
        return null
    }
    fun findByBacktracking(start: Point, end: Point): LineString? {
        val shortestPath = spatialManager.findShortestPath(start, end)
        val base = 50000
        val pin = 80
        if (shortestPath != null){
            for (i in 20 until 81) {
                val dis = (pin - i) * base
                if (dis <= 0) continue
                val point = spatialManager.findPointAlongPath(shortestPath, dis.toDouble())
                if(point != null){
                    val entities = spatialManager.findEntityInsideCircle((i * base).toDouble(), point)
                    if(!entities.isEmpty()){

                    }
                }
            }
        }
        return null
    }
}