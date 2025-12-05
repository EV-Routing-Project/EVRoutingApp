package com.quest.evrouting.algorithm.infrastructure.adapter.tool.routing

import com.quest.evrouting.algorithm.domain.model.LineString
import com.quest.evrouting.algorithm.domain.model.Point
import com.quest.evrouting.algorithm.domain.port.tool.routing.RoutingProviderPort
import com.quest.evrouting.algorithm.config.utils.RoutingProfile
import com.quest.evrouting.apiservice.mapbox.MapboxApiClient

class RoutingProviderAdapter(
    private val mapboxToken: String = "KEY"
) : RoutingProviderPort {
    override suspend fun findRoute(
        start: Point,
        end: Point,
        profile: RoutingProfile
    ): LineString? {
        val response = MapboxApiClient.directionsService.getDirections(
            profile = profile.value,
            coordinates = "${start.lon},${start.lat};${end.lon},${end.lat}",
            accessToken = mapboxToken
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