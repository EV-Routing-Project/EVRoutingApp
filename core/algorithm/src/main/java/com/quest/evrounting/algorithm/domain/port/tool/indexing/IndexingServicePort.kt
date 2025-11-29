package com.quest.evrounting.algorithm.domain.port.tool.indexing

import com.quest.evrounting.algorithm.domain.model.Point
import com.quest.evrounting.algorithm.domain.port.model.StationPort

interface IndexingServicePort {
    fun insert(station: StationPort)

    fun delete(station: StationPort)

    fun update(station: StationPort)

    fun findStationsInsideCircle(radius: Double, point: Point) : List<StationPort>

    fun estimatedCount(radius: Double, center: Point) : Int
}