package com.quest.evrounting.data.simulation

data class ChargingSession(
    val sessionId: String,
    val carId: String,
    val connectionId: Int,
    val startTime: Long,
    val estimatedEndTime: Long
)
