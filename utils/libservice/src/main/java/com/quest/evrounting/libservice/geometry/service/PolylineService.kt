package com.quest.evrounting.libservice.geometry.service

import com.quest.evrounting.libservice.geometry.infrastructure.config.Dependencies
import com.quest.evrounting.libservice.geometry.domain.model.LineString
import com.quest.evrounting.libservice.geometry.domain.manager.PolylineManager

object PolylineService {
    private val polylineManager: PolylineManager = Dependencies.polylineManager

    fun decode(encodedPath: String): LineString? {
        return polylineManager.decode(encodedPath)
    }

    fun encode(lineString: LineString): String {
        return polylineManager.encode(lineString)
    }
}