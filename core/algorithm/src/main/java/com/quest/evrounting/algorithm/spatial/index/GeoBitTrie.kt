package com.quest.evrounting.algorithm.spatial.index

import com.quest.evrounting.algorithm.domain.model.Geohash
import com.quest.evrounting.algorithm.domain.model.Point
import com.quest.evrounting.algorithm.domain.port.GeohashPort
import com.quest.evrounting.algorithm.spatial.entity.BaseEntity
import com.quest.evrounting.algorithm.utils.GeoUtils
import kotlin.math.max

class GeoBitTrie(val geohashTools: GeohashPort) : IEntityManager {
    private val root: GeoNode = GeoNode.createRoot()
    private val mapEntities: MutableMap<BaseEntity, GeoNode> = mutableMapOf()
    fun count(): Int = root.count()
    override fun insert(entity: BaseEntity){
        var node: GeoNode? = root
        while(node != null && !node.isLeaf){
            val idx = node.getIndexOfEntity(entity)
            node.cnt[idx]++
            if(node.children != null){
                if(node.children[idx] == null){
                    node.children[idx] = GeoNode.createFrom(node)
                }
            }
            node = node.children?.get(idx)
        }
        if(node != null && node.isLeaf){
            val idx = node.getIndexOfEntity(entity)
            node.cnt[idx]++
            mapEntities[entity] = node
            node.entities?.add(entity)
            if(entity.status) node.onlEntities?.add(entity)
        }
    }

    override fun delete(entity: BaseEntity) {
        var node: GeoNode? = mapEntities.get(entity)
        if(node != null && node.isLeaf) {
            node.entities?.remove(entity)
            node.onlEntities?.remove(entity)
        }
        while(node != null) {
            val idx = node.getIndexOfEntity(entity)
            node.cnt[idx]--
            node = node.parent
        }
        mapEntities.remove(entity)
    }

    override fun offEntity(entity: BaseEntity){
        if(!entity.status) return
        var node: GeoNode? = mapEntities.get(entity)
        if(node != null && node.isLeaf){
            node.onlEntities?.remove(entity)
        }
        while(node != null){
            val idx = node.getIndexOfEntity(entity)
            node.cnt[idx]--
            node = node.parent
        }
    }
    override fun onlEntity(entity: BaseEntity){
        if(entity.status) return
        var node: GeoNode? = mapEntities.get(entity)
        if(node != null && node.isLeaf){
            node.onlEntities?.add(entity)
        }
        while(node != null){
            val idx = node.getIndexOfEntity(entity)
            node.cnt[idx]++
            node = node.parent
        }
    }

    //<editor-fold desc="Query">
    /**
     * radius is in meters
     */
    fun getLevelOfRadius(radius: Double, point: Point) : Int {
        for(i in (GeoUtils.MAX_LEVEL downTo GeoUtils.MIN_LEVEL)){
            val latSize = geohashTools.getLatSize(i)
            val lonSize = geohashTools.getLonSize(i, point)
            if(2 * radius >= lonSize && lonSize >= radius){
                if(2 * radius >= latSize){
                    return i
                } else {
                    return max(i - 1, GeoUtils.MIN_LEVEL)
                }
            }
        }
        return GeoUtils.MIN_LEVEL
    }

    override fun filter(radius: Double, center: Point): List<BaseEntity> {
        val level = getLevelOfRadius(radius,center)
        val listEntity = mutableListOf<BaseEntity>()
        val listGeohash = geohashTools.getGeohashGridForPoint(center, level)
        for(geohash in listGeohash){
            val hash = geohashTools.adjustGeohashPrecision(geohash, GeoUtils.SIGNIFICANT_BITS)
            val node = getNodeFromGeohash(hash, level)
            if(node != null){
                listEntity.addAll(getOnlEntityFromNode(node))
            }
        }
        return listEntity
    }

    override fun count(radius: Double, center: Point): Int {
        val level = getLevelOfRadius(radius, center)
        var count = 0
        val listGeohash = geohashTools.getGeohashGridForPoint(center, level)
        for(geohash in listGeohash){
            val hash = geohashTools.adjustGeohashPrecision(geohash, GeoUtils.SIGNIFICANT_BITS)
            val node = getNodeFromGeohash(hash, level)
            count += countOnlEntityFromNode(node)
        }
        return count
    }

    private fun getNodeFromGeohash(geohash: Geohash, level: Int): GeoNode? {
        var node: GeoNode? = root
        while(node != null && node.level != level){
            val idx = node.getIndexOfGeohash(geohash)
            node = node.children?.get(idx)
        }
        return node
    }

    private fun getOnlEntityFromNode(node: GeoNode?) : List<BaseEntity> {
        val listEntity = mutableListOf<BaseEntity>()
        getOnlEntityFromNode(node, listEntity)
        return listEntity
    }

    private fun getOnlEntityFromNode(node: GeoNode?, listEntity: MutableList<BaseEntity>){
        if(node == null) return
        if(node.isLeaf){
            if(node.onlEntities != null){
                listEntity.addAll(node.onlEntities)
            }
            return
        }
        if(node.children != null) {
            for (count in node.cnt.withIndex()) {
                if (count.value > 0 && node.children[count.index] != null){
                    getOnlEntityFromNode(node.children[count.index], listEntity)
                }
            }
        }
    }

    private fun countOnlEntityFromNode(node: GeoNode?) : Int {
        if(node == null) return 0
        return node.count()
    }

    //</editor-fold>

    //<editor-fold desc="Class GeoNode">
    private class GeoNode(
        val parent: GeoNode? = null,
        val level: Int,
        val isLeaf: Boolean,
        val children: Array<GeoNode?>? = arrayOfNulls(2),
        val cnt: Array<Int> = arrayOf(0, 0),
        val entities: MutableSet<BaseEntity>? = null,
        val onlEntities: MutableSet<BaseEntity>? = null
    ) {
        companion object {
            fun createRoot() : GeoNode {
                return GeoNode(
                    null,
                    GeoUtils.MAX_LEVEL,
                    false,
                )
            }

            fun createFrom(parent: GeoNode) : GeoNode? {
                if(parent.level == GeoUtils.MIN_LEVEL) return null
                val level = parent.level - 1
                var entities: MutableSet<BaseEntity>? = null
                var onlEntities: MutableSet<BaseEntity>? = null
                var children: Array<GeoNode?>? = arrayOfNulls(2)
                if(level == GeoUtils.MIN_LEVEL) {
                    entities = mutableSetOf()
                    onlEntities = mutableSetOf()
                    children = null
                }
                return GeoNode(
                    parent,
                    level,
                    isLeaf = (level == GeoUtils.MIN_LEVEL),
                    entities = entities,
                    children = children,
                    onlEntities = onlEntities
                )
            }
        }
        fun count(): Int = cnt.sum()
        fun getIndexOfEntity(entity: BaseEntity): Int {
            return getIndexOfGeohash(entity.geohash)
        }

        fun getIndexOfGeohash(geohash: Geohash): Int {
            val idx: Long = (geohash.value shr this.level) and 1
            return idx.toInt()
        }
    }
    //</editor-fold>
}