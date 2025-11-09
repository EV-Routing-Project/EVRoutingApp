package com.quest.evrounting.libservice.geometry.domain.port

import com.quest.evrounting.libservice.geometry.domain.constant.GeometryConstant
import com.quest.evrounting.libservice.geometry.domain.model.Point

interface MeasurementPort {
    fun getHaversineDistance(startPoint: Point, endPoint: Point, units: GeometryConstant): Double
}