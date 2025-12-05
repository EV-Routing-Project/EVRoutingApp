package com.quest.evrouting.algorithm.infrastructure.adapter.tool.code

import com.quest.evrouting.algorithm.domain.model.LineString
import com.quest.evrouting.algorithm.domain.port.tool.code.PolylineProviderPort
import com.quest.evrouting.algorithm.infrastructure.mapper.toDomain
import com.quest.evrouting.algorithm.infrastructure.mapper.toExternal
import com.quest.evrouting.libservice.geometry.ServiceKit

class PolylineProviderAdapter : PolylineProviderPort {
    private val polylineService = ServiceKit.polylineService
    override fun decode(encodedPath: String): LineString? {
        return polylineService.decode(encodedPath)?.toDomain()
    }

    override fun encode(lineString: LineString): String {
        return polylineService.encode(lineString.toExternal())
    }
}