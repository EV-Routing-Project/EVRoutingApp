package com.quest.evrouting.phone.domain.model

data class EVSE(
    val id: String,
    val groupId: String,
    val time: Int,
    val preBatteryPercent: Double,
    val batteryPercent: Double
)
