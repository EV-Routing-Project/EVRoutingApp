package com.quest.evrounting.algorithm.domain.modelport

import com.quest.evrounting.algorithm.domain.model.Point

interface IVehicle {
    val batteryConsumptionRate: Double
    val totalBatteryEnergy: Int
    var currentBatteryCapacity: Int
    var currentLocation : Point

    fun calculateEnergyForDistance(distance: Double): Double
    fun calculateDistanceWithEnergy(energy: Int): Double

    fun calculateTimeForDistance(distance: Double): Long
    fun calculateChargingTime(energyToCharge: Int): Long
    fun calculateTimeForEnergy(energyConsumed: Int): Long
}