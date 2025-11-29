package com.quest.evrounting.algorithm.domain.port.tool.code

import com.quest.evrounting.algorithm.domain.model.LineString

interface PolylineProviderPort {
    fun decode(encodedPath: String): LineString?
    fun encode(lineString: LineString): String
}