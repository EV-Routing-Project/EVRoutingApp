package com.quest.evrounting.algorithm.geospatial.model


interface GeoEntity {
    val id: String
    val geohash: Long
    val status: Boolean
}