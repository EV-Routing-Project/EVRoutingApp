package com.quest.evrounting.algorithm.geospatial.model

sealed class BaseGeoEntity : GeoEntity {
    override fun equals(other: Any?): Boolean {
        if(this === other) return true
        if(other !is BaseGeoEntity) return false
        return this.id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}