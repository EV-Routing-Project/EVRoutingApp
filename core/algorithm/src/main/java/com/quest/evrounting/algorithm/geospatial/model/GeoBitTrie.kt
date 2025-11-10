package com.quest.evrounting.algorithm.geospatial.model

import com.quest.evrounting.algorithm.geospatial.model.GeoNode

class GeoBitTrie {
    val root: GeoNode = GeoNode();
    val mapGeoStation: MutableMap<GeoStation, GeoNode?> = mutableMapOf()
    fun insert(station: GeoStation){
        mapGeoStation.put(station, root.insert(station))
    }
    fun delete(station: GeoStation){
        val node = mapGeoStation.get(station)
        node?.delete(station)
    }
    fun count(): Int = root.count()
}