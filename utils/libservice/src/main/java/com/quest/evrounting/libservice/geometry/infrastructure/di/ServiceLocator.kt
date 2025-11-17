package com.quest.evrounting.libservice.geometry.infrastructure.di

import com.quest.evrounting.libservice.geometry.domain.manager.GeohashManager
import com.quest.evrounting.libservice.geometry.domain.manager.GeometryManager
import com.quest.evrounting.libservice.geometry.infrastructure.adapter.MeasurementAdapter
import com.quest.evrounting.libservice.geometry.infrastructure.adapter.PolylineAdapter
import com.quest.evrounting.libservice.geometry.domain.manager.MeasurementManager
import com.quest.evrounting.libservice.geometry.domain.manager.PolylineManager
import com.quest.evrounting.libservice.geometry.infrastructure.adapter.GeohashAdapter

object ServiceLocator {

    val polylineManager: PolylineManager = PolylineManager(PolylineAdapter())

    val measurementManager: MeasurementManager = MeasurementManager(MeasurementAdapter())

    val geometryManager: GeometryManager = GeometryManager()

    val geohashManager: GeohashManager = GeohashManager(GeohashAdapter())
}