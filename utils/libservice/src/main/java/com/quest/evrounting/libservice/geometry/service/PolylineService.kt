package com.quest.evrounting.libservice.geometry.service

import com.quest.evrounting.libservice.geometry.infrastructure.di.ServiceLocator
import com.quest.evrounting.libservice.geometry.domain.model.LineString
import com.quest.evrounting.libservice.geometry.domain.manager.PolylineManager

object PolylineService {
    private val polylineManager: PolylineManager = ServiceLocator.polylineManager

    fun decode(encodedPath: String): LineString? {
        return polylineManager.decode(encodedPath)
    }

    fun encode(lineString: LineString): String {
        return polylineManager.encode(lineString)
    }
}