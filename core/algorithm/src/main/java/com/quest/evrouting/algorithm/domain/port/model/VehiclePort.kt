package com.quest.evrouting.algorithm.domain.port.model

import com.quest.evrouting.algorithm.domain.model.Point

interface VehiclePort {
    fun getBatteryConsumptionRate(): Double
    fun getTotalBatteryEnergy(): Int
    fun getCurrentBatteryCapacity(): Int
    fun getCurrentLocation(): Point

    fun calculateEnergyForDistance(distance: Double): Int
    fun calculateDistanceWithEnergy(energy: Int): Double

    fun calculateTimeForDistance(distance: Double): Long
    fun calculateChargingTime(energyToCharge: Int): Long
    fun calculateTimeForEnergy(energyConsumed: Int): Long
}