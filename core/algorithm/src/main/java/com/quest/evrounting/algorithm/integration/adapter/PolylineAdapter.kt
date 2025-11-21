package com.quest.evrounting.algorithm.integration.adapter

import com.quest.evrounting.algorithm.domain.model.LineString
import com.quest.evrounting.algorithm.domain.port.PolylinePort
import com.quest.evrounting.algorithm.integration.mapper.toDomain
import com.quest.evrounting.algorithm.integration.mapper.toExternal
import com.quest.evrounting.libservice.geometry.ServiceKit

class PolylineAdapter : PolylinePort {
    private val polylineService = ServiceKit.polylineService
    override fun decode(encodedPath: String): LineString? {
        return polylineService.decode(encodedPath)?.toDomain()
    }

    override fun encode(lineString: LineString): String {
        return polylineService.encode(lineString.toExternal())
    }
}