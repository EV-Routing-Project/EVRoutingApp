package com.quest.evrouting.configuration.utils.libservice.geometry

import com.quest.evrouting.libservice.geometry.domain.port.GeohashServicePort
import com.quest.evrouting.libservice.geometry.domain.port.GeometryServicePort
import com.quest.evrouting.libservice.geometry.domain.port.MeasurementServicePort
import com.quest.evrouting.libservice.geometry.domain.port.PolylineServicePort
import com.quest.evrouting.libservice.geometry.infrastructure.adapter.GeohashServiceAdapter
import com.quest.evrouting.libservice.geometry.infrastructure.adapter.GeometryServiceAdapter
import com.quest.evrouting.libservice.geometry.infrastructure.adapter.MeasurementServiceAdapter
import com.quest.evrouting.libservice.geometry.infrastructure.adapter.PolylineServiceAdapter

object GeometryLibServiceConfig {
    val geohashService: GeohashServicePort by lazy {
        GeohashServiceAdapter()
    }

    val measurementService: MeasurementServicePort by lazy {
        MeasurementServiceAdapter()
    }

    val geometryService: GeometryServicePort by lazy {
        GeometryServiceAdapter()
    }

    val polylineService: PolylineServicePort by lazy {
        PolylineServiceAdapter()
    }
}