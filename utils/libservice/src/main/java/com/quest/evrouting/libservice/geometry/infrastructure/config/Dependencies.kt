package com.quest.evrouting.libservice.geometry.infrastructure.config

import com.quest.evrouting.libservice.geometry.domain.manager.GeohashManager
import com.quest.evrouting.libservice.geometry.domain.manager.GeometryManager
import com.quest.evrouting.libservice.geometry.infrastructure.adapter.MeasurementAdapter
import com.quest.evrouting.libservice.geometry.infrastructure.adapter.PolylineAdapter
import com.quest.evrouting.libservice.geometry.domain.manager.MeasurementManager
import com.quest.evrouting.libservice.geometry.domain.manager.PolylineManager
import com.quest.evrouting.libservice.geometry.domain.port.GeohashPort
import com.quest.evrouting.libservice.geometry.domain.port.MeasurementPort
import com.quest.evrouting.libservice.geometry.domain.port.PolylinePort
import com.quest.evrouting.libservice.geometry.infrastructure.adapter.GeohashAdapter

object Dependencies {
    fun createPolylineManager(polylineAdapter: PolylinePort): PolylineManager {
        return PolylineManager(polylineAdapter)
    }

    fun createMeasurementManager(measurementAdapter: MeasurementPort): MeasurementManager {
        return MeasurementManager(measurementAdapter)
    }

    fun createGeohashManager(geohashAdapter: GeohashPort): GeohashManager {
        return GeohashManager(geohashAdapter)
    }

    fun createGeometryManager(): GeometryManager {
        return GeometryManager()
    }

    val polylineManager: PolylineManager by lazy {
        createPolylineManager(PolylineAdapter())
    }

    val measurementManager: MeasurementManager by lazy {
        createMeasurementManager(MeasurementAdapter())
    }

    val geohashManager: GeohashManager by lazy {
        createGeohashManager(GeohashAdapter())
    }

    val geometryManager: GeometryManager by lazy {
        createGeometryManager()
    }
}