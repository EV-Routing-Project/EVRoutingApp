package com.quest.evrounting.algorithm.spatial.index

import com.quest.evrounting.algorithm.domain.model.Point
import com.quest.evrounting.algorithm.spatial.entity.BaseEntity

interface IEntityManager {
    fun insert(entity: BaseEntity)

    fun delete(entity: BaseEntity)

    fun offEntity(entity: BaseEntity)

    fun onlEntity(entity: BaseEntity)

    fun filter(radius: Double, center: Point) : List<BaseEntity>

    fun count(radius: Double, center: Point) : Int
}