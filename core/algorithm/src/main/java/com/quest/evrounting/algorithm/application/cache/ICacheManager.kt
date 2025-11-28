package com.quest.evrounting.algorithm.application.cache

import com.quest.evrounting.algorithm.domain.modelport.IStation
import com.quest.evrounting.algorithm.domain.modelport.IPath

interface ICacheManager {
    fun insert(start: IStation, end: IStation, path: IPath)
    fun request(start: IStation, end: IStation) : IPath?
    fun requestAll(): List<IPath>
    fun initWithOtherCache(otherCacheManager : ICacheManager)
}