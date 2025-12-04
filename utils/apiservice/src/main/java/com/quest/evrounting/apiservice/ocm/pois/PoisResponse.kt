package com.quest.evrounting.apiservice.ocm.pois

import com.quest.evrounting.apiservice.ocm.model.AddressInfo
import com.quest.evrounting.apiservice.ocm.model.Connections
import com.quest.evrounting.apiservice.ocm.model.Operator
import com.quest.evrounting.apiservice.ocm.model.UsageType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PoisResponse(
    @SerialName("ID") val id: Int,
    @SerialName("UUID") val uuid: String,
    @SerialName("AddressInfo") val addressInfo: AddressInfo,
    @SerialName("OperatorInfo") val operatorInfo: Operator? = null,
    @SerialName("UsageType") val usageType: UsageType? = null,
    @SerialName("Connections") val connections: List<Connections>,
)