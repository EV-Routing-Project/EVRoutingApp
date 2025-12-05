package com.quest.evrouting.apiservice.ocm.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CurrentType(
    @SerialName("ID") val id: Int,
    @SerialName("Title") val title: String,
)