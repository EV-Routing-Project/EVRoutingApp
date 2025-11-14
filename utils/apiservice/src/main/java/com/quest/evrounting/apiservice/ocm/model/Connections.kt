package com.quest.evrounting.apiservice.ocm.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Connections(
    @SerialName("ID") val id: Int,
    @SerialName("ConnectionType") val connectionType: ConnectionType,
    @SerialName("PowerKW") val powerKW: Double? = null,
    @SerialName("CurrentType") val currentType: CurrentType? = null,
    @SerialName("Quantity") val quantity: Int? = null
)
