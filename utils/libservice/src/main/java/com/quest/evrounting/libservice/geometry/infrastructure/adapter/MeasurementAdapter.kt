package com.quest.evrounting.libservice.geometry.infrastructure.adapter

import com.quest.evrounting.libservice.geometry.domain.port.MeasurementPort
import com.mapbox.turf.TurfMeasurement
import com.quest.evrounting.libservice.geometry.domain.model.LineString
import com.quest.evrounting.libservice.geometry.utils.GeometryUnit
import com.quest.evrounting.libservice.geometry.infrastructure.mapper.toExternal
import com.quest.evrounting.libservice.geometry.domain.model.Point

class MeasurementAdapter: MeasurementPort {
    override fun getHaversineDistance(startPoint: Point, endPoint: Point, units: GeometryUnit): Double {
        return TurfMeasurement.distance(
            startPoint.toExternal(),
            endPoint.toExternal(),
            units.toExternal()
        )
    }

    override fun getLengthOfLineString(path: LineString, units: GeometryUnit): Double {
        return TurfMeasurement.length(path.toExternal(), units.toExternal())
    }
}