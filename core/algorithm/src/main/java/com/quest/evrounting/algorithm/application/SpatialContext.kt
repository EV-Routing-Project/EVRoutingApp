package com.quest.evrounting.algorithm.application

import com.quest.evrounting.algorithm.domain.model.Point
import com.quest.evrounting.algorithm.application.index.IStationManager
import com.quest.evrounting.algorithm.application.searching.ISearchingManager
import com.quest.evrounting.algorithm.domain.modelport.IPath
import com.quest.evrounting.algorithm.domain.modelport.IStation
import com.quest.evrounting.algorithm.domain.modelport.IVehicle

class SpatialContext (
    private val searchingManager: ISearchingManager,
    stations: List<IStation>
) {
    private val stationManager: IStationManager = searchingManager.stationManager

    init {
        for(station in stations){
            stationManager.insert(station)
        }
    }

    // <editor-fold desc="Station Manager">
    fun insert(station: IStation){
        stationManager.insert(station)
    }

    fun insert(stations: List<IStation>){
        for(station in stations){
            insert(station)
        }
    }

    fun update(station: IStation){
        stationManager.update(station)
    }

    fun update(stations: List<IStation>) {
        for(station in stations){
            update(station)
        }
    }

    fun delete(station: IStation){
        stationManager.delete(station)
    }

    fun delete(stations: List<IStation>){
        for(station in stations) {
            delete(station)
        }
    }
    //  </editor-fold>

    suspend fun findOptimalPath (
        vehicle: IVehicle,
        target: Point,
        minBatteryPercent: Int,
        maxBatteryPercent: Int
    ): IPath? {
        return searchingManager.findOptimalPath(
            vehicle,
            target,
            minBatteryPercent,
            maxBatteryPercent
        )
    }
}