package com.quest.evrounting.algorithm.geospatial.model

interface GeoEntity {
    val geohash: Long
    val status: Boolean
    var node: GeoNode?
}