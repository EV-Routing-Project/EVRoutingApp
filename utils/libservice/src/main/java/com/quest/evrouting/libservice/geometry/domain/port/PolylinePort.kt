package com.quest.evrouting.libservice.geometry.domain.port

import com.quest.evrouting.libservice.geometry.domain.model.LineString

interface PolylinePort {
    fun decode(encodedPath: String, precision: Int): LineString?

    fun encode(lineString: LineString, precision: Int): String
}