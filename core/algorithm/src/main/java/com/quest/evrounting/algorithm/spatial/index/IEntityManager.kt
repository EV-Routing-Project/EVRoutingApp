package com.quest.evrounting.algorithm.spatial.index

import com.quest.evrounting.algorithm.domain.model.Point
import com.quest.evrounting.algorithm.spatial.entity.BaseEntity

interface IEntityManager {
    fun insert(entity: BaseEntity)

    fun delete(entity: BaseEntity)

    fun update(entity: BaseEntity)

    fun findEntityInsideCircle(radius: Double, point: Point) : List<BaseEntity>

    fun estimatedCount(radius: Double, center: Point) : Int
}