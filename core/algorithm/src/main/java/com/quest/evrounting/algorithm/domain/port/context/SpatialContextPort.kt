package com.quest.evrounting.algorithm.domain.port.context

import com.quest.evrounting.algorithm.domain.model.Point
import com.quest.evrounting.algorithm.domain.port.model.PathPort
import com.quest.evrounting.algorithm.domain.port.model.StationPort

interface SpatialContextPort {

    fun getHaversineDistance(
        start: Point,
        end: Point
    ): Double

    fun getHaversineDistance(
        start: StationPort,
        end: StationPort
    ): Double

    suspend fun getShortestPath(
        start: Point,
        end: Point
    ): PathPort?

    suspend fun getShortestPath(
        start: StationPort,
        end: StationPort
    ): PathPort?

    fun findStationsInsideCircle(
        radius: Double,
        center: Point
    ): List<StationPort>

    suspend fun findStationsWithinDrivingDistance(
        start: Point,
        maxDistance: Double
    ): Map<StationPort, PathPort>

    suspend fun findStationsWithinDrivingDistance(
        start: StationPort,
        maxDistance: Double
    ) : Map<StationPort, PathPort>

    suspend fun findReachableStationsFromTarget (
        target: Point,
        maxDistance: Double,
    ): Map<StationPort, PathPort>


    fun insert(station: StationPort)
    fun insert(stations: List<StationPort>)
    fun update(station: StationPort)
    fun update(stations: List<StationPort>)
    fun delete(station: StationPort)
    fun delete(stations: List<StationPort>)
}