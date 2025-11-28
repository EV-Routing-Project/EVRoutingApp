package com.quest.evrounting.algorithm.domain.modelport

import com.quest.evrounting.algorithm.domain.model.Geohash
import com.quest.evrounting.algorithm.domain.model.Point

interface IStation {
    val id: String
    val point: Point
    val geohash: Geohash
    val status: Boolean
}