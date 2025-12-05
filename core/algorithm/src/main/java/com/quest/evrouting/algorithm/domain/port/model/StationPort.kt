package com.quest.evrouting.algorithm.domain.port.model

import com.quest.evrouting.algorithm.domain.model.Geohash
import com.quest.evrouting.algorithm.domain.model.Point

interface StationPort {
    fun getId(): String
    fun getLocation(): Point
    fun getGeohash(): Geohash
    fun getStatus(): Boolean
}