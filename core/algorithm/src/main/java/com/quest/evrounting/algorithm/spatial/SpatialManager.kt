package com.quest.evrounting.algorithm.spatial

import com.quest.evrounting.algorithm.domain.model.LineString
import com.quest.evrounting.algorithm.domain.model.Point
import com.quest.evrounting.algorithm.spatial.cache.ICacheManager
import com.quest.evrounting.algorithm.spatial.entity.BaseEntity
import com.quest.evrounting.algorithm.spatial.index.IEntityManager
import com.quest.evrounting.algorithm.spatial.routing.IRoutingManager

class SpatialManager (
    val entityManager: IEntityManager,
    val cacheManager: ICacheManager,
    val routingManager: IRoutingManager,
    entities: List<BaseEntity>
) {
    init {
        for(entity in entities){
            entityManager.insert(entity)
        }
    }

    fun update(entities: List<BaseEntity>) {
        for(entity in entities){
            entityManager.update(entity)
        }
    }

    fun delete(entity: BaseEntity){
        entityManager.delete(entity)
    }

    fun delete(entities: List<BaseEntity>){
        for(entity in entities) {
            delete(entity)
        }
    }

    fun findEntityInsideCircle(radius: Double, point: Point) : List<BaseEntity> {
        return entityManager.findEntityInsideCircle(radius, point)
    }

    fun estimatedCount(radius: Double, center: Point) : Int {
        return entityManager.estimatedCount(radius, center)
    }

    fun findShortestPath(start: Point, end: Point): LineString? {
        return routingManager.findShortestPath(start, end)
    }

    fun findShortestPath(start: BaseEntity, end: BaseEntity) : LineString? {
        var shortestPath = cacheManager.request(start, end)
        if(shortestPath == null){
            shortestPath = findShortestPath(start.point, end.point)
            if(shortestPath != null){
                cacheManager.insert(start, end, shortestPath)
            }
        }
        return shortestPath
    }

    fun findPathAlongPath(
        path: LineString,
        distanceAlong: Double
    ): LineString? {
        return routingManager.findPathAlongPath(path, distanceAlong)
    }

    fun findPointAlongPath(
        path: LineString,
        distanceAlong: Double
    ): Point? {
        return routingManager.findPointAlongPath(path, distanceAlong)
    }
}