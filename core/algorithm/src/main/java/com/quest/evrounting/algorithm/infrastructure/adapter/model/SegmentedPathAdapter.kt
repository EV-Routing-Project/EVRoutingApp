package com.quest.evrounting.algorithm.infrastructure.adapter.model

import com.quest.evrounting.algorithm.domain.port.model.PathPort
import com.quest.evrounting.algorithm.domain.model.LineString
import com.quest.evrounting.algorithm.domain.model.Point

class SegmentedPathAdapter(
    private val paths: MutableList<PathPort> = mutableListOf()
) : PathPort {
    private var distance: Double = 0.0
    override fun getStartLocation(): Point? {
        return paths.firstOrNull()?.getStartLocation()
    }

    override fun getEndLocation(): Point? {
        return paths.lastOrNull()?.getEndLocation()
    }

    override fun toLineString(): LineString? {
        if(isEmptyPath()) return null
        val points = mutableListOf<Point>()
        for(path in paths){
            if(!path.isEmptyPath()){
                val lineString = path.toLineString()
                if(lineString != null){
                    points.addAll(lineString.coordinates)
                }
            }
        }
        return LineString(points)
    }

    override fun getDistance(): Double {
        return paths.sumOf {it.getDistance()}
    }

    override fun isEmptyPath(): Boolean {
        return paths.isEmpty()
    }

    fun add(path: PathPort) {
        if(path.isEmptyPath() || this == path) return
        if(isEmptyPath() || (this.getEndLocation() == path.getStartLocation())){
            paths.add(path)
        }
    }
}