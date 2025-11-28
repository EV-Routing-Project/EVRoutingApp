package com.quest.evrounting.algorithm.domain.modelport

import com.quest.evrounting.algorithm.domain.model.LineString
import com.quest.evrounting.algorithm.domain.model.Point

interface IPath {
    fun start(): Point?
    fun end(): Point?
    fun toLineString() : LineString?
    fun distance(): Double
    fun isEmptyPath(): Boolean
}