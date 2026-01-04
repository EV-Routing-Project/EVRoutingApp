package com.quest.evrouting.apiservice.mapbox.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Geometry(
    @SerialName("coordinates") val coordinates: List<List<Double>>,
    @SerialName("type") val type: String
)
