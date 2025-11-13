package com.quest.evrounting.algorithm.geospatial.model

import com.quest.evrounting.algorithm.geospatial.utils.GeoUtils
import com.quest.evrounting.libservice.geometry.ServiceKit
import kotlin.math.max

class GeoBitTrie {
    private val root: GeoNode = GeoNode.createRoot()
    private val geohashTools = ServiceKit.geohashService
    private val mapEntities: MutableMap<BaseGeoEntity, GeoNode> = mutableMapOf()
    fun count(): Int = root.count()
    fun insert(entity: BaseGeoEntity){
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

    fun delete(entity: BaseGeoEntity) {
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

    fun offEntity(entity: BaseGeoEntity){
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
    fun onlEntity(entity: BaseGeoEntity){
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

    /**
     * radius is in meters
     */
    fun getLevelOfRadius(radius: Double, lat: Double) : Int {
        for(i in (GeoUtils.MAX_LEVEL downTo GeoUtils.MIN_LEVEL)){
            val latSize = geohashTools.getLatSize(i)
            val lonSize = geohashTools.getLonSize(i, lat)
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

    fun filter(radius: Double, lon: Double, lat: Double): List<BaseGeoEntity> {
        val level = getLevelOfRadius(radius,lat)
        val listEntity = mutableListOf<BaseGeoEntity>()
        val listGeohash = geohashTools.getGeohashGridForPoint(lon, lat, level)
        for(geohash in listGeohash){
            val hash = geohashTools.adjustGeohashPrecision(geohash, GeoUtils.SIGNIFICANT_BITS)
            val node = getNodeFromGeohash(hash.value, level)
            if(node != null){
                listEntity.addAll(getOnlEntityFromNode(node))
            }
        }
        return listEntity
    }

    fun count(radius: Double, lon: Double, lat: Double): Int {
        val level = getLevelOfRadius(radius,lat)
        var count = 0
        val listGeohash = geohashTools.getGeohashGridForPoint(lon, lat, level)
        for(geohash in listGeohash){
            val hash = geohashTools.adjustGeohashPrecision(geohash, GeoUtils.SIGNIFICANT_BITS)
            val node = getNodeFromGeohash(hash.value, level)
            count += countOnlEntityFromNode(node)
        }
        return count
    }

    private fun getNodeFromGeohash(geohash: Long, level: Int): GeoNode? {
        var node: GeoNode? = root
        while(node != null && node.level != level){
            val idx = node.getIndexOfGeohash(geohash)
            node = node.children?.get(idx)
        }
        return node
    }

    private fun getOnlEntityFromNode(node: GeoNode?) : List<BaseGeoEntity> {
        val listEntity = mutableListOf<BaseGeoEntity>()
        getOnlEntityFromNode(node, listEntity)
        return listEntity
    }

    private fun getOnlEntityFromNode(node: GeoNode?, listEntity: MutableList<BaseGeoEntity>){
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

    //<editor-fold desc="Class GeoNode">
    private class GeoNode(
        val parent: GeoNode? = null,
        val level: Int,
        val isLeaf: Boolean,
        val children: Array<GeoNode?>? = arrayOfNulls(2),
        val cnt: Array<Int> = arrayOf(0, 0),
        val entities: MutableSet<BaseGeoEntity>? = null,
        val onlEntities: MutableSet<BaseGeoEntity>? = null
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
                var entities: MutableSet<BaseGeoEntity>? = null
                var onlEntities: MutableSet<BaseGeoEntity>? = null
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
        fun getIndexOfEntity(entity: BaseGeoEntity): Int {
            return getIndexOfGeohash(entity.geohash)
        }

        fun getIndexOfGeohash(geohash: Long): Int {
            val idx: Long = (geohash shr this.level) and 1
            return idx.toInt()
        }
    }
    //</editor-fold>
}