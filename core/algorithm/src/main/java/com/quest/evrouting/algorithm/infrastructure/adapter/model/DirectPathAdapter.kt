package com.quest.evrouting.algorithm.infrastructure.adapter.model

import com.quest.evrouting.algorithm.domain.port.model.PathPort
import com.quest.evrouting.algorithm.domain.model.LineString
import com.quest.evrouting.algorithm.domain.model.Point

class DirectPathAdapter(
    private val path: LineString,
) : PathPort {
    override fun getStartLocation(): Point? {
        return path.coordinates.first()
    }

    override fun getEndLocation(): Point? {
        return path.coordinates.last()
    }

    override fun toLineString(): LineString? {
        return path
    }

    override fun getDistance(): Double {
        return path.distance
    }

    override fun isEmptyPath(): Boolean {
        return false
    }
}