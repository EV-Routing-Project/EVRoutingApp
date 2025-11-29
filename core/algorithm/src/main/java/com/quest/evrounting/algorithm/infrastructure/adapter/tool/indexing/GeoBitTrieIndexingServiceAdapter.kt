package com.quest.evrounting.algorithm.infrastructure.adapter.tool.indexing

import com.quest.evrounting.algorithm.domain.model.Geohash
import com.quest.evrounting.algorithm.domain.model.Point
import com.quest.evrounting.algorithm.domain.port.tool.code.GeohashProviderPort
import com.quest.evrounting.algorithm.domain.port.tool.geometry.GeometryProviderPort
import com.quest.evrounting.algorithm.domain.port.tool.indexing.IndexingServicePort
import com.quest.evrounting.algorithm.domain.port.model.StationPort
import com.quest.evrounting.algorithm.config.utils.GeoUtils
import kotlin.math.max

class GeoBitTrieIndexingServiceAdapter (
    val geohashTool: GeohashProviderPort,
    val geometryTool: GeometryProviderPort
) : IndexingServicePort {
    private val root: Node = Node.createRoot()
    private val mapStations: MutableMap<StationPort, Node> = mutableMapOf()
    fun count(): Int = root.count()
    override fun insert(station: StationPort){
        var node: Node? = root
        while(node != null && !node.isLeaf){
            val idx = node.getIndexOfStation(station)
            node.cnt[idx]++
            if(node.children != null){
                if(node.children[idx] == null){
                    node.children[idx] = Node.createFrom(node)
                }
            }
            node = node.children?.get(idx)
        }
        if(node != null && node.isLeaf){
            val idx = node.getIndexOfStation(station)
            node.cnt[idx]++
            mapStations[station] = node
            node.stations?.add(station)
            if(station.getStatus()) node.onlStations?.add(station)
        }
    }

    override fun delete(station: StationPort) {
        var node: Node? = mapStations.get(station)
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

    fun offStation(station: StationPort){
        if(!station.getStatus()) return
        var node: Node? = mapStations.get(station)
        if(node != null && node.isLeaf){
            node.onlStations?.remove(station)
        }
        while(node != null){
            val idx = node.getIndexOfStation(station)
            node.cnt[idx]--
            node = node.parent
        }
    }
    fun onlStation(station: StationPort){
        if(station.getStatus()) return
        var node: Node? = mapStations.get(station)
        if(node != null && node.isLeaf){
            node.onlStations?.add(station)
        }
        while(node != null){
            val idx = node.getIndexOfStation(station)
            node.cnt[idx]++
            node = node.parent
        }
    }

    override fun update(station: StationPort) {
        if(station.getStatus()){
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

    fun filter(radius: Double, center: Point): List<StationPort> {
        val level = getLevelOfRadius(radius,center)
        val listStation = mutableListOf<StationPort>()
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

    override fun findStationsInsideCircle(radius: Double, point: Point): List<StationPort> {
        if(estimatedCount(radius, point) == 0) return emptyList()
        val stationFilterMap = filter(radius, point).associateBy { it.getLocation() }
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

    private fun getNodeFromGeohash(geohash: Geohash, level: Int): Node? {
        var node: Node? = root
        while(node != null && node.level != level){
            val idx = node.getIndexOfGeohash(geohash)
            node = node.children?.get(idx)
        }
        return node
    }

    private fun getOnlStationsFromNode(node: Node?) : List<StationPort> {
        val listStation = mutableListOf<StationPort>()
        getOnlStationsFromNode(node, listStation)
        return listStation
    }

    private fun getOnlStationsFromNode(node: Node?, listStation: MutableList<StationPort>){
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

    private fun countOnlStationFromNode(node: Node?) : Int {
        if(node == null) return 0
        return node.count()
    }

    //</editor-fold>

    //<editor-fold desc="Class Node">
    private class Node(
        val parent: Node? = null,
        val level: Int,
        val isLeaf: Boolean,
        val children: Array<Node?>? = arrayOfNulls(2),
        val cnt: Array<Int> = arrayOf(0, 0),
        val stations: MutableSet<StationPort>? = null,
        val onlStations: MutableSet<StationPort>? = null
    ) {
        companion object {
            fun createRoot() : Node {
                return Node(
                    null,
                    GeoUtils.MAX_LEVEL,
                    false,
                )
            }

            fun createFrom(parent: Node) : Node? {
                if(parent.level == GeoUtils.MIN_LEVEL) return null
                val level = parent.level - 1
                val isNewLeaf = (level == GeoUtils.MIN_LEVEL)
                return Node(
                    parent,
                    level,
                    isLeaf = isNewLeaf,
                    stations = if(isNewLeaf) mutableSetOf() else null,
                    children = if(isNewLeaf) null else arrayOfNulls(2),
                    onlStations = if(isNewLeaf) mutableSetOf() else null
                )
            }
        }
        fun count(): Int = cnt.sum()
        fun getIndexOfStation(station: StationPort): Int {
            return getIndexOfGeohash(station.getGeohash())
        }

        fun getIndexOfGeohash(geohash: Geohash): Int {
            val idx: Long = (geohash.value shr this.level) and 1
            return idx.toInt()
        }
    }
    //</editor-fold>
}