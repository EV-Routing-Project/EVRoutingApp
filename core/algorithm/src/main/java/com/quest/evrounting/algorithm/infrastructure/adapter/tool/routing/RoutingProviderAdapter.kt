package com.quest.evrounting.algorithm.infrastructure.adapter.tool.routing

import com.quest.evrounting.algorithm.domain.model.LineString
import com.quest.evrounting.algorithm.domain.model.Point
import com.quest.evrounting.algorithm.domain.port.tool.routing.RoutingProviderPort
import com.quest.evrounting.algorithm.config.utils.RoutingProfile
import com.quest.evrounting.apiservice.mapbox.MapboxApiClient

class RoutingProviderAdapter : RoutingProviderPort {
    override suspend fun findRoute(
        start: Point,
        end: Point,
        profile: RoutingProfile
    ): LineString? {
        val response = MapboxApiClient.directionsService.getDirections(
            profile = profile.value,
            coordinates = "${start.lon},${start.lat};${end.lon},${end.lat}",
            accessToken = "Key"
        )
        if(response.isSuccessful){
            val directions = response.body()
            val route = directions?.routes?.firstOrNull()
            val coordinate = route?.geometry?.coordinates
            if(coordinate != null)  return LineString(
                coordinate.map { Point(it[0], it[1]) },
                route.distance
            )
        }
        return null
    }
}