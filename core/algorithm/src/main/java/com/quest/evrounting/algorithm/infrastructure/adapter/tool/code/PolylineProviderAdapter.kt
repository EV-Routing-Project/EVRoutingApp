package com.quest.evrounting.algorithm.infrastructure.adapter.tool.code

import com.quest.evrounting.algorithm.domain.model.LineString
import com.quest.evrounting.algorithm.domain.port.tool.code.PolylineProviderPort
import com.quest.evrounting.algorithm.infrastructure.mapper.toDomain
import com.quest.evrounting.algorithm.infrastructure.mapper.toExternal
import com.quest.evrounting.libservice.geometry.ServiceKit

class PolylineProviderAdapter : PolylineProviderPort {
    private val polylineService = ServiceKit.polylineService
    override fun decode(encodedPath: String): LineString? {
        return polylineService.decode(encodedPath)?.toDomain()
    }

    override fun encode(lineString: LineString): String {
        return polylineService.encode(lineString.toExternal())
    }
}