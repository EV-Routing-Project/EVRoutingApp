package com.quest.evrounting.algorithm.geospatial.model

sealed class BaseGeoEntity : GeoEntity {
    override var node: GeoNode? = null
        set(value) {
            if(field == null){
                field = value
            }
        }
}