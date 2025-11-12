package com.quest.evrounting.libservice.geometry.external.di

import com.quest.evrounting.libservice.geometry.domain.manager.GeohashManager
import com.quest.evrounting.libservice.geometry.external.adapter.MeasurementAdapter
import com.quest.evrounting.libservice.geometry.external.adapter.PolylineAdapter
import com.quest.evrounting.libservice.geometry.domain.manager.MeasurementManager
import com.quest.evrounting.libservice.geometry.domain.manager.PolylineManager
import com.quest.evrounting.libservice.geometry.external.adapter.GeohashAdapter

object ServiceLocator {

    val polylineManager: PolylineManager = PolylineManager(PolylineAdapter())

    val measurementManager: MeasurementManager = MeasurementManager(MeasurementAdapter())

    val geohashManager: GeohashManager = GeohashManager(GeohashAdapter())
}