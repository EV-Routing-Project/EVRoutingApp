package com.quest.evrounting.libservice.geometry.external.mapper

import com.quest.evrounting.libservice.geometry.domain.constant.GeometryConstant
import com.mapbox.turf.TurfConstants

fun GeometryConstant.toExternal(): String {
    return when(this){
        GeometryConstant.UNIT_METERS -> TurfConstants.UNIT_METERS
    }
}