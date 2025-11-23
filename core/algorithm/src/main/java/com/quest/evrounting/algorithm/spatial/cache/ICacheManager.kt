package com.quest.evrounting.algorithm.spatial.cache

import com.quest.evrounting.algorithm.domain.model.LineString
import com.quest.evrounting.algorithm.spatial.entity.BaseEntity

interface ICacheManager {
    fun insert(start: BaseEntity, end: BaseEntity, path: LineString)
    fun request(start: BaseEntity, end: BaseEntity) : LineString?
}