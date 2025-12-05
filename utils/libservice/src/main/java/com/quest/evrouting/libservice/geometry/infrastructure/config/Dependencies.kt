package com.quest.evrouting.libservice.geometry.infrastructure.config

import com.quest.evrouting.libservice.geometry.domain.manager.GeohashManager
import com.quest.evrouting.libservice.geometry.domain.manager.GeometryManager
import com.quest.evrouting.libservice.geometry.infrastructure.adapter.MeasurementServiceAdapter
import com.quest.evrouting.libservice.geometry.infrastructure.adapter.PolylineServiceAdapter
import com.quest.evrouting.libservice.geometry.domain.manager.MeasurementManager
import com.quest.evrouting.libservice.geometry.domain.manager.PolylineManager
import com.quest.evrouting.libservice.geometry.domain.port.GeohashServicePort
import com.quest.evrouting.libservice.geometry.domain.port.MeasurementServicePort
import com.quest.evrouting.libservice.geometry.domain.port.PolylineServicePort
import com.quest.evrouting.libservice.geometry.infrastructure.adapter.GeohashServiceAdapter

object Dependencies {
    fun createPolylineManager(polylineAdapter: PolylineServicePort): PolylineManager {
        return PolylineManager(polylineAdapter)
    }

    fun createMeasurementManager(measurementAdapter: MeasurementServicePort): MeasurementManager {
        return MeasurementManager(measurementAdapter)
    }

    fun createGeohashManager(geohashAdapter: GeohashServicePort): GeohashManager {
        return GeohashManager(geohashAdapter)
    }

    fun createGeometryManager(): GeometryManager {
        return GeometryManager()
    }

    val polylineManager: PolylineManager by lazy {
        createPolylineManager(PolylineServiceAdapter())
    }

    val measurementManager: MeasurementManager by lazy {
        createMeasurementManager(MeasurementServiceAdapter())
    }

    val geohashManager: GeohashManager by lazy {
        createGeohashManager(GeohashServiceAdapter())
    }

    val geometryManager: GeometryManager by lazy {
        createGeometryManager()
    }
}