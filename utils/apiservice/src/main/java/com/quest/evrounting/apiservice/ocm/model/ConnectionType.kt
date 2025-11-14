package com.quest.evrounting.apiservice.ocm.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ConnectionType (
    @SerialName("ID") val id: Int,
    @SerialName("Title") val title: String,
    @SerialName("FormalName") val formalName: String? = null
)