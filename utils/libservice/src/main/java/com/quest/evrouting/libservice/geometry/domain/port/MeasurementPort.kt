package com.quest.evrouting.libservice.geometry.domain.port

import com.quest.evrouting.libservice.geometry.domain.model.LineString
import com.quest.evrouting.libservice.geometry.utils.GeometryUnit
import com.quest.evrouting.libservice.geometry.domain.model.Point

interface MeasurementPort {
    fun getHaversineDistance(startPoint: Point, endPoint: Point, units: GeometryUnit): Double
    fun getLengthOfLineString(path: LineString, units: GeometryUnit): Double
}