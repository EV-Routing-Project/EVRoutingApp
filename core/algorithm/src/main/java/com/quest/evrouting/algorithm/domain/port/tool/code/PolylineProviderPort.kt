package com.quest.evrouting.algorithm.domain.port.tool.code

import com.quest.evrouting.algorithm.domain.model.LineString

interface PolylineProviderPort {
    fun decode(encodedPath: String): LineString?
    fun encode(lineString: LineString): String
}