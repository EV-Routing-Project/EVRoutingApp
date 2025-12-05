package com.quest.evrouting.apiservice.ocm.pois

import com.quest.evrouting.apiservice.ocm.model.AddressInfo
import com.quest.evrouting.apiservice.ocm.model.Connections
import com.quest.evrouting.apiservice.ocm.model.Operator
import com.quest.evrouting.apiservice.ocm.model.UsageType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PoisResponse(
    @SerialName("ID") val id: Int,
    @SerialName("UUID") val uuid: String,
    @SerialName("AddressInfo") val addressInfo: AddressInfo,
    @SerialName("OperatorInfo") val operatorInfo: Operator,
    @SerialName("UsageType") val usageType: UsageType,
    @SerialName("Connections") val connections: List<Connections>,
)