package com.quest.evrounting.apiservice.ocm.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Country(
    @SerialName("ID") val id: Int,
    @SerialName("Title") val title: String,
    @SerialName("ISOCode") val isoCode: String
)