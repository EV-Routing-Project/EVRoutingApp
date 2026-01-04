package com.quest.evrouting.libservice.geometry.domain.port

import com.quest.evrouting.libservice.geometry.domain.model.LineString

interface PolylineServicePort {
    companion object{
        const val PRECISION: Int = 6
    }
    fun decode(encodedPath: String, precision: Int = PRECISION): LineString?

    fun encode(lineString: LineString, precision: Int = PRECISION): String
}