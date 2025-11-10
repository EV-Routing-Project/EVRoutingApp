package com.quest.evrounting.algorithm.geospatial.model

class GeoLeaf<T> (
    val geohash: Long,
    val entity: T,
    val node: GeoNode<T>
) {

}