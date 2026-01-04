package com.quest.evrouting.algorithm.domain.usecase

import com.quest.evrouting.algorithm.domain.model.Point
import com.quest.evrouting.algorithm.domain.port.model.PathPort
import com.quest.evrouting.algorithm.domain.port.model.VehiclePort

interface ISearchingUseCase {
    suspend fun findOptimalPath(
        vehicle: VehiclePort,
        target: Point,
        minBatteryPercent: Int,
        maxBatteryPercent: Int,
        minIdealBatteryPercent: Int,
        maxIdealBatteryPercent: Int
    ): PathPort?
}