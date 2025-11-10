package com.quest.evrounting.libservice.geometry.external.mapper

import com.quest.evrounting.libservice.geometry.domain.unit.GeometryUnit
import com.mapbox.turf.TurfConstants

fun GeometryUnit.toExternal(): String {
    return when(this){
        GeometryUnit.UNIT_CENTIMETERS -> TurfConstants.UNIT_CENTIMETERS
        GeometryUnit.UNIT_METERS -> TurfConstants.UNIT_METERS
        GeometryUnit.UNIT_KILOMETERS -> TurfConstants.UNIT_KILOMETERS
        else -> TurfConstants.UNIT_METERS
    }
}