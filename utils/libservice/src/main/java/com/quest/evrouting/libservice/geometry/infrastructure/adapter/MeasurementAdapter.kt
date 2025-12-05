package com.quest.evrouting.libservice.geometry.infrastructure.adapter

import com.quest.evrouting.libservice.geometry.domain.port.MeasurementPort
import com.mapbox.turf.TurfMeasurement
import com.quest.evrouting.libservice.geometry.domain.model.LineString
import com.quest.evrouting.libservice.geometry.utils.GeometryUnit
import com.quest.evrouting.libservice.geometry.infrastructure.mapper.toExternal
import com.quest.evrouting.libservice.geometry.domain.model.Point

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