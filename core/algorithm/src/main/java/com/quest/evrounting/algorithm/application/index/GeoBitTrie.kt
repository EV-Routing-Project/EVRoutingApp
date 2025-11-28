package com.quest.evrounting.algorithm.application.index

import com.quest.evrounting.algorithm.domain.model.Geohash
import com.quest.evrounting.algorithm.domain.model.Point
import com.quest.evrounting.algorithm.domain.port.GeohashPort
import com.quest.evrounting.algorithm.domain.port.GeometryPort
import com.quest.evrounting.algorithm.domain.modelport.IStation
import com.quest.evrounting.algorithm.utils.GeoUtils
import kotlin.math.max

class GeoBitTrie(
    val geohashTool: GeohashPort,
    val geometryTool: GeometryPort
) : IStationManager {
    private val root: GeoNode = GeoNode.createRoot()
    private val mapStations: MutableMap<IStation, GeoNode> = mutableMapOf()
    fun count(): Int = root.count()
    override fun insert(station: IStation){
        var node: GeoNode? = root
        while(node != null && !node.isLeaf){
            val idx = node.getIndexOfStation(station)
            node.cnt[idx]++
            if(node.children != null){
                if(node.children[idx] == null){
                    node.children[idx] = GeoNode.createFrom(node)
                }
            }
            node = node.children?.get(idx)
        }
        if(node != null && node.isLeaf){
            val idx = node.getIndexOfStation(station)
            node.cnt[idx]++
            mapStations[station] = node
            node.stations?.add(station)
            if(station.status) node.onlStations?.add(station)
        }
    }

    override fun delete(station: IStation) {
        var node: GeoNode? = mapStations.get(station)
        if(node != null && node.isLeaf) {
            node.stations?.remove(station)
            node.onlStations?.remove(station)
        }
        while(node != null) {
            val idx = node.getIndexOfStation(station)
            node.cnt[idx]--
            node = node.parent
        }
        mapStations.remove(station)
    }

    fun offStation(station: IStation){
        if(!station.status) return
        var node: GeoNode? = mapStations.get(station)
        if(node != null && node.isLeaf){
            node.onlStations?.remove(station)
        }
        while(node != null){
            val idx = node.getIndexOfStation(station)
            node.cnt[idx]--
            node = node.parent
        }
    }
    fun onlStation(station: IStation){
        if(station.status) return
        var node: GeoNode? = mapStations.get(station)
        if(node != null && node.isLeaf){
            node.onlStations?.add(station)
        }
        while(node != null){
            val idx = node.getIndexOfStation(station)
            node.cnt[idx]++
            node = node.parent
        }
    }

    override fun update(station: IStation) {
        if(station.status){
            onlStation(station)
        } else {
            offStation(station)
        }
    }

    //<editor-fold desc="Query">
    /**
     * radius is in meters
     */
    fun getLevelOfRadius(radius: Double, point: Point) : Int {
        for(i in (GeoUtils.MAX_LEVEL downTo GeoUtils.MIN_LEVEL)){
            val latSize = geohashTool.getLatSize(i)
            val lonSize = geohashTool.getLonSize(i, point)
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

    fun filter(radius: Double, center: Point): List<IStation> {
        val level = getLevelOfRadius(radius,center)
        val listStation = mutableListOf<IStation>()
        val listGeohash = geohashTool.getGeohashGridForPoint(center, level)
        for(geohash in listGeohash){
            val hash = geohashTool.adjustGeohashPrecision(geohash, GeoUtils.SIGNIFICANT_BITS)
            val node = getNodeFromGeohash(hash, level)
            if(node != null){
                listStation.addAll(getOnlStationsFromNode(node))
            }
        }
        return listStation
    }

    override fun findStationsInsideCircle(radius: Double, point: Point): List<IStation> {
        if(estimatedCount(radius, point) == 0) return emptyList()
        val stationFilterMap = filter(radius, point).associateBy { it.point }
        return geometryTool.findPointsInsideCircle(
            stationFilterMap.keys.toList(),
            radius,
            point
        ).mapNotNull {
                point -> stationFilterMap[point]
        }
    }

    override fun estimatedCount(radius: Double, center: Point): Int {
        val level = getLevelOfRadius(radius, center)
        var count = 0
        val listGeohash = geohashTool.getGeohashGridForPoint(center, level)
        for(geohash in listGeohash){
            val hash = geohashTool.adjustGeohashPrecision(geohash, GeoUtils.SIGNIFICANT_BITS)
            val node = getNodeFromGeohash(hash, level)
            count += countOnlStationFromNode(node)
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

    private fun getOnlStationsFromNode(node: GeoNode?) : List<IStation> {
        val listStation = mutableListOf<IStation>()
        getOnlStationsFromNode(node, listStation)
        return listStation
    }

    private fun getOnlStationsFromNode(node: GeoNode?, listStation: MutableList<IStation>){
        if(node == null) return
        if(node.isLeaf){
            if(node.onlStations != null){
                listStation.addAll(node.onlStations)
            }
            return
        }
        if(node.children != null) {
            for (count in node.cnt.withIndex()) {
                if (count.value > 0 && node.children[count.index] != null){
                    getOnlStationsFromNode(node.children[count.index], listStation)
                }
            }
        }
    }

    private fun countOnlStationFromNode(node: GeoNode?) : Int {
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
        val stations: MutableSet<IStation>? = null,
        val onlStations: MutableSet<IStation>? = null
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
                var stations: MutableSet<IStation>? = null
                var onlStations: MutableSet<IStation>? = null
                var children: Array<GeoNode?>? = arrayOfNulls(2)
                if(level == GeoUtils.MIN_LEVEL) {
                    stations = mutableSetOf()
                    onlStations = mutableSetOf()
                    children = null
                }
                return GeoNode(
                    parent,
                    level,
                    isLeaf = (level == GeoUtils.MIN_LEVEL),
                    stations = stations,
                    children = children,
                    onlStations = onlStations
                )
            }
        }
        fun count(): Int = cnt.sum()
        fun getIndexOfStation(station: IStation): Int {
            return getIndexOfGeohash(station.geohash)
        }

        fun getIndexOfGeohash(geohash: Geohash): Int {
            val idx: Long = (geohash.value shr this.level) and 1
            return idx.toInt()
        }
    }
    //</editor-fold>
}