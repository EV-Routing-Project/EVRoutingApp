package com.quest.evrounting.algorithm.domain.port

import com.quest.evrounting.algorithm.domain.model.LineString

interface PolylinePort {
    fun decode(encodedPath: String): LineString?
    fun encode(lineString: LineString): String
}