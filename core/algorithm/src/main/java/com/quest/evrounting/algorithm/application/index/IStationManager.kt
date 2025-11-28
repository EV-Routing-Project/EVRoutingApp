package com.quest.evrounting.algorithm.application.index

import com.quest.evrounting.algorithm.domain.model.Point
import com.quest.evrounting.algorithm.domain.modelport.IStation

interface IStationManager {
    fun insert(station: IStation)

    fun delete(station: IStation)

    fun update(station: IStation)

    fun findStationsInsideCircle(radius: Double, point: Point) : List<IStation>

    fun estimatedCount(radius: Double, center: Point) : Int
}