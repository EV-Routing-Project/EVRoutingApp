package com.quest.evrounting.algorithm.application.entity

import com.quest.evrounting.algorithm.domain.modelport.IPath
import com.quest.evrounting.algorithm.domain.model.LineString
import com.quest.evrounting.algorithm.domain.model.Point

class DirectPath(
    private val path: LineString,
) : IPath {
    override fun start(): Point? {
        return path.coordinates.first()
    }

    override fun end(): Point? {
        return path.coordinates.last()
    }

    override fun toLineString(): LineString? {
        return path
    }

    override fun distance(): Double {
        return path.distance
    }

    override fun isEmptyPath(): Boolean {
        return false
    }
}