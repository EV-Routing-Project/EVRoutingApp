package com.quest.evrounting.libservice.geometry.external.adapter

import com.quest.evrounting.libservice.geometry.domain.port.MeasurementPort
import com.mapbox.turf.TurfMeasurement
import com.quest.evrounting.libservice.geometry.domain.constant.GeometryConstant
import com.quest.evrounting.libservice.geometry.external.mapper.toExternal
import com.quest.evrounting.libservice.geometry.domain.model.Point

class MeasurementAdapter: MeasurementPort {
    override fun getHaversineDistance(startPoint: Point, endPoint: Point, units: GeometryConstant): Double {
        return TurfMeasurement.distance(
            startPoint.toExternal(),
            endPoint.toExternal(),
            units.toExternal()
        )
    }
}