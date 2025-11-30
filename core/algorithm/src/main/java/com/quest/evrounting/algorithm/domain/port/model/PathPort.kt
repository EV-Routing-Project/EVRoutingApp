package com.quest.evrounting.algorithm.domain.port.model

import com.quest.evrounting.algorithm.domain.model.LineString
import com.quest.evrounting.algorithm.domain.model.Point

interface PathPort {
    fun getStartLocation(): Point?
    fun getEndLocation(): Point?
    fun toLineString() : LineString?
    fun getDistance(): Double
    fun isEmptyPath(): Boolean
}