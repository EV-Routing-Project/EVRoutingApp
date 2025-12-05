package com.quest.evrouting.libservice.geometry.infrastructure.adapter

import com.quest.evrouting.libservice.geometry.domain.port.MeasurementServicePort
import com.mapbox.turf.TurfMeasurement
import com.quest.evrouting.libservice.geometry.domain.model.LineString
import com.quest.evrouting.libservice.geometry.utils.GeometryUnit
import com.quest.evrouting.libservice.geometry.infrastructure.mapper.toExternal
import com.quest.evrouting.libservice.geometry.domain.model.Point
import com.quest.evrouting.libservice.geometry.service.GeometryService.distance
import com.quest.evrouting.libservice.geometry.service.GeometryService.getLengthOfPath

class MeasurementServiceAdapter: MeasurementServicePort {
    override fun getHaversineDistance(startPoint: Point, endPoint: Point, units: GeometryUnit): Double {
        return TurfMeasurement.distance(
            startPoint.toExternal(),
            endPoint.toExternal(),
            units.toExternal()
        )
    }

    override fun getLengthOfLineString(path: LineString, units: GeometryUnit): Double {
        return TurfMeasurement.length(path.toExternal(), units.toExternal())
    }

    override fun findPointsInsideCircle(
        points: List<Point>,
        radius: Double,
        units: GeometryUnit,
        center: Point
    ) : MutableList<Point>{
        val pointsInside: MutableList<Point> = mutableListOf()
        for(point in points){
            if(distance(point, center, units) < radius){
                pointsInside.add(point)
            }
        }
        return pointsInside
    }

    override fun findPathAlongPath(
        path: LineString,
        distanceAlong: Double,
        units: GeometryUnit
    ): LineString? {
        if(distanceAlong < 0){
            return null
        }
        if(path.coordinates.size < 2){
            return null
        }
        if(distanceAlong > getLengthOfPath(path)){
            return path
        }
        val pointsAlongPath = mutableListOf<Point>()
        val points = path.coordinates
        var distanceTraveled: Double = 0.0
        for(i in (0 until points.size - 1)){
            val startPoint = points[i]
            val endPoint = points[i + 1]
            val currPath = distance(startPoint, endPoint, units)
            pointsAlongPath.add(startPoint)
            if(distanceTraveled + currPath >= distanceAlong){
                val remainingDistance = distanceAlong - distanceTraveled
                val ratio = remainingDistance / currPath
                val lon = startPoint.lon + (endPoint.lon - startPoint.lon) * ratio
                val lat = startPoint.lat + (endPoint.lat - startPoint.lat) * ratio
                val alt = startPoint.alt + (endPoint.alt - startPoint.alt) * ratio
                pointsAlongPath.add(Point(lon, lat, alt))
                return LineString(pointsAlongPath)
            }
            distanceTraveled += currPath
        }
        return null
    }

    override fun findPointAlongPath (
        path: LineString,
        distanceAlong: Double,
        units: GeometryUnit
    ): Point? {
        if(distanceAlong < 0){
            return path.coordinates.firstOrNull()
        }
        if(path.coordinates.size < 2){
            return path.coordinates.firstOrNull()
        }
        if(distanceAlong > getLengthOfPath(path)){
            return path.coordinates.lastOrNull()
        }
        var distanceTraveled = 0.0
        val points = path.coordinates
        for(i in (0 until points.size - 1)){
            val startPoint = points[i]
            val endPoint = points[i + 1]
            val currPath = distance(startPoint, endPoint, units)
            if(distanceTraveled + currPath >= distanceAlong){
                val remainingDistance = distanceAlong - distanceTraveled
                val ratio = remainingDistance / currPath
                val lon = startPoint.lon + (endPoint.lon - startPoint.lon) * ratio
                val lat = startPoint.lat + (endPoint.lat - startPoint.lat) * ratio
                val alt = startPoint.alt + (endPoint.alt - startPoint.alt) * ratio
                return Point(lon, lat, alt)
            }
            distanceTraveled += currPath
        }
        return null
    }
}