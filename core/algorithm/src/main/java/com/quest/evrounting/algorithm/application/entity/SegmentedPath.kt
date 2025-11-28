package com.quest.evrounting.algorithm.application.entity

import com.quest.evrounting.algorithm.domain.modelport.IPath
import com.quest.evrounting.algorithm.domain.model.LineString
import com.quest.evrounting.algorithm.domain.model.Point

class SegmentedPath(
    private val paths: MutableList<IPath> = mutableListOf()
) : IPath {
    private var distance: Double = 0.0
    override fun start(): Point? {
        return paths.firstOrNull()?.start()
    }

    override fun end(): Point? {
        return paths.lastOrNull()?.end()
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

    override fun distance(): Double {
        return paths.sumOf {it.distance()}
    }

    override fun isEmptyPath(): Boolean {
        return paths.isEmpty()
    }

    fun addLast(path: IPath) {
        if(path.isEmptyPath() || this == path) return
        if(isEmptyPath() || (this.end() == path.start())){
            paths.add(path)
        }
    }
}