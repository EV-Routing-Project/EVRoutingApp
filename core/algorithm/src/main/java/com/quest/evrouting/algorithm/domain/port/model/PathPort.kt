package com.quest.evrouting.algorithm.domain.port.model

import com.quest.evrouting.algorithm.domain.model.LineString
import com.quest.evrouting.algorithm.domain.model.Point

interface PathPort {
    fun getStartLocation(): Point?
    fun getEndLocation(): Point?
    fun toLineString() : LineString?
    fun getDistance(): Double
    fun isEmptyPath(): Boolean
}