package com.quest.evrouting.algorithm.domain.port.tool.code

import com.quest.evrouting.algorithm.domain.model.Geohash
import com.quest.evrouting.algorithm.domain.model.Point

interface GeohashProviderPort {
    fun encode(point: Point, significantBits: Int): Geohash
    fun getGeohashGridForPoint(point: Point, significantBits: Int): List<Geohash>
    fun getLonSize(bits: Int, point: Point): Double
    fun getLatSize(bits: Int): Double
    fun adjustGeohashPrecision(geohash: Geohash, significantOfSystem: Int) : Geohash
}