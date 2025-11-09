package com.quest.evrounting.libservice.geometry.domain.port

import com.quest.evrounting.libservice.geometry.domain.model.LineString

interface PolylinePort {
    fun decode(encodedPath: String, precision: Int): LineString?

    fun encode(lineString: LineString, precision: Int): String
}