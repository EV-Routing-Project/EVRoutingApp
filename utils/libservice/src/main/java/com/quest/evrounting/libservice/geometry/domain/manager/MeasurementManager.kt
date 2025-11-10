package com.quest.evrounting.libservice.geometry.domain.manager

import com.quest.evrounting.libservice.geometry.domain.model.LineString
import com.quest.evrounting.libservice.geometry.domain.unit.GeometryUnit
import com.quest.evrounting.libservice.geometry.domain.port.MeasurementPort
import com.quest.evrounting.libservice.geometry.domain.model.Point

class MeasurementManager(val adapter: MeasurementPort) {
    fun getHaversineDistance(startPoint: Point, endPoint: Point, units: GeometryUnit = GeometryUnit.UNIT_METERS): Double {
        return adapter.getHaversineDistance(startPoint,endPoint, units)
    }

    fun getLengthOfLineString(path: LineString, units: GeometryUnit = GeometryUnit.UNIT_METERS): Double {
        return adapter.getLengthOfLineString(path, units)
    }
}