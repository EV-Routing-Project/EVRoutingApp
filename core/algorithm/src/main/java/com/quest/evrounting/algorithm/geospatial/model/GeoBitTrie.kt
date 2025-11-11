package com.quest.evrounting.algorithm.geospatial.model

import com.quest.evrounting.algorithm.geospatial.model.GeoNode

class GeoBitTrie {
    val root: GeoNode = GeoNode.createRoot();
    val mapEntities: MutableMap<Long, BaseGeoEntity> = mutableMapOf()
    fun insert(){

    }
    fun delete(){

    }
    fun count(): Int = root.count()
}