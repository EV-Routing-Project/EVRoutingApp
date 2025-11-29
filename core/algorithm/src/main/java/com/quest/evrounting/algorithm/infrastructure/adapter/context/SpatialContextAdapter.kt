package com.quest.evrounting.algorithm.infrastructure.adapter.context

import com.quest.evrounting.algorithm.domain.model.Point
import com.quest.evrounting.algorithm.domain.port.context.SpatialContextPort
import com.quest.evrounting.algorithm.domain.port.model.PathPort
import com.quest.evrounting.algorithm.domain.port.model.StationPort
import com.quest.evrounting.algorithm.domain.port.tool.cache.CacheServicePort
import com.quest.evrounting.algorithm.domain.port.tool.indexing.IndexingServicePort
import com.quest.evrounting.algorithm.domain.port.tool.routing.RoutingServicePort
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

class SpatialContextAdapter (
    private val indexingService: IndexingServicePort,
    private val cacheService: CacheServicePort,
    private val routingService: RoutingServicePort,
    stations: List<StationPort>
): SpatialContextPort {
    init {
        for(station in stations){
            indexingService.insert(station)
        }
    }

    // <editor-fold desc="CRUD">
    override fun getHaversineDistance(
        start: Point,
        end: Point
    ): Double {
        return routingService.getHaversineDistance(start, end)
    }

    override fun getHaversineDistance(
        start: StationPort,
        end: StationPort
    ): Double {
        return getHaversineDistance(start.getLocation(), end.getLocation())
    }

    override suspend fun getShortestPath(
        start: Point,
        end: Point
    ): PathPort? {
        return routingService.findShortestPath(start, end)
    }

    override suspend fun getShortestPath(
        start: StationPort,
        end: StationPort
    ): PathPort? {
        cacheService.request(start , end)?.let {
            return it
        }
        return getShortestPath(start.getLocation(), end.getLocation())?.also {
            cacheService.insert(start, end, it)
        }
    }

    override fun findStationsInsideCircle(
        radius: Double,
        center: Point
    ): List<StationPort> {
        return indexingService.findStationsInsideCircle(radius, center)
    }

    override suspend fun findStationsWithinDrivingDistance(
        start: Point,
        maxDistance: Double
    ): Map<StationPort, PathPort> {
        val candidatesStation = findStationsInsideCircle(maxDistance, start)
        if(candidatesStation.isEmpty()) return emptyMap()
        return coroutineScope {
            candidatesStation.map {
                station -> async {
                    val path = getShortestPath(start, station.getLocation())
                    if(path != null && path.getDistance() <= maxDistance){
                        station to path
                    } else {
                        null
                    }
                }
            }.awaitAll().filterNotNull().toMap()
        }
    }

    override suspend fun findStationsWithinDrivingDistance(
        start: StationPort,
        maxDistance: Double
    ) : Map<StationPort, PathPort> {
        val candidatesStation = findStationsInsideCircle(maxDistance, start.getLocation())
        if(candidatesStation.isEmpty()) return emptyMap()
        return coroutineScope {
            candidatesStation.map {
                station -> async {
                    val path = getShortestPath(start, station)
                    if(path != null && path.getDistance() <= maxDistance){
                        station to path
                    } else {
                        null
                    }
                }
            }.awaitAll().filterNotNull().toMap()
        }
    }

    override suspend fun findReachableStationsFromTarget(
        target: Point,
        maxDistance: Double
    ): Map<StationPort, PathPort> {
        val candidatesStation = findStationsInsideCircle(maxDistance, target)
        if(candidatesStation.isEmpty()) return emptyMap()
        return coroutineScope {
            candidatesStation.map {
                station -> async {
                    val path = getShortestPath(station.getLocation(), target)
                    if(path != null && path.getDistance() <= maxDistance){
                        station to path
                    } else {
                        null
                    }
                }
            }.awaitAll().filterNotNull().toMap()
        }
    }

    override fun insert(station: StationPort){
        indexingService.insert(station)
    }

    override fun insert(stations: List<StationPort>){
        for(station in stations){
            insert(station)
        }
    }

    override fun update(station: StationPort){
        indexingService.update(station)
    }

    override fun update(stations: List<StationPort>) {
        for(station in stations){
            update(station)
        }
    }

    override fun delete(station: StationPort){
        indexingService.delete(station)
    }

    override fun delete(stations: List<StationPort>){
        for(station in stations) {
            delete(station)
        }
    }
    //  </editor-fold>
}