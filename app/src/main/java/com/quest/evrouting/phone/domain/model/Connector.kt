package com.quest.evrouting.phone.domain.model

data class Connector(
    val connectorType: String,
    val powerType: String,
    val connectorFormat: String,
    val maxElectricPower: Int
)
