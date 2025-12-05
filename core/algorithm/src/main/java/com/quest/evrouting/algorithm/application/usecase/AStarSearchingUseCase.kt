package com.quest.evrouting.algorithm.application.usecase


import com.quest.evrouting.algorithm.domain.model.Point
import com.quest.evrouting.algorithm.domain.port.context.SpatialContextPort
import com.quest.evrouting.algorithm.domain.port.model.PathPort
import com.quest.evrouting.algorithm.domain.port.model.StationPort
import com.quest.evrouting.algorithm.domain.port.model.VehiclePort
import com.quest.evrouting.algorithm.domain.usecase.ISearchingUseCase
import com.quest.evrouting.algorithm.infrastructure.adapter.model.SegmentedPathAdapter
import java.util.PriorityQueue

class AStarSearchingUseCase(
    private val spatialContext: SpatialContextPort,
) : ISearchingUseCase {
    override suspend fun findOptimalPath(
        vehicle: VehiclePort,
        target: Point,
        minBatteryPercent: Int,
        maxBatteryPercent: Int,
        minIdealBatteryPercent: Int,
        maxIdealBatteryPercent: Int
    ): PathPort? {
        val startLocation = vehicle.getCurrentLocation()
        val startBattery = vehicle.getCurrentBatteryCapacity()
        val maxDistance = vehicle.calculateDistanceWithEnergy(maxBatteryPercent - minBatteryPercent)
        val shortestPath = spatialContext.getShortestPath(startLocation, target)
        val mapStationsTarget = findReachableStationsFromTarget(
            target, vehicle,
            maxBatteryPercent,
            minIdealBatteryPercent,
            maxIdealBatteryPercent
        )
//
        if(mapStationsTarget.isEmpty()) return shortestPath
//
        val startNode = Node(
            startLocation,
            startBattery,
        )
        val queue = PriorityQueue<Node>(compareBy { it.fScore })
        val gScores = mutableMapOf<StationPort, Long>()

        spatialContext.findStationsWithinDrivingDistance(
            startLocation,
            vehicle.calculateDistanceWithEnergy(startBattery - minBatteryPercent)
        ).forEach {(station, path) ->
            val battery = startBattery - vehicle.calculateEnergyForDistance(path.getDistance())
            val gScore = vehicle.calculateTimeForDistance(path.getDistance()) + vehicle.calculateChargingTime(maxBatteryPercent - battery)
            val hScore = vehicle.calculateTimeForDistance(spatialContext.getHaversineDistance(station.getLocation(), target))
            queue.add(Node(
                location = station.getLocation(),
                batteryCapacity = battery,
                gScore = gScore,
                fScore = gScore + hScore,
                preNode = startNode,
                path = path,
                station = station
            ))
            gScores[station] = gScore
        }

        if(queue.isEmpty()) return shortestPath

        while(!queue.isEmpty()){
            val currNode = queue.poll()
            val station = currNode.station ?: continue
            val gScore = gScores[station]
            if(gScore != null && currNode.gScore > gScore) continue

            if(mapStationsTarget.containsKey(station)) {
                return createAPath(mapStationsTarget, currNode)
            }

            spatialContext.findStationsWithinDrivingDistance(
                station,
                maxDistance
            ).forEach { (station, path) ->
                val battery = maxBatteryPercent - vehicle.calculateEnergyForDistance(path.getDistance())
                val gScore = currNode.gScore + vehicle.calculateTimeForDistance(path.getDistance()) + vehicle.calculateChargingTime(maxBatteryPercent - battery)
                val hScore = vehicle.calculateTimeForDistance(spatialContext.getHaversineDistance(station.getLocation(), target))
                val visitedGScore = gScores[station]
                if(visitedGScore == null || gScore <= visitedGScore) {
                    queue.add(Node(
                        location = station.getLocation(),
                        batteryCapacity =  battery,
                        gScore = gScore,
                        fScore = gScore + hScore,
                        preNode = currNode,
                        path = path,
                        station = station
                    ))
                    gScores[station] = gScore
                }
            }
        }
        return shortestPath
    }

    private suspend fun findReachableStationsFromTarget (
        target: Point,
        vehicle: VehiclePort,
        maxBattery: Int,
        minBatteryTargetPercent: Int,
        maxBatteryTargetPercent: Int,
    ): Map<StationPort, PathPort> {
        for(i in maxBatteryTargetPercent downTo minBatteryTargetPercent){
            val distance = vehicle.calculateDistanceWithEnergy((maxBattery - i))
            val reachableStations = spatialContext.findReachableStationsFromTarget(target, distance)
            if(reachableStations.isNotEmpty()) return reachableStations
        }
        return emptyMap()
    }

    private fun createAPath(mapStationsTarget: Map<StationPort, PathPort>, lastNode: Node): PathPort? {
        val path = SegmentedPathAdapter()
        val paths = mutableListOf<PathPort>()
        val lastPath = mapStationsTarget[lastNode.station]
        if(lastPath != null){
            paths.add(lastPath)
            var currNode: Node? = lastNode
            while(currNode != null){
                val currPath = currNode.path
                if(currPath != null){
                    paths.add(currPath)
                }
                currNode = currNode.preNode
            }
            paths.reverse()
        }
        for(p in paths){
            path.add(p)
        }
        return path
    }

    private class Node(
        val location: Point,
        val batteryCapacity: Int,
        val gScore: Long = 0L,
        val fScore: Long = 0L,
        val preNode: Node? = null,
        val path: PathPort? = null,
        val station: StationPort? = null
    )
}