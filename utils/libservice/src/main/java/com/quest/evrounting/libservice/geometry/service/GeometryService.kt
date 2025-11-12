package com.quest.evrounting.libservice.geometry.service

import com.quest.evrounting.libservice.geometry.domain.unit.GeometryUnit
import com.quest.evrounting.libservice.geometry.domain.model.Point
import com.quest.evrounting.libservice.geometry.external.di.ServiceLocator
import com.quest.evrounting.libservice.geometry.domain.manager.MeasurementManager
import com.quest.evrounting.libservice.geometry.domain.model.LineString
import com.quest.evrounting.libservice.geometry.domain.model.Polygon

object GeometryService {
    private val measurementManager: MeasurementManager = ServiceLocator.measurementManager

    fun createPoint(lon: Double, lat: Double, alt: Double = 0.0): Point? {
        return try {
            Point(lon, lat, alt)
        } catch(e: IllegalArgumentException){
            null
        }
    }

    fun createLineString(points: List<Point>) : LineString? {
        return try {
            LineString(points)
        } catch(e: IllegalArgumentException){
            null
        }
    }

    fun createPolygon(points: List<List<Point>>) : Polygon? {
        return try {
            Polygon(points)
        } catch(e: IllegalArgumentException){
            null
        }
    }

    fun distance(startPoint: Point, endPoint: Point, units: GeometryUnit = GeometryUnit.UNIT_METERS): Double {
        return measurementManager.getHaversineDistance(startPoint, endPoint, units)
    }

    fun findPointsInsideCircle(
        points: List<Point>,
        radius: Double,
        units: GeometryUnit = GeometryUnit.UNIT_METERS,
        cenLat: Double,
        cenLon: Double,
        cenAlt: Double = 0.0
    ) : MutableList<Point>{
        val pointsInside: MutableList<Point> = mutableListOf()
        val cenPoint: Point = Point(cenLon, cenLat, cenAlt)
        for(point in points){
            if(distance(point, cenPoint, units) < radius){
                pointsInside.add(point)
            }
        }
        return pointsInside
    }

    fun getLengthOfPath(path: LineString, units: GeometryUnit = GeometryUnit.UNIT_METERS): Double {
        return measurementManager.getLengthOfLineString(path, units)
    }

    fun findPathAlongPath(
        path: LineString,
        distanceAlong: Double,
        units: GeometryUnit = GeometryUnit.UNIT_METERS
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

    fun findPointAlongPath (
        path: LineString,
        distanceAlong: Double,
        units: GeometryUnit = GeometryUnit.UNIT_METERS
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