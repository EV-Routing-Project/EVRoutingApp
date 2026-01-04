package com.quest.evrouting.phone.data.remote.api.backendServer.directions.dto.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RouteDto(
    @SerialName("geometry") val geometry: String,
    @SerialName("distance") val distance: Double,
    @SerialName("duration") val duration: Double,
    @SerialName("chargePoints") val chargePoints: List<ChargePointWrapperDto>
)


//@Serializable
//data class ChargePointWrapperDto(
//    // Thuộc tính "coordinate" sẽ lấy giá trị từ key "chargePoint" trong JSON
//    @SerialName("chargePoint") val coordinate: ChargePointDto
//)
//
//@Serializable
//data class ChargePointDto(
//    @SerialName("id") val id: Int
//)