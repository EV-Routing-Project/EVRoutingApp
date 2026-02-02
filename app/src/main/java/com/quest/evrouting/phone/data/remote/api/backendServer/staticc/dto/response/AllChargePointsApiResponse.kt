package com.quest.evrouting.phone.data.remote.api.backendServer.staticc.dto.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AllChargePointsApiResponse(
    @SerialName("list_poi") val data : List<POI>
)
