package com.quest.evrouting.libservice.geometry.domain.port

import com.quest.evrouting.libservice.geometry.domain.model.Geohash
import com.quest.evrouting.libservice.geometry.domain.model.Point

interface GeohashServicePort {

//    fun getAdjacent(geohash: Geohash): List<Geohash>
//    fun getLonDegree(bits: Int): Double
//    fun getLatDegree(bits: Int): Double
//    fun convertLonDegreeToMeters(lonDegree: Double, point: Point): Double
//    fun convertLatDegreeToMeters(latDegree: Double): Double

    fun encode(point: Point, significantBits: Int): Geohash
    fun getGeohashGridForPoint(point: Point, significantBits: Int): List<Geohash>
    fun createGeohashFromLongValue(value: Long, significantBits: Int): Geohash
    fun getLonSize(bits: Int, point: Point): Double
    fun getLatSize(bits: Int): Double
    fun adjustGeohashPrecision(geohash: Geohash, significantOfSystem: Int) : Geohash
}