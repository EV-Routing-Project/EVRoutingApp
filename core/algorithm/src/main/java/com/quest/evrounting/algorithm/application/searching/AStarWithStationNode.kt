package com.quest.evrounting.algorithm.application.searching

import com.quest.evrounting.algorithm.application.cache.ICacheManager
import com.quest.evrounting.algorithm.application.entity.SegmentedPath
import com.quest.evrounting.algorithm.application.index.IStationManager
import com.quest.evrounting.algorithm.application.routing.IRoutingManager
import com.quest.evrounting.algorithm.domain.modelport.IPath
import com.quest.evrounting.algorithm.domain.modelport.IStation
import com.quest.evrounting.algorithm.domain.modelport.IVehicle
import com.quest.evrounting.algorithm.domain.model.Point
import com.quest.evrounting.algorithm.utils.GeoUtils
import java.util.PriorityQueue

class AStarWithStationNode(
    override val stationManager: IStationManager,
    override val cacheManager: ICacheManager,
    override val routingManager: IRoutingManager
) : ISearchingManager {
    override suspend fun findOptimalPath(
        vehicle: IVehicle,
        target: Point,
        minBatteryPercent: Int,
        maxBatteryPercent: Int
    ): IPath? {
        val path = SegmentedPath()
        val startLocation = vehicle.currentLocation
        val startBattery = vehicle.currentBatteryCapacity
        val queue = PriorityQueue<Node>(compareBy { it.fScore })
        val output = getOutPut(target, vehicle, maxBatteryPercent, GeoUtils.IDEAL_BATTERY_CAPACITY)
        queue.add(Node(
            startLocation,
            0.0,
            getHaversineDistance(startLocation, target),
            startBattery))
        while(!queue.isEmpty()){
            val currNode = queue.poll()
        }
        return path
    }

    private suspend fun getOutPut(
        target: Point,
        vehicle: IVehicle,
        maxBatteryPercent: Int,
        targetBattery: Int
    ): Set<IStation>? {
        val distance = vehicle.calculateDistanceWithEnergy((maxBatteryPercent - targetBattery))
        val stations = stationManager.findStationsInsideCircle(distance, target)
        if(stations.isEmpty()) return null
        val resStations = mutableSetOf<IStation>()
        for(station in stations){
            val path = getShortestPath(station.point, target)
            if(path != null && path.distance() <= distance){
                resStations.add(station)
            }
        }
        return resStations
    }

    private fun getHaversineDistance(
        start: Point,
        end: Point
    ): Double {
        return routingManager.getHaversineDistance(start, end)
    }

    private suspend fun getShortestPath(
        start: Point,
        end: Point
    ) : IPath? {
        return routingManager.findShortestPath(start, end)
    }

    private suspend fun getShortestPath (
        start: IStation,
        end: IStation
    ) : IPath? {
        var path = cacheManager.request(start, end)
        if(path == null){
            path = getShortestPath(start.point, end.point)
            if(path != null){
                cacheManager.insert(start, end, path)
            }
        }
        return path
    }


    private class Node(
        val location: Point,
        var gScore: Double = Double.MAX_VALUE,
        var fScore: Double = Double.MAX_VALUE,
        val batteryCapacity: Int,
        var preNode: Node? = null,
        var station: IStation? = null
    )
}