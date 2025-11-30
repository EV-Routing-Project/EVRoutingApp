package com.quest.evrounting.algorithm.domain.port.tool.cache

import com.quest.evrounting.algorithm.domain.port.model.PathPort
import com.quest.evrounting.algorithm.domain.port.model.StationPort

interface CacheServicePort {
    fun insert(start: StationPort, end: StationPort, path: PathPort)
    fun request(start: StationPort, end: StationPort) : PathPort?
    fun requestAll(): List<PathPort>
    fun initWithOtherCache(otherCacheManager : CacheServicePort)
}