package com.quest.evrounting.libservice.geometry.domain.manager

import com.quest.evrounting.libservice.geometry.domain.constant.GeometryConstant
import com.quest.evrounting.libservice.geometry.domain.port.MeasurementPort
import com.quest.evrounting.libservice.geometry.domain.model.Point

class MeasurementManager(val adapter: MeasurementPort) {
    fun getHaversineDistance(startPoint: Point, endPoint: Point, units: GeometryConstant = GeometryConstant.UNIT_METERS): Double {
        return adapter.getHaversineDistance(startPoint,endPoint, units)
    }
}