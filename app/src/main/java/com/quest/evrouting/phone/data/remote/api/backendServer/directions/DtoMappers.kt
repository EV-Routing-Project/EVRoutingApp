package com.quest.evrouting.phone.data.remote.api.backendServer.directions

import com.mapbox.geojson.LineString
import com.quest.evrouting.phone.data.remote.api.backendServer.directions.dto.response.RouteDto
import com.quest.evrouting.phone.domain.model.Route

fun RouteDto.toRoute(): Route {
    val decodedGeometry = LineString.fromPolyline(this.geometry, 6).coordinates()

    if (decodedGeometry.isEmpty()) {
        throw IllegalArgumentException("Không thể giải mã geometry từ API: ${this.geometry}")
    }

    val chargePointIds = this.chargePoints.map { chargePointWrapper ->
        chargePointWrapper.chargePointInfo.id
    }

    return Route(
        geometry = decodedGeometry,
        duration = this.duration,
        distance = this.distance,
        chargePointsOnRoute = chargePointIds
    )
}