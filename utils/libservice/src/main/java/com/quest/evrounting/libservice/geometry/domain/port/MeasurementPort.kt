package com.quest.evrounting.libservice.geometry.domain.port

import com.quest.evrounting.libservice.geometry.domain.model.LineString
import com.quest.evrounting.libservice.geometry.utils.GeometryUnit
import com.quest.evrounting.libservice.geometry.domain.model.Point

interface MeasurementPort {
    fun getHaversineDistance(startPoint: Point, endPoint: Point, units: GeometryUnit): Double
    fun getLengthOfLineString(path: LineString, units: GeometryUnit): Double
}