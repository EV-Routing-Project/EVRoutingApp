package com.quest.evrounting.algorithm.spatial

import com.quest.evrounting.algorithm.spatial.model.BaseGeoEntity
import com.quest.evrounting.algorithm.spatial.model.GeoBitTrie
import com.quest.evrounting.libservice.geometry.ServiceKit
import com.quest.evrounting.libservice.geometry.domain.model.Point

object GeoManager {
    val filtering: GeoBitTrie = GeoBitTrie()
    val geometryTools = ServiceKit.geometryService

    fun initFiltering(listEntities: List<BaseGeoEntity>){
        filtering.lateInit(listEntities)
    }

    fun updateEntities(listEntities: List<BaseGeoEntity>){
        for(entity in listEntities){
            if(entity.status) filtering.onlEntity(entity)
            else filtering.offEntity(entity)
        }
    }

    fun queryEntitiesOfRadius(radius: Double, lon: Double, lat: Double): List<BaseGeoEntity> {
        val candidateEntities = filtering.filter(radius, lon, lat)
        if(candidateEntities.isEmpty()) return emptyList()
        val entitiesByPoint = candidateEntities.groupBy { Point(it.lon, it.lat) }
        val pointsInCircle = geometryTools.findPointsInsideCircle(
            points=entitiesByPoint.keys.toList(),
            radius=radius,
            cenLat=lat,
            cenLon=lon
        )
        return pointsInCircle.flatMap { point -> entitiesByPoint[point] ?: emptyList() }
    }
}