package com.quest.evrounting.algorithm.domain.port.model

import com.quest.evrounting.algorithm.domain.model.Geohash
import com.quest.evrounting.algorithm.domain.model.Point

interface StationPort {
    fun getId(): String
    fun getLocation(): Point
    fun getGeohash(): Geohash
    fun getStatus(): Boolean
}