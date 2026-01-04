package com.quest.evrouting.phone.data.remote.api.backendServer.directions.dto.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChargePointWrapperDto(
    // Thuộc tính "coordinate" sẽ lấy giá trị từ key "chargePoint" trong JSON
    @SerialName("chargePoint") val chargePointInfo: ChargePointDto
)