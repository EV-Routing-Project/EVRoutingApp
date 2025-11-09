package com.quest.evrounting.libservice.geometry.service

import com.quest.evrounting.libservice.geometry.domain.constant.GeometryConstant
import com.quest.evrounting.libservice.geometry.domain.model.Point
import com.quest.evrounting.libservice.geometry.external.di.ServiceLocator
import com.quest.evrounting.libservice.geometry.domain.manager.MeasurementManager

object GeometryService {
    val measurementManager: MeasurementManager = ServiceLocator.measurementManager
    fun distance(startPoint: Point, endPoint: Point, units: GeometryConstant = GeometryConstant.UNIT_METERS): Double {
        return measurementManager.getHaversineDistance(startPoint, endPoint, units)
    }
    fun findPointsInsideCircle(
        points: List<Point>,
        radius: Double,
        units: GeometryConstant = GeometryConstant.UNIT_METERS,
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
}