package com.quest.evrounting.algorithm.spatial

import com.quest.evrounting.algorithm.domain.model.Point
import com.quest.evrounting.algorithm.spatial.cache.ICacheManager
import com.quest.evrounting.algorithm.spatial.entity.BaseEntity
import com.quest.evrounting.algorithm.spatial.index.IEntityManager

class SpatialManager (
    val entityManager: IEntityManager,
    val cacheManager: ICacheManager,
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
}