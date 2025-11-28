package com.quest.evrounting.algorithm.application.searching

import com.quest.evrounting.algorithm.application.cache.ICacheManager
import com.quest.evrounting.algorithm.application.index.IStationManager
import com.quest.evrounting.algorithm.application.routing.IRoutingManager
import com.quest.evrounting.algorithm.domain.modelport.IPath
import com.quest.evrounting.algorithm.domain.modelport.IVehicle
import com.quest.evrounting.algorithm.domain.model.Point


interface ISearchingManager {
    val stationManager: IStationManager
    val cacheManager: ICacheManager
    val routingManager: IRoutingManager
    suspend fun findOptimalPath(
        vehicle: IVehicle,
        target: Point,
        minBatteryPercent: Int,
        maxBatteryPercent: Int
    ): IPath?
}