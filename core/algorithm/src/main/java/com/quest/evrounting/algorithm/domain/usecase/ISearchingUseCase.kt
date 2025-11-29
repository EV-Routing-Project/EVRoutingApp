package com.quest.evrounting.algorithm.domain.usecase

import com.quest.evrounting.algorithm.domain.port.tool.cache.CacheServicePort
import com.quest.evrounting.algorithm.domain.port.tool.indexing.IndexingServicePort
import com.quest.evrounting.algorithm.domain.model.Point
import com.quest.evrounting.algorithm.domain.port.context.SpatialContextPort
import com.quest.evrounting.algorithm.domain.port.model.PathPort
import com.quest.evrounting.algorithm.domain.port.model.VehiclePort
import com.quest.evrounting.algorithm.domain.port.tool.routing.RoutingServicePort

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