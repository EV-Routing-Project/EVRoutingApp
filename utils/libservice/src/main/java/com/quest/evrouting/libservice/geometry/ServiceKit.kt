package com.quest.evrouting.libservice.geometry

import com.quest.evrouting.libservice.geometry.service.GeohashService
import com.quest.evrouting.libservice.geometry.service.GeometryService
import com.quest.evrouting.libservice.geometry.service.PolylineService

object ServiceKit {
    val geometryService = GeometryService
    val geohashService = GeohashService
    val polylineService = PolylineService
}