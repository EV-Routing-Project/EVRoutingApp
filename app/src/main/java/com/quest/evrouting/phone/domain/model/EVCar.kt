package com.quest.evrouting.phone.domain.model

data class EVCar(
    override val totalPowerKwh: Double,
    override val currentPowerKwh: Double,
    override val averageSpeedKmh: Double,
) : Vehicle
