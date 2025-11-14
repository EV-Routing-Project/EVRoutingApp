package com.quest.evrounting.apiservice.ocm.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Operator(
    @SerialName("ID") val id: Int,
    @SerialName("Title") val title: String,
    @SerialName("WebsiteURL") val websiteURL: String? = null
)