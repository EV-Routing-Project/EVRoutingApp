package com.quest.evrouting.algorithm.infrastructure.adapter.tool.code

import com.quest.evrouting.algorithm.domain.model.LineString
import com.quest.evrouting.algorithm.domain.port.tool.code.PolylineProviderPort
import com.quest.evrouting.algorithm.infrastructure.mapper.toDomain
import com.quest.evrouting.algorithm.infrastructure.mapper.toExternal
import com.quest.evrouting.libservice.geometry.ServiceKit
import com.quest.evrouting.libservice.geometry.domain.port.PolylineServicePort

class PolylineProviderAdapter(
    private val polylineService: PolylineServicePort
) : PolylineProviderPort {
    override fun decode(encodedPath: String): LineString? {
        return polylineService.decode(encodedPath)?.toDomain()
    }

    override fun encode(lineString: LineString): String {
        return polylineService.encode(lineString.toExternal())
    }
}