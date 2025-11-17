package com.quest.evrounting.algorithm.domain.port

import com.quest.evrounting.algorithm.domain.model.Geohash
import com.quest.evrounting.algorithm.domain.model.Point

interface GeohashPort {
    fun encode(point: Point, significantBits: Int): Geohash
    fun getGeohashGridForPoint(point: Point, significantBits: Int): List<Geohash>
    fun getLonSize(bits: Int, point: Point): Double
    fun getLatSize(bits: Int): Double
    fun adjustGeohashPrecision(geohash: Geohash, significantOfSystem: Int) : Geohash
}