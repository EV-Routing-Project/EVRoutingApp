package com.quest.evrounting.algorithm.spatial.entity

import com.quest.evrounting.algorithm.domain.model.Geohash
import com.quest.evrounting.algorithm.domain.model.Point

sealed class BaseEntity(
    val id: String,
    val point: Point,
    val geohash: Geohash,
    val status: Boolean
) : IEntity {
    override fun equals(other: Any?): Boolean {
        if(this === other) return true
        if(other !is BaseEntity) return false
        return this.id == other.id
    }
    override fun hashCode(): Int {
        return id.hashCode()
    }
}