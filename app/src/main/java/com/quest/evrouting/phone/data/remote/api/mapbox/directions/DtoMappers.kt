//package com.quest.evrouting.phone.data.remote.api.mapbox.directions
//
//import com.quest.evrouting.phone.data.remote.api.mapbox.directions.dto.RouteDto
//import com.quest.evrouting.phone.domain.model.Route
//
///**
// * Chuyển đổi từ RouteDto (Data Layer) sang Route (Domain Layer).
// */
//fun RouteDto.toRoute(): Route {
//    return Route(
//        geometry = this.geometry,
//        distance = this.distance,
//        duration = this.duration
//    )
//}
