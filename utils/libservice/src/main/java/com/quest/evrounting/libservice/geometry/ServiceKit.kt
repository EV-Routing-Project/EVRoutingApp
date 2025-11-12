package com.quest.evrounting.libservice.geometry

import com.quest.evrounting.libservice.geometry.service.GeohashService
import com.quest.evrounting.libservice.geometry.service.GeometryService
import com.quest.evrounting.libservice.geometry.service.PolylineService

object ServiceKit {
    val geometryService = GeometryService
    val geohashService = GeohashService
    val polylineService = PolylineService
}