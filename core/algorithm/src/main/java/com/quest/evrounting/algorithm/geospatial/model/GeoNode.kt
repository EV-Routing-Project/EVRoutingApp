package com.quest.evrounting.algorithm.geospatial.model

import com.quest.evrounting.algorithm.geospatial.utils.GeoUtils

class GeoNode(
    val parent: GeoNode? = null,
    val level: Int,
    val isLeaf: Boolean,
    val children: Array<GeoNode?>? = arrayOfNulls(2),
    val cnt: Array<Int> = arrayOf(0, 0),
    val entities: MutableSet<BaseGeoEntity>? = null
) {
    companion object {
        fun createRoot() : GeoNode {
            return GeoNode(
                null,
                GeoUtils.MAX_LEVEL,
                false,
                entities = mutableSetOf()
            )
        }

        fun createFrom(parent: GeoNode) : GeoNode? {
            if(parent.level == GeoUtils.MIN_LEVEL) return null
            val level = parent.level - 1
            var entities: MutableSet<BaseGeoEntity>? = null
            var children: Array<GeoNode?>? = arrayOfNulls(2)
            if(level == GeoUtils.MIN_LEVEL) {
                entities = mutableSetOf()
                children = null
            }
            return GeoNode(
                parent,
                level,
                isLeaf = (level == GeoUtils.MIN_LEVEL),
                entities = entities,
                children = children
            )
        }
    }
    fun count(): Int = cnt.sum()
    fun getIndexOfEntity(entity: BaseGeoEntity): Int {
        val idx: Long = (entity.geohash shr this.level) and 1
        return idx.toInt()
    }
}